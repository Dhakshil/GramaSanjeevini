package com.example.grama_sanjeevini.ui.pharmacist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grama_sanjeevini.constants.theme.PrimaryColor
import com.example.grama_sanjeevini.constants.theme.PrimaryDark
import com.example.grama_sanjeevini.data.model.Medicine
import com.example.grama_sanjeevini.viewmodel.PharmacistViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddListingScreen(
    onBack: () -> Unit,
    viewModel: PharmacistViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    var name        by remember { mutableStateOf("") }
    var brand       by remember { mutableStateOf("") }
    var category    by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dosage      by remember { mutableStateOf("") }
    var mrp         by remember { mutableStateOf("") }
    var price       by remember { mutableStateOf("") }
    var stock       by remember { mutableStateOf("") }
    var batch       by remember { mutableStateOf("") }
    var expiry      by remember { mutableStateOf("") }

    val categories = listOf("Antibiotics", "Pain Relief", "Fever", "First Aid", "Vitamins", "Hydration", "Allergy", "Digestive", "Other")
    var categoryExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F7F5))) {

        // Header
        Box(modifier = Modifier.fillMaxWidth()
            .background(Brush.verticalGradient(listOf(PrimaryDark, PrimaryColor)))
            .padding(top = 48.dp, bottom = 20.dp, start = 4.dp, end = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
                Spacer(Modifier.width(4.dp))
                Column {
                    Text("Add New Listing", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Fill in medicine details below", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                }
            }
        }

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp)) {

            // Medicine Details
            SectionLabel("MEDICINE DETAILS")
            AddField("Medicine Name", name, KeyboardType.Text, KeyboardCapitalization.Words) { name = it }
            AddField("Brand", brand, KeyboardType.Text, KeyboardCapitalization.Words) { brand = it }

            // Category dropdown
            Text("Category", fontSize = 11.sp, letterSpacing = 1.sp, color = Color.Gray,
                modifier = Modifier.padding(bottom = 6.dp, top = 4.dp))
            ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = it }) {
                OutlinedTextField(
                    value = category, onValueChange = {},
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    readOnly = true, label = { Text("Select category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor, unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f))
                )
                ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                    categories.forEach { cat ->
                        DropdownMenuItem(text = { Text(cat) }, onClick = { category = cat; categoryExpanded = false })
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            AddField("Dosage (e.g. 500mg, 200ml)", dosage, KeyboardType.Text) { dosage = it }
            AddField("Description", description, KeyboardType.Text, KeyboardCapitalization.Sentences) { description = it }

            Spacer(Modifier.height(8.dp))

            // Pricing
            SectionLabel("PRICING")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    AddField("MRP (₹)", mrp, KeyboardType.Decimal) { mrp = it }
                }
                Column(modifier = Modifier.weight(1f)) {
                    AddField("Selling Price (₹)", price, KeyboardType.Decimal) { price = it }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Stock & Batch
            SectionLabel("STOCK & BATCH")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    AddField("Quantity", stock, KeyboardType.Number) { stock = it }
                }
                Column(modifier = Modifier.weight(1f)) {
                    AddField("Batch Number", batch, KeyboardType.Text, KeyboardCapitalization.Characters) { batch = it }
                }
            }
            AddField("Expiry Date (MM/YYYY)", expiry, KeyboardType.Text) { expiry = it }

            Spacer(Modifier.height(24.dp))

            // Save button
            Button(
                onClick = {
                    val medicine = Medicine(
                        id = "", name = name.trim(), brand = brand.trim(),
                        category = category.ifBlank { "Other" },
                        description = description.trim(), dosage = dosage.trim(),
                        mrp = mrp.toDoubleOrNull() ?: 0.0,
                        sellingPrice = price.toDoubleOrNull() ?: 0.0,
                        stockCount = stock.toIntOrNull() ?: 0,
                        expiryDate = expiry.trim(), batchNumber = batch.trim(),
                        pharmacyId = "myPharmacy", pharmacyName = "My Pharmacy", distanceKm = 0.0
                    )
                    scope.launch {
                        viewModel.addMedicine(medicine)
                        onBack()
                    }
                },
                enabled = !viewModel.isSaving && name.isNotBlank() && price.isNotBlank() && stock.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                if (viewModel.isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text("Save Listing", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionLabel(label: String) {
    Text(label, fontSize = 11.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight.Bold,
        color = Color.Gray, modifier = Modifier.padding(top = 8.dp, bottom = 10.dp))
}

@Composable
private fun AddField(
    label: String,
    value: String,
    keyboardType: KeyboardType,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
        label = { Text(label, fontSize = 13.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, capitalization = capitalization),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryColor, unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f))
    )
}
