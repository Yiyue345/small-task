package com.example.smalltask.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smalltask.BaseActivity
import com.example.smalltask.R
import com.example.smalltask.databinding.ActivityFinishBinding
import com.example.smalltask.learning.MyDatabaseHelper

class FinishActivity : BaseActivity() {

    private lateinit var binding: ActivityFinishBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFinishBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val mode = intent.getStringExtra("mode")
        val counts = intent.getIntExtra("counts", 0)

        val getInfo = getSharedPreferences("latest", MODE_PRIVATE)
        val userName = getInfo.getString("username", "")!!

        val dbHelper = MyDatabaseHelper(this, "Database${userName}.db", 1)
        val db = dbHelper.writableDatabase

        binding.finishCountsText.text = getString(R.string.finish_counts, counts)

        binding.finishBtn.setOnClickListener {
            finish()
        }
        if (mode == "learning") {
            binding.moreBtn.setOnClickListener {
                val intent = Intent(this, LearningActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        else if (mode == "review") {
            val userWordCursor = db.query("UserWord", null, null, null, null, null, null)
            var i = 0
            if (userWordCursor.moveToFirst()) {

                do {
                    val lastTime = userWordCursor.getLong(userWordCursor.getColumnIndexOrThrow("lastTime"))
                    val times = userWordCursor.getInt(userWordCursor.getColumnIndexOrThrow("times"))
                    val today = (System.currentTimeMillis() / 86400000L) * 86400000L
                    if (today > lastTime + (times - 1) / 2 * today) { // (time-1)/2 可能还能改
                        i++
                    }
                } while (userWordCursor.moveToNext())
            }
            userWordCursor.close()
            if (i > 0) {
                binding.moreBtn.setOnClickListener {
                    val intent = Intent(this, ReviewActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            else {
                binding.moreBtn.visibility = View.GONE
            }

        }

    }
}