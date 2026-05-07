package com.example.grama_sanjeevini.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.grama_sanjeevini.data.model.Medicine
import com.example.grama_sanjeevini.data.model.Pharmacy

class HomeViewModel : ViewModel() {

    // ── Dummy medicines ────────────────────────────────────────────────────────
    private val allMedicines = listOf(
        Medicine(
            id = "m1", name = "Amoxicillin", brand = "Cipla", category = "Antibiotics",
            description = "Broad-spectrum antibiotic for bacterial infections",
            dosage = "500mg", mrp = 120.0, sellingPrice = 85.0, stockCount = 24,
            expiryDate = "08/2026", batchNumber = "CPL2024A1",
            pharmacyId = "p1", pharmacyName = "Sri Dhanvantari Medical", distanceKm = 0.8
        ),
        Medicine(
            id = "m2", name = "Paracetamol", brand = "GSK", category = "Pain Relief",
            description = "Relieves mild to moderate pain and fever",
            dosage = "650mg", mrp = 25.0, sellingPrice = 18.0, stockCount = 150,
            expiryDate = "12/2026", batchNumber = "GSK2024P2",
            pharmacyId = "p1", pharmacyName = "Sri Dhanvantari Medical", distanceKm = 0.8
        ),
        Medicine(
            id = "m3", name = "Dolo 650", brand = "Micro Labs", category = "Fever",
            description = "Fast-acting tablet for fever and body ache",
            dosage = "650mg", mrp = 42.0, sellingPrice = 32.0, stockCount = 87,
            expiryDate = "03/2027", batchNumber = "MCL2024D3",
            pharmacyId = "p2", pharmacyName = "Village Care Pharmacy", distanceKm = 1.4
        ),
        Medicine(
            id = "m4", name = "Crepe Bandage Roll", brand = "3M", category = "First Aid",
            description = "Elastic bandage for sprains and support dressing",
            dosage = "10cm x 4m", mrp = 60.0, sellingPrice = 45.0, stockCount = 30,
            expiryDate = "06/2028", batchNumber = "3M2024B4",
            pharmacyId = "p2", pharmacyName = "Village Care Pharmacy", distanceKm = 1.4
        ),
        Medicine(
            id = "m5", name = "Vitamin C", brand = "HealthVit", category = "Vitamins",
            description = "Immune booster and antioxidant supplement",
            dosage = "1000mg", mrp = 250.0, sellingPrice = 195.0, stockCount = 12,
            expiryDate = "01/2027", batchNumber = "HV2024V5",
            pharmacyId = "p3", pharmacyName = "Arogya Medical Store", distanceKm = 2.1
        ),
        Medicine(
            id = "m6", name = "ORS Powder", brand = "Electroral", category = "Hydration",
            description = "Oral rehydration salts for dehydration and diarrhoea",
            dosage = "21g / sachet", mrp = 30.0, sellingPrice = 22.0, stockCount = 8,
            expiryDate = "09/2026", batchNumber = "ELC2024O6",
            pharmacyId = "p1", pharmacyName = "Sri Dhanvantari Medical", distanceKm = 0.8
        ),
        Medicine(
            id = "m7", name = "Cetirizine", brand = "Sanofi", category = "Allergy",
            description = "Antihistamine for seasonal allergies and hay fever",
            dosage = "10mg", mrp = 75.0, sellingPrice = 55.0, stockCount = 3,
            expiryDate = "11/2026", batchNumber = "SNF2024C7",
            pharmacyId = "p3", pharmacyName = "Arogya Medical Store", distanceKm = 2.1
        ),
        Medicine(
            id = "m8", name = "Antacid Syrup", brand = "Digene", category = "Digestive",
            description = "Relieves acidity, heartburn and indigestion",
            dosage = "200ml bottle", mrp = 110.0, sellingPrice = 88.0, stockCount = 45,
            expiryDate = "07/2026", batchNumber = "DGN2024A8",
            pharmacyId = "p2", pharmacyName = "Village Care Pharmacy", distanceKm = 1.4
        ),
        Medicine(
            id = "m9", name = "Ibuprofen", brand = "Abbott", category = "Pain Relief",
            description = "NSAID for pain, inflammation and fever",
            dosage = "400mg", mrp = 55.0, sellingPrice = 40.0, stockCount = 60,
            expiryDate = "02/2027", batchNumber = "ABT2024I9",
            pharmacyId = "p1", pharmacyName = "Sri Dhanvantari Medical", distanceKm = 0.8
        ),
        Medicine(
            id = "m10", name = "Betadine Solution", brand = "Win-Medicare", category = "First Aid",
            description = "Antiseptic solution for wound cleaning",
            dosage = "100ml", mrp = 95.0, sellingPrice = 75.0, stockCount = 20,
            expiryDate = "04/2027", batchNumber = "WM2024B10",
            pharmacyId = "p3", pharmacyName = "Arogya Medical Store", distanceKm = 2.1
        )
    )

    // ── Dummy pharmacies ───────────────────────────────────────────────────────
    val pharmacies: List<Pharmacy> = listOf(
        Pharmacy(
            id = "p1", name = "Sri Dhanvantari Medical",
            address = "No. 12, 5th Cross, Koramangala",
            areaName = "Koramangala", phone = "+91 98765 43210",
            distanceKm = 0.8, openTime = "8:00 AM", closeTime = "9:00 PM", isOpen = true,
            medicines = allMedicines.filter { it.pharmacyId == "p1" }
        ),
        Pharmacy(
            id = "p2", name = "Village Care Pharmacy",
            address = "Near Bus Stand, Anekal Main Road",
            areaName = "Anekal", phone = "+91 87654 32109",
            distanceKm = 1.4, openTime = "7:00 AM", closeTime = "10:00 PM", isOpen = true,
            medicines = allMedicines.filter { it.pharmacyId == "p2" }
        ),
        Pharmacy(
            id = "p3", name = "Arogya Medical Store",
            address = "Plot 45, Sarjapur Ring Road",
            areaName = "Sarjapur", phone = "+91 76543 21098",
            distanceKm = 2.1, openTime = "9:00 AM", closeTime = "8:00 PM", isOpen = false,
            medicines = allMedicines.filter { it.pharmacyId == "p3" }
        )
    )

    val medicines: List<Medicine> = allMedicines

    val categories = listOf("All", "Antibiotics", "Pain Relief", "Fever", "First Aid", "Vitamins", "Hydration", "Allergy", "Digestive")

    var searchQuery by mutableStateOf("")
    var selectedCategory by mutableStateOf("All")

    val filteredMedicines: List<Medicine>
        get() {
            var result = allMedicines
            if (selectedCategory != "All") {
                result = result.filter { it.category == selectedCategory }
            }
            if (searchQuery.isNotBlank()) {
                result = result.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.brand.contains(searchQuery, ignoreCase = true) ||
                    it.category.contains(searchQuery, ignoreCase = true)
                }
            }
            return result
        }

    fun getPharmacyById(id: String): Pharmacy? = pharmacies.find { it.id == id }
}
