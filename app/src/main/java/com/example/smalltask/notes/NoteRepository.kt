package com.example.smalltask.notes

class NoteRepository(private val noteDao: NoteDao) {
    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun update(note: Note) {
        noteDao.update(note)
    }

    suspend fun getAllNotes(): List<Note> {
        return noteDao.getAllNotes()
    }

    suspend fun getDeletedNotes(): List<Note> {
        return noteDao.getDeletedNotes()
    }

    suspend fun deleteNote(noteId: Long) {
        noteDao.deleteNote(noteId)
    }

    suspend fun restoreNote(noteId: Long) {
        noteDao.restoreNote(noteId)
    }
    suspend fun getNoteById(noteId: Long): Note? {
        return noteDao.getNoteById(noteId)
    }

    suspend fun deleteAllNotes() {
        return noteDao.deleteAllNotes()
    }

    suspend fun deleteNoteById(noteId: Long) {
        noteDao.deleteNoteById(noteId)
    }

}
