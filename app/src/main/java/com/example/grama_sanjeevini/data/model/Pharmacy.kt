package com.example.grama_sanjeevini.data.model

data class Pharmacy(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val areaName: String = "",
    val phone: String = "",
    val distanceKm: Double = 0.0,
    val openTime: String = "",
    val closeTime: String = "",
    val isOpen: Boolean = true,
    val medicines: List<Medicine> = emptyList(),
    // GPS coordinates stored in Firestore
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val ownerId: String = ""
)
