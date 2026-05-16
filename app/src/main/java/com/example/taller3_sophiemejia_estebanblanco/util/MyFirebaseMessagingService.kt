package com.example.taller3_sophiemejia_estebanblanco.notifications

import android.util.Log
import com.example.taller3_sophiemejia_estebanblanco.showNotification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i("FCM", "Mensaje recibido")

        val title = remoteMessage.data["title"] ?: remoteMessage.notification?.title
        val body  = remoteMessage.data["body"]  ?: remoteMessage.notification?.body
        val userId = remoteMessage.data["targetUserId"]

        if (title != null && body != null) {
            showNotification(title, body, this, userId)
        }
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("FCM", "Token renovado: $token")
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseDatabase.getInstance()
            .getReference("users/$uid/fcmToken")
            .setValue(token)
    }
}