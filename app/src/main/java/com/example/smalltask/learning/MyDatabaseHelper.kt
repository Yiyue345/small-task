package com.example.smalltask.learning

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseHelper(val context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {


    val saveCreateWords = """CREATE TABLE IF NOT EXISTS Word (
            |id INTEGER PRIMARY KEY AUTOINCREMENT,
            |word TEXT,
            |accent TEXT,
            |meanCn TEXT,
            |meanEn TEXT,
            |sentence TEXT,
            |sentenceTrans TEXT
            |)""".trimMargin()

    val createUserWord = """CREATE TABLE IF NOT EXISTS UserWord (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        word TEXT, 
        lastTime INTEGER,
        times INTEGER INTEGER NOT NULL DEFAULT 1
        )
    """.trimIndent() // 每个单词是否背过，上次什么时候背的，背了几次

    val createUserInfo = """CREATE TABLE IF NOT EXISTS UserInfo (
        |username TEXT,
        |learnWordsEachTime INTEGER NOT NULL DEFAULT 10,
        |reviewWordsEachTime INTEGER NOT NULL DEFAULT 20,
        |learnWords INTEGER NOT NULL DEFAULT 0,
        |learnHours INTEGER NOT NULL DEFAULT 0
        |)""".trimMargin()

    val createLearningRecords = """CREATE TABLE IF NOT EXISTS LearningRecords (
        |type TEXT,
        |date INTEGER,
        |duration INTEGER,
        |words INTEGER
        |)
    """.trimMargin()



    override fun onCreate(db: SQLiteDatabase) {

    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {

    }
}