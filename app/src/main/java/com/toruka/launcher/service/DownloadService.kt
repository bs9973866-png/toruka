package com.toruka.launcher.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.toruka.launcher.MainActivity
import com.toruka.launcher.R
import kotlinx.coroutines.*

class DownloadService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val notificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    companion object {
        const val CHANNEL_ID = "toruka_download_channel"
        const val NOTIFICATION_ID = 1
        const val EXTRA_VERSION_ID = "version_id"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val versionId = intent?.getStringExtra(EXTRA_VERSION_ID) ?: return START_NOT_STICKY

        startForeground(NOTIFICATION_ID, buildNotification("Подготовка загрузки...", 0))

        serviceScope.launch {
            try {
                // Download logic here
                updateNotification("Загрузка $versionId", 50)
                
                // Simulate download completion
                delay(2000)
                
                updateNotification("Установка завершена", 100)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            } catch (e: Exception) {
                updateNotification("Ошибка загрузки: ${e.message}", 0, isError = true)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Загрузки Minecraft",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Уведомления о загрузке версий Minecraft"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(content: String, progress: Int, isError: Boolean = false): android.app.Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Toruka Launcher")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setProgress(100, progress, progress == 0 && !isError)
            .setOngoing(!isError)
            .build()
    }

    private fun updateNotification(content: String, progress: Int, isError: Boolean = false) {
        notificationManager.notify(NOTIFICATION_ID, buildNotification(content, progress, isError))
    }
}
