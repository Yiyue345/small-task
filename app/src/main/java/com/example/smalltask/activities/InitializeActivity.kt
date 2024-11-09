package com.example.smalltask.activities

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.RequestBuilder
import com.example.smalltask.BaseActivity
import com.example.smalltask.MyDatabaseHelper
import com.example.smalltask.R
import com.example.smalltask.classes.AllWords
import com.example.smalltask.databinding.ActivityInitializeBinding
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request

class InitializeActivity : BaseActivity() {

    lateinit var binding: ActivityInitializeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityInitializeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dbHelper = MyDatabaseHelper(this, "words.db", 1)
        val db = dbHelper.writableDatabase

//        db.execSQL(dbHelper.createWords)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("http://10.0.2.2/words.json").build()

                val response = client.newCall(request).execute()
                val responseData = response.body?.string()
                if (responseData != null) {
                    val moshi = Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                    val jsonAdapter = moshi.adapter(AllWords::class.java)
                    val allWords =  jsonAdapter.fromJson(responseData)

//                    val type = Types.newParameterizedType(List::class.java, String::class.java)
//                    val jsonAdapter2 = moshi.adapter<List<String>>(type)
//                    val words: List<String>? = allWords?.let { jsonAdapter2.fromJson(it.list) }


                    allWords?.let {
                        for (word in it.list) {
                            db.execSQL("insert into Word (word) values(?)", arrayOf(word))
                            Log.d("test", word)
                        }
                    }


                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }


}