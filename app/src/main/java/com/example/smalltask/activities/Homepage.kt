package com.example.smalltask.activities

import android.os.Bundle
import android.util.Log.d
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.room.Database
import com.example.smalltask.BaseActivity
import com.example.smalltask.R
import com.example.smalltask.databinding.ActivityHomepageBinding
import com.example.smalltask.fragment.HomepageFragment
import com.example.smalltask.fragment.NothingFragment
import com.example.smalltask.fragment.SettingsFragment
import com.example.smalltask.learning.MyDatabaseHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File

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



}