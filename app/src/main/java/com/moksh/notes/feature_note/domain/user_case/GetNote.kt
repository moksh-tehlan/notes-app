package com.moksh.notes.feature_note.domain.user_case

import com.moksh.notes.feature_note.domain.model.Note
import com.moksh.notes.feature_note.domain.repository.NoteRepository

class GetNote(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id:Int): Note?{
        return repository.getNote(id)
    }
}