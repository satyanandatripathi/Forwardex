package com.forwardex.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class RuleEngineForegroundService : Service() {
    override fun onCreate() {
        super.onCreate()
        ensureChannel()
        startForeground(1001, notification("Forwardex engine active"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onBind(intent: Intent?): IBinder? = null

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(
                NotificationChannel("forwardex_engine", "Forwardex Engine", NotificationManager.IMPORTANCE_LOW)
            )
        }
    }

    private fun notification(content: String): Notification =
        NotificationCompat.Builder(this, "forwardex_engine")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle("Forwardex")
            .setContentText(content)
            .build()
}
