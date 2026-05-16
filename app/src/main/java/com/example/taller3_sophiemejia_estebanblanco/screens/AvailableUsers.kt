package com.example.taller3_sophiemejia_estebanblanco.screens

import android.Manifest
import android.os.Build
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.taller3_sophiemejia_estebanblanco.auth
import com.example.taller3_sophiemejia_estebanblanco.model.User
import com.example.taller3_sophiemejia_estebanblanco.navigation.AppScreens
import com.example.taller3_sophiemejia_estebanblanco.shared.MyBottomBar
import com.example.taller3_sophiemejia_estebanblanco.shared.MyButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import coil.compose.AsyncImage

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AvailableUsers(navController: NavController) {

    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }

    var hasRequested by remember { mutableStateOf(false) }

    val permissionGranted = notificationPermission == null || notificationPermission.status.isGranted

    Scaffold(
        bottomBar = {
            MyBottomBar(navController = navController, indexActual = 1)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (permissionGranted) {
                AvailableUsersContent(navController = navController)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!hasRequested && !notificationPermission!!.status.shouldShowRationale) {
                        Button(onClick = {
                            hasRequested = true
                            notificationPermission.launchPermissionRequest()
                        }) {
                            Text("Pedir permiso de notificaciones")
                        }
                    } else if (notificationPermission!!.status.shouldShowRationale) {
                        Button(onClick = {
                            notificationPermission.launchPermissionRequest()
                        }) {
                            Text("Pedir permiso de notificaciones")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Las notificaciones son necesarias para avisarte cuando otro usuario se marca como disponible, incluso si la aplicación está cerrada.",
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Button(onClick = {}, enabled = false) {
                            Text("Permiso denegado")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Para recibir notificaciones ve a Configuración del teléfono → Info de la app → Permisos → Notificaciones y actívalas manualmente.",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AvailableUsersContent(navController: NavController) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isMeAvailable) "Disponible" else "Oculto",
                    fontSize = 13.sp
                )
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = user.profilepic,
                                contentDescription = "Foto de perfil de ${user.name}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "${user.name} ${user.lastname}",
                                fontSize = 16.sp
                            )
                        }

                        Button(
                            onClick = { navController.navigate("${AppScreens.availableMap.name}/$userId") },
                            modifier = Modifier
                                .width(90.dp)
                                .height(40.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.azulBonito),
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "Ver", fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}