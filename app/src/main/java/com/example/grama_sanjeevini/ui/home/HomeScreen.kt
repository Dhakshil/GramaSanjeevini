package com.example.grama_sanjeevini.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grama_sanjeevini.R
import com.example.grama_sanjeevini.constants.theme.*
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
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    // Location permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) viewModel.fetchLocation()
    }

    // Check / request permission on first composition
    LaunchedEffect(Unit) {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {
            viewModel.fetchLocation()
        } else {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    val areaLabel = when {
        viewModel.locationLoading -> "Locating…"
        viewModel.userLocation != null -> viewModel.userLocation!!.areaName
        else -> "Location unavailable"
    }

    val headerGradient = Brush.verticalGradient(
        listOf(colorScheme.primaryContainer, colorScheme.primary)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // ── Header ────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerGradient)
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
                            tint = colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Column {
                            Text(
                                areaLabel,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Poppins
                            )
                            Text(
                                "Showing nearest pharmacies",
                                color = Color.White.copy(alpha = 0.55f),
                                fontSize = 11.sp,
                                fontFamily = Poppins
                            )
                        }
                        if (viewModel.locationLoading || viewModel.pharmaciesLoading) {
                            Spacer(Modifier.width(8.dp))
                            CircularProgressIndicator(
                                color = colorScheme.secondary,
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }

                    // Fake search bar
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { onNavigateToSearch() },
                        shape = RoundedCornerShape(16.dp),
                        color = colorScheme.surface,
                        tonalElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_search),
                                contentDescription = null,
                                tint = colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Search medicines, brands…",
                                color = colorScheme.onBackground.copy(alpha = 0.4f),
                                fontSize = 14.sp,
                                fontFamily = Poppins
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
                    color = colorScheme.onBackground.copy(alpha = 0.4f),
                    fontFamily = Poppins
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
                            listOf(colorScheme.primaryContainer, colorScheme.primary, SecondaryDark)
                        )
                    )
                    .clickable { }
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
                            color = Color.White, fontSize = 17.sp,
                            fontWeight = FontWeight.Bold, fontFamily = Poppins
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Get medicines delivered from the nearest pharmacy",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp, lineHeight = 17.sp, fontFamily = Poppins
                        )
                        Spacer(Modifier.height(12.dp))
                        Surface(shape = RoundedCornerShape(10.dp), color = Color.White.copy(alpha = 0.25f)) {
                            Text(
                                "  Upload Now →  ",
                                color = Color.White, fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold, fontFamily = Poppins,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                    Text("💊", fontSize = 52.sp, modifier = Modifier.padding(start = 8.dp))
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
                    fontSize = 17.sp, fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground, fontFamily = Poppins,
                    modifier = Modifier.weight(1f)
                )
                if (viewModel.pharmaciesLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "${viewModel.pharmacies.size} found",
                        fontSize = 13.sp, color = colorScheme.primary,
                        fontWeight = FontWeight.Medium, fontFamily = Poppins
                    )
                }
            }
        }

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
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(emoji, fontSize = 22.sp)
            Text(
                label, fontSize = 10.sp,
                color = colorScheme.onBackground,
                fontWeight = FontWeight.Medium, fontFamily = Poppins
            )
        }
    }
}

@Composable
private fun PharmacyCard(pharmacy: Pharmacy, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        pharmacy.name, fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onBackground, fontFamily = Poppins
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        pharmacy.address, fontSize = 12.sp,
                        color = colorScheme.onBackground.copy(alpha = 0.5f),
                        lineHeight = 16.sp, fontFamily = Poppins
                    )
                }
                Surface(shape = CircleShape, color = colorScheme.primary.copy(alpha = 0.1f)) {
                    Text(
                        "${pharmacy.distanceKm} km",
                        color = colorScheme.primary, fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold, fontFamily = Poppins,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = colorScheme.onBackground.copy(alpha = 0.08f))
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(8.dp).clip(CircleShape)
                            .background(if (pharmacy.isOpen) SuccessColor else ErrorColor)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (pharmacy.isOpen) "Open" else "Closed",
                        fontSize = 12.sp, fontWeight = FontWeight.Medium,
                        color = if (pharmacy.isOpen) SuccessColor else ErrorColor,
                        fontFamily = Poppins
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "· ${pharmacy.openTime} – ${pharmacy.closeTime}",
                        fontSize = 12.sp,
                        color = colorScheme.onBackground.copy(alpha = 0.45f),
                        fontFamily = Poppins
                    )
                }
                Surface(shape = RoundedCornerShape(8.dp), color = colorScheme.background) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_call), contentDescription = null,
                            tint = colorScheme.primary, modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            pharmacy.phone, fontSize = 11.sp,
                            color = colorScheme.primary,
                            fontWeight = FontWeight.Medium, fontFamily = Poppins
                        )
                    }
                }
            }
        }
    }
}
