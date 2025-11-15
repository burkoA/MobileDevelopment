package com.example.regestrationofbroadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ProductReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, NotificationService::class.java).apply {
            putExtras(intent.extras!!)
        }

        context.startForegroundService(serviceIntent)
    }
}