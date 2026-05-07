package com.example.grama_sanjeevini.data.repository

import com.example.grama_sanjeevini.data.model.Pharmacy
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PharmacyRepository {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Fetch all pharmacies from Firestore, compute real distance from user GPS,
     * and return sorted list (nearest first).
     *
     * Falls back to an empty list (caller uses hardcoded dummies) if Firestore is empty.
     */
    suspend fun getNearbyPharmacies(
        userLat: Double,
        userLng: Double
    ): List<Pharmacy> {
        return try {
            val snapshot = db.collection("pharmacies").get().await()
            if (snapshot.isEmpty) return emptyList()

            snapshot.documents.mapNotNull { doc ->
                val lat  = doc.getDouble("lat")  ?: return@mapNotNull null
                val lng  = doc.getDouble("lng")  ?: return@mapNotNull null
                val name = doc.getString("name") ?: return@mapNotNull null

                val rawDist = LocationRepository.distanceKm(userLat, userLng, lat, lng)
                val distKm  = Math.round(rawDist * 10) / 10.0

                Pharmacy(
                    id         = doc.id,
                    name       = name,
                    address    = doc.getString("address")   ?: "",
                    areaName   = doc.getString("areaName")  ?: "",
                    phone      = doc.getString("phone")     ?: "",
                    distanceKm = distKm,
                    openTime   = doc.getString("openTime")  ?: "8:00 AM",
                    closeTime  = doc.getString("closeTime") ?: "9:00 PM",
                    isOpen     = doc.getBoolean("isOpen")   ?: true,
                    lat        = lat,
                    lng        = lng,
                    ownerId    = doc.getString("ownerId")   ?: ""
                )
            }.sortedBy { it.distanceKm }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
