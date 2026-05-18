package com.example.taller3_sophiemejia_estebanblanco.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.taller3_sophiemejia_estebanblanco.model.User
import com.example.taller3_sophiemejia_estebanblanco.shared.MyBottomBar
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.compose.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.roundToInt

data class shareState(
    val myLocation: LatLng? = null,
    val trackedLocation: LatLng? = null,
    val trackedUser: String = "Cargando...",
    val distanceText: String = "Calculando distancia..."
)

class ShareViewModel : ViewModel() {
    private val _shareState = MutableStateFlow(shareState())
    val shareState: StateFlow<shareState> = _shareState.asStateFlow()

    fun updateMyLocation(newLocation: LatLng?) {
        _shareState.update { it.copy(myLocation = newLocation) }
    }

    fun updateTrackedLocation(newLocation: LatLng?) {
        _shareState.update { it.copy(trackedLocation = newLocation) }
    }

    fun updateTrackedUser(newName: String) {
        _shareState.update { it.copy(trackedUser = newName) }
    }

    fun updateDistanceText(newText: String) {
        _shareState.update { it.copy(distanceText = newText) }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun sharedLocation(trackedUserId: String, viewModel: ShareViewModel = viewModel(), navController: NavController) {
    val context = LocalContext.current
    val locationProvider = remember { LocationServices.getFusedLocationProviderClient(context) }
    val currentUserId = remember { FirebaseAuth.getInstance().currentUser?.uid }
    val database = remember { FirebaseDatabase.getInstance().getReference("users") }
    val state by viewModel.shareState.collectAsState()

    val bogota = LatLng(4.6288, -74.0640)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bogota, 12f)
    }

    LaunchedEffect(state.myLocation, state.trackedLocation) {
        state.trackedLocation?.let { local ->
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(local, 15f),
                durationMs = 1000
            )
        }

        if (state.myLocation != null && state.trackedLocation != null) {
            val results = FloatArray(1)
            Location.distanceBetween(
                state.myLocation!!.latitude, state.myLocation!!.longitude,
                state.trackedLocation!!.latitude, state.trackedLocation!!.longitude,
                results
            )
            val distanceMeters = results[0].roundToInt()
            val text =
                if (distanceMeters > 1000) "${distanceMeters / 1000.0} km" else "$distanceMeters m"
            viewModel.updateDistanceText(text)
        }
    }

    DisposableEffect(trackedUserId) {
        val userRef = FirebaseDatabase.getInstance().getReference("users/$trackedUserId")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    viewModel.updateTrackedUser(user.name)
                    viewModel.updateTrackedLocation(LatLng(user.latitude, user.longitude))
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        userRef.addValueEventListener(listener)
        onDispose { userRef.removeEventListener(listener) }
    }

    DisposableEffect(Unit) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateDistanceMeters(10f)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation
                if (location != null) {
                    viewModel.updateMyLocation(LatLng(location.latitude, location.longitude))

                    if (currentUserId != null) {
                        val updates = mapOf<String, Any>(
                            "latitude" to location.latitude,
                            "longitude" to location.longitude
                        )
                        database.child(currentUserId).updateChildren(updates)
                    }
                }
            }
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

            locationProvider.lastLocation.addOnSuccessListener { loc ->
                loc?.let {
                    val initialLoc = LatLng(it.latitude, it.longitude)
                    viewModel.updateMyLocation(initialLoc)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(initialLoc, 15f)
                }
            }
        }
        onDispose { locationProvider.removeLocationUpdates(locationCallback) }
    }

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
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = false)
            ) {
                state.myLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Tú",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    )
                }
                state.trackedLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = state.trackedUser,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )
                }
            }

            if (state.myLocation != null && state.trackedLocation != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                        .align(Alignment.TopCenter)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Siguiendo a: ${state.trackedUser}", fontWeight = FontWeight.Bold)
                        Text(
                            "Distancia: ${state.distanceText}",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }
    }
}