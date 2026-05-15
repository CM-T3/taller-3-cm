package com.example.taller3_sophiemejia_estebanblanco.notifications

import android.util.Log
import com.example.taller3_sophiemejia_estebanblanco.showNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i("FirebaseApp", "Message Received!!!")

        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body

        val userId = remoteMessage.data["targetUserId"]

        if (title != null && body != null) {
            showNotification(title, body, this, userId)
        }
    }
}