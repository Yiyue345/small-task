package com.example.smalltask

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Region
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.intellij.lang.annotations.Language
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


    fun Context.updateLocate(language: String, region: String): Context { // 就是重新设置了语言和地区然后重新那啥
        val locale = Locale(language, region)
        Locale.setDefault(locale)

        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)

        return createConfigurationContext(configuration)
    }
}