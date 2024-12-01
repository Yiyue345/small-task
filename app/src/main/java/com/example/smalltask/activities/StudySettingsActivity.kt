package com.example.smalltask.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smalltask.BaseActivity
import com.example.smalltask.R
import com.example.smalltask.databinding.ActivityStudySettingsBinding
import com.example.smalltask.learning.MyDatabaseHelper
import java.io.File

class StudySettingsActivity : BaseActivity() {

    private lateinit var binding: ActivityStudySettingsBinding

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val getLanguage = getSharedPreferences("latest", MODE_PRIVATE)
        val language = getLanguage.getString("language", "zh") ?: "zh"
        val region = getLanguage.getString("region", "CN") ?: "CN"
        updateLocate(language, region)
        enableEdgeToEdge()
        binding = ActivityStudySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        val file = File(this.filesDir, "background.png")
        val getInfo = getSharedPreferences("latest", MODE_PRIVATE)
        val userName = getInfo.getString("username", "")!!

        val dbHelper = MyDatabaseHelper(this, "Database${userName}.db", 1)
        val db = dbHelper.writableDatabase

        val cursor = db.query("UserInfo", null, null, null, null, null, null)
        var learnWordsEachTime = 10
        var reviewWordsEachTime = 0

        if (cursor.moveToFirst()) {
            reviewWordsEachTime = cursor.getInt(cursor.getColumnIndexOrThrow("reviewWordsEachTime"))
            learnWordsEachTime = cursor.getInt(cursor.getColumnIndexOrThrow("learnWordsEachTime"))
        }
        cursor.close()

        binding.editLearnEachTime.setText("$learnWordsEachTime")
        binding.editReviewEachTime.setText("$reviewWordsEachTime")

        pickImageLauncher = registerForActivityResult( // 代替某个过时的onActivityResult
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                if (imageUri != null) {
                    handleSelectedImage(imageUri)
                }
                check()
            }
        }

        check()

        binding.setBackgroundBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*" // 图片only
            pickImageLauncher.launch(intent)
            check()
        }

        binding.resetBackgroundBtn.setOnClickListener {
            file.delete()
            check()
        }

        binding.applyBtn.setOnClickListener {
            if (!binding.editLearnEachTime.text.toString().isBlank() && !binding.editLearnEachTime.text.toString().isBlank()) {
                learnWordsEachTime = binding.editLearnEachTime.text.toString().toInt()
                reviewWordsEachTime = binding.editReviewEachTime.text.toString().toInt()
                if (learnWordsEachTime <= 0 || reviewWordsEachTime <= 0) {
                    Toast.makeText(this, getString(R.string.illegal_number), Toast.LENGTH_SHORT).show()
                }
                else {
                    db.execSQL("UPDATE UserInfo SET learnWordsEachTime = ?, reviewWordsEachTime = ? WHERE username = ?", arrayOf(learnWordsEachTime, reviewWordsEachTime, userName))
                    Toast.makeText(this, getString(R.string.apply_successful), Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(this, getString(R.string.empty_number), Toast.LENGTH_SHORT).show()
            }

        }

    }


    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = this.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun handleSelectedImage(imageUri: Uri): File {
        val directory= this.filesDir
        val file = File(directory, "background.png")
        val bitmap = getBitmapFromUri(imageUri)
        if (bitmap != null) {
            file.outputStream().use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
        }
        return file
    }

    private fun check() {
        val file = File(this.filesDir, "background.png")
        if (file.exists()) {
            binding.resetBackgroundBtn.visibility = View.VISIBLE
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            binding.backgroundImage.setImageBitmap(bitmap)
        }
        else {
            binding.resetBackgroundBtn.visibility = View.GONE
            binding.backgroundImage.setImageBitmap(null)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}