package com.example.grama_sanjeevini.data.model

data class Pharmacy(
    val id: String,
    val name: String,
    val address: String,
    val areaName: String,
    val phone: String,
    val distanceKm: Double,
    val openTime: String,
    val closeTime: String,
    val isOpen: Boolean,
    val medicines: List<Medicine> = emptyList()
)
