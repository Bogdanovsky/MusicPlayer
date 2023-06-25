package com.bogdanovsky.musicplayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi

class MyApp : Application() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "NotifChannelName",
            NotificationManager.IMPORTANCE_DEFAULT)
        notificationChannel.description = "Notification channel description"
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
        super.onCreate()
    }
}