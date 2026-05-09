package com.example.grama_sanjeevini.data.model

/**
 * Represents a prescription receipt uploaded by a customer.
 * Stored in Firestore under collection "prescriptions".
 */
data class Prescription(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhone: String = "",
    val pharmacyId: String = "",
    val receiptUrl: String = "",       // Firebase Storage download URL
    val uploadedAt: Long = 0L,
    val status: String = "pending"    // pending | reviewed | fulfilled
)
