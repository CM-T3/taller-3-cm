package com.example.taller3_sophiemejia_estebanblanco.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.compose.ui.res.colorResource
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
import com.example.taller3_sophiemejia_estebanblanco.R
import com.example.taller3_sophiemejia_estebanblanco.model.User
import com.example.taller3_sophiemejia_estebanblanco.navigation.AppScreens
import com.example.taller3_sophiemejia_estebanblanco.shared.MyButton
import com.example.taller3_sophiemejia_estebanblanco.shared.validEmailAddress
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import com.google.firebase.storage.FirebaseStorage

data class RegisterState(
    val name: String = "",
    val lastname: String = "",
    val id: String = "",
    val email: String = "",
    val password: String = "",
    val profilepic: String = "",
    val nameError: String = "",
    val lastnameError: String = "",
    val idError: String = "",
    val emailError: String = "",
    val passwordError: String = ""
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

    fun updateNameError(newValue: String) {
        _state.update { it.copy(nameError = newValue) }
    }

    fun updateLastnameError(newValue: String) {
        _state.update { it.copy(lastnameError = newValue) }
    }

    fun updateIdError(newValue: String) {
        _state.update { it.copy(idError = newValue) }
    }

    fun updateEmailError(newValue: String) {
        _state.update { it.copy(emailError = newValue) }
    }

    fun updatePasswordError(newValue: String) {
        _state.update { it.copy(passwordError = newValue) }
    }
}

@Composable
fun Register(controller: NavController, viewModel: RegisterViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    var uriImage by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

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
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Registro de Usuario", fontSize = 30.sp, fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(36.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.updateName(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombres") },
                    placeholder = { Text("Ingrese su nombre") },
                    supportingText = {
                        if (state.nameError.isNotEmpty()) {
                            Text(state.nameError, color = Color.Red)
                        }
                    })

                OutlinedTextField(
                    value = state.lastname,
                    onValueChange = { viewModel.updateLastname(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Apellidos") },
                    placeholder = { Text("Ingrese su apellido") },
                    supportingText = {
                        if (state.lastnameError.isNotEmpty()) {
                            Text(state.lastnameError, color = Color.Red)
                        }
                    })

                OutlinedTextField(
                    value = state.id,
                    onValueChange = { viewModel.updateId(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("No. de Identificación") },
                    placeholder = { Text("Ingrese su identificación") },
                    supportingText = {
                        if (state.idError.isNotEmpty()) {
                            Text(state.idError, color = Color.Red)
                        }
                    })

                OutlinedTextField(
                    value = state.email,
                    onValueChange = { viewModel.updateEmail(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email") },
                    placeholder = { Text("Ingrese su email") },
                    supportingText = {
                        if (state.emailError.isNotEmpty()) {
                            Text(state.emailError, color = Color.Red)
                        }
                    })

                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.updatePassword(it) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    label = { Text("Contraseña") },
                    placeholder = { Text("Ingrese su contraseña") },
                    supportingText = {
                        if (state.passwordError.isNotEmpty()) {
                            Text(state.passwordError, color = Color.Red)
                        }
                    })
            }

            Spacer(modifier = Modifier.height(36.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Foto de Perfil",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )


                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = if (uriImage != null) Color.Green else Color.Red,
                            shape = CircleShape
                        ), contentAlignment = Alignment.Center
                ) {
                    if (uriImage != null) {
                        AsyncImage(
                            model = uriImage,
                            contentDescription = "Foto seleccionada",
                            modifier = Modifier.fillMaxSize()
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

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { gallery.launch("image/*") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonColors(
                            contentColor = Color.White,
                            containerColor = colorResource(R.color.azulBonito),
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.Gray
                        )
                    ) {
                        Text("Galería")
                    }
                    Button(
                        onClick = { camera.launch(uriCamera) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonColors(
                            contentColor = Color.White,
                            containerColor = colorResource(R.color.azulBonito),
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.Gray
                        )
                    ) {
                        Text("Cámara")
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            MyButton(text = "Registrarse") {
                if (validateForm(viewModel, state, uriImage != null, context)) {
                    auth.createUserWithEmailAndPassword(state.email, state.password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser = auth.currentUser
                                firebaseUser?.let { user ->
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName("${state.name} ${state.lastname}").build()
                                    user.updateProfile(profileUpdates)
                                    val storageRef =
                                        FirebaseStorage.getInstance().reference.child("images/profile/${user.uid}/image.jpg")

                                    uriImage?.let { uri ->
                                        storageRef.putFile(uri)
                                            .addOnSuccessListener { taskSnapshot ->
                                                taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->

                                                    val nuevoUsuario = User(
                                                        name = state.name,
                                                        lastname = state.lastname,
                                                        id = state.id,
                                                        email = state.email,
                                                        password = state.password,
                                                        latitude = 0.0,
                                                        longitude = 0.0,
                                                        available = false,
                                                        profilepic = downloadUrl.toString()
                                                    )

                                                    database.getReference("users/${user.uid}")
                                                        .setValue(nuevoUsuario)
                                                        .addOnCompleteListener {
                                                            controller.navigate(AppScreens.home.name)
                                                        }
                                                }
                                            }.addOnFailureListener { exception ->
                                                Toast.makeText(
                                                    context,
                                                    "Error al subir imagen: ${exception.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Error Auth: ${task.exception?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.e("Register", "Error: ${task.exception?.message}")
                            }
                        }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

fun validateForm(
    model: RegisterViewModel, state: RegisterState, hasImage: Boolean, context: Context
): Boolean {
    if (state.name.isEmpty()) {
        model.updateNameError("Name is empty")
        return false
    } else {
        model.updateNameError("")
    }

    if (state.lastname.isEmpty()) {
        model.updateLastnameError("Lastname is empty")
        return false
    } else {
        model.updateLastnameError("")
    }

    if (state.id.isEmpty()) {
        model.updateIdError("ID is empty")
        return false
    } else {
        model.updateIdError("")
    }

    if (state.email.isEmpty()) {
        model.updateEmailError("Email is empty")
        return false
    } else {
        model.updateEmailError("")
    }

    if (!validEmailAddress(state.email)) {
        model.updateEmailError("Not a valid address")
        return false
    } else {
        model.updateEmailError("")
    }

    if (state.password.isEmpty()) {
        model.updatePasswordError("Password is empty")
        return false
    } else {
        model.updatePasswordError("")
    }

    if (state.password.length < 6) {
        model.updatePasswordError("Password is too short")
        return false
    } else {
        model.updatePasswordError("")
    }

    if (!hasImage) {
        Toast.makeText(context, "Profile picture is required", Toast.LENGTH_SHORT).show()
        return false
    }

    return true
}