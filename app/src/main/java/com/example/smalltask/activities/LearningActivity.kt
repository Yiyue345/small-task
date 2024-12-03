package com.example.smalltask.activities

import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.smalltask.BaseActivity
import com.example.smalltask.R
import com.example.smalltask.databinding.ActivityLearningBinding
import com.example.smalltask.fragments.AnsFragment
import com.example.smalltask.fragments.ChooseFragment
import com.example.smalltask.fragments.FinishFragment
import com.example.smalltask.fragments.OnlyEnFragment
import com.example.smalltask.fragments.SentenceFragment
import com.example.smalltask.learning.MyDatabaseHelper
import com.example.smalltask.learning.Word
import com.example.smalltask.learning.WordViewModel
import java.io.File

class LearningActivity : BaseActivity() {

    lateinit var binding: ActivityLearningBinding

    private lateinit var wordViewModel: WordViewModel


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
        val userName = getInfo.getString("username", "")!!

        val dbHelper = MyDatabaseHelper(this, "Database${userName}.db", 1)
        val db = dbHelper.writableDatabase

        val cursor = db.query("UserInfo", null, null, null, null, null, null)
        var learnWordsEachTime = 10
        var learnWords = 0

        if (cursor.moveToFirst()) {
            learnWords = cursor.getInt(cursor.getColumnIndexOrThrow("learnWords"))
            learnWordsEachTime = cursor.getInt(cursor.getColumnIndexOrThrow("learnWordsEachTime"))
        }
        cursor.close()

        val wordDbHelper = MyDatabaseHelper(this, "words.db", 1)
        val wordDb = wordDbHelper.readableDatabase

        setSupportActionBar(binding.toolbar3)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        wordViewModel = ViewModelProvider(this)[WordViewModel::class.java]
        wordViewModel.username = userName
        wordViewModel.setLearningList(learnWordsEachTime) // 所有单词初始学习次数为0

        val startId = learnWords
        val endId = learnWords + learnWordsEachTime - 1

        val wordCursor = wordDb.query("Word", // GPT太强悍了
            null, // 我全都要
            "id BETWEEN ? AND ?",
            arrayOf("$startId", "$endId"),
            null, null, "id ASC") // ASC指升序(ascending)

        if (wordCursor.moveToFirst()) {
            do {
                val word = wordCursor.getString(wordCursor.getColumnIndexOrThrow("word")) // 更安全
                val accent = wordCursor.getString(wordCursor.getColumnIndexOrThrow("accent"))
                val meanCn = wordCursor.getString(wordCursor.getColumnIndexOrThrow("meanCn"))
                val meanEn = wordCursor.getString(wordCursor.getColumnIndexOrThrow("meanEn"))
                val sentence = wordCursor.getString(wordCursor.getColumnIndexOrThrow("sentence"))
                val sentenceTrans = wordCursor.getString(wordCursor.getColumnIndexOrThrow("sentenceTrans"))
                val wordObject = Word(word, accent, meanCn, meanEn, sentence, sentenceTrans)
                wordViewModel.addWord(wordObject)

            }
                while (wordCursor.moveToNext())
        }
        wordCursor.close()

        wordViewModel.setLearningList(learnWordsEachTime) // 一组多少个词
        wordViewModel.mode = "learning"
        wordViewModel.startId = startId
        wordViewModel.endId = endId

        wordViewModel.startTime = System.currentTimeMillis()

//            for (word in wordViewModel.words.value!!) {
//
//            }
        var flag = 1 // 丑陋但有效
        // 虽然有点bug但无伤大雅，吧……

        wordViewModel.wordLearningList.observe(this) {
            val ansFragment = supportFragmentManager.findFragmentByTag("ansFragment")
            val fragment = supportFragmentManager.findFragmentByTag("learningFragment")

            binding.progress.text = getString(R.string.study_progress, wordViewModel.counts, learnWordsEachTime)

            if (flag == 1) { // 第一次别覆盖掉了
                flag = 0
            }
            else {
                if (ansFragment != null) { //如果上一个是ans
                    supportFragmentManager.beginTransaction()
                        .remove(ansFragment)
                        .commit()
                    wordViewModel.number = (wordViewModel.number + 1) % learnWordsEachTime
                    wordViewModel.wordLearningList.value?.let { it1 ->
                        if (it1[wordViewModel.number] == 0) {
                            replaceFragment(ChooseFragment())
                        }
                        else if (it1[wordViewModel.number] == 1) {
                            replaceFragment(SentenceFragment())
                        }
                        else if (it1[wordViewModel.number] == 2) {
                            replaceFragment(OnlyEnFragment())
                        }
                        else if (it1[wordViewModel.number] == 3) {
                            db.execSQL("UPDATE UserInfo SET learnWords = ? WHERE username = ?", arrayOf(learnWords + wordViewModel.counts, userName))


                            if (wordViewModel.counts == learnWordsEachTime) {
                                wordViewModel.refreshEndTime()
                                val values = ContentValues().apply {
                                    put("type", "learning")
                                    put("date", (System.currentTimeMillis() / 86400000L) * 86400000L)
                                    put("duration", wordViewModel.endTime - wordViewModel.startTime)
                                    put("words", learnWordsEachTime)
                                } // 学习记录
                                db.insert("LearningRecords", null, values)
                                replaceFragment(FinishFragment())
                            }
                            else {
                                wordViewModel.wordLearningList.value = wordViewModel.wordLearningList.value
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

        replaceFragment(ChooseFragment())

        wordViewModel.finish.observe(this) { it ->
            if (it) {
                val intent = Intent(this, FinishActivity::class.java)
                intent.putExtra("mode", "learning")
                intent.putExtra("counts", wordViewModel.counts)
                startActivity(intent)
                setResult(RESULT_OK)
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
        setResult(RESULT_OK)
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.test_menu, menu)
//        return true
//    }
//
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> quitLearning()
//            R.id.test -> {
//                val intent = Intent(this, InitializeActivity::class.java)
//                startActivity(intent)
//            }
        }
        return true
    }

    override fun onBackPressed() {
        quitLearning()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.learningLayout, fragment, "learningFragment") // 这是tag！
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

    private fun quitLearning() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.close_page_title))
            .setMessage(getString(R.string.close_page_message))
            .setPositiveButton(getString(R.string.close_page_yes)) { _, _ ->
                super.onBackPressed()
            }
            .setNegativeButton(getString(R.string.close_page_no)) { _, _ ->

            }
            .create()
            .show()
    }

}