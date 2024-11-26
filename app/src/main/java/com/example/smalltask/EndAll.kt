package com.example.smalltask

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EndAll : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_end_all)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val clean = intent.getBooleanExtra("clean", true)
        // 加几行东西
        if (clean) {
            val savePassword = getSharedPreferences("latest", MODE_PRIVATE).edit()
            savePassword.putBoolean("login", false)
            savePassword.apply()
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        ActivityCollector.finishAll()

    }
}