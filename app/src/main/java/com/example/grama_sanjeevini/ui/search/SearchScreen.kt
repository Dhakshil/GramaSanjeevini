package com.example.grama_sanjeevini.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grama_sanjeevini.R
import com.example.grama_sanjeevini.constants.theme.*
import com.example.grama_sanjeevini.data.model.Medicine
import com.example.grama_sanjeevini.viewmodel.HomeViewModel

@Composable
fun SearchScreen(viewModel: HomeViewModel = viewModel()) {
    val cs = MaterialTheme.colorScheme

    Column(modifier = Modifier.fillMaxSize().background(cs.background)) {

        // Sticky top bar
        Surface(color = cs.surface, shadowElevation = 4.dp) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                OutlinedTextField(
                    value = viewModel.searchQuery,
                    onValueChange = { viewModel.searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Search medicines, brands…", fontSize = 14.sp, fontFamily = Poppins,
                            color = cs.onBackground.copy(alpha = 0.4f))
                    },
                    leadingIcon = {
                        Icon(painterResource(R.drawable.ic_search), null,
                            tint = cs.primary, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        if (viewModel.searchQuery.isNotEmpty())
                            IconButton(onClick = { viewModel.searchQuery = "" }) {
                                Text("✕", fontSize = 14.sp, color = cs.onBackground.copy(alpha = 0.4f))
                            }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cs.primary,
                        unfocusedBorderColor = cs.onBackground.copy(alpha = 0.15f)
                    )
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.categories.forEach { cat ->
                        FilterChip(
                            selected = viewModel.selectedCategory == cat,
                            onClick = { viewModel.selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp, fontFamily = Poppins) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = cs.primary,
                                selectedLabelColor = cs.onPrimary,
                                containerColor = Color.Transparent,
                                labelColor = cs.onBackground.copy(alpha = 0.5f)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = viewModel.selectedCategory == cat,
                                borderColor = cs.onBackground.copy(alpha = 0.15f),
                                selectedBorderColor = cs.primary
                            )
                        )
                    }
                }
            }
        }

        val results = viewModel.filteredMedicines
        if (results.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("No medicines found", fontSize = 16.sp, fontWeight = FontWeight.Medium,
                        color = cs.onBackground.copy(alpha = 0.5f), fontFamily = Poppins)
                    Text("Try a different search or category", fontSize = 13.sp,
                        color = cs.onBackground.copy(alpha = 0.3f), fontFamily = Poppins)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "${results.size} result${if (results.size != 1) "s" else ""} found",
                        fontSize = 13.sp, color = cs.onBackground.copy(alpha = 0.45f),
                        fontFamily = Poppins, modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(results) { medicine -> MedicineCard(medicine = medicine) }
            }
        }
    }
}

@Composable
fun MedicineCard(medicine: Medicine) {
    val cs = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                // Image + discount badge
                Box(
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp))
                        .background(cs.primary.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💊", fontSize = 36.sp)
                    if (medicine.discountPercent > 0) {
                        Surface(
                            modifier = Modifier.align(Alignment.TopStart).padding(4.dp),
                            shape = RoundedCornerShape(6.dp), color = ErrorColor
                        ) {
                            Text(
                                "${medicine.discountPercent}% OFF", color = Color.White, fontSize = 9.sp,
                                fontWeight = FontWeight.Bold, fontFamily = Poppins,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            medicine.name, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                            color = cs.onBackground, fontFamily = Poppins, modifier = Modifier.weight(1f)
                        )
                        Surface(shape = RoundedCornerShape(6.dp), color = cs.primary.copy(alpha = 0.1f)) {
                            Text(
                                medicine.category, fontSize = 10.sp, color = cs.primary,
                                fontWeight = FontWeight.Medium, fontFamily = Poppins,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(3.dp))
                    Text("${medicine.brand} · ${medicine.dosage}", fontSize = 12.sp,
                        color = cs.onBackground.copy(alpha = 0.45f), fontFamily = Poppins)
                    Spacer(Modifier.height(4.dp))
                    Text(medicine.description, fontSize = 11.sp,
                        color = cs.onBackground.copy(alpha = 0.5f), fontFamily = Poppins,
                        lineHeight = 15.sp, maxLines = 2)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("₹${medicine.sellingPrice.toInt()}", fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold, color = cs.primary, fontFamily = Poppins)
                        Spacer(Modifier.width(6.dp))
                        Text("₹${medicine.mrp.toInt()}", fontSize = 13.sp,
                            color = cs.onBackground.copy(alpha = 0.4f), fontFamily = Poppins,
                            textDecoration = TextDecoration.LineThrough)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = cs.onBackground.copy(alpha = 0.07f))
            Spacer(Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val stockColor = when {
                    medicine.isOutOfStock -> ErrorColor
                    medicine.isLowStock   -> WarningColor
                    else                  -> SuccessColor
                }
                MedInfoChip(when {
                    medicine.isOutOfStock -> "Out of stock"
                    medicine.isLowStock   -> "Only ${medicine.stockCount} left"
                    else                  -> "${medicine.stockCount} in stock"
                }, stockColor)
                MedInfoChip("📍 ${medicine.distanceKm} km", cs.secondary)
            }
            Spacer(Modifier.height(6.dp))
            Text("@ ${medicine.pharmacyName}", fontSize = 11.sp,
                color = cs.onBackground.copy(alpha = 0.4f),
                fontWeight = FontWeight.Medium, fontFamily = Poppins)
        }
    }
}

@Composable
fun MedInfoChip(label: String, color: Color) {
    Surface(shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.1f)) {
        Text(
            label, fontSize = 11.sp, color = color, fontWeight = FontWeight.SemiBold,
            fontFamily = Poppins,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
