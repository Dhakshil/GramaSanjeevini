package com.example.grama_sanjeevini.data.repository

import android.net.Uri
import com.example.grama_sanjeevini.data.model.Prescription
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PrescriptionRepository {

    private val auth      = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage   = FirebaseStorage.getInstance()

    /**
     * Upload a prescription image to Firebase Storage and save the metadata
     * in Firestore under the "prescriptions" collection.
     *
     * @param imageUri         Local URI of the image selected by the user
     * @param nearestPharmacyId The nearest pharmacy's Firestore document ID
     * @param userName         The display name of the uploading user
     * @return                 The Firebase Storage download URL of the uploaded image
     */
    suspend fun uploadPrescription(
        imageUri: Uri,
        nearestPharmacyId: String,
        userName: String
    ): String {
        val uid  = auth.currentUser?.uid  ?: error("User not authenticated")
        val phone = auth.currentUser?.phoneNumber ?: ""

        // 1. Upload image to Firebase Storage
        val timestamp  = System.currentTimeMillis()
        val storageRef = storage.reference.child("receipts/$uid/$timestamp.jpg")
        storageRef.putFile(imageUri).await()
        val downloadUrl = storageRef.downloadUrl.await().toString()

        // 2. Write Firestore document
        val docRef = firestore.collection("prescriptions").document()
        val prescription = mapOf(
            "id"           to docRef.id,
            "userId"       to uid,
            "userName"     to userName,
            "userPhone"    to phone,
            "pharmacyId"   to nearestPharmacyId,
            "receiptUrl"   to downloadUrl,
            "uploadedAt"   to timestamp,
            "status"       to "pending"
        )
        docRef.set(prescription).await()

        return downloadUrl
    }

    /**
     * Real-time listener: returns all prescriptions for a given pharmacy,
     * ordered newest first.
     */
    fun getPrescriptionsForStore(storeId: String): Flow<List<Prescription>> = callbackFlow {
        val listener = firestore.collection("prescriptions")
            .whereEqualTo("pharmacyId", storeId)
            .orderBy("uploadedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                if (snap != null) {
                    val list = snap.documents.mapNotNull { doc ->
                        runCatching {
                            Prescription(
                                id          = doc.getString("id")          ?: doc.id,
                                userId      = doc.getString("userId")      ?: "",
                                userName    = doc.getString("userName")    ?: "Unknown",
                                userPhone   = doc.getString("userPhone")   ?: "",
                                pharmacyId  = doc.getString("pharmacyId") ?: "",
                                receiptUrl  = doc.getString("receiptUrl")  ?: "",
                                uploadedAt  = doc.getLong("uploadedAt")    ?: 0L,
                                status      = doc.getString("status")      ?: "pending"
                            )
                        }.getOrNull()
                    }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    /**
     * Update the status of a prescription (e.g., from "pending" to "reviewed").
     */
    suspend fun updateStatus(prescriptionId: String, newStatus: String) {
        firestore.collection("prescriptions")
            .document(prescriptionId)
            .update("status", newStatus)
            .await()
    }
}
