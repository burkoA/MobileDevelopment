package com.example.shopmanager.database

import com.example.shopmanager.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await

class RealtimeDBRepository(private val userId: String) {

    private val database = FirebaseDatabase.getInstance(
        "https://comexampleshopmanager-default-rtdb.europe-west1.firebasedatabase.app/"
    )

    private val myProductsRef = database.getReference("users")
        .child(userId)
        .child("products")

    private val allProductsRef = database.getReference("users")

    fun listenToMyProducts(onChange: (List<Product>) -> Unit) {
        myProductsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(Product::class.java) }
                onChange(list)
            }
            override fun onCancelled(error: DatabaseError) { }
        })
    }

    fun listenToAllProducts(onChange: (List<Product>) -> Unit) {
        allProductsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val all = mutableListOf<Product>()

                snapshot.children.forEach { userNode ->
                    userNode.child("products").children.forEach { p ->
                        p.getValue(Product::class.java)?.let { all.add(it) }
                    }
                }

                onChange(all)
            }

            override fun onCancelled(error: DatabaseError) { }
        })
    }

    suspend fun addProduct(product: Product) {
        val id = myProductsRef.push().key!!
        product.id = id
        myProductsRef.child(id).setValue(product).await()
    }

    suspend fun updateProduct(product: Product) {
        myProductsRef.child(product.id!!).setValue(product).await()
    }

    suspend fun deleteProduct(id: String) {
        myProductsRef.child(id).removeValue().await()
    }
}