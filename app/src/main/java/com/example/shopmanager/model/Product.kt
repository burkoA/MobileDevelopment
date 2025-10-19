package com.example.shopmanager.model

data class Product(
    val id: Int? = null,
    val productName: String,
    val price: Double,
    val count: Int,
    val isBought: Boolean
)
