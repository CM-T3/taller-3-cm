package com.example.taller3_sophiemejia_estebanblanco.screens

import android.widget.Toast
import android.widget.Toast.makeText
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.example.taller3_sophiemejia_estebanblanco.R

import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.taller3_sophiemejia_estebanblanco.auth
import com.example.taller3_sophiemejia_estebanblanco.model.User
import com.example.taller3_sophiemejia_estebanblanco.navigation.AppScreens
import com.example.taller3_sophiemejia_estebanblanco.shared.MyBottomBar
import com.example.taller3_sophiemejia_estebanblanco.shared.MyButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun AvailableUsers(navController: NavController) {
    var userList by remember { mutableStateOf(emptyList<Pair<String, User>>()) }
    var isMeAvailable by remember { mutableStateOf(true) }

    val actUserId = FirebaseAuth.getInstance().currentUser?.uid
    val database = FirebaseDatabase.getInstance().getReference("users")

    LaunchedEffect(actUserId) {
        actUserId?.let { uid ->
            database.child(uid).child("available").get().addOnSuccessListener { snapshot ->
                isMeAvailable = snapshot.getValue(Boolean::class.java) ?: true
            }
        }
    }

    DisposableEffect(Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<Pair<String, User>>()
                for (child in snapshot.children) {
                    val userId = child.key ?: continue
                    val user = child.getValue(User::class.java)

                    if (user != null && userId != actUserId && user.available) {
                        users.add(Pair(userId, user))
                    }
                }
                userList = users
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        database.addValueEventListener(listener)
        onDispose { database.removeEventListener(listener) }
    }

    Scaffold(
        bottomBar = {
            MyBottomBar(navController = navController, indexActual = 1)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Usuarios Disponibles",
                    style = MaterialTheme.typography.titleLarge
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isMeAvailable) "Disponible" else "Oculto", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.width(8.dp))
                    Switch(
                        checked = isMeAvailable,
                        onCheckedChange = { checked ->
                            isMeAvailable = checked
                            actUserId?.let { uid ->
                                database.child(uid).child("available").setValue(checked)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = colorResource(R.color.azulBonito),
                            uncheckedThumbColor = Color.DarkGray,
                            uncheckedTrackColor = Color.LightGray,
                            checkedBorderColor = Color.Transparent,
                            uncheckedBorderColor = Color.Gray
                        )
                    )
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(userList) { (userId, user) ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(20.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Perfil",
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(text = "${user.name} ${user.lastname}")
                            }

                            MyButton("Ver") {
                                navController.navigate("${AppScreens.availableMap.name}/$userId")
                            }
                        }
                    }
                }
            }
        }
    }
}