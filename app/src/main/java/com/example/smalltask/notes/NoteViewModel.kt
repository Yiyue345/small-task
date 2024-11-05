package com.example.smalltask.notes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {
    val notes = MutableLiveData<MutableList<Note>>(mutableListOf()) // 初始化为空列表
    val deletedNotes = MutableLiveData<MutableList<Note>>(mutableListOf()) // 初始化为空列表
    val note = MutableLiveData<Note?>(Note(title = "", content = "", editTime = 0))

    // 加载所有笔记
    fun fetchNotes() {
        viewModelScope.launch {
            notes.value = repository.getAllNotes() as MutableList<Note>?
        }
    }

    // 加载已删除的笔记
    fun fetchDeletedNotes() {
        viewModelScope.launch {
            deletedNotes.value = repository.getDeletedNotes() as MutableList<Note>?
        }
    }

    // 添加新笔记
    fun addNote(title: String, content: String) {
        val note = Note(title = title, content = content, editTime = 0)
        viewModelScope.launch {
            repository.insert(note)
            fetchNotes() // 重新加载笔记
        }
    }

    // 更新当前笔记内容
    fun updateNoteContent(content: String) {
        note.value?.let {
            it.content = content
            viewModelScope.launch {
                repository.update(it)
                fetchNotes() // 更新后重新加载
            }
        }
    }

    // 删除笔记
    fun deleteNote(noteId: Long) {
        viewModelScope.launch {
            repository.deleteNoteById(noteId)
            fetchNotes() // 重新加载笔记
        }
    }

    // 恢复已删除的笔记
    fun restoreNote(noteId: Long) {
        viewModelScope.launch {
            repository.restoreNote(noteId)
            fetchNotes()
            fetchDeletedNotes()
        }
    }

    // 撤销操作
    fun undo() {
        // 添加撤销逻辑
        fetchDeletedNotes()
    }

    // 重做操作
    fun redo() {
        // 添加重做逻辑
    }

    // 保存当前笔记内容
    fun saveNoteContent(content: String) {
        note.value?.let {
            it.content = content
            updateNoteContent(content)
        }
    }

    // 设置当前笔记
    fun setCurrentNote(note: Note?) {
        this.note.value = note
    }

    fun addDeletedNote(note: Note) {
        val currentDeletedNotes = deletedNotes.value?.toMutableList() ?: mutableListOf()

        // 添加新的笔记到 deletedNotes 列表
        currentDeletedNotes.add(note)

        // 更新 deletedNotes 的 LiveData 值
        deletedNotes.value = currentDeletedNotes
    }

}
