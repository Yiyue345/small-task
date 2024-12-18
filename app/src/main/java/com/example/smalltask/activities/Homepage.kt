package com.example.smalltask.activities

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.smalltask.BaseActivity
import com.example.smalltask.HomepageViewModel
import com.example.smalltask.NotificationReceiver
import com.example.smalltask.R
import com.example.smalltask.ViewPagerAdapter
import com.example.smalltask.databinding.ActivityHomepageBinding
import com.example.smalltask.fragments.HomepageFragment
import com.example.smalltask.fragments.SettingsFragment
import com.example.smalltask.fragments.StatsFragment
import com.example.smalltask.learning.MyDatabaseHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.Calendar

class Homepage : BaseActivity() {

    lateinit var binding: ActivityHomepageBinding
    private lateinit var homepageViewModel: HomepageViewModel

    private var backPressTime = 0L
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Log.d("LocaleCheck", resources.configuration.locales.toString())

        initWordsDatabase(this) // words你在吗？
        initCiallo(this)

        homepageViewModel = ViewModelProvider(this)[HomepageViewModel::class.java]

        val getInfo = getSharedPreferences("latest", MODE_PRIVATE)
        val userName = getInfo.getString("username", "")!!
        homepageViewModel.username = userName
        val databaseFile = this.getDatabasePath("Database${userName}.db") // 居然还省了一步

        if (!databaseFile.exists()) {
            val dbHelper = MyDatabaseHelper(this, "Database${userName}.db", 1)
            val db = dbHelper.writableDatabase
            db.execSQL(dbHelper.createUserInfo)
            db.execSQL(dbHelper.createUserWord)
            db.execSQL(dbHelper.createLearningRecords)
            db.execSQL("INSERT INTO UserInfo (username) VALUES (?)", arrayOf(userName))
        }


        val navigation : BottomNavigationView = findViewById<BottomNavigationView>(R.id.navigationView)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { //Android13你怎么回事
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)

                }
            }
        }

        scheduleDailyNotification(this) // 这会带来一个bug，但可以治标不治本
        // 就这样吧，除非很闲不然不会优化了

        val adapter = ViewPagerAdapter(this)
        binding.switchFragment.adapter = adapter

        binding.switchFragment.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            // 绑定navigation
            override fun onPageSelected(position: Int) {
                navigation.menu.getItem(position).isChecked = true
            }
        })
//        binding.switchFragment.isUserInputEnabled = false // 禁用滑动切换
        binding.switchFragment.currentItem = 0

        navigation.selectedItemId = R.id.homepage
        navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.options ->{
                    binding.switchFragment.setCurrentItem(2, true)

                }
                R.id.homepage -> {
                    binding.switchFragment.setCurrentItem(0, true)

                }
                R.id.stats -> {
                    binding.switchFragment.setCurrentItem(1, true)

                }

            }
            true
        }
    }

    override fun onBackPressed() {
        if (backPressTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        }
        else {
            toast?.cancel()
            toast = Toast.makeText(this, getString(R.string.close_toast), Toast.LENGTH_SHORT)
            toast?.show()
        }
        backPressTime = System.currentTimeMillis()
    }

    private fun initWordsDatabase(context: Context) {
        val dbFile = context.getDatabasePath("words.db")
        if (!dbFile.exists()) {
            val inputStream: InputStream = context.assets.open("words.db") // 创建一个assets的输入流
            val outputStream: OutputStream = FileOutputStream(dbFile) // 再创建一个对应的输出流

            val buffer = ByteArray(1024) // 一次写入这么多
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length) // 一次写入
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()
        }

    }

    private fun initCiallo(context: Context){
        val cialloFile = File(context.cacheDir, "Ciallo_audio.mp3")
        if (!cialloFile.exists()) {
            val inputStream: InputStream = context.assets.open("ciallo.mp3")
            val outputStream: OutputStream = FileOutputStream(cialloFile)

            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length) // 一次写入
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()
        }
    }

    private fun scheduleDailyNotification(context: Context) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val today = System.currentTimeMillis() / 86400000L * 86400000L

        if (calendar.timeInMillis < System.currentTimeMillis() || homepageViewModel.getTodayTime(this, today) > 0) {
            // 如果过了十二点或者已经完成了，就不催命了
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, // 设备休眠亦能触发
            calendar.timeInMillis, // 设置第一次触发时间
            AlarmManager.INTERVAL_DAY, // 一天一次
            pendingIntent
        )
    }

}