package com.moksh.notes.feature_note.presentation.add_edit_note

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moksh.notes.feature_note.domain.model.InvalidNoteException
import com.moksh.notes.feature_note.domain.model.Note
import com.moksh.notes.feature_note.domain.user_case.NoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(private val noteUseCase: NoteUseCase, savedStateHandle: SavedStateHandle) :
    ViewModel() {
    var noteTextState by mutableStateOf(NoteTextFieldState(hint = "Enter the title"))
        private set

    var noteContentState by mutableStateOf(NoteTextFieldState(hint = "Enter some content"))
        private set

    var noteColorState by mutableIntStateOf(Note.noteColors.random().toArgb())
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentNoteId: Int? = null

    init {
        savedStateHandle.get<Int>("noteId")?.let { noteId ->
            if (noteId != -1) {
                viewModelScope.launch {
                    noteUseCase.getNote(noteId)?.also { note ->
                        currentNoteId = note.id
                        noteTextState = noteTextState.copy(text = note.title, isHintVisible = false)
                        noteContentState = noteContentState.copy(text = note.content, isHintVisible = false)
                        noteColorState = note.color
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditNoteEvent) {
        when (event) {
            is AddEditNoteEvent.EnteredTitle -> noteTextState = noteTextState.copy(text = event.value)
            is AddEditNoteEvent.ChangeTitleFocus -> noteTextState =
                noteTextState.copy(isHintVisible = !event.focusState.isFocused && noteTextState.text.isBlank())

            is AddEditNoteEvent.EnteredContent -> noteContentState = noteContentState.copy(text = event.value)
            is AddEditNoteEvent.ChangeContentFocus -> noteContentState =
                noteContentState.copy(isHintVisible = !event.focusState.isFocused && noteContentState.text.isBlank())

            is AddEditNoteEvent.ChangeColor -> noteColorState = event.newColor
            is AddEditNoteEvent.SaveNote -> viewModelScope.launch {
                try {
                    noteUseCase.addNote(
                        Note(
                            title = noteTextState.text,
                            content = noteContentState.text,
                            timeStamp = System.currentTimeMillis(),
                            color = noteColorState,
                            id = currentNoteId,
                        )
                    )
                    _eventFlow.emit(UiEvent.SaveNote)
                } catch (e: InvalidNoteException) {
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(
                            message = e.message ?: "Couldn't save note"
                        )
                    )
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveNote : UiEvent()
    }


}