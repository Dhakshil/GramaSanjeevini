package com.example.grama_sanjeevini.ui.pharmacist

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.grama_sanjeevini.R
import com.example.grama_sanjeevini.constants.AppStrings
import com.example.grama_sanjeevini.constants.theme.*
import com.example.grama_sanjeevini.data.model.Prescription
import com.example.grama_sanjeevini.viewmodel.PharmacistViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PrescriptionsScreen(viewModel: PharmacistViewModel) {
    val cs = MaterialTheme.colorScheme
    var selectedPrescription by remember { mutableStateOf<Prescription?>(null) }
    var snackMessage by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(cs.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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
                            "Prescriptions",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 13.sp, fontFamily = Poppins
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Uploaded Receipts",
                                color = Color.White, fontSize = 22.sp,
                                fontWeight = FontWeight.Bold, fontFamily = Poppins,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    "${viewModel.prescriptions.count { it.status == "pending" }} pending",
                                    color = Color.White, fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold, fontFamily = Poppins,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ── Error ─────────────────────────────────────────────────
            if (viewModel.prescriptionsError.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp).fillMaxWidth(),
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
                                viewModel.prescriptionsError,
                                fontSize = 13.sp, color = cs.error, fontFamily = Poppins
                            )
                        }
                    }
                }
            }

            // ── Empty state ───────────────────────────────────────────
            if (viewModel.prescriptions.isEmpty() && viewModel.prescriptionsError.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(R.drawable.ic_prescription),
                                contentDescription = null,
                                tint = cs.onBackground.copy(alpha = 0.2f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "No prescriptions yet",
                                fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                                color = cs.onBackground.copy(alpha = 0.4f), fontFamily = Poppins
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Uploaded prescriptions from customers\nwill appear here.",
                                fontSize = 13.sp, color = cs.onBackground.copy(alpha = 0.3f),
                                fontFamily = Poppins,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // ── Prescription list ─────────────────────────────────────
            items(viewModel.prescriptions, key = { it.id }) { prescription ->
                PrescriptionCard(
                    prescription = prescription,
                    isUpdating   = viewModel.prescriptionUpdating == prescription.id,
                    modifier     = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                    onViewImage  = { selectedPrescription = it },
                    onMarkReviewed = {
                        viewModel.updatePrescriptionStatus(it.id, "reviewed")
                        snackMessage = AppStrings.INFO_RECEIPT_REVIEWED
                    }
                )
            }
        }

        // ── Snackbar ──────────────────────────────────────────────────
        if (snackMessage.isNotEmpty()) {
            LaunchedEffect(snackMessage) {
                kotlinx.coroutines.delay(3000)
                snackMessage = ""
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = cs.primary,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = cs.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(snackMessage, color = cs.onPrimary, fontSize = 13.sp, fontFamily = Poppins)
                    }
                }
            }
        }
    }

    // ── Full-screen image dialog ───────────────────────────────────────────────
    selectedPrescription?.let { p ->
        Dialog(
            onDismissRequest = { selectedPrescription = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.92f))
                    .clickable { selectedPrescription = null },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = p.receiptUrl,
                        contentDescription = "Prescription by ${p.userName}",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "From: ${p.userName} · ${formatTime(p.uploadedAt)}",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp, fontFamily = Poppins
                    )
                    Text(
                        "Tap anywhere to close",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 11.sp, fontFamily = Poppins
                    )
                }
            }
        }
    }
}

@Composable
private fun PrescriptionCard(
    prescription: Prescription,
    isUpdating: Boolean,
    modifier: Modifier = Modifier,
    onViewImage: (Prescription) -> Unit,
    onMarkReviewed: (Prescription) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val statusColor = when (prescription.status) {
        "reviewed"  -> SuccessColor
        "fulfilled" -> cs.primary
        else        -> WarningColor
    }
    val statusLabel = when (prescription.status) {
        "reviewed"  -> "Reviewed"
        "fulfilled" -> "Fulfilled"
        else        -> "Pending"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top row: user info + status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(cs.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        prescription.userName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        color = cs.primary, fontFamily = Poppins
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        prescription.userName,
                        fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                        color = cs.onBackground, fontFamily = Poppins
                    )
                    Text(
                        prescription.userPhone.ifEmpty { "No phone" },
                        fontSize = 12.sp, color = cs.onBackground.copy(alpha = 0.5f),
                        fontFamily = Poppins
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        statusLabel,
                        fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                        color = statusColor, fontFamily = Poppins,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Prescription image preview
            AsyncImage(
                model = prescription.receiptUrl,
                contentDescription = "Receipt",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onViewImage(prescription) },
                placeholder = painterResource(R.drawable.ic_prescription),
                error = painterResource(R.drawable.ic_prescription)
            )

            Spacer(Modifier.height(10.dp))

            // Time
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    contentDescription = null,
                    tint = cs.onBackground.copy(alpha = 0.4f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Uploaded ${formatTime(prescription.uploadedAt)}",
                    fontSize = 12.sp,
                    color = cs.onBackground.copy(alpha = 0.45f),
                    fontFamily = Poppins
                )
            }

            if (prescription.status == "pending") {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = cs.onBackground.copy(alpha = 0.07f))
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onViewImage(prescription) },
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, cs.primary)
                    ) {
                        Text(
                            "View Full",
                            fontSize = 13.sp, color = cs.primary,
                            fontWeight = FontWeight.Medium, fontFamily = Poppins
                        )
                    }
                    Button(
                        onClick = { onMarkReviewed(prescription) },
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessColor),
                        enabled = !isUpdating
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White, strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Mark Reviewed",
                                fontSize = 12.sp, color = Color.White,
                                fontWeight = FontWeight.SemiBold, fontFamily = Poppins
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(epochMs: Long): String {
    if (epochMs == 0L) return "Just now"
    return try {
        SimpleDateFormat("dd MMM, h:mm a", Locale.getDefault()).format(Date(epochMs))
    } catch (_: Exception) { "–" }
}
