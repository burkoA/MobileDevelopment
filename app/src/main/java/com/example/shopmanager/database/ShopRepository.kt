package com.example.shopmanager.database

import com.example.shopmanager.model.Shop
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await

class ShopRepository(private val userId: String) {
    private val database = FirebaseDatabase.getInstance(
        "https://comexampleshopmanager-default-rtdb.europe-west1.firebasedatabase.app/"
    )

    private val shopsRef = database.getReference("users")
        .child(userId)
        .child("shops")

    fun listenToShops(onChange: (List<Shop>) -> Unit) {
        shopsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(Shop::class.java) }
                onChange(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    suspend fun addShop(shop: Shop) {
        val id = shopsRef.push().key!!
        shop.id = id
        shopsRef.child(id).setValue(shop).await()
    }

    suspend fun deleteShop(shop: Shop) {
        shop.id?.let { shopsRef.child(it).removeValue().await() }
    }

    suspend fun getShopById(shopId: String): Shop? {
        val snapshot = shopsRef.child(shopId).get().await()
        return snapshot.getValue(Shop::class.java)
    }
}