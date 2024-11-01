package com.example.smalltask

import android.os.Bundle
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.smalltask.databinding.ActivityHomepageBinding
import com.example.smalltask.fragment.HomepageFragment
import com.example.smalltask.fragment.NothingFragment
import com.example.smalltask.fragment.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainLayout, fragment)
            .commit()
    }



}