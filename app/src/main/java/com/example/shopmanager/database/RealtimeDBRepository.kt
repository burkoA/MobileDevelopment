package com.example.shopmanager.database

import com.example.shopmanager.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class RealtimeDBRepository(private val userId: String) {

    private val db = FirebaseDatabase.getInstance("https://comexampleshopmanager-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("users")
        .child(userId)
        .child("products")

    suspend fun addProduct(product: Product) {
        val id = db.push().key!!
        product.id = id
        db.child(id).setValue(product).await()
    }

    suspend fun getProducts(): List<Product> {
        val snapshot = db.get().await()
        return snapshot.children.mapNotNull { it.getValue(Product::class.java) }
    }

    suspend fun updateProduct(product: Product) {
        db.child(product.id!!).setValue(product).await()
    }

    suspend fun deleteProduct(id: String) {
        db.child(id).removeValue().await()
    }
}