package com.example.myapplication.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FirebaseInstanceService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FirebaseLog"
        private const val CHANNEL_ID = "firebase_channel"
    }


    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")
        remoteMessage.notification?.let {
            it.title?.let { title ->
                it.body?.let { body ->
                    showNotification(
                        title,
                        body
                    )
                }
            }
        }
        remoteMessage.data.takeIf { it.isNotEmpty() }?.let { handleDataPayload(it) }

    }


    private fun handleDataPayload(data: Map<String, String>) {
        if (data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: $data")
            val title = data["title"] ?: "Notification"
            val message = data["body"] ?: "you got a new message."
            showNotification(title, message)
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(title: String, message: String) {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this)
            .notify(System.currentTimeMillis().toInt(), notification)
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Firebase Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications FCM"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}