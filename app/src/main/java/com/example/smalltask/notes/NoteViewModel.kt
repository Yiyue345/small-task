package com.example.smalltask.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val noteDao: NoteDao = NoteDatabase.getDatabase(application).noteDao()
    private val notes = MutableLiveData<List<Note>>()

    private val undoStack = mutableListOf<Note>()
    private val redoStack = mutableListOf<Note>()

}