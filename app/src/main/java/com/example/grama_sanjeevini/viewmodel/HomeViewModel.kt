package com.example.grama_sanjeevini.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grama_sanjeevini.data.model.Medicine
import com.example.grama_sanjeevini.data.model.Pharmacy
import com.example.grama_sanjeevini.data.repository.LocationRepository
import com.example.grama_sanjeevini.data.repository.PharmacyRepository
import com.example.grama_sanjeevini.data.repository.UserLocation
import kotlinx.coroutines.launch

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val locationRepo  = LocationRepository(app.applicationContext)
    private val pharmacyRepo  = PharmacyRepository()

    // ── Location state ─────────────────────────────────────────────────────────
    var userLocation by mutableStateOf<UserLocation?>(null)
        private set
    var locationLoading by mutableStateOf(false)
        private set

    // ── Pharmacy loading state ──────────────────────────────────────────────────
    var pharmaciesLoading by mutableStateOf(false)
        private set

    // Hardcoded coordinates for demo/fallback pharmacies (Bengaluru area)
    private val pharmacyCoords = mapOf(
        "p1" to Pair(12.9352, 77.6245),  // Koramangala
        "p2" to Pair(12.7086, 77.6933),  // Anekal
        "p3" to Pair(12.8535, 77.7001)   // Sarjapur
    )

    // ── Dummy medicines for fallback ────────────────────────────────────────────
    private val baseMedicines = listOf(
        Medicine("m1", "Amoxicillin", "Cipla", "Antibiotics",
            "Broad-spectrum antibiotic for bacterial infections",
            "500mg", 120.0, 85.0, 24, "08/2026", "CPL2024A1", "p1", "Sri Dhanvantari Medical", 0.8),
        Medicine("m2", "Paracetamol", "GSK", "Pain Relief",
            "Relieves mild to moderate pain and fever",
            "650mg", 25.0, 18.0, 150, "12/2026", "GSK2024P2", "p1", "Sri Dhanvantari Medical", 0.8),
        Medicine("m3", "Dolo 650", "Micro Labs", "Fever",
            "Fast-acting tablet for fever and body ache",
            "650mg", 42.0, 32.0, 87, "03/2027", "MCL2024D3", "p2", "Village Care Pharmacy", 1.4),
        Medicine("m4", "Crepe Bandage Roll", "3M", "First Aid",
            "Elastic bandage for sprains and support dressing",
            "10cm x 4m", 60.0, 45.0, 30, "06/2028", "3M2024B4", "p2", "Village Care Pharmacy", 1.4),
        Medicine("m5", "Vitamin C", "HealthVit", "Vitamins",
            "Immune booster and antioxidant supplement",
            "1000mg", 250.0, 195.0, 12, "01/2027", "HV2024V5", "p3", "Arogya Medical Store", 2.1),
        Medicine("m6", "ORS Powder", "Electroral", "Hydration",
            "Oral rehydration salts for dehydration and diarrhoea",
            "21g / sachet", 30.0, 22.0, 8, "09/2026", "ELC2024O6", "p1", "Sri Dhanvantari Medical", 0.8),
        Medicine("m7", "Cetirizine", "Sanofi", "Allergy",
            "Antihistamine for seasonal allergies and hay fever",
            "10mg", 75.0, 55.0, 3, "11/2026", "SNF2024C7", "p3", "Arogya Medical Store", 2.1),
        Medicine("m8", "Antacid Syrup", "Digene", "Digestive",
            "Relieves acidity, heartburn and indigestion",
            "200ml bottle", 110.0, 88.0, 45, "07/2026", "DGN2024A8", "p2", "Village Care Pharmacy", 1.4),
        Medicine("m9", "Ibuprofen", "Abbott", "Pain Relief",
            "NSAID for pain, inflammation and fever",
            "400mg", 55.0, 40.0, 60, "02/2027", "ABT2024I9", "p1", "Sri Dhanvantari Medical", 0.8),
        Medicine("m10", "Betadine Solution", "Win-Medicare", "First Aid",
            "Antiseptic solution for wound cleaning",
            "100ml", 95.0, 75.0, 20, "04/2027", "WM2024B10", "p3", "Arogya Medical Store", 2.1)
    )

    // ── Computed pharmacies ─────────────────────────────────────────────────────
    var pharmacies by mutableStateOf(buildDummyPharmacies(null))
        private set

    var medicines by mutableStateOf(baseMedicines)
        private set

    val categories = listOf("All", "Antibiotics", "Pain Relief", "Fever", "First Aid", "Vitamins", "Hydration", "Allergy", "Digestive")

    var searchQuery by mutableStateOf("")
    var selectedCategory by mutableStateOf("All")

    val filteredMedicines: List<Medicine>
        get() {
            var result = medicines
            if (selectedCategory != "All") result = result.filter { it.category == selectedCategory }
            if (searchQuery.isNotBlank()) result = result.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.brand.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
            }
            return result
        }

    fun getPharmacyById(id: String): Pharmacy? = pharmacies.find { it.id == id }

    /** Called from UI once location permission is granted */
    fun fetchLocation() {
        if (locationLoading || userLocation != null) return
        locationLoading = true
        viewModelScope.launch {
            locationRepo.getLocationFlow().collect { loc ->
                userLocation = loc
                if (loc != null) {
                    refreshDistances(loc)
                    loadFirestorePharmacies(loc)
                }
                locationLoading = false
            }
        }
    }

    /** Try to load real pharmacies from Firestore; fall back to dummies if empty */
    private fun loadFirestorePharmacies(loc: UserLocation) {
        pharmaciesLoading = true
        viewModelScope.launch {
            val real = pharmacyRepo.getNearbyPharmacies(loc.lat, loc.lng)
            pharmacies = if (real.isNotEmpty()) {
                real
            } else {
                // No Firestore data — keep using recalculated dummy pharmacies
                buildDummyPharmacies(loc)
            }
            pharmaciesLoading = false
        }
    }

    private fun refreshDistances(loc: UserLocation) {
        val updatedMeds = baseMedicines.map { med ->
            val coords = pharmacyCoords[med.pharmacyId]
            if (coords != null) {
                val d = LocationRepository.distanceKm(loc.lat, loc.lng, coords.first, coords.second)
                med.copy(distanceKm = Math.round(d * 10) / 10.0)
            } else med
        }
        medicines = updatedMeds
        pharmacies = buildDummyPharmacies(loc)
    }

    private fun buildDummyPharmacies(loc: UserLocation?): List<Pharmacy> {
        fun dist(pid: String): Double {
            if (loc == null) return pharmacyCoords[pid]?.let { 0.0 } ?: 0.0
            val c = pharmacyCoords[pid] ?: return 0.0
            val raw = LocationRepository.distanceKm(loc.lat, loc.lng, c.first, c.second)
            return Math.round(raw * 10) / 10.0
        }
        val meds = if (loc == null) baseMedicines else medicines
        return listOf(
            Pharmacy("p1", "Sri Dhanvantari Medical", "No. 12, 5th Cross, Koramangala",
                "Koramangala", "+91 98765 43210", dist("p1"), "8:00 AM", "9:00 PM", true,
                meds.filter { it.pharmacyId == "p1" },
                pharmacyCoords["p1"]!!.first, pharmacyCoords["p1"]!!.second),
            Pharmacy("p2", "Village Care Pharmacy", "Near Bus Stand, Anekal Main Road",
                "Anekal", "+91 87654 32109", dist("p2"), "7:00 AM", "10:00 PM", true,
                meds.filter { it.pharmacyId == "p2" },
                pharmacyCoords["p2"]!!.first, pharmacyCoords["p2"]!!.second),
            Pharmacy("p3", "Arogya Medical Store", "Plot 45, Sarjapur Ring Road",
                "Sarjapur", "+91 76543 21098", dist("p3"), "9:00 AM", "8:00 PM", false,
                meds.filter { it.pharmacyId == "p3" },
                pharmacyCoords["p3"]!!.first, pharmacyCoords["p3"]!!.second)
        ).sortedBy { it.distanceKm }  // nearest first
    }
}
