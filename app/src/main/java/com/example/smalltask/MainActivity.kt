package com.example.smalltask

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.smalltask.activities.Homepage
import com.example.smalltask.activities.Register
import com.example.smalltask.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "Ciallo!")
        val getPassword = getSharedPreferences("latest", MODE_PRIVATE)
//        val language = getPassword.getString("language", "zh") ?: "zh"
//        val region = getPassword.getString("region", "CN") ?: "CN"
//        updateLocate(language, region)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

//        val getPassword = getSharedPreferences("latest", MODE_PRIVATE)

        if (getPassword.getBoolean("isChecked", false)){
            binding.username.setText(getPassword.getString("username", ""))
            binding.password.setText(getPassword.getString("password", ""))
            binding.rememberPassword.isChecked = true
            if (getPassword.getBoolean("login", false)){
                val savePassword = getSharedPreferences("latest", MODE_PRIVATE).edit()
                savePassword.putBoolean("login", true)
                savePassword.apply()
                val intent = Intent(this, Homepage::class.java)
                startActivity(intent)
                Toast.makeText(this, getString(R.string.login), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        else{
            binding.username.setText(getPassword.getString("username", ""))
        }



        binding.register.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
        binding.login.setOnClickListener {
            val inputUsername = binding.username.text.toString()
            val inputPassword = binding.password.text.toString()

            if (inputUsername.isBlank()) binding.usernameIsEmpty.visibility = View.VISIBLE
            else binding.usernameIsEmpty.visibility = View.GONE
            if (inputPassword.isBlank()) binding.passwordIsEmpty.visibility = View.VISIBLE
            else binding.passwordIsEmpty.visibility = View.GONE

            if (inputUsername.isBlank() or inputPassword.isBlank()) {
                Toast.makeText(this, "用户名与密码不可为空", Toast.LENGTH_SHORT).show()
            }
            else {
                val prefs = getSharedPreferences("password", MODE_PRIVATE)
                val checker = prefs.getString(inputUsername, "")

                if (checker?.isBlank() == true) {
                    Toast.makeText(this, getString(R.string.user_not_exist_toast), Toast.LENGTH_SHORT).show()
                }
                else if (checker != inputPassword) {
                    Toast.makeText(this, getString(R.string.wrong_password), Toast.LENGTH_SHORT).show()
                }
                else {
                    binding.usernameIsEmpty.visibility = View.GONE
                    binding.passwordIsEmpty.visibility = View.GONE
                    Toast.makeText(this, getString(R.string.login), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Homepage::class.java)
                    if (binding.rememberPassword.isChecked){
                        val savePassword = getSharedPreferences("latest", MODE_PRIVATE).edit()
                        savePassword.putBoolean("isChecked", true)
                        savePassword.putString("username", inputUsername)
                        savePassword.putString("password", inputPassword)
                        savePassword.putBoolean("login", true)
                        savePassword.apply()
                    }
                    else{
                        val savePassword = getSharedPreferences("latest", MODE_PRIVATE).edit()
                        savePassword.putBoolean("isChecked", false)
                        savePassword.putString("username", inputUsername)
                        savePassword.putString("password", "")
                        savePassword.apply()
                    }

                    startActivity(intent)
                    finish()
                } // 密码正确
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        val username: EditText = findViewById(R.id.username)
        val password: EditText = findViewById(R.id.password)
        val inputUsername = username.text.toString()
        val inputPassword = password.text.toString()
        val rememberPassword : CheckBox = findViewById(R.id.remember_password)

        val getPassword = getSharedPreferences("latest", MODE_PRIVATE)
        val language = getPassword.getString("language", "zh")
        val region = getPassword.getString("region", "CN")
        if (rememberPassword.isChecked){
            val savePassword = getSharedPreferences("latest", MODE_PRIVATE).edit()
            savePassword.putBoolean("isChecked", true)
            savePassword.putString("username", inputUsername)
            savePassword.putString("password", inputPassword)
            savePassword.putString("language", language)
            savePassword.putString("region", region)
            savePassword.apply()
        }
        else{
            val savePassword = getSharedPreferences("latest", MODE_PRIVATE).edit()
            savePassword.putBoolean("isChecked", false)
            savePassword.putString("username", inputUsername)
            savePassword.putString("password", "")
            savePassword.putString("language", language)
            savePassword.putString("region", region)
            savePassword.apply()
        }
    }



//    fun Context.updateLocate(language: String, region: String): Context {
//        val locale = Locale(language, region)
//        Locale.setDefault(locale)
//
//        val configuration = Configuration(resources.configuration)
//        configuration.setLocale(locale)
//
//        return createConfigurationContext(configuration)
//    }

}

