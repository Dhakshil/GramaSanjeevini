package com.example.grama_sanjeevini.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val role: String = "CUSTOMER",
    val gender: String = "",
    val age: String = "",
    val bloodType: String = "",
    val createdAt: Long = 0L
)
