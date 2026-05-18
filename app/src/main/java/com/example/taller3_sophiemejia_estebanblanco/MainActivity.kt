package com.example.taller3_sophiemejia_estebanblanco

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.taller3_sophiemejia_estebanblanco.navigation.AppScreens
import com.example.taller3_sophiemejia_estebanblanco.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

lateinit var auth: FirebaseAuth

class MainActivity : ComponentActivity() {

    private val targetUserIdState = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        targetUserIdState.value = intent.getStringExtra("targetUserId")


        setContent {
            val navController = rememberNavController()
            val targetUserId by targetUserIdState

            LaunchedEffect(Unit) {
                val uid = auth.currentUser?.uid ?: return@LaunchedEffect
                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                    FirebaseDatabase.getInstance()
                        .getReference("users/$uid/fcmToken")
                        .setValue(token)
                }
            }

            LaunchedEffect(targetUserId) {
                val userId = targetUserId ?: return@LaunchedEffect
                if (auth.currentUser == null) {
                    targetUserIdState.value = null
                    return@LaunchedEffect
                }
                navController.addOnDestinationChangedListener(
                    object : androidx.navigation.NavController.OnDestinationChangedListener {
                        override fun onDestinationChanged(
                            controller: androidx.navigation.NavController,
                            destination: androidx.navigation.NavDestination,
                            arguments: android.os.Bundle?
                        ) {

                            controller.removeOnDestinationChangedListener(this)
                            controller.navigate("${AppScreens.availableMap.name}/$userId") {
                                launchSingleTop = true
                            }
                            targetUserIdState.value = null
                        }
                    }
                )
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