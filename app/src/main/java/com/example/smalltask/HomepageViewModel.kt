package com.example.smalltask

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.smalltask.learning.MyDatabaseHelper

class HomepageViewModel : ViewModel() {
    var username = ""

    var reviewWords = 0

    fun getTodayCounts(context: Context, today: Long): Int {
        val dbHelper = MyDatabaseHelper(context, "Database${username}.db", 1)
        val db = dbHelper.readableDatabase

        val currentDate = (today / 86400000L) * 86400000L
        val todayCountsCursor = db.query("LearningRecords",
            null,
            "date = ?",
            arrayOf("$currentDate"),
            null,
            null, // 倒序查找
            null)
        var counts = 0
        if (todayCountsCursor.moveToFirst()) { // 今天学了多少词
            do {
                counts += todayCountsCursor.getInt(todayCountsCursor.getColumnIndexOrThrow("words"))
            } while (todayCountsCursor.moveToNext())
        }
        todayCountsCursor.close()
        return counts
    }

    fun getSevenDaysCounts(context: Context): MutableList<Int> {
        val sevenDaysCounts = MutableList(7) {0}
        val today = System.currentTimeMillis() / 86400000L * 86400000L
        for (i in 0 until 7) {
            sevenDaysCounts[6 - i] = getTodayCounts(context, today - 86400000L * i)
        }
        return sevenDaysCounts
    }

    fun getSevenDaysTime(context: Context): MutableList<Int> {
        val sevenDaysTime = MutableList(7) {0}
        val today = System.currentTimeMillis() / 86400000L * 86400000L
        for (i in 0 until 7) {
            sevenDaysTime[6 - i] = (getTodayTime(context, today - 86400000L * i) / 60000L).toInt()
        }
        return sevenDaysTime
    }

    fun getTodayTime(context: Context, today: Long): Long {
        val dbHelper = MyDatabaseHelper(context, "Database${username}.db", 1)
        val db = dbHelper.readableDatabase

        val currentDate = (today / 86400000L) * 86400000L
        val todayTimeCursor = db.query("LearningRecords",
            null,
            "date = ?",
            arrayOf("$currentDate"),
            null,
            null,
            null)
        var todayTime = 0L
        if (todayTimeCursor.moveToFirst()) {
            do {
                todayTime += todayTimeCursor.getLong(todayTimeCursor.getColumnIndexOrThrow("duration"))
            } while (todayTimeCursor.moveToNext())
        }

        todayTimeCursor.close()
        return todayTime
    }

    fun getAllWords(context: Context): Int {
        val dbHelper = MyDatabaseHelper(context, "Database${username}.db", 1)
        val db = dbHelper.readableDatabase

        val userCursor = db.query("UserInfo",
            null,
            "username = ?",
            arrayOf(username),
            null,
            null,
            null,)
        var allWords = 0
        if (userCursor.moveToFirst()){
            allWords = userCursor.getInt(userCursor.getColumnIndexOrThrow("learnWords"))
        }
        userCursor.close()
        return allWords
    }

    fun getAllTime(context: Context): Long {
        val dbHelper = MyDatabaseHelper(context, "Database${username}.db", 1)
        val db = dbHelper.readableDatabase

        val userCursor = db.query("UserInfo",
            null,
            "username = ?",
            arrayOf(username),
            null,
            null,
            null,)
        var allTime = 0L
        if (userCursor.moveToFirst()){
            allTime = userCursor.getLong(userCursor.getColumnIndexOrThrow("learnHours"))
        }
        userCursor.close()

        return allTime
    }

}