package com.example.taller3_sophiemejia_estebanblanco.screens

import android.util.Log

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.navigation.NavController
import com.example.taller3_sophiemejia_estebanblanco.shared.MyButton
import com.example.taller3_sophiemejia_estebanblanco.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.taller3_sophiemejia_estebanblanco.navigation.AppScreens
import com.google.firebase.database.FirebaseDatabase




@Composable
fun Register(controller: NavController) {
    var name by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = "Nombres", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Ingrese su nombre") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Apellidos", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = lastname,
                    onValueChange = { lastname = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Ingrese su apellido") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "No. de Identificación", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = id,
                    onValueChange = { id = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Ingrese su número de identificación") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Email", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Ingrese su email") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Contraseña", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Ingrese su contraseña") }
                )
                Spacer(modifier = Modifier.height(32.dp))

                MyButton(text = "Registrarse") {
                    val database = FirebaseDatabase.getInstance()

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userAuth = auth.currentUser

                                userAuth?.let { firebaseUser ->
                                    val upcrb = UserProfileChangeRequest.Builder()
                                    upcrb.setDisplayName("$name $lastname")
                                    firebaseUser.updateProfile(upcrb.build())

                                    val nuevoUsuario = User(
                                        name = name,
                                        lastname = lastname,
                                        id = id,
                                        email = email,
                                        latitude = latitude.toDoubleOrNull() ?: 0.0,
                                        longitude = longitude.toDoubleOrNull() ?: 0.0
                                    )

                                    val myRef = database.getReference("users/" + firebaseUser.uid)
                                    myRef.setValue(nuevoUsuario)
                                    controller.navigate(AppScreens.home.name)
                                }
                            } else {
                                Log.e("MYTAG", "Error: " + task.exception?.message)
                            }
                        }
                }
            }
        }
    }
}