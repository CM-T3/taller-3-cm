package com.example.taller3_sophiemejia_estebanblanco.model

data class User(
    val name: String = "",
    val lastname: String = "",
    val email: String = "",
    val password: String = "",
    val profilepic: String = "",
    val id: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val available: Boolean = true,
    val fcmToken: String = ""
)