package com.example.smalltask

import android.content.Context
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

class ResetPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reset_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val back : Button = findViewById(R.id.back2)
        val yesButton : Button = findViewById(R.id.yesButton)
        val noButton : Button = findViewById(R.id.cancelButton2)
        val oldPassword : EditText = findViewById(R.id.oldPassword)
        val newPassword : EditText = findViewById(R.id.newPassword)

        val oldPasswordEmpty : TextView = findViewById(R.id.oldPasswordEmpty)
        val newPasswordEmpty : TextView = findViewById(R.id.newPasswordEmpty)
        val samePassword : TextView = findViewById(R.id.samePassword)
        val wrongPassword : TextView = findViewById(R.id.wrongPassword)

        val getUsername = getSharedPreferences("latest", Context.MODE_PRIVATE)
        val username = getUsername.getString("username", "")

        back.setOnClickListener{
            finish()
        }
        noButton.setOnClickListener {
            finish()
        }


        yesButton.setOnClickListener {

            wrongPassword.visibility = View.GONE
            samePassword.visibility = View.GONE
            val inputOldPassword = oldPassword.text.toString()
            val inputNewPassword = newPassword.text.toString()

            if (inputOldPassword.isBlank()) oldPasswordEmpty.visibility = View.VISIBLE
            else oldPasswordEmpty.visibility = View.GONE
            if (inputNewPassword.isBlank()) newPasswordEmpty.visibility = View.VISIBLE
            else newPasswordEmpty.visibility = View.GONE



            if (inputOldPassword.isBlank() or inputNewPassword.isBlank()){
                Toast.makeText(this, "两个框都要有东西！", Toast.LENGTH_SHORT).show()
            }
            else if(inputOldPassword == inputNewPassword){
                Toast.makeText(this, "新旧密码相同……\n你在干什么？", Toast.LENGTH_SHORT).show()
                samePassword.visibility = View.VISIBLE
            }
            else{
                samePassword.visibility = View.GONE
                val editor = getSharedPreferences("password", Context.MODE_PRIVATE).edit()
                val prefs = getSharedPreferences("password", Context.MODE_PRIVATE)
                val password = prefs.getString(username, "")
                if (inputOldPassword != password){
                    wrongPassword.visibility = View.VISIBLE
                    Toast.makeText(this, "旧密码错误",  Toast.LENGTH_SHORT).show()
                }
                else{
                    wrongPassword.visibility = View.GONE
                    val editorL = getSharedPreferences("latest", Context.MODE_PRIVATE).edit()
                    editorL.putString("password", inputNewPassword)
                    editor.putString(username, inputNewPassword)
                    editor.apply()
                    editorL.apply()
                    Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show()
                    finish()
                }


            }
        }




    }
}