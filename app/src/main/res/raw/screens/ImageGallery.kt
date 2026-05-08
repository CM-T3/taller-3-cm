package com.example.taller2_sophiemejia_estebanblanco.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fitInside
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import java.io.File
import androidx.core.net.toUri


@Composable

fun ImageGallery() {
    val contexto = LocalContext.current
    var uriImagen by remember { mutableStateOf<Uri?>(null) }

    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uriImagen = uri
    }


    val uriCamera = FileProvider.getUriForFile(
        contexto,
        "${contexto.packageName}.fileprovider",
        File(contexto.filesDir, "foto_camara.jpg")
    )


    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        if (exito) {
            val nuevaUriUnica = "${uriCamera}?v=${System.currentTimeMillis()}".toUri()
            uriImagen = nuevaUriUnica
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(1F)
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (uriImagen != null) {
                AsyncImage(
                    model = uriImagen,
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier.size(500.dp),
                    contentScale= ContentScale.Fit
                )
            } else {
                Text("Cargue una imagen de la galería o la cámara")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { gallery.launch("image/*") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Galería")
            }

            Button(
                onClick = {
                    camera.launch(uriCamera)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cámara")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CameraPreview() {
    ImageGallery()
}
