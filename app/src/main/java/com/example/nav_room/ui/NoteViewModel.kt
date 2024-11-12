package com.example.nav_room.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nav_room.data.Note
import com.example.nav_room.data.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {
    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri: StateFlow<Uri?> get() = _photoUri
    val allNotes: StateFlow<List<Note>> = repository.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())



    fun addNote(note: Note) {
        viewModelScope.launch {
            repository.insert(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }

    // Saat update note di ViewModel
    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.update(note)  // Update catatan dengan URI foto yang baru
            Log.d("NoteViewModel", "Note updated with photoUri: ${note.photoUri}")
        }
    }



    suspend fun getNoteById(id: Int): Note? {
        return repository.getNoteById(id)

    }

    fun setCapturedPhotoUri(uri: Uri?) {
        _photoUri.value = uri
    }

}