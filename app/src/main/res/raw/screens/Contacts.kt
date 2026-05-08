package com.example.taller2_sophiemejia_estebanblanco.screens

import android.Manifest
import android.content.ContentResolver
import android.provider.ContactsContract
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taller2_sophiemejia_estebanblanco.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

data class Contacto(val id: String, val nombre: String, val telefono: String)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Contacts() {
    val contexto = LocalContext.current
    val contentResolver: ContentResolver = contexto.contentResolver
    val estadoPermisoContactos = rememberPermissionState(Manifest.permission.READ_CONTACTS)

    if (estadoPermisoContactos.status.isGranted) {
        val contactos = cargarContactos(contentResolver)
        LazyColumn {
            items(contactos) { contacto ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier.size(48.dp),
                            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.teal_700))
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Icono de contacto",
                                    tint = Color.White
                                )
                            }
                        }

                        Text(
                            text = contacto.id,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Text(
                            text = contacto.nombre,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Se requiere permiso para leer contactos")
            Button(onClick = { estadoPermisoContactos.launchPermissionRequest() }) {
                Text("Solicitar Permiso")
            }
        }
    }
}

fun cargarContactos(contentResolver: ContentResolver): List<Contacto> {
    val contactos = mutableListOf<Contacto>()
    val projection = arrayOf(
        ContactsContract.CommonDataKinds.Phone._ID,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )
    val cursor = contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        projection, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
    )

    if (cursor != null) {
        val idColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)
        val nameColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val numberColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

        var contactIdCounter = 1
        while (cursor.moveToNext()) {
            val idVisual = contactIdCounter.toString()
            val nombre = cursor.getString(nameColumn)
            val numero = cursor.getString(numberColumn)
            contactos.add(Contacto(idVisual, nombre, numero))
            contactIdCounter++
        }
        cursor.close()
    }
    return contactos
}