package com.example.smalltask.activities

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.smalltask.BaseActivity
import com.example.smalltask.R
import com.example.smalltask.databinding.ActivityHomepageBinding
import com.example.smalltask.fragment.HomepageFragment
import com.example.smalltask.fragment.NothingFragment
import com.example.smalltask.fragment.SettingsFragment
import com.example.smalltask.learning.MyDatabaseHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class Homepage : BaseActivity() {

    lateinit var binding: ActivityHomepageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        setSupportActionBar(binding.toolbar)

//        val dbHelper = MyDatabaseHelper(this, "words.db", 1)
//        val db = dbHelper.writableDatabase

//        runBlocking {
//            val cursor = db.query("Word", null, null, null, null, null, null)
//            if (cursor.moveToFirst()) {
//                do {
//                    val word = cursor.getString(cursor.getColumnIndex("word"))
//                    val id = cursor.getInt(cursor.getColumnIndex("id"))
//                    Log.d(id.toString(), word)
//                } while (cursor.moveToNext())
//            }
//        }
        initWordsDatabase(this) // words你在吗？

        val getInfo = getSharedPreferences("latest", MODE_PRIVATE)
        val userName = getInfo.getString("username", "")
        val databaseFile = this.getDatabasePath("Database${userName}.db") // 居然还省了一步

        if (!databaseFile.exists()) {
            val dbHelper = MyDatabaseHelper(this, "Database${userName}.db", 1)
            val db = dbHelper.writableDatabase
            db.execSQL(dbHelper.createUserInfo)
            db.execSQL(dbHelper.createUserWord)
            db.execSQL(dbHelper.createLearningRecords)
            db.execSQL("INSERT INTO UserInfo (username) VALUES (?)", arrayOf(userName))
        }


        val navigation : BottomNavigationView = findViewById<BottomNavigationView>(R.id.navigationView)

        replaceFragment(HomepageFragment())

        navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.me ->{
                    replaceFragment(SettingsFragment())
                    true
                }
                R.id.homepage -> {
                    replaceFragment(HomepageFragment())
                    true
                }
                R.id.nothing -> {
                    replaceFragment(NothingFragment())
                    true
                }
                else -> false
            }

        }

    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainLayout, fragment)
            .commit()
    }

    private fun initWordsDatabase(context: Context) {
        val dbFile = context.getDatabasePath("words.db")
        if (!dbFile.exists()) {
            val inputStream: InputStream = context.assets.open("words.db") // 创建一个assets的输入流
            val outputStream: OutputStream = FileOutputStream(dbFile) // 再创建一个对应的输出流

            val buffer = ByteArray(1024) // 一次写入这么多
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length) // 一次写入
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()
        }
    }


}