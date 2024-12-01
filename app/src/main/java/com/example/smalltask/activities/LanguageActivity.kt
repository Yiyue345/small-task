package com.example.smalltask.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smalltask.BaseActivity
import com.example.smalltask.R
import com.example.smalltask.databinding.ActivityLanguageBinding
import com.example.smalltask.items.LanguageAdapter
import com.example.smalltask.items.UserLanguage

class LanguageActivity : BaseActivity() {

    private val userLanguageList = ArrayList<UserLanguage>()

    lateinit var binding: ActivityLanguageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        initUserLanguage()
        binding.languageList.layoutManager = LinearLayoutManager(this)
        binding.languageList.adapter = LanguageAdapter(this, userLanguageList)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
    

    private fun initUserLanguage() {
        userLanguageList.add(UserLanguage(getString(R.string.en_us), getString(R.string.en_us_show), "en", "US"))
        userLanguageList.add(UserLanguage(getString(R.string.zh), getString(R.string.zh_cn_show), "zh", ""))
        userLanguageList.add(UserLanguage(getString(R.string.zh_me), getString(R.string.zh_me_show), "zh", "ME"))
    }
}