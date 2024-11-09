package com.example.smalltask

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseHelper(val context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {

        var username = ""

    val saveCreateWords = """CREATE TABLE IF NOT EXISTS Word (
            |id INTEGER PRIMARY KEY AUTOINCREMENT,
            |word TEXT
            |)""".trimMargin()

    val createUserData = """CREATE TABLE IF NOT EXISTS UserWord (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        finish INTEGER, 
        times INTEGER
        )
    """.trimIndent()

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(saveCreateWords)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {

    }
}