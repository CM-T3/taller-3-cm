package com.example.taller3_sophiemejia_estebanblanco.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.taller3_sophiemejia_estebanblanco.model.User
import com.example.taller3_sophiemejia_estebanblanco.navigation.AppScreens
import com.example.taller3_sophiemejia_estebanblanco.shared.MyButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

data class RegisterState(
    val name: String = "",
    val lastname: String = "",
    val id: String = "",
    val email: String = "",
    val password: String = "",
    val profilepic: String = ""
)

class RegisterViewModel : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun updateName(newValue: String) {
        _state.update { it.copy(name = newValue) }
    }

    fun updateLastname(newValue: String) {
        _state.update { it.copy(lastname = newValue) }
    }

    fun updateId(newValue: String) {
        _state.update { it.copy(id = newValue) }
    }

    fun updateEmail(newValue: String) {
        _state.update { it.copy(email = newValue) }
    }

    fun updatePassword(newValue: String) {
        _state.update { it.copy(password = newValue) }
    }

    fun updateProfilePic(newValue: String) {
        _state.update { it.copy(profilepic = newValue) }
    }
}

@Composable
fun Register(controller: NavController, viewModel: RegisterViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    var uriImage by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val isFormValid = state.name.isNotBlank() &&
            state.lastname.isNotBlank() &&
            state.id.isNotBlank() &&
            state.email.isNotBlank() &&
            state.password.isNotBlank() &&
            uriImage != null

    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            uriImage = uri
            viewModel.updateProfilePic(uri.toString())
        }
    }

    val uriCamera = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            File(context.filesDir, "foto_camara.jpg")
        )
    }

    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        if (exito) {
            val nuevaUriUnica = "${uriCamera}?v=${System.currentTimeMillis()}".toUri()
            uriImage = nuevaUriUnica
            viewModel.updateProfilePic(nuevaUriUnica.toString())
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(Modifier.height(30.dp))
                Text("Registro de Usuario", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(20.dp))

                Text("Nombres", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.updateName(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Ingrese su nombre") })
                Spacer(Modifier.height(16.dp))

                Text("Apellidos", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = state.lastname,
                    onValueChange = { viewModel.updateLastname(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Ingrese su apellido") })
                Spacer(Modifier.height(16.dp))

                Text("No. de Identificación", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = state.id,
                    onValueChange = { viewModel.updateId(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Ingrese su identificación") })
                Spacer(Modifier.height(16.dp))

                Text("Email", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { viewModel.updateEmail(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Ingrese su email") })
                Spacer(Modifier.height(16.dp))

                Text("Contraseña", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.updatePassword(it) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    label = { Text("Ingrese su contraseña") })
                Spacer(Modifier.height(20.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Foto de Perfil (Obligatoria)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Debes tomarte o seleccionar una foto de perfil donde se vea claramente tu rostro para completar el registro.",
                        fontSize = 13.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .border(
                                width = 2.dp,
                                color = if (uriImage != null) Color.Green else Color.Red,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uriImage != null) {
                            AsyncImage(
                                model = uriImage,
                                contentDescription = "Foto seleccionada",
                                modifier = Modifier.fillMaxSize(),

                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Sin foto",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { gallery.launch("image/*") }) {
                            Text("Galería")
                        }
                        Button(onClick = { camera.launch(uriCamera) }) {
                            Text("Cámara")
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                MyButton(text = "Registrarse", enabled = isFormValid) {
                    auth.createUserWithEmailAndPassword(state.email, state.password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser = auth.currentUser
                                firebaseUser?.let { user ->
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName("${state.name} ${state.lastname}").build()
                                    user.updateProfile(profileUpdates)

                                    val nuevoUsuario = User(
                                        name = state.name,
                                        lastname = state.lastname,
                                        id = state.id,
                                        email = state.email,
                                        password = state.password,
                                        latitude = 0.0,
                                        longitude = 0.0,
                                        available = false,
                                        profilepic = state.profilepic
                                    )

                                    database.getReference("users/${user.uid}")
                                        .setValue(nuevoUsuario).addOnCompleteListener {
                                            controller.navigate(AppScreens.home.name)
                                        }
                                }

                            } else {
                                Log.e("Register", "Error: ${task.exception?.message}")
                            }
                        }
                }
                Spacer(Modifier.height(25.dp))      
            }
        }
    }
}