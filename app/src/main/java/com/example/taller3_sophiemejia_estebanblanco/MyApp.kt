package com.example.taller3_sophiemejia_estebanblanco

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class MyApp : Application() {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "notificaion_fcm"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "FCM Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Channel for FCM Notifications"
            val notManager = getSystemService(NotificationManager::class.java)
            notManager?.createNotificationChannel(channel)
        }
    }
}

fun showNotification(title: String, message: String, context: Context, targetUserId: String? = null) {
    val notManager = context.getSystemService(NotificationManager::class.java)
    val notification: Notification

    if (targetUserId != null) {
        // Preparamos el Intent para abrir MainActivity enviándole el ID del usuario [cite: 366, 370]
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("targetUserId", targetUserId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        notification = NotificationCompat.Builder(context, MyApp.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent) // Al tocar, ejecuta el PendingIntent [cite: 372, 377]
            .build()
    } else {
        notification = NotificationCompat.Builder(context, MyApp.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .build()
    }
    notManager?.notify(1, notification)
}