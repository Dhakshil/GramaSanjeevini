package com.example.grama_sanjeevini.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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

// ── Quick category definitions (using drawable icon IDs) ──────────────────────
private data class CategoryItem(val iconRes: Int, val label: String)

private val quickCategories = listOf(
    CategoryItem(R.drawable.ic_pills,      "Pills"),
    CategoryItem(R.drawable.ic_bandage,    "First Aid"),
    CategoryItem(R.drawable.ic_vitamins,   "Vitamins"),
    CategoryItem(R.drawable.ic_allergy,    "Allergy"),
    CategoryItem(R.drawable.ic_digestive,  "Digestive"),
    CategoryItem(R.drawable.ic_hydration,  "Hydration")
)

@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToShop: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    // ── Permission & image pickers ─────────────────────────────────────────────
    var pendingUploadUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.uploadReceipt(uri)
        }
    }

    val storagePermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = perms.values.any { it }
        if (granted) imagePickerLauncher.launch("image/*")
    }

    // Location permission launcher
    val locationPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) viewModel.fetchLocation()
    }

    // Check / request location permission on first composition
    LaunchedEffect(Unit) {
        val fine   = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {
            viewModel.fetchLocation()
        } else {
            locationPermLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    // Upload state side effects (dismiss after success)
    LaunchedEffect(viewModel.uploadState) {
        if (viewModel.uploadState is HomeViewModel.UploadState.Success) {
            kotlinx.coroutines.delay(4000)
            viewModel.clearUploadState()
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

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // ── Header ────────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(headerGradient)
                        .padding(top = 32.dp, bottom = 16.dp, start = 20.dp, end = 20.dp)
                ) {
                    Column {
                        // Location row — compact
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_location),
                                contentDescription = null,
                                tint = colorScheme.secondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                areaLabel,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Poppins,
                                modifier = Modifier.weight(1f)
                            )
                            if (viewModel.locationLoading || viewModel.pharmaciesLoading) {
                                CircularProgressIndicator(
                                    color = colorScheme.secondary,
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }

                        // Fake search bar
                        Surface(
                            modifier = Modifier.fillMaxWidth().clickable { onNavigateToSearch() },
                            shape = RoundedCornerShape(14.dp),
                            color = colorScheme.surface,
                            tonalElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_search),
                                    contentDescription = null,
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
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
                Column(modifier = Modifier.padding(top = 18.dp, start = 20.dp, end = 20.dp)) {
                    Text(
                        "QUICK ACCESS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = colorScheme.onBackground.copy(alpha = 0.4f),
                        fontFamily = Poppins
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        quickCategories.forEach { cat ->
                            QuickCategoryChip(
                                iconRes = cat.iconRes,
                                label   = cat.label,
                                onClick = onNavigateToSearch
                            )
                        }
                    }
                }
            }

            // ── Prescription Upload Banner ─────────────────────────────
            item {
                val uploadState = viewModel.uploadState
                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(colorScheme.primaryContainer, colorScheme.primary, SecondaryDark)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_prescription),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Upload Prescription",
                                    color = Color.White, fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold, fontFamily = Poppins
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Share with your nearest pharmacy for faster service",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp, lineHeight = 17.sp, fontFamily = Poppins
                            )
                            Spacer(Modifier.height(12.dp))

                            when (uploadState) {
                                is HomeViewModel.UploadState.Uploading -> {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            "Uploading…",
                                            color = Color.White, fontSize = 12.sp,
                                            fontFamily = Poppins
                                        )
                                    }
                                }
                                is HomeViewModel.UploadState.Success -> {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Filled.CheckCircle,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            uploadState.url,
                                            color = Color.White, fontSize = 12.sp,
                                            fontFamily = Poppins
                                        )
                                    }
                                }
                                is HomeViewModel.UploadState.Error -> {
                                    Text(
                                        uploadState.message,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 12.sp, fontFamily = Poppins
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    UploadButton { imagePickerLauncher.launch("image/*") }
                                }
                                else -> {
                                    UploadButton {
                                        // Check storage permission first
                                        val readImages = ContextCompat.checkSelfPermission(
                                            context, Manifest.permission.READ_MEDIA_IMAGES
                                        )
                                        if (readImages == PackageManager.PERMISSION_GRANTED) {
                                            imagePickerLauncher.launch("image/*")
                                        } else {
                                            storagePermLauncher.launch(
                                                arrayOf(
                                                    Manifest.permission.READ_MEDIA_IMAGES,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_prescription),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ── Nearby Pharmacies Header ──────────────────────────────
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_medical_services),
                        contentDescription = null,
                        tint = colorScheme.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
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
                    onClick  = { onNavigateToShop(pharmacy.id) }
                )
            }
        }
    }
}

// ── Upload Now button ──────────────────────────────────────────────────────────
@Composable
private fun UploadButton(onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White.copy(alpha = 0.25f),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_upload),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "Upload Now",
                color = Color.White, fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold, fontFamily = Poppins
            )
        }
    }
}

// ── Category chip ──────────────────────────────────────────────────────────────
@Composable
private fun QuickCategoryChip(iconRes: Int, label: String, onClick: () -> Unit) {
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
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                tint = colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Text(
                label, fontSize = 10.sp,
                color = colorScheme.onBackground,
                fontWeight = FontWeight.Medium, fontFamily = Poppins
            )
        }
    }
}

// ── Pharmacy card ──────────────────────────────────────────────────────────────
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
            // ── Name + distance ────────────────────────────────────
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
                    Spacer(Modifier.height(3.dp))
                    Text(
                        pharmacy.address, fontSize = 12.sp,
                        color = colorScheme.onBackground.copy(alpha = 0.5f),
                        lineHeight = 16.sp, fontFamily = Poppins
                    )
                }
                Spacer(Modifier.width(8.dp))
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
            HorizontalDivider(color = colorScheme.onBackground.copy(alpha = 0.07f))
            Spacer(Modifier.height(10.dp))

            // ── Open/closed + timings + phone ──────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: status dot + "Open"/"Closed" stacked with timings
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (pharmacy.isOpen) SuccessColor else ErrorColor)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            if (pharmacy.isOpen) "Open" else "Closed",
                            fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                            color = if (pharmacy.isOpen) SuccessColor else ErrorColor,
                            fontFamily = Poppins
                        )
                    }
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "${pharmacy.openTime} – ${pharmacy.closeTime}",
                        fontSize = 11.sp,
                        color = colorScheme.onBackground.copy(alpha = 0.45f),
                        fontFamily = Poppins
                    )
                }

                // Right: phone chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colorScheme.primary.copy(alpha = 0.07f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_call),
                            contentDescription = null,
                            tint = colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            pharmacy.phone,
                            fontSize = 12.sp,
                            color = colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins
                        )
                    }
                }
            }
        }
    }
}
