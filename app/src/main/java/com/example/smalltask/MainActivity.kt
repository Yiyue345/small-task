package com.example.smalltask

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "Ciallo!")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val login: Button = findViewById(R.id.login)
        val register: Button = findViewById(R.id.register)
        val username: EditText = findViewById(R.id.username)
        val password: EditText = findViewById(R.id.password)
        val usernameIsEmpty: TextView = findViewById(R.id.username_is_empty)
        val passwordIsEmpty: TextView = findViewById(R.id.password_is_empty)
        val rememberPassword : CheckBox = findViewById(R.id.remember_password)

        val getPassword = getSharedPreferences("latest", Context.MODE_PRIVATE)
        if (getPassword.getBoolean("isChecked", false)){
            username.setText(getPassword.getString("username", ""))
            password.setText(getPassword.getString("password", ""))
            rememberPassword.isChecked = true
        }
        else{
            username.setText(getPassword.getString("username", ""))
        }

        register.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
        login.setOnClickListener {
            val inputUsername = username.text.toString()
            val inputPassword = password.text.toString()

            if (inputUsername.isBlank()) usernameIsEmpty.visibility = View.VISIBLE
            else usernameIsEmpty.visibility = View.GONE
            if (inputPassword.isBlank()) passwordIsEmpty.visibility = View.VISIBLE
            else passwordIsEmpty.visibility = View.GONE

            if (inputUsername.isBlank() or inputPassword.isBlank()) {
                Toast.makeText(this, "用户名与密码不可为空", Toast.LENGTH_SHORT).show()
            }
            else {
                val prefs = getSharedPreferences("password", Context.MODE_PRIVATE)
                val checker = prefs.getString(inputUsername, "")

                if (checker?.isBlank() == true) {
                    Toast.makeText(this, "用户不存在", Toast.LENGTH_SHORT).show()
                }
                else if (checker != inputPassword) {
                    Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show()
                }
                else {
                    usernameIsEmpty.visibility = View.GONE
                    passwordIsEmpty.visibility = View.GONE
                    Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Homepage::class.java)
                    if (rememberPassword.isChecked){
                        val savePassword = getSharedPreferences("latest", Context.MODE_PRIVATE).edit()
                        savePassword.putBoolean("isChecked", true)
                        savePassword.putString("username", inputUsername)
                        savePassword.putString("password", inputPassword)
                        savePassword.apply()
                    }
                    else{
                        val savePassword = getSharedPreferences("latest", Context.MODE_PRIVATE).edit()
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

        if (rememberPassword.isChecked){
            val savePassword = getSharedPreferences("latest", Context.MODE_PRIVATE).edit()
            savePassword.putBoolean("isChecked", true)
            savePassword.putString("username", inputUsername)
            savePassword.putString("password", inputPassword)
            savePassword.apply()
        }
        else{
            val savePassword = getSharedPreferences("latest", Context.MODE_PRIVATE).edit()
            savePassword.putBoolean("isChecked", false)
            savePassword.putString("username", inputUsername)
            savePassword.putString("password", "")
            savePassword.apply()
        }
    }

}

