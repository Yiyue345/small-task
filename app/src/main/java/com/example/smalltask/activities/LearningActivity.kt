package com.example.smalltask.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.smalltask.BaseActivity
import com.example.smalltask.R
import com.example.smalltask.learning.Word
import com.example.smalltask.databinding.ActivityLearningBinding
import com.example.smalltask.fragment.ChooseFragment
import com.example.smalltask.learning.MyDatabaseHelper
import com.example.smalltask.learning.WordViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class LearningActivity : BaseActivity() {

    lateinit var binding: ActivityLearningBinding

    private lateinit var wordViewModel: WordViewModel

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLearningBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val getInfo = getSharedPreferences("latest", MODE_PRIVATE)
        val userName = getInfo.getString("username", "")

        val dbHelper = MyDatabaseHelper(this, "Database${userName}.db", 1)
        val db = dbHelper.writableDatabase

        val cursor = db.query("UserInfo", null, null, null, null, null, null)
        var learnWordsEachTime = 10
        var learnWords = 0

        if (cursor.moveToFirst()) {
            learnWords = cursor.getInt(cursor.getColumnIndex("learnWords"))
            learnWordsEachTime = cursor.getInt(cursor.getColumnIndex("learnWordsEachTime"))
        }
        cursor.close()

        val wordDbHelper = MyDatabaseHelper(this, "words.db", 1)

        setSupportActionBar(binding.toolbar3)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        wordViewModel = ViewModelProvider(this)[WordViewModel::class.java]

        wordViewModel.setLearningList(learnWordsEachTime)

        val wordName = "adapt"
        CoroutineScope(Dispatchers.IO).launch {

            if (wordName != "Ciallo") {
                try {

                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url("https://cdn.jsdelivr.net/gh/lyc8503/baicizhan-word-meaning-API@master/data/words/$wordName.json")
                        .build()

                    val response = client.newCall(request).execute()
                    val responseData = response.body?.string()
                    var word: Word? = null
                    if (responseData != null) {
                        word = parseWordWithMoshi(responseData)
                        word?.let { wordViewModel.addWord(it) }
                    }
//                    Log.d("Word", wordViewModel.words.value.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else {
                Log.d("Ciallo", "Ciallo")
            }

            // 假设上面获取完了

            wordViewModel.setLearningList(10)
            Log.d("list", wordViewModel.wordLearningList.value.toString())

//            for (word in wordViewModel.words.value!!) {
//
//            }
            replaceFragment(ChooseFragment())
        }

    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.test_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> finish()
//            R.id.test -> {
//                val intent = Intent(this, InitializeActivity::class.java)
//                startActivity(intent)
//            }
//        }
//        return true
//    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.learningLayout, fragment)
            .commit()
    }

    private fun parseWordWithMoshi(jsonData: String): Word? {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter = moshi.adapter(Word::class.java)
        val word = jsonAdapter.fromJson(jsonData)
        return word
    }

}