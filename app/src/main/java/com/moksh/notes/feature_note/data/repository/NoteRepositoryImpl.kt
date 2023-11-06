package com.moksh.notes.feature_note.data.repository

import com.moksh.notes.feature_note.data.data_source.NoteDao
import com.moksh.notes.feature_note.domain.model.Note
import com.moksh.notes.feature_note.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class NoteRepositoryImpl(private val dao: NoteDao) : NoteRepository {


    override fun getNotes(): Flow<List<Note>> {
        return dao.getNoteList()
    }

    override suspend fun getNote(id: Int): Note? {
        return dao.getNoteById(id)
    }

    override suspend fun insertNote(note: Note) {
        dao.insertNote(note)
    }

    override suspend fun deleteNote(note: Note) {
        dao.deleteNote(note)
    }
}
