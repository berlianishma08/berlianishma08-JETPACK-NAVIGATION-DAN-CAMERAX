package com.example.nav_room.ui.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.nav_room.data.Note
import com.example.nav_room.ui.NoteViewModel

@Composable
fun DetailScreen(navController: NavController, viewModel: NoteViewModel, noteId: Int?) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // Mengambil data catatan saat ini jika noteId tidak null
    LaunchedEffect(noteId) {
        noteId?.let {
            val note = viewModel.getNoteById(it)
            note?.let {
                title = it.title
                content = it.content
                photoUri = it.photoUri?.let { uriString -> Uri.parse(uriString) }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { navController.navigate("camera") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Photo")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tampilkan foto jika photoUri tidak null
        photoUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (noteId == null) {
                    viewModel.addNote(Note(0, title, content, photoUri?.toString()))
                } else {
                    viewModel.updateNote(Note(noteId, title, content, photoUri?.toString()))
                }
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (noteId == null) "Save Note" else "Update Note")
        }
    }

    LaunchedEffect(Unit) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("photoUri")
            ?.observeForever { uriString ->
                uriString?.let {
                    photoUri = Uri.parse(it) // Set photoUri ke foto terbaru
                    Log.d("DetailScreen", "Photo URI received: $photoUri") // Debug log
                }
            }
    }

}
