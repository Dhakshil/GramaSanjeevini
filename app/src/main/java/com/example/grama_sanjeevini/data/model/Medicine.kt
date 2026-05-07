package com.example.grama_sanjeevini.data.model

data class Medicine(
    val id: String,
    val name: String,
    val brand: String,
    val category: String,
    val description: String,
    val dosage: String,
    val mrp: Double,
    val sellingPrice: Double,
    val stockCount: Int,
    val expiryDate: String,
    val batchNumber: String,
    val pharmacyId: String,
    val pharmacyName: String,
    val distanceKm: Double,
    val imageUrl: String = ""
) {
    val discountPercent: Int
        get() = if (mrp > 0) ((mrp - sellingPrice) / mrp * 100).toInt() else 0
    val isLowStock: Boolean
        get() = stockCount in 1..5
    val isOutOfStock: Boolean
        get() = stockCount == 0
}
