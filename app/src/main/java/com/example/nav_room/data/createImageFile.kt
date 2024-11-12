package com.example.nav_room.data

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir = context.getExternalFilesDir("images")
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}
