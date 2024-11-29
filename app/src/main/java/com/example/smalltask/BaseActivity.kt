package com.example.smalltask

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

open class BaseActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("BaseActivity", javaClass.simpleName)
        ActivityCollector.addActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }

    override fun attachBaseContext(base: Context) {
        val getLanguage = base.getSharedPreferences("latest", MODE_PRIVATE)
        val language = getLanguage.getString("language", "zh") ?: "zh"
        val region = getLanguage.getString("region", "CN") ?: "CN"
        super.attachBaseContext(base.updateLocate(language, region))
    }


    fun Context.updateLocate(language: String, region: String): Context { // 就是重新设置了语言和地区然后重新那啥
        val locale = Locale(language, region)
        Locale.setDefault(locale)

        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)

        return createConfigurationContext(configuration)
    }
}