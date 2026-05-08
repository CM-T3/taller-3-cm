package com.example.taller3_sophiemejia_estebanblanco

import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.taller3_sophiemejia_estebanblanco.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth

lateinit var auth : FirebaseAuth
lateinit var geocoder: Geocoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        geocoder = Geocoder(this)
        auth = FirebaseAuth.getInstance()
        enableEdgeToEdge()
        setContent {
            Navigation()
        }
    }
}

