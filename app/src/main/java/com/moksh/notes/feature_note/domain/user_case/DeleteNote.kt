package com.moksh.notes.feature_note.domain.user_case

import com.moksh.notes.feature_note.domain.model.Note
import com.moksh.notes.feature_note.domain.repository.NoteRepository

class DeleteNote(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        repository.deleteNote(note)
    }
}
