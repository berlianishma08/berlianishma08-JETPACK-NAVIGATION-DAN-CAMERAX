import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.nav_room.data.createImageFile
import com.example.nav_room.ui.NoteViewModel
import java.io.File

@Composable
fun CameraScreen(navController: NavController, viewModel: NoteViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    // Membuat tampilan preview kamera
    val previewView = remember { PreviewView(context) }

    // Jalankan efek saat composable diload
    LaunchedEffect(cameraProviderFuture) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        // Buat imageCapture untuk mengambil foto
        imageCapture = ImageCapture.Builder().build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            // Lepas semua binding dan bind ke lifecycle
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (exc: Exception) {
            Log.e("CameraScreen", "Use case binding failed", exc)
        }
    }

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        Button(
            onClick = {
                val photoFile = createImageFile(context)
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                imageCapture?.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val savedUri = Uri.fromFile(photoFile)
                            // Menyimpan URI foto yang baru di viewModel
                            viewModel.setCapturedPhotoUri(savedUri)  // Setel URI foto baru di ViewModel
                            navController.previousBackStackEntry?.savedStateHandle?.set("photoUri", savedUri.toString()) // Kirim URI ke DetailScreen
                            navController.popBackStack()
                        }

                        override fun onError(exc: ImageCaptureException) {
                            Log.e("CameraScreen", "Photo capture failed: ${exc.message}", exc)
                        }
                    }
                )


            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text("Capture")
        }
    }
}