package com.team.score.Utils

import com.team.score.OnBoarding.OnboardingActivity
import com.team.score.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM Token", "onNewToken: $token")
        // 서버에 토큰 업데이트 필요 시 추가
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("FirebaseService", "Message received from: ${message.from}")

        val title = message.notification?.title.toString()
        val body = message.notification?.body.toString()

        // 알림 표시
        showNotification(title, body)
    }

    private fun showNotification(
        messageTitle: String,
        messageBody: String
    ) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = (System.currentTimeMillis() / 7).toInt() // 고유 ID 지정

        createNotificationChannel(notificationManager)

        val intent = Intent(this, OnboardingActivity::class.java)
            .apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.mipmap.ic_launcher_foreground
                )
            )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                enableVibration(true)
                description = CHANNEL_DESCRIPTION
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_NAME = "ScoreNotification"
        private const val CHANNEL_DESCRIPTION = "Channel For Score Notification"
        private const val CHANNEL_ID = "fcm_default_channel"
    }
}

