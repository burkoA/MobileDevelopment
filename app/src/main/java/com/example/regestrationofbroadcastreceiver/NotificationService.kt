package com.example.regestrationofbroadcastreceiver

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationService : Service() {

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val name = intent?.getStringExtra("productName")
        val price = intent?.getStringExtra("price")
        val count = intent?.getStringExtra("count")

        createChannel()

        val intent = Intent().apply {
            component = ComponentName(
                "com.example.shopmanager",
                "com.example.shopmanager.ShoppingActivity"
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val pending = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "productChannel")
            .setContentTitle("Dodano nowy produkt")
            .setContentText("$name - ilość: $count, cena: $price")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()

        startForeground(1, notification)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?) = null


    fun createChannel() {
        val notificationChannel = NotificationChannel(
            "productChannel",
            "products",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(notificationChannel)
    }
}