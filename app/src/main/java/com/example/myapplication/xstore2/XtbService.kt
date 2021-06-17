package com.example.myapplication.xstore2

import android.app.*
import android.content.Intent
import android.os.HandlerThread
import android.os.IBinder
import android.os.Process
import android.widget.Toast
import com.example.myapplication.MainActivity
import com.example.myapplication.R

class XtbService : Service() {
    override fun onCreate() {
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        val thread = HandlerThread(
            "ServiceStartArguments",
            Process.THREAD_PRIORITY_FOREGROUND
        )
        thread.start()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()
        startServiceInForeground()

        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    private fun startServiceInForeground() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        createNotificationChannel()
        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Content Title")
            .setContentText("Content text")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setTicker("Ticker text")
            .build()
        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(
            NotificationManager::class.java
        )
        manager.createNotificationChannel(serviceChannel)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val CHANNEL_ID = "ForegroundServiceChannel"
        private const val ONGOING_NOTIFICATION_ID = 101
    }
}