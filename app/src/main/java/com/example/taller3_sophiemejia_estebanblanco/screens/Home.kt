package com.example.taller3_sophiemejia_estebanblanco.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.activity.compose.BackHandler
import com.example.taller3_sophiemejia_estebanblanco.shared.MyBottomBar
import com.example.taller3_sophiemejia_estebanblanco.util.readJsonPos
import com.google.accompanist.permissions.*
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Home(navController: NavController) {
    BackHandler { }
    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    val permission = rememberPermissionState(locationPermission)

    var hasRequested by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            MyBottomBar(navController = navController, indexActual = 0)
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (permission.status.isGranted) {
                LocationWithMapRequest()
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!hasRequested && !permission.status.shouldShowRationale) {
                        Button(onClick = {
                            hasRequested = true
                            permission.launchPermissionRequest()
                        }) {
                            Text("Pedir permiso de localización")
                        }
                    } else if (permission.status.shouldShowRationale) {
                        Button(onClick = {
                            permission.launchPermissionRequest()
                        }) {
                            Text("Pedir permiso de localización")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Es importante acceder a tu ubicación para calcular las distancias y mostrar tu posición actual en el mapa respecto a los puntos de interés.",
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Button(onClick = { }, enabled = false) {
                            Text("Permiso denegado")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Para otorgar permisos ir directamente a la info de la app en sus configuraciones.",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LocationWithMapRequest() {
    val context = LocalContext.current
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION

    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }

    val locationRequest = remember { createLocationRequest() }

    val currentUserId = remember { FirebaseAuth.getInstance().currentUser?.uid }
    val database = remember { FirebaseDatabase.getInstance().getReference("users") }

    val locationCallback = remember {
        createLocationCallback { result ->
            result.lastLocation?.let {
                latitude = it.latitude
                longitude = it.longitude

                if (currentUserId != null) {
                    val updates = mapOf<String, Any>(
                        "latitude" to it.latitude,
                        "longitude" to it.longitude
                    )
                    database.child(currentUserId).updateChildren(updates)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        if(ContextCompat.checkSelfPermission(context, locationPermission) == PackageManager.PERMISSION_GRANTED) {
            locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
        onDispose { locationClient.removeLocationUpdates(locationCallback) }
    }

    val defaultLocation = LatLng(4.6288, -74.0640)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    LaunchedEffect(latitude, longitude) {
        if (latitude != 0.0 && longitude != 0.0) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 15f),
                durationMs = 1000
            )
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = false)
    ) {
        if (latitude != 0.0 && longitude != 0.0) {
            Marker(
                state = MarkerState(position = LatLng(latitude, longitude)),
                title = "Tu ubicación",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            )
        }


        val jsonPois = remember { readJsonPos(context) }
        jsonPois.forEach { it ->
            Marker(
                state = MarkerState(position = it.location),
                title = it.name
            )
        }
    }
}

fun createLocationRequest(): LocationRequest {
    return LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 10000
    )
        .setWaitForAccurateLocation(true)
        .setMinUpdateIntervalMillis(5000)
        .build()
}

fun createLocationCallback(onLocationChange: (LocationResult) -> Unit): LocationCallback {
    return object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            onLocationChange(locationResult)
        }
    }
}