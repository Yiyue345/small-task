package com.example.smalltask.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smalltask.R

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val back : Button = findViewById(R.id.back)
        val register : Button = findViewById(R.id.register)
        val cancelButton : Button = findViewById(R.id.cancel_button)
        val username : EditText = findViewById(R.id.username)
        val password : EditText = findViewById(R.id.password)
        val password1 : EditText = findViewById(R.id.password1)
        val usernameIsEmpty : TextView = findViewById(R.id.username_is_empty)
        val passwordIsEmpty : TextView = findViewById(R.id.password_is_empty)
        val wrongPassword : TextView = findViewById(R.id.wrong_password)

        back.setOnClickListener{
            finish()
        }
        cancelButton.setOnClickListener{
            finish()
        }

        register.setOnClickListener{
            val inputUsername = username.text.toString()
            val inputPassword = password.text.toString()
            val inputPassword1 = password1.text.toString()

            val prefs = getSharedPreferences("password", MODE_PRIVATE)
            val checker = prefs.getString(inputUsername, "")

            if (inputUsername.isBlank()) usernameIsEmpty.visibility = View.VISIBLE
            else usernameIsEmpty.visibility = View.GONE
            if (inputPassword.isBlank()) passwordIsEmpty.visibility = View.VISIBLE
            else passwordIsEmpty.visibility = View.GONE

            if (inputPassword != inputPassword1) wrongPassword.visibility = View.VISIBLE


            if (inputUsername.isBlank() or inputPassword.isBlank()){
//                Toast.makeText(this, "用户名与密码不可为空", Toast.LENGTH_SHORT).show()
            }
            else if (checker?.isBlank() == false){
                Toast.makeText(this, "用户名已被注册", Toast.LENGTH_SHORT).show()
            }

            else if (inputPassword == inputPassword1){
                usernameIsEmpty.visibility = View.GONE
                passwordIsEmpty.visibility = View.GONE
                wrongPassword.visibility = View.GONE

                val editor = getSharedPreferences("password", MODE_PRIVATE).edit()
                editor.putString(inputUsername, inputPassword)
                editor.apply()


                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}