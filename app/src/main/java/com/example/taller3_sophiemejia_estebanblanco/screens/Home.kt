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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.taller3_sophiemejia_estebanblanco.shared.MyBottomBar
import com.example.taller3_sophiemejia_estebanblanco.util.readJsonPos
import com.google.accompanist.permissions.*
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Home(navController: NavController) {
    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    val permission = rememberPermissionState(locationPermission)
    var showButton by remember { mutableStateOf(false) }

    SideEffect {
        if (!permission.status.isGranted) {
            if (permission.status.shouldShowRationale) {
                showButton = true
            } else {
                showButton = false
                permission.launchPermissionRequest()
            }
        }
    }
    Scaffold(
        bottomBar = {
            MyBottomBar(navController = navController, indexActual = 0)
        }
    ) { paddingValues ->
        // Envuelve tu contenido actual en un Box o Column aplicando el paddingValues
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (permission.status.isGranted) {
                LocationWithMapRequest()
            } else {

                Column(
                    modifier = Modifier.fillMaxSize().padding(15.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (showButton) {
                        Text("Se requiere acceder a gps")
                        Button(onClick = { permission.launchPermissionRequest() }) {
                            Text("Pedir permiso de localizacion")
                        }
                    } else {
                        Text("NO hay acceso")
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
        if(ContextCompat.checkSelfPermission(context, locationPermission)== PackageManager.PERMISSION_GRANTED) {
            locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
        onDispose {locationClient.removeLocationUpdates(locationCallback)}
    }

    if (latitude != 0.0 && longitude != 0.0) {
        val currentLocation = LatLng(latitude, longitude)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(currentLocation, 15f)
        }


        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false)
        ) {
            Marker(
                state = MarkerState(position = currentLocation),
                title = "Tu ubicación",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            )

            val jsonPois = readJsonPos(context)
            jsonPois.forEach { it ->
                Marker(
                    state = MarkerState(position = it.location),
                    title = it.name
                )
            }
        }



    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Calculando GPS...")
        }
    }
}


fun createLocationRequest(): LocationRequest {
    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 10000
    )
        .setWaitForAccurateLocation(true)
        .setMinUpdateIntervalMillis(5000)
        .build()

    return locationRequest
}

fun createLocationCallback(onLocationChange: (LocationResult) -> Unit): LocationCallback {
    val callback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            onLocationChange(locationResult)
        }
    }
    return callback
}
