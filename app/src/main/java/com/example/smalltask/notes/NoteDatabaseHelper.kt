package com.example.smalltask.notes

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NoteDatabaseHelper(val context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version){

        private val createNote =
            """create table Note (
                    id integer primary key autoincrement,
                    title text,
                    content text,
                    editTime)"""

        private val createRecord =
            """create table Record (
                    id integer primary key autoincrement,
                    realId integer,
                    title text,
                    content text,
                    isDeleted integer)"""

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createNote)
        db.execSQL(createRecord)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("drop table if exists Record")
        onCreate(db)
    }
}