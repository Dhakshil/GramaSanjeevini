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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
                    .padding(top = 52.dp, bottom = 28.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    Text(
                        "Pharmacist Dashboard",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp, fontFamily = Poppins
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "My Store",
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
                    label = "Total Items",
                    value = viewModel.totalItems.toString(),
                    emoji = "📦",
                    accentColor = cs.primary,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "Low Stock",
                    value = viewModel.lowStockCount.toString(),
                    emoji = "⚠️",
                    accentColor = WarningColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ── Inventory header + Add button ─────────────────────────
        item {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Inventory", fontSize = 17.sp, fontWeight = FontWeight.Bold,
                    color = cs.onBackground, fontFamily = Poppins
                )
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
                    Text(
                        "No inventory yet. Tap '+ Add New' to begin.",
                        color = cs.onBackground.copy(alpha = 0.4f),
                        fontSize = 14.sp, fontFamily = Poppins
                    )
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
    emoji: String,
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
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(emoji, fontSize = 26.sp)
                Surface(shape = RoundedCornerShape(8.dp), color = accentColor.copy(alpha = 0.1f)) {
                    Text(
                        value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                        color = accentColor, fontFamily = Poppins,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                label, fontSize = 13.sp, color = cs.onBackground.copy(alpha = 0.5f),
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
                    Text("💊", fontSize = 22.sp)
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
