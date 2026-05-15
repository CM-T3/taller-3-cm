package com.example.taller3_sophiemejia_estebanblanco

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.taller3_sophiemejia_estebanblanco.navigation.AppScreens
import com.example.taller3_sophiemejia_estebanblanco.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

lateinit var auth : FirebaseAuth
lateinit var geocoder: Geocoder

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    private val targetUserIdState = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        geocoder = Geocoder(this)
        auth = FirebaseAuth.getInstance()

        targetUserIdState.value = intent.getStringExtra("targetUserId")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val targetUserId by targetUserIdState

            LaunchedEffect(Unit) {
                FirebaseMessaging.getInstance().subscribeToTopic("available_users")
            }

            LaunchedEffect(targetUserId) {
                if (targetUserId != null) {
                    if (auth.currentUser != null) {
                        navController.navigate("${AppScreens.availableMap.name}/$targetUserId")
                    } else {
                        navController.navigate(AppScreens.login.name)
                    }
                    targetUserIdState.value = null
                }
            }

            Navigation(navController)
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        targetUserIdState.value = intent.getStringExtra("targetUserId")
    }
}