package com.example.shopmanager.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ProductBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.example.shopmanager.NEW_PRODUCT_ADDED") {
            val name = intent.getStringExtra("productName")
            val price = intent.getStringExtra("price")
            val count = intent.getStringExtra("count")
            val bought = intent.getBooleanExtra("isBought", false)

            Toast.makeText(
                context,
                "Added: $name | price: $price | count: $count | bought: $bought",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}