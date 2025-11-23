package com.example.shopmanager.model

data class Product(
    var id: String? = "",
    var productName: String = "",
    var price: Double = 0.0,
    var count: Int = 0,
    var isBought: Boolean = false
)
