package com.example.shopmanager.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.shopmanager.database.ShopRepository
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeofenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return
        val transition = event.geofenceTransition
        val geofences = event.triggeringGeofences ?: return

        geofences.forEach { gf ->
            val shopName = gf.requestId.substringAfter(":")
            val message = when (transition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> "Welcome to $shopName! Promotion is active!"
                Geofence.GEOFENCE_TRANSITION_EXIT -> "Goodbye from $shopName! See you again!"
                else -> return@forEach
            }

            NotificationHelper.sendNotification(context, "Shop Alert", message)
        }
    }
}