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
import com.example.grama_sanjeevini.constants.AppStrings
import com.example.grama_sanjeevini.constants.theme.*
import com.example.grama_sanjeevini.data.model.Medicine
import com.example.grama_sanjeevini.viewmodel.PharmacistViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddListingScreen(
    onBack: () -> Unit,
    viewModel: PharmacistViewModel = viewModel()
) {
    val cs    = MaterialTheme.colorScheme
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

    // Validation errors
    var nameError  by remember { mutableStateOf("") }
    var priceError by remember { mutableStateOf("") }
    var stockError by remember { mutableStateOf("") }

    val categories = listOf(
        "Antibiotics", "Pain Relief", "Fever", "First Aid",
        "Vitamins", "Hydration", "Allergy", "Digestive", "Other"
    )
    var categoryExpanded by remember { mutableStateOf(false) }

    // Watch for save success to navigate back
    LaunchedEffect(viewModel.isSaving) {
        // When isSaving flips to false and there's no error, we just saved
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background)
    ) {

        // ── Header ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(cs.primaryContainer, cs.primary)))
                .padding(top = 48.dp, bottom = 20.dp, start = 4.dp, end = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
                Spacer(Modifier.width(4.dp))
                Column {
                    Text(
                        "Add New Listing", color = Color.White, fontSize = 18.sp,
                        fontWeight = FontWeight.Bold, fontFamily = Poppins
                    )
                    Text(
                        "Fill in medicine details below",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp, fontFamily = Poppins
                    )
                }
            }
        }

        // ── Form ─────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {

            // Save error banner (from ViewModel)
            if (viewModel.saveError.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cs.error.copy(alpha = 0.08f))
                ) {
                    Text(
                        viewModel.saveError,
                        fontSize = 13.sp, color = cs.error, fontFamily = Poppins,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            // Medicine Details section
            SectionLabel("MEDICINE DETAILS")
            AddField(
                label = "Medicine Name *",
                value = name,
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words,
                error = nameError,
                onValueChange = { name = it; nameError = "" }
            )
            AddField("Brand", brand, KeyboardType.Text, KeyboardCapitalization.Words) { brand = it }

            // Category dropdown
            Text(
                "CATEGORY", fontSize = 11.sp, letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold,
                color = cs.onBackground.copy(alpha = 0.4f), fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 6.dp, top = 4.dp)
            )
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    readOnly = true,
                    label = { Text("Select category", fontFamily = Poppins, fontSize = 13.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cs.primary,
                        unfocusedBorderColor = cs.onBackground.copy(alpha = 0.2f)
                    )
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat, fontFamily = Poppins) },
                            onClick = { category = cat; categoryExpanded = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            AddField("Dosage (e.g. 500mg, 200ml)", dosage, KeyboardType.Text) { dosage = it }
            AddField("Description", description, KeyboardType.Text, KeyboardCapitalization.Sentences) { description = it }

            Spacer(Modifier.height(8.dp))

            // Pricing section
            SectionLabel("PRICING")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    AddField("MRP (₹)", mrp, KeyboardType.Decimal) { mrp = it }
                }
                Column(modifier = Modifier.weight(1f)) {
                    AddField(
                        label = "Selling Price (₹) *",
                        value = price,
                        keyboardType = KeyboardType.Decimal,
                        error = priceError,
                        onValueChange = { price = it; priceError = "" }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Stock & Batch section
            SectionLabel("STOCK & BATCH")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    AddField(
                        label = "Quantity *",
                        value = stock,
                        keyboardType = KeyboardType.Number,
                        error = stockError,
                        onValueChange = { stock = it; stockError = "" }
                    )
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
                    // Validate
                    var valid = true
                    if (name.isBlank()) {
                        nameError = "Medicine name is required"
                        valid = false
                    }
                    if (price.isBlank() || price.toDoubleOrNull() == null) {
                        priceError = "Enter a valid price"
                        valid = false
                    }
                    if (stock.isBlank() || stock.toIntOrNull() == null) {
                        stockError = "Enter a valid quantity"
                        valid = false
                    }
                    if (valid) {
                        viewModel.clearSaveError()
                        val medicine = Medicine(
                            id = "", name = name.trim(), brand = brand.trim(),
                            category = category.ifBlank { "Other" },
                            description = description.trim(), dosage = dosage.trim(),
                            mrp = mrp.toDoubleOrNull() ?: 0.0,
                            sellingPrice = price.toDoubleOrNull() ?: 0.0,
                            stockCount = stock.toIntOrNull() ?: 0,
                            expiryDate = expiry.trim(), batchNumber = batch.trim(),
                            pharmacyId = viewModel.storeId,
                            pharmacyName = viewModel.storeName,
                            distanceKm = 0.0
                        )
                        scope.launch {
                            viewModel.addMedicine(medicine)
                            // Navigate back only if no error
                            if (viewModel.saveError.isEmpty()) {
                                onBack()
                            }
                        }
                    }
                },
                enabled = !viewModel.isSaving,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = cs.primary)
            ) {
                if (viewModel.isSaving) {
                    CircularProgressIndicator(
                        color = cs.onPrimary,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Save Listing", fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold, fontFamily = Poppins,
                        color = cs.onPrimary
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionLabel(label: String) {
    val cs = MaterialTheme.colorScheme
    Text(
        label, fontSize = 11.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight.Bold,
        color = cs.onBackground.copy(alpha = 0.4f), fontFamily = Poppins,
        modifier = Modifier.padding(top = 8.dp, bottom = 10.dp)
    )
}

@Composable
private fun AddField(
    label: String,
    value: String,
    keyboardType: KeyboardType,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    error: String = "",
    onValueChange: (String) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
        label = { Text(label, fontSize = 13.sp, fontFamily = Poppins) },
        singleLine = true,
        isError = error.isNotEmpty(),
        supportingText = if (error.isNotEmpty()) {
            { Text(error, color = cs.error, fontSize = 11.sp, fontFamily = Poppins) }
        } else null,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            capitalization = capitalization
        ),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = cs.primary,
            unfocusedBorderColor = cs.onBackground.copy(alpha = 0.2f)
        )
    )
}
