package com.example.shopmanager.model

data class Shop(
    var id: String? = "",
    var name: String = "",
    var description: String = "",
    var radius: Float = 100f,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)
