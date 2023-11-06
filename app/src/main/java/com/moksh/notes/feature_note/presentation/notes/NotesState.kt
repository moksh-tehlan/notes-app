package com.moksh.notes.feature_note.presentation.notes

import com.moksh.notes.feature_note.domain.model.Note
import com.moksh.notes.feature_note.domain.utils.NoteOrder
import com.moksh.notes.feature_note.domain.utils.OrderType

data class NotesState(
    val noteList: List<Note> = emptyList(),
    val noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending),
    val isOrderSectionVisible: Boolean = false,
)