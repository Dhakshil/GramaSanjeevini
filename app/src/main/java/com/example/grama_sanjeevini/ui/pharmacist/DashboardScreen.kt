package com.example.grama_sanjeevini.ui.pharmacist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grama_sanjeevini.R
import com.example.grama_sanjeevini.constants.theme.*
import com.example.grama_sanjeevini.data.model.Medicine
import com.example.grama_sanjeevini.viewmodel.PharmacistViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun DashboardScreen(
    onAddListing: () -> Unit,
    viewModel: PharmacistViewModel = viewModel()
) {
    val cs = MaterialTheme.colorScheme

    if (viewModel.storeLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = cs.primary)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(cs.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ── Header ────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(cs.primaryContainer, cs.primary)))
                    .padding(top = 48.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    Text(
                        "Pharmacist Dashboard",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp, fontFamily = Poppins
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        viewModel.storeName,
                        color = Color.White, fontSize = 22.sp,
                        fontWeight = FontWeight.Bold, fontFamily = Poppins
                    )
                    val phone = FirebaseAuth.getInstance().currentUser?.phoneNumber ?: ""
                    if (phone.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            phone,
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 13.sp, fontFamily = Poppins
                        )
                    }
                    // Store error
                    if (viewModel.storeError.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = cs.error.copy(alpha = 0.15f)
                        ) {
                            Text(
                                viewModel.storeError,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp, fontFamily = Poppins,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // ── Metric cards ──────────────────────────────────────────
        item {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    label       = "Total Items",
                    value       = viewModel.totalItems.toString(),
                    iconRes     = R.drawable.ic_inventory,
                    accentColor = cs.primary,
                    modifier    = Modifier.weight(1f)
                )
                MetricCard(
                    label       = "Low Stock",
                    value       = viewModel.lowStockCount.toString(),
                    iconRes     = R.drawable.ic_info,
                    accentColor = WarningColor,
                    modifier    = Modifier.weight(1f)
                )
                MetricCard(
                    label       = "Pending Rx",
                    value       = viewModel.prescriptions.count { it.status == "pending" }.toString(),
                    iconRes     = R.drawable.ic_prescription,
                    accentColor = cs.tertiary,
                    modifier    = Modifier.weight(1f)
                )
            }
        }

        // ── Save error banner ─────────────────────────────────────
        if (viewModel.saveError.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cs.error.copy(alpha = 0.08f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_info),
                            contentDescription = null,
                            tint = cs.error,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            viewModel.saveError,
                            fontSize = 13.sp, color = cs.error, fontFamily = Poppins,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // ── Inventory header + Add button ─────────────────────────
        item {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_inventory),
                        contentDescription = null,
                        tint = cs.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Inventory", fontSize = 17.sp, fontWeight = FontWeight.Bold,
                        color = cs.onBackground, fontFamily = Poppins
                    )
                }
                Button(
                    onClick = onAddListing,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = cs.primary),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "+ Add New", fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold, fontFamily = Poppins,
                        color = cs.onPrimary
                    )
                }
            }
        }

        // ── Inventory list ────────────────────────────────────────
        if (viewModel.inventory.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(R.drawable.ic_inventory),
                            contentDescription = null,
                            tint = cs.onBackground.copy(alpha = 0.2f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "No inventory yet.",
                            color = cs.onBackground.copy(alpha = 0.5f),
                            fontSize = 14.sp, fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Tap '+ Add New' to add your first medicine.",
                            color = cs.onBackground.copy(alpha = 0.35f),
                            fontSize = 12.sp, fontFamily = Poppins
                        )
                    }
                }
            }
        } else {
            items(viewModel.inventory) { medicine ->
                InventoryRow(
                    medicine = medicine,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    iconRes: Int,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = accentColor.copy(alpha = 0.1f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(iconRes),
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                    color = accentColor, fontFamily = Poppins
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                label, fontSize = 12.sp, color = cs.onBackground.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium, fontFamily = Poppins
            )
        }
    }
}

@Composable
private fun InventoryRow(medicine: Medicine, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Medicine icon bubble
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = cs.primary.copy(alpha = 0.08f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_pills),
                        contentDescription = null,
                        tint = cs.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    medicine.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                    color = cs.onBackground, fontFamily = Poppins
                )
                Text(
                    "${medicine.brand} · ${medicine.dosage}", fontSize = 12.sp,
                    color = cs.onBackground.copy(alpha = 0.45f), fontFamily = Poppins
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Batch: ${medicine.batchNumber}  ·  Exp: ${medicine.expiryDate}",
                    fontSize = 11.sp, color = cs.onBackground.copy(alpha = 0.35f), fontFamily = Poppins
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${medicine.sellingPrice.toInt()}/unit", fontSize = 13.sp,
                    fontWeight = FontWeight.Bold, color = cs.primary, fontFamily = Poppins
                )
                Spacer(Modifier.height(6.dp))
                val stockColor = when {
                    medicine.isOutOfStock -> ErrorColor
                    medicine.isLowStock   -> WarningColor
                    else                  -> SuccessColor
                }
                Surface(shape = RoundedCornerShape(8.dp), color = stockColor.copy(alpha = 0.12f)) {
                    Text(
                        "${medicine.stockCount} pcs", fontSize = 12.sp, color = stockColor,
                        fontWeight = FontWeight.Bold, fontFamily = Poppins,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
