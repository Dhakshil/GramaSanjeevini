package com.example.grama_sanjeevini.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grama_sanjeevini.R
import com.example.grama_sanjeevini.constants.theme.PrimaryColor
import com.example.grama_sanjeevini.constants.theme.PrimaryDark
import com.example.grama_sanjeevini.constants.theme.SecondaryColor
import com.example.grama_sanjeevini.data.model.Pharmacy
import com.example.grama_sanjeevini.viewmodel.HomeViewModel

private val quickCategories = listOf(
    "💊" to "Pills/Fibers",
    "🩹" to "First Aid",
    "🧴" to "Vitamins",
    "🤧" to "Allergy",
    "🍃" to "Digestive",
    "💧" to "Hydration"
)

@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToShop: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7F5)),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // ── Header ────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(PrimaryDark, PrimaryColor)
                        )
                    )
                    .padding(top = 52.dp, bottom = 28.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    // Location row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_location),
                            contentDescription = null,
                            tint = Color(0xFF80E27E),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Column {
                            Text(
                                "Koramangala, Bengaluru",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Your current area",
                                color = Color.White.copy(alpha = 0.55f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Fake search bar
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToSearch() },
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        tonalElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_search),
                                contentDescription = null,
                                tint = PrimaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Search medicines, brands…",
                                color = Color.Gray.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // ── Quick Categories ──────────────────────────────────────
        item {
            Column(modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp)) {
                Text(
                    "QUICK ACCESS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = Color.Gray
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    quickCategories.forEach { (emoji, label) ->
                        QuickCategoryChip(emoji = emoji, label = label, onClick = onNavigateToSearch)
                    }
                }
            }
        }

        // ── Prescription Banner ───────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFF388E3C))
                        )
                    )
                    .clickable { /* upload prescription action */ }
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "📋 Upload Prescription",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Get medicines delivered from the nearest pharmacy",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.25f)
                        ) {
                            Text(
                                "  Upload Now →  ",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                    Text(
                        "💊",
                        fontSize = 52.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        // ── Nearby Pharmacies Header ──────────────────────────────
        item {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Nearby Pharmacies",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "${viewModel.pharmacies.size} found",
                    fontSize = 13.sp,
                    color = PrimaryColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // ── Pharmacy Cards ────────────────────────────────────────
        items(viewModel.pharmacies) { pharmacy ->
            PharmacyCard(
                pharmacy = pharmacy,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                onClick = { onNavigateToShop(pharmacy.id) }
            )
        }
    }
}

@Composable
private fun QuickCategoryChip(emoji: String, label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(emoji, fontSize = 22.sp)
            Text(
                label,
                fontSize = 10.sp,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PharmacyCard(
    pharmacy: Pharmacy,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        pharmacy.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        pharmacy.address,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )
                }
                // Distance badge
                Surface(
                    shape = CircleShape,
                    color = PrimaryColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        "${pharmacy.distanceKm} km",
                        color = PrimaryColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
            Spacer(Modifier.height(12.dp))

            // Bottom info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Open/close status + hours
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (pharmacy.isOpen) Color(0xFF4CAF50) else Color(0xFFE53935))
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (pharmacy.isOpen) "Open" else "Closed",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (pharmacy.isOpen) Color(0xFF388E3C) else Color(0xFFE53935)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "· ${pharmacy.openTime} – ${pharmacy.closeTime}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Phone chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF5F5F5)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_call),
                            contentDescription = null,
                            tint = PrimaryColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            pharmacy.phone,
                            fontSize = 11.sp,
                            color = PrimaryColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
