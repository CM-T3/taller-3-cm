package com.example.taller3_sophiemejia_estebanblanco.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
}

@Composable
fun Register(controller: NavController, viewModel: RegisterViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()

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
                    label = { Text("Ingrese su contraseña") })
                Spacer(Modifier.height(32.dp))

                MyButton(text = "Registrarse") {
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
                                        available = true,
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
            }
        }
    }
}