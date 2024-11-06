package com.example.smalltask

import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smalltask.databinding.ActivityEditingBinding
import com.example.smalltask.notes.NoteDatabaseHelper

class EditingActivity : BaseActivity() {

    lateinit var binding: ActivityEditingBinding

    private val dbHelper = NoteDatabaseHelper(this, "Notes.db", 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val db = dbHelper.writableDatabase
        val value = ContentValues().apply {
            put("title", binding.titleBox.text.toString())
            put("content", binding.editBox.text.toString())
            put("editTime", System.currentTimeMillis())
        }
        db.insert("Note", null, value)

        // 返回结果给调用者
        setResult(RESULT_OK)
        finish()
    }
}