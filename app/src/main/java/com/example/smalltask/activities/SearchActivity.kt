package com.example.smalltask.activities

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.smalltask.BaseActivity
import com.example.smalltask.R
import com.example.smalltask.classes.YoudaoWord
import com.example.smalltask.databinding.ActivitySearchBinding
import com.example.smalltask.learning.MyDatabaseHelper
import com.example.smalltask.learning.Word
import com.example.smalltask.learning.WordViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class SearchActivity : BaseActivity() {

    lateinit var binding: ActivitySearchBinding
    private lateinit var wordViewModel: WordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val getLanguage = getSharedPreferences("latest", MODE_PRIVATE)
        val language = getLanguage.getString("language", "zh") ?: "zh"
        val region = getLanguage.getString("region", "CN") ?: "CN"
        updateLocate(language, region)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar3)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        check()
        wordViewModel = ViewModelProvider(this)[WordViewModel::class.java]
        val word = intent.getStringExtra("word")

        val wordDbHelper = MyDatabaseHelper(this, "words.db", 1)
        val wordDb = wordDbHelper.readableDatabase

        val wordCursor = wordDb.query("Word",
            null,
            "LOWER(word) = LOWER(?)",
            arrayOf(word),
            null, null, null) // ASC指升序(ascending)

        if (wordCursor.moveToFirst()){

            val word = wordCursor.getString(wordCursor.getColumnIndexOrThrow("word")) // 更安全
            val accent = wordCursor.getString(wordCursor.getColumnIndexOrThrow("accent"))
            val meanCn = wordCursor.getString(wordCursor.getColumnIndexOrThrow("meanCn"))
            val meanEn = wordCursor.getString(wordCursor.getColumnIndexOrThrow("meanEn"))
            val sentence = wordCursor.getString(wordCursor.getColumnIndexOrThrow("sentence"))
            val sentenceTrans = wordCursor.getString(wordCursor.getColumnIndexOrThrow("sentenceTrans"))
            val wordObject = Word(word, accent, meanCn, meanEn, sentence, sentenceTrans)
            wordViewModel.addWord(wordObject)

            word?.let {
                if (it.lowercase() == "ciallo") {
                    binding.cialloView.loadUrl("https://ciallo.cc/")
                    binding.cialloView.setOnTouchListener { _, _ -> true } // 禁用一切触摸事件
                    binding.cialloView.visibility = View.VISIBLE
                }
            }

            lifecycleScope.launch(Dispatchers.IO) {
                wordViewModel.playWordAudio(applicationContext)
            }

            wordViewModel.words.value?.let { binding.wordName.text = it[0].word }
            wordViewModel.words.value?.let { binding.accentTextView.text = it[0].accent }
            wordViewModel.words.value?.let { binding.meaningTextView.text = it[0].meanCn.replace("；  ", "\n") }
            wordViewModel.words.value?.let { binding.meaningEnTextView.text = it[0].meanEn }
            wordViewModel.words.value?.let { binding.sentenceTextView.text = it[0].sentence }
            wordViewModel.words.value?.let { binding.sentenceTransTextView.text = it[0].sentenceTrans }

            binding.soundImageView.visibility = View.VISIBLE
            binding.enMeanHint.visibility = View.VISIBLE
            binding.accentBtn.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    wordViewModel.playWordAudio(applicationContext)
                }
            }
        }
        else  {
            lifecycleScope.launch(Dispatchers.IO) {

                try {
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url("https://dict.youdao.com/suggest?num=1&doctype=json&q=$word")
                        .build()

                    val response = client.newCall(request).execute()
                    val responseData = response.body?.string()
                    if (responseData != null) {
                        val moshi = Moshi.Builder()
                            .add(KotlinJsonAdapterFactory())
                            .build()
                        val jsonAdapter = moshi.adapter(YoudaoWord::class.java)
                        val result = jsonAdapter.fromJson(responseData)

                        lifecycleScope.launch(Dispatchers.Main) {
                            if (result != null) {
                                if (result.result.code == 200) { // 成功
                                    val accent = ""
                                    val meanCn = result.data.entries?.get(0)?.explain!!
                                    val meanEn = ""
                                    val sentence = ""
                                    val sentenceTrans = ""
                                    val wordObject =
                                        word?.let { Word(it, accent, meanCn, meanEn, sentence, sentenceTrans) }

                                    wordObject?.let { wordViewModel.addWord(it) }

                                    wordViewModel.words.value?.let { binding.wordName.text = it[0].word }
                                    wordViewModel.words.value?.let { binding.accentTextView.text = it[0].accent }
                                    wordViewModel.words.value?.let { binding.meaningTextView.text = it[0].meanCn.replace("; ", "\n") }
                                    wordViewModel.words.value?.let { binding.meaningEnTextView.text = it[0].meanEn }
                                    wordViewModel.words.value?.let { binding.sentenceTextView.text = it[0].sentence }
                                    wordViewModel.words.value?.let { binding.sentenceTransTextView.text = it[0].sentenceTrans }

                                    binding.soundImageView.visibility = View.VISIBLE
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        wordViewModel.playWordAudio(applicationContext)
                                    }

                                    binding.accentBtn.setOnClickListener {
                                        lifecycleScope.launch(Dispatchers.IO) {
                                            wordViewModel.playWordAudio(applicationContext)
                                        }
                                    }
                                }
                                else if (result.result.code == 404) { // 不存在
                                    binding.wordName.visibility = View.GONE
                                    binding.accentTextView.visibility = View.GONE
                                    binding.meaningTextView.visibility = View.GONE
                                    binding.meaningEnTextView.visibility = View.GONE
                                    binding.sentenceTextView.visibility = View.GONE
                                    binding.sentenceTransTextView.visibility = View.GONE
                                    binding.errorTextView.text = getString(R.string.no_exist_word, word)
                                    binding.errorTextView.visibility = View.VISIBLE
                                }

                            }

                        }


                    }
                } catch (_: Exception) { // 失败
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.wordName.visibility = View.GONE
                        binding.accentTextView.visibility = View.GONE
                        binding.meaningTextView.visibility = View.GONE
                        binding.meaningEnTextView.visibility = View.GONE
                        binding.sentenceTextView.visibility = View.GONE
                        binding.sentenceTransTextView.visibility = View.GONE
                        binding.errorTextView.text = getString(R.string.fail_to_search)
                        binding.errorTextView.visibility = View.VISIBLE
                    }

                }






            }
        }


        wordCursor.close()
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }


    private fun check() {
        val file = File(this.filesDir, "background.png")
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            binding.backgroundImage.setImageBitmap(bitmap)
        }
        else {
            binding.backgroundImage.setImageBitmap(null)
        }
    }
}