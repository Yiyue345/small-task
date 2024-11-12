package com.example.smalltask.activities

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smalltask.BaseActivity
import com.example.smalltask.R
import com.example.smalltask.classes.AllWords
import com.example.smalltask.databinding.ActivityInitializeBinding
import com.example.smalltask.learning.MyDatabaseHelper
import com.example.smalltask.learning.Word
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class InitializeActivity : BaseActivity() { // 生成words数据库的

    private var i = 0

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

        binding.init.setOnClickListener {
            init()
        }


    }

    suspend fun makeRequestWithRetry(
        client: OkHttpClient,
        url: String,
        maxRetries: Int = 5,
        delayMills: Long = 1000L
    ): Response? {
        repeat(maxRetries) { attempt ->
            try {
                val request = Request.Builder().url(url).build()
                return client.newCall(request).execute().also {
                    if (it.isSuccessful) {
                        Log.d("RequestSuccessful", it.body.toString())
                        return it
                    }
                }
            } catch (e: Exception) {
                Log.w("RequestFailed", "${attempt + 1}: ${e.message}")
                delay(delayMills)
            }
        }
        return null
    }

    private fun parseWordWithMoshi(jsonData: String): Word? {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter = moshi.adapter(Word::class.java)
        val word = jsonAdapter.fromJson(jsonData)
        return word
    }

    fun replaceSpecialChars(input: String): String {
        // 使用正则表达式替换空格和指定的特殊符号为下划线
        return input.replace(Regex("[ /\\\\:*?\"<>|]+"), "_")
    }

    fun init() {
        val dbHelper = MyDatabaseHelper(this, "words.db", 1)
        val db = dbHelper.writableDatabase

        db.execSQL(dbHelper.saveCreateWords)
        val total = 10928

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
                    val allWords = jsonAdapter.fromJson(responseData)

                    allWords?.let {
                        Log.d("cia?", it.list[0])
                        while (i <= total) {
                            if (allWords.list[i] == "ciallo"){
                                allWords.let { Log.d("cia?", it.list[0]) }
                            }
                            val word = allWords.list[i]
                            val okWord = replaceSpecialChars(word)

                            val wordUrl = "http://10.0.2.2/words/${okWord}.json"

                            val response = makeRequestWithRetry(client, wordUrl)
                            val responseData = response?.body?.string()
                            var word1: Word? = null
                            if (responseData != null) {
                                word1 = parseWordWithMoshi(responseData)

                                val values = ContentValues().apply {
                                    put("id", i)
                                    word1?.let { it1 -> put("word", it1.word) }
                                    word1?.let { it1 -> put("accent", it1.accent) }
                                    word1?.let { it1 -> put("meanCn", it1.meanCn) }
                                    word1?.let { it1 -> put("meanEn", it1.meanEn) }
                                    word1?.let { it1 -> put("sentence", it1.sentence) }
                                    word1?.let { it1 -> put("sentenceTrans", it1.sentenceTrans) }
                                }
                                db.insertWithOnConflict("word", null, values, SQLiteDatabase.CONFLICT_IGNORE) // 忽略相同id
                            }
                            Log.d("test", okWord)


                            i++
                        }
                    }


                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.d("OK", "finish")
        }
    }
}