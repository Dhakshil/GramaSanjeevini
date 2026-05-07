package com.example.grama_sanjeevini.ui.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
fun ShopDetailScreen(
    pharmacyId: String,
    onBack: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val pharmacy = viewModel.getPharmacyById(pharmacyId)
    val cs = MaterialTheme.colorScheme

    if (pharmacy == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Pharmacy not found", color = cs.onBackground.copy(alpha = 0.4f), fontFamily = Poppins)
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize().background(cs.background)) {

        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(cs.primaryContainer, cs.primary)))
                    .padding(top = 48.dp, bottom = 28.dp, start = 16.dp, end = 20.dp)
            ) {
                Column {
                    IconButton(onClick = onBack, modifier = Modifier.padding(bottom = 8.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                    Text(
                        pharmacy.name, color = Color.White, fontSize = 20.sp,
                        fontWeight = FontWeight.Bold, fontFamily = Poppins
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        pharmacy.address, color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp, fontFamily = Poppins
                    )

                    Spacer(Modifier.height(16.dp))

                    // Info chips row
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (pharmacy.isOpen) SuccessColor.copy(0.25f) else ErrorColor.copy(0.25f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    Modifier.size(7.dp).clip(CircleShape)
                                        .background(if (pharmacy.isOpen) Color(0xFF81C784) else Color(0xFFEF9A9A))
                                )
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    if (pharmacy.isOpen) "Open" else "Closed",
                                    color = Color.White, fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold, fontFamily = Poppins
                                )
                            }
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.15f)) {
                            Text(
                                "${pharmacy.openTime} – ${pharmacy.closeTime}",
                                color = Color.White, fontSize = 12.sp, fontFamily = Poppins,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.15f)) {
                            Text(
                                "${pharmacy.distanceKm} km away",
                                color = Color.White, fontSize = 12.sp, fontFamily = Poppins,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Surface(shape = RoundedCornerShape(10.dp), color = Color.White.copy(alpha = 0.2f)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_call), null,
                                tint = Color.White, modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                pharmacy.phone, color = Color.White, fontSize = 14.sp,
                                fontWeight = FontWeight.Medium, fontFamily = Poppins
                            )
                        }
                    }
                }
            }
        }

        // Section header
        item {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Medicines in Stock", fontSize = 17.sp, fontWeight = FontWeight.Bold,
                    color = cs.onBackground, fontFamily = Poppins, modifier = Modifier.weight(1f)
                )
                Text(
                    "${pharmacy.medicines.size} items", fontSize = 13.sp,
                    color = cs.primary, fontWeight = FontWeight.Medium, fontFamily = Poppins
                )
            }
        }

        if (pharmacy.medicines.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No medicines listed yet.", color = cs.onBackground.copy(alpha = 0.4f),
                        fontSize = 14.sp, fontFamily = Poppins
                    )
                }
            }
        } else {
            items(pharmacy.medicines) { med ->
                ShopMedicineRow(
                    medicine = med,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun ShopMedicineRow(medicine: Medicine, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    Card(
        modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(46.dp).clip(RoundedCornerShape(10.dp))
                    .background(cs.primary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text("💊", fontSize = 22.sp)
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
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${medicine.sellingPrice.toInt()}", fontSize = 15.sp,
                    fontWeight = FontWeight.Bold, color = cs.primary, fontFamily = Poppins
                )
                Text(
                    "₹${medicine.mrp.toInt()} MRP", fontSize = 11.sp,
                    color = cs.onBackground.copy(alpha = 0.35f), fontFamily = Poppins,
                    textDecoration = TextDecoration.LineThrough
                )
            }
            Spacer(Modifier.width(10.dp))
            val stockColor = when {
                medicine.isOutOfStock -> ErrorColor
                medicine.isLowStock   -> WarningColor
                else                  -> SuccessColor
            }
            Surface(shape = RoundedCornerShape(8.dp), color = stockColor.copy(alpha = 0.1f)) {
                Text(
                    "${medicine.stockCount}", fontSize = 12.sp, color = stockColor,
                    fontWeight = FontWeight.Bold, fontFamily = Poppins,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
