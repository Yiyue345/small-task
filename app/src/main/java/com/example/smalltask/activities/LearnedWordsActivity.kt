package com.example.smalltask.activities

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smalltask.BaseActivity
import com.example.smalltask.R
import com.example.smalltask.databinding.ActivityLearnedWordsBinding
import com.example.smalltask.items.WordAdapter
import com.example.smalltask.learning.MyDatabaseHelper
import com.example.smalltask.learning.Word

class LearnedWordsActivity : BaseActivity() {

    lateinit var binding: ActivityLearnedWordsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLearnedWordsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        val dbHelper = MyDatabaseHelper(this, "words.db", 1)
        val db = dbHelper.readableDatabase

        var wordList = ArrayList<Word>()
        val cursor = db.query("Word",
            null,
            "id < ?",
            arrayOf("${getAllWords(this)}"),
            null,
            null,
            null)

        if (cursor.moveToFirst()){ // 真麻烦
            do {
                val word = cursor.getString(cursor.getColumnIndexOrThrow("word"))
                val accent = cursor.getString(cursor.getColumnIndexOrThrow("accent"))
                val meanCn = cursor.getString(cursor.getColumnIndexOrThrow("meanCn"))
                val meanEn = cursor.getString(cursor.getColumnIndexOrThrow("meanEn"))
                val sentence = cursor.getString(cursor.getColumnIndexOrThrow("sentence"))
                val sentenceTrans = cursor.getString(cursor.getColumnIndexOrThrow("sentenceTrans"))
                val wordObject = Word(word, accent, meanCn, meanEn, sentence, sentenceTrans)
                wordList.add(wordObject)
            } while (cursor.moveToNext())
        }
        cursor.close()

        if (wordList.isEmpty()) {
            binding.zeroWord.visibility = View.VISIBLE
        }

        binding.allWordsList.layoutManager = LinearLayoutManager(this)
        binding.allWordsList.adapter = WordAdapter(wordList)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    private fun getAllWords(context: Context): Int {
        val getInfo = getSharedPreferences("latest", MODE_PRIVATE)
        val username = getInfo.getString("username", "")!!
        val dbHelper = MyDatabaseHelper(context, "Database${username}.db", 1)
        val db = dbHelper.readableDatabase

        val userCursor = db.query("UserInfo",
            null,
            "username = ?",
            arrayOf(username),
            null,
            null,
            null,)
        var allWords = 0
        if (userCursor.moveToFirst()){
            allWords = userCursor.getInt(userCursor.getColumnIndexOrThrow("learnWords"))
        }
        userCursor.close()
        return allWords
    }
}