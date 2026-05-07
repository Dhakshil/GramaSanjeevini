package com.example.grama_sanjeevini.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grama_sanjeevini.data.model.Medicine
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PharmacistViewModel : ViewModel() {

    // ── Initial dummy inventory ────────────────────────────────────────────────
    private val _inventory = mutableStateListOf(
        Medicine(
            id = "pm1", name = "Amoxicillin", brand = "Cipla", category = "Antibiotics",
            description = "Broad-spectrum antibiotic", dosage = "500mg",
            mrp = 120.0, sellingPrice = 85.0, stockCount = 24,
            expiryDate = "08/2026", batchNumber = "CPL2024A1",
            pharmacyId = "myPharmacy", pharmacyName = "My Pharmacy", distanceKm = 0.0
        ),
        Medicine(
            id = "pm2", name = "Paracetamol", brand = "GSK", category = "Pain Relief",
            description = "Fever and pain relief", dosage = "650mg",
            mrp = 25.0, sellingPrice = 18.0, stockCount = 150,
            expiryDate = "12/2026", batchNumber = "GSK2024P2",
            pharmacyId = "myPharmacy", pharmacyName = "My Pharmacy", distanceKm = 0.0
        ),
        Medicine(
            id = "pm3", name = "ORS Powder", brand = "Electroral", category = "Hydration",
            description = "Oral rehydration salts", dosage = "21g / sachet",
            mrp = 30.0, sellingPrice = 22.0, stockCount = 4,
            expiryDate = "09/2026", batchNumber = "ELC2024O6",
            pharmacyId = "myPharmacy", pharmacyName = "My Pharmacy", distanceKm = 0.0
        ),
        Medicine(
            id = "pm4", name = "Cetirizine", brand = "Sanofi", category = "Allergy",
            description = "Antihistamine for allergies", dosage = "10mg",
            mrp = 75.0, sellingPrice = 55.0, stockCount = 3,
            expiryDate = "11/2026", batchNumber = "SNF2024C7",
            pharmacyId = "myPharmacy", pharmacyName = "My Pharmacy", distanceKm = 0.0
        ),
        Medicine(
            id = "pm5", name = "Antacid Syrup", brand = "Digene", category = "Digestive",
            description = "Relieves acidity and heartburn", dosage = "200ml",
            mrp = 110.0, sellingPrice = 88.0, stockCount = 45,
            expiryDate = "07/2026", batchNumber = "DGN2024A8",
            pharmacyId = "myPharmacy", pharmacyName = "My Pharmacy", distanceKm = 0.0
        )
    )

    val inventory: List<Medicine> get() = _inventory

    val totalItems: Int get() = _inventory.size
    val lowStockCount: Int get() = _inventory.count { it.isLowStock }

    var isSaving by mutableStateOf(false)
        private set

    fun addMedicine(medicine: Medicine) {
        viewModelScope.launch {
            isSaving = true
            delay(1500L) // simulate network/save
            _inventory.add(medicine.copy(id = "pm${System.currentTimeMillis()}"))
            isSaving = false
        }
    }
}
