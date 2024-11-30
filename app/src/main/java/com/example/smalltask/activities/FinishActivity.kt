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
            binding.moreBtn.visibility = View.GONE
        }

    }
}