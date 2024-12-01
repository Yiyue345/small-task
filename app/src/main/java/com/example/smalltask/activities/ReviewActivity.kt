package com.example.smalltask.activities

import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.smalltask.BaseActivity
import com.example.smalltask.R
import com.example.smalltask.databinding.ActivityReviewBinding
import com.example.smalltask.fragment.AnsFragment
import com.example.smalltask.fragment.ChooseFragment
import com.example.smalltask.fragment.FinishFragment
import com.example.smalltask.fragment.OnlyEnFragment
import com.example.smalltask.fragment.OnlyZhFragment
import com.example.smalltask.learning.MyDatabaseHelper
import com.example.smalltask.learning.Word
import com.example.smalltask.learning.WordViewModel
import java.io.File

class ReviewActivity : BaseActivity() {

    lateinit var binding: ActivityReviewBinding

    private lateinit var wordViewModel: WordViewModel
    private val oneDay = 86400000L

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val getLanguage = getSharedPreferences("latest", MODE_PRIVATE)
        val language = getLanguage.getString("language", "zh") ?: "zh"
        val region = getLanguage.getString("region", "CN") ?: "CN"
        updateLocate(language, region)
        enableEdgeToEdge()
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val today = (System.currentTimeMillis() / oneDay) * oneDay
        val getInfo = getSharedPreferences("latest", MODE_PRIVATE)
        val userName = getInfo.getString("username", "")!!

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        wordViewModel = ViewModelProvider(this)[WordViewModel::class.java]

        wordViewModel.mode = "review"
        wordViewModel.username = userName
        wordViewModel.today = today
        val dbHelper = MyDatabaseHelper(this, "Database${userName}.db", 1)
        val db = dbHelper.writableDatabase

        val cursor = db.query("UserInfo", null, null, null, null, null, null)
        var reviewWordsEachTime = 20

        if (cursor.moveToFirst()) {
            reviewWordsEachTime = cursor.getInt(cursor.getColumnIndexOrThrow("reviewWordsEachTime"))
        }
        cursor.close()


        val userWordCursor = db.query("UserWord",
            null,
            null,
            null,
            null,
            null,
            null
            )

        val wordDbHelper = MyDatabaseHelper(this, "words.db", 1)
        val wordDb = wordDbHelper.readableDatabase


        var i = 0
        if (userWordCursor.moveToFirst()) { // word列表初始化

            do {
                val userWord = userWordCursor.getString(userWordCursor.getColumnIndexOrThrow("word"))
                val lastTime = userWordCursor.getLong(userWordCursor.getColumnIndexOrThrow("lastTime"))
                val times = userWordCursor.getInt(userWordCursor.getColumnIndexOrThrow("times"))

                if (today > lastTime + (times - 1) * (times - 1) * today) { // (time-1)^2 还要修改
                    val wordCursor = wordDb.query("word",
                        null,
                        "word = ?",
                        arrayOf(userWord),
                        null,
                        null,
                        null)
                    wordCursor.moveToFirst() // 千万别忘
                    val word = wordCursor.getString(wordCursor.getColumnIndexOrThrow("word")) // 更安全
                    val accent = wordCursor.getString(wordCursor.getColumnIndexOrThrow("accent"))
                    val meanCn = wordCursor.getString(wordCursor.getColumnIndexOrThrow("meanCn"))
                    val meanEn = wordCursor.getString(wordCursor.getColumnIndexOrThrow("meanEn"))
                    val sentence = wordCursor.getString(wordCursor.getColumnIndexOrThrow("sentence"))
                    val sentenceTrans = wordCursor.getString(wordCursor.getColumnIndexOrThrow("sentenceTrans"))
                    val wordObject = Word(word, accent, meanCn, meanEn, sentence, sentenceTrans)
                    wordViewModel.addWord(wordObject)
                    i++
                    wordCursor.close()
                }
            } while (userWordCursor.moveToNext() && i < reviewWordsEachTime)
        }
        userWordCursor.close()
        reviewWordsEachTime = i

        wordViewModel.setReviewList(reviewWordsEachTime)
        val startTime = System.currentTimeMillis()
        wordViewModel.startTime = startTime

        var flag = 1
        wordViewModel.wordReviewList.observe(this) {
            val ansFragment = supportFragmentManager.findFragmentByTag("ansFragment")
            val fragment = supportFragmentManager.findFragmentByTag("reviewFragment")

            binding.progress.text = getString(R.string.study_progress, wordViewModel.counts, reviewWordsEachTime)

            if (flag == 1) { // 第一次别覆盖掉了
                flag = 0
            }
            else {
                if (ansFragment != null) { //如果上一个是ans
                    supportFragmentManager.beginTransaction()
                        .remove(ansFragment)
                        .commit()
                    wordViewModel.number = (wordViewModel.number + 1) % reviewWordsEachTime

                    wordViewModel.wordReviewList.value?.let { it1 -> // 此处数值需改
                        if (it1[wordViewModel.number] == 0) {
                            replaceFragment(OnlyEnFragment())
                        }
                        else if (it1[wordViewModel.number] >= 1 && it1[wordViewModel.number] < 4) {
                            replaceFragment(ChooseFragment())
                        }
                        else if (it1[wordViewModel.number] >= 4 && it1[wordViewModel.number] <= 6) {
                            replaceFragment(OnlyZhFragment())
                        }
                        else if (it1[wordViewModel.number] > 6) {
                            // 更新单词学习次数

                            if (wordViewModel.counts == reviewWordsEachTime) {
                                wordViewModel.refreshEndTime()
                                val values = ContentValues().apply {
                                    put("type", "review")
                                    put("date", (System.currentTimeMillis() / 86400000L) * 86400000L)
                                    put("duration", wordViewModel.endTime - wordViewModel.startTime)
                                    put("words", reviewWordsEachTime)
                                } // 学习记录
                                db.insert("LearningRecords", null, values)
                                replaceFragment(FinishFragment())
                            }
                            else {
                                wordViewModel.wordReviewList.value = wordViewModel.wordReviewList.value
                            }
                        }
                    }
                }

                else if (fragment != null){
                    supportFragmentManager.beginTransaction()
                        .remove(fragment)
                        .commit()
                    replaceAnsFragment(AnsFragment())
                }
            }

        }
        check()

        replaceFragment(OnlyZhFragment())

        wordViewModel.finish.observe(this) { it ->
            if (it) {
                val intent = Intent(this, FinishActivity::class.java)
                intent.putExtra("mode", "review")
                intent.putExtra("counts", wordViewModel.counts)
                startActivity(intent)
                finish()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        wordViewModel.refreshEndTime()
        val duration = wordViewModel.endTime - wordViewModel.startTime
        val dbHelper = MyDatabaseHelper(this, "Database${wordViewModel.username}.db", 1)
        val db = dbHelper.writableDatabase
        db.execSQL("UPDATE UserInfo SET learnHours = learnHours + ? WHERE username = ?", arrayOf(duration, wordViewModel.username))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }



    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.learningLayout, fragment, "reviewFragment") // 这是tag！
            .commit()
    }

    private fun replaceAnsFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.learningLayout, fragment, "ansFragment") // 这是tag！
            .commit()
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