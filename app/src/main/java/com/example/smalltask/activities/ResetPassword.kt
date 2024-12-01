package com.example.smalltask.activities

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smalltask.BaseActivity
import com.example.smalltask.R
import com.example.smalltask.databinding.ActivityResetPasswordBinding

class ResetPassword : BaseActivity() {

    lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        val getUsername = getSharedPreferences("latest", MODE_PRIVATE)
        val username = getUsername.getString("username", "")


        binding.cancelButton2.setOnClickListener {
            finish()
        }


        binding.yesButton.setOnClickListener {

            binding.wrongPassword.visibility = View.GONE
            binding.samePassword.visibility = View.GONE
            val inputOldPassword = binding.oldPassword.text.toString()
            val inputNewPassword = binding.newPassword.text.toString()

            if (inputOldPassword.isBlank()) binding.oldPasswordEmpty.visibility = View.VISIBLE
            else binding.oldPasswordEmpty.visibility = View.GONE
            if (inputNewPassword.isBlank()) binding.newPasswordEmpty.visibility = View.VISIBLE
            else binding.newPasswordEmpty.visibility = View.GONE



            if (inputOldPassword.isBlank() or inputNewPassword.isBlank()){
                Toast.makeText(this, getString(R.string.cannot_empty), Toast.LENGTH_SHORT).show()
            }
            else if(inputOldPassword == inputNewPassword){
                Toast.makeText(this, getString(R.string.same_password_toast), Toast.LENGTH_SHORT).show()
                binding.samePassword.visibility = View.VISIBLE
            }
            else{
                binding.samePassword.visibility = View.GONE
                val editor = getSharedPreferences("password", MODE_PRIVATE).edit()
                val prefs = getSharedPreferences("password", MODE_PRIVATE)
                val password = prefs.getString(username, "")
                if (inputOldPassword != password){
                    binding.wrongPassword.visibility = View.VISIBLE
                    Toast.makeText(this, getString(R.string.wrong_pass_word_toast),  Toast.LENGTH_SHORT).show()
                }
                else{
                    binding.wrongPassword.visibility = View.GONE
                    val editorL = getSharedPreferences("latest", MODE_PRIVATE).edit()
                    editorL.putString("password", inputNewPassword)
                    editor.putString(username, inputNewPassword)
                    editor.apply()
                    editorL.apply()
                    Toast.makeText(this, getString(R.string.modified_successfully), Toast.LENGTH_SHORT).show()
                    finish()
                }


            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }


}