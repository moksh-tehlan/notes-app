package com.moksh.notes.feature_note.presentation.notes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moksh.notes.feature_note.domain.model.Note
import com.moksh.notes.feature_note.domain.user_case.NoteUseCase
import com.moksh.notes.feature_note.domain.utils.NoteOrder
import com.moksh.notes.feature_note.domain.utils.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    val noteUseCase: NoteUseCase
) : ViewModel() {
    var noteState by mutableStateOf(NotesState())
        private set

    private var recentlyDeletedNote: Note? = null;

    private var notesJob: Job? = null

    init {
        getNotes(NoteOrder.Date(OrderType.Descending))
    }

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.Order -> {
                if (noteState.noteOrder::class == event.noteOrder::class &&
                    noteState.noteOrder.orderType == event.noteOrder.orderType
                ) {
                    return
                }
                getNotes(event.noteOrder)
            }

            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    noteUseCase.deleteNote(event.note)
                    recentlyDeletedNote = event.note
                }
            }

            is NotesEvent.RestoreNote -> {
                viewModelScope.launch {
                    noteUseCase.addNote(recentlyDeletedNote ?: return@launch)
                    recentlyDeletedNote = null
                }
            }

            is NotesEvent.ToggleOrderSection -> {
                noteState = noteState.copy(isOrderSectionVisible = !noteState.isOrderSectionVisible)
            }
        }
    }

    private fun getNotes(noteOrder: NoteOrder) {
        notesJob?.cancel()
        notesJob = noteUseCase.getNotes(noteOrder).onEach { notes ->
            noteState = noteState.copy(
                noteList = notes,
                noteOrder = noteOrder
            )
        }.launchIn(viewModelScope)
    }
}
