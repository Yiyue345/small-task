package com.example.smalltask.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smalltask.BaseActivity
import com.example.smalltask.R
import com.example.smalltask.databinding.ActivityRegisterBinding

class Register : BaseActivity() {

    lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        binding.back.setOnClickListener{
            finish()
        }
        binding.cancelButton.setOnClickListener{
            finish()
        }

        binding.register.setOnClickListener{
            val inputUsername = binding.username.text.toString()
            val inputPassword = binding.password.text.toString()
            val inputPassword1 = binding.password1.text.toString()

            val prefs = getSharedPreferences("password", MODE_PRIVATE)
            val checker = prefs.getString(inputUsername, "")

            if (inputUsername.isBlank()) binding.usernameIsEmpty.visibility = View.VISIBLE
            else binding.usernameIsEmpty.visibility = View.GONE
            if (inputPassword.isBlank()) binding.passwordIsEmpty.visibility = View.VISIBLE
            else binding.passwordIsEmpty.visibility = View.GONE

            if (inputPassword != inputPassword1) binding.wrongPassword.visibility = View.VISIBLE


            if (inputUsername.isBlank() or inputPassword.isBlank()){
//                Toast.makeText(this, "用户名与密码不可为空", Toast.LENGTH_SHORT).show()
            }
            else if (checker?.isBlank() == false){
                Toast.makeText(this, "用户名已被注册", Toast.LENGTH_SHORT).show()
            }

            else if (inputPassword == inputPassword1){
                binding.usernameIsEmpty.visibility = View.GONE
                binding.passwordIsEmpty.visibility = View.GONE
                binding.wrongPassword.visibility = View.GONE

                val editor = getSharedPreferences("password", MODE_PRIVATE).edit()
                editor.putString(inputUsername, inputPassword)
                editor.apply()


                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}