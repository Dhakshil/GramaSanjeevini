package com.example.grama_sanjeevini.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grama_sanjeevini.constants.AppStrings
import com.example.grama_sanjeevini.data.model.Medicine
import com.example.grama_sanjeevini.data.model.Pharmacy
import com.example.grama_sanjeevini.data.model.Prescription
import com.example.grama_sanjeevini.data.repository.PrescriptionRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PharmacistViewModel : ViewModel() {

    private val auth             = FirebaseAuth.getInstance()
    private val firestore        = FirebaseFirestore.getInstance()
    private val prescriptionRepo = PrescriptionRepository()

    // ── Store info ─────────────────────────────────────────────────────────────
    var storeId    by mutableStateOf("")
        private set
    var storeName  by mutableStateOf("My Store")
        private set
    var storeError by mutableStateOf("")
        private set
    var storeLoading by mutableStateOf(true)
        private set

    // ── Inventory ──────────────────────────────────────────────────────────────
    private val _inventory = mutableStateListOf<Medicine>()
    val inventory: List<Medicine> get() = _inventory

    val totalItems: Int    get() = _inventory.size
    val lowStockCount: Int get() = _inventory.count { it.isLowStock }

    var isSaving by mutableStateOf(false)
        private set
    var saveError by mutableStateOf("")
        private set

    // ── Prescriptions ──────────────────────────────────────────────────────────
    var prescriptions     by mutableStateOf<List<Prescription>>(emptyList())
        private set
    var prescriptionsError by mutableStateOf("")
        private set
    var prescriptionUpdating by mutableStateOf<String?>(null)
        private set

    init {
        loadStoreAndData()
    }

    /**
     * Load the pharmacist's store from Firestore (matched by ownerId),
     * then load inventory and start the prescriptions real-time listener.
     */
    private fun loadStoreAndData() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            storeLoading = true
            storeError   = ""
            try {
                val snap = firestore.collection("pharmacies")
                    .whereEqualTo("ownerId", uid)
                    .limit(1)
                    .get().await()

                if (!snap.isEmpty) {
                    val doc = snap.documents.first()
                    storeId   = doc.id
                    storeName = doc.getString("name") ?: "My Store"
                    loadInventory(storeId)
                    observePrescriptions(storeId)
                } else {
                    // No store registered yet — start with empty state
                    storeId   = "temp_${uid}"
                    storeName = "My Store"
                    storeError = ""
                }
            } catch (e: Exception) {
                val msg = e.message?.lowercase() ?: ""
                storeError = when {
                    msg.contains("network") || msg.contains("timeout") -> AppStrings.ERR_NETWORK
                    else -> AppStrings.ERR_STORE_LOAD_FAILED
                }
            } finally {
                storeLoading = false
            }
        }
    }

    private fun loadInventory(sid: String) {
        viewModelScope.launch {
            try {
                val snap = firestore.collection("pharmacies")
                    .document(sid)
                    .collection("inventory")
                    .get().await()
                val items = snap.documents.mapNotNull { doc ->
                    runCatching {
                        Medicine(
                            id           = doc.id,
                            name         = doc.getString("name")          ?: "",
                            brand        = doc.getString("brand")         ?: "",
                            category     = doc.getString("category")      ?: "Other",
                            description  = doc.getString("description")   ?: "",
                            dosage       = doc.getString("dosage")        ?: "",
                            mrp          = doc.getDouble("mrp")           ?: 0.0,
                            sellingPrice = doc.getDouble("sellingPrice")  ?: 0.0,
                            stockCount   = doc.getLong("stockCount")?.toInt() ?: 0,
                            expiryDate   = doc.getString("expiryDate")   ?: "",
                            batchNumber  = doc.getString("batchNumber")  ?: "",
                            pharmacyId   = sid,
                            pharmacyName = storeName,
                            distanceKm   = 0.0
                        )
                    }.getOrNull()
                }
                _inventory.clear()
                _inventory.addAll(items)
            } catch (_: Exception) { /* inventory load fail is non-critical */ }
        }
    }

    private fun observePrescriptions(sid: String) {
        viewModelScope.launch {
            prescriptionsError = ""
            try {
                prescriptionRepo.getPrescriptionsForStore(sid).collect { list ->
                    prescriptions = list
                }
            } catch (e: Exception) {
                val msg = e.message?.lowercase() ?: ""
                prescriptionsError = when {
                    msg.contains("network") || msg.contains("timeout") -> AppStrings.ERR_NETWORK
                    else -> AppStrings.ERR_PRESCRIPTIONS_LOAD_FAILED
                }
            }
        }
    }

    /**
     * Save a new medicine to Firestore under this pharmacist's store inventory,
     * then refresh the local list.
     */
    fun addMedicine(medicine: Medicine) {
        val sid = storeId.ifEmpty { return }
        viewModelScope.launch {
            isSaving  = true
            saveError = ""
            try {
                val docRef = firestore.collection("pharmacies")
                    .document(sid)
                    .collection("inventory")
                    .document()

                val data = mapOf(
                    "name"         to medicine.name,
                    "brand"        to medicine.brand,
                    "category"     to medicine.category,
                    "description"  to medicine.description,
                    "dosage"       to medicine.dosage,
                    "mrp"          to medicine.mrp,
                    "sellingPrice" to medicine.sellingPrice,
                    "stockCount"   to medicine.stockCount,
                    "expiryDate"   to medicine.expiryDate,
                    "batchNumber"  to medicine.batchNumber,
                    "pharmacyId"   to sid,
                    "pharmacyName" to storeName
                )
                docRef.set(data).await()
                // Reload inventory after save
                loadInventory(sid)
            } catch (e: Exception) {
                val msg = e.message?.lowercase() ?: ""
                saveError = when {
                    msg.contains("network") || msg.contains("timeout") -> AppStrings.ERR_NETWORK
                    else -> AppStrings.ERR_SAVE_LISTING_FAILED
                }
            } finally {
                isSaving = false
            }
        }
    }

    /**
     * Update prescription status to "reviewed" or "fulfilled".
     */
    fun updatePrescriptionStatus(prescriptionId: String, newStatus: String) {
        viewModelScope.launch {
            prescriptionUpdating = prescriptionId
            try {
                prescriptionRepo.updateStatus(prescriptionId, newStatus)
            } catch (_: Exception) { }
            finally { prescriptionUpdating = null }
        }
    }

    fun clearSaveError() { saveError = "" }
}
