package com.example.smalltask.notes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [Note::class])
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        private var instance: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext,
                NoteDatabase::class.java, "note_database")
                .build().apply {
                    instance = this
                }
        }
    }



}