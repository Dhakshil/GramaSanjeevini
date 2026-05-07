package com.example.grama_sanjeevini.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grama_sanjeevini.R
import com.example.grama_sanjeevini.constants.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    isDark: Boolean = false,
    onToggleDarkMode: () -> Unit = {},
    onLogOut: () -> Unit
) {
    val auth      = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }
    val scope     = rememberCoroutineScope()
    val cs        = MaterialTheme.colorScheme

    // Profile state
    var displayName  by remember { mutableStateOf("") }
    var displayPhone by remember { mutableStateOf("") }
    var displayRole  by remember { mutableStateOf("Customer") }
    var isLoading    by remember { mutableStateOf(true) }

    // Health vitals
    var gender    by remember { mutableStateOf("") }
    var age       by remember { mutableStateOf("") }
    var bloodType by remember { mutableStateOf("") }

    // Edit vitals dialog
    var showVitalsDialog by remember { mutableStateOf(false) }
    var isSavingVitals   by remember { mutableStateOf(false) }

    // Fetch user document from Firestore
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            try {
                val doc = firestore.collection("users").document(uid).get().await()
                displayName  = doc.getString("name")  ?: "User"
                displayPhone = doc.getString("phone") ?: (auth.currentUser?.phoneNumber ?: "")
                displayRole  = doc.getString("role")
                    ?.let { if (it == "PHARMACIST") "Pharmacist" else "Customer" } ?: "Customer"
                gender    = doc.getString("gender")    ?: ""
                age       = doc.getString("age")       ?: ""
                bloodType = doc.getString("bloodType") ?: ""
            } catch (_: Exception) {
                displayName  = "User"
                displayPhone = auth.currentUser?.phoneNumber ?: ""
            }
        }
        isLoading = false
    }

    val initials = displayName.trim().split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2).joinToString("").ifEmpty { "U" }

    // ── Edit Vitals Dialog ─────────────────────────────────────────────────────
    if (showVitalsDialog) {
        var dGender    by remember { mutableStateOf(gender) }
        var dAge       by remember { mutableStateOf(age) }
        var dBloodType by remember { mutableStateOf(bloodType) }

        val genderOptions    = listOf("Male", "Female", "Other", "Prefer not to say")
        val bloodTypeOptions = listOf("A+", "A−", "B+", "B−", "AB+", "AB−", "O+", "O−", "Unknown")
        var genderExpanded    by remember { mutableStateOf(false) }
        var bloodTypeExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isSavingVitals) showVitalsDialog = false },
            title = {
                Text(
                    "Health Profile", fontFamily = Poppins,
                    fontWeight = FontWeight.Bold, color = cs.onBackground
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Gender dropdown
                    ExposedDropdownMenuBox(
                        expanded = genderExpanded,
                        onExpandedChange = { genderExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = dGender,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            readOnly = true,
                            label = { Text("Gender", fontFamily = Poppins, fontSize = 13.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(genderExpanded) },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = cs.primary,
                                unfocusedBorderColor = cs.onBackground.copy(alpha = 0.2f)
                            )
                        )
                        ExposedDropdownMenu(genderExpanded, { genderExpanded = false }) {
                            genderOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt, fontFamily = Poppins) },
                                    onClick = { dGender = opt; genderExpanded = false }
                                )
                            }
                        }
                    }

                    // Age field
                    OutlinedTextField(
                        value = dAge,
                        onValueChange = { if (it.length <= 3 && it.all(Char::isDigit)) dAge = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Age", fontFamily = Poppins, fontSize = 13.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = cs.primary,
                            unfocusedBorderColor = cs.onBackground.copy(alpha = 0.2f)
                        )
                    )

                    // Blood type dropdown
                    ExposedDropdownMenuBox(
                        expanded = bloodTypeExpanded,
                        onExpandedChange = { bloodTypeExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = dBloodType,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            readOnly = true,
                            label = { Text("Blood Type", fontFamily = Poppins, fontSize = 13.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(bloodTypeExpanded) },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = cs.primary,
                                unfocusedBorderColor = cs.onBackground.copy(alpha = 0.2f)
                            )
                        )
                        ExposedDropdownMenu(bloodTypeExpanded, { bloodTypeExpanded = false }) {
                            bloodTypeOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt, fontFamily = Poppins) },
                                    onClick = { dBloodType = opt; bloodTypeExpanded = false }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val uid = auth.currentUser?.uid ?: return@Button
                        isSavingVitals = true
                        scope.launch {
                            try {
                                // Use set with merge=true so it works even if fields don't exist yet
                                firestore.collection("users").document(uid)
                                    .set(
                                        mapOf(
                                            "gender"    to dGender,
                                            "age"       to dAge,
                                            "bloodType" to dBloodType
                                        ),
                                        SetOptions.merge()
                                    ).await()
                                gender    = dGender
                                age       = dAge
                                bloodType = dBloodType
                            } catch (_: Exception) { }
                            isSavingVitals   = false
                            showVitalsDialog = false
                        }
                    },
                    enabled = !isSavingVitals,
                    colors = ButtonDefaults.buttonColors(containerColor = cs.primary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isSavingVitals) {
                        CircularProgressIndicator(
                            color = cs.onPrimary,
                            modifier = Modifier.size(18.dp), strokeWidth = 2.dp
                        )
                    } else {
                        Text("Save", fontFamily = Poppins, fontWeight = FontWeight.SemiBold,
                            color = cs.onPrimary)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showVitalsDialog = false }, enabled = !isSavingVitals) {
                    Text("Cancel", color = cs.onBackground.copy(alpha = 0.5f), fontFamily = Poppins)
                }
            },
            containerColor = cs.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // ── Main screen ────────────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Gradient header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(cs.primaryContainer, cs.primary)))
                .padding(top = 56.dp, bottom = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(40.dp))
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initials, fontSize = 30.sp, fontWeight = FontWeight.Bold,
                            color = Color.White, fontFamily = Poppins)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(displayName, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                        color = Color.White, fontFamily = Poppins)
                    Spacer(Modifier.height(4.dp))
                    Text(displayPhone, fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f), fontFamily = Poppins)
                    Spacer(Modifier.height(8.dp))
                    Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.2f)) {
                        Text(
                            displayRole, fontSize = 12.sp, color = Color.White,
                            fontWeight = FontWeight.Medium, fontFamily = Poppins,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Health Vitals Section
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "HEALTH PROFILE",
                fontSize = 11.sp, fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp, color = cs.onBackground.copy(alpha = 0.4f),
                fontFamily = Poppins, modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { showVitalsDialog = true },
                modifier = Modifier.size(32.dp)
            ) {
                Text("✏️", fontSize = 16.sp)
            }
        }
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            VitalCard("Gender",     gender.ifBlank { "—" },    "👤", Modifier.weight(1f))
            VitalCard("Age",        if (age.isNotBlank()) "${age}y" else "—", "🎂", Modifier.weight(1f))
            VitalCard("Blood Type", bloodType.ifBlank { "—" }, "🩸", Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))

        // Address Card
        Text(
            "ADDRESS", fontSize = 11.sp, fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp, color = cs.onBackground.copy(alpha = 0.4f),
            fontFamily = Poppins,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = cs.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                        .background(cs.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painterResource(R.drawable.ic_location), null,
                        tint = cs.primary, modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Home", fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                        color = cs.onBackground, fontFamily = Poppins)
                    Text("Location detected via GPS", fontSize = 12.sp,
                        color = cs.onBackground.copy(alpha = 0.5f), fontFamily = Poppins)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Account menu
        Text(
            "ACCOUNT", fontSize = 11.sp, fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp, color = cs.onBackground.copy(alpha = 0.4f),
            fontFamily = Poppins,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = cs.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                MenuRow("📦", "My Orders") {}
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = cs.onBackground.copy(alpha = 0.06f)
                )
                // Dark / Light Mode toggle row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isDark) "🌙" else "☀️", fontSize = 18.sp)
                    Spacer(Modifier.width(14.dp))
                    Text(
                        if (isDark) "Dark Mode" else "Light Mode",
                        fontSize = 15.sp, color = cs.onBackground,
                        modifier = Modifier.weight(1f), fontFamily = Poppins
                    )
                    Switch(
                        checked = isDark,
                        onCheckedChange = { onToggleDarkMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = cs.onPrimary,
                            checkedTrackColor = cs.primary
                        )
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = cs.onBackground.copy(alpha = 0.06f)
                )
                MenuRow("⚙️", "Settings") {}
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = cs.onBackground.copy(alpha = 0.06f)
                )
                MenuRow("🛡️", "Privacy Policy") {}
            }
        }

        Spacer(Modifier.height(20.dp))

        // Log Out
        Button(
            onClick = { auth.signOut(); onLogOut() },
            modifier = Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ErrorColor)
        ) {
            Text(
                "Log Out", fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                color = Color.White, fontFamily = Poppins
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun VitalCard(label: String, value: String, emoji: String, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    Card(
        modifier = modifier, shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 24.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                value, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                color = if (value == "—") cs.onBackground.copy(alpha = 0.3f) else cs.primary,
                fontFamily = Poppins
            )
            Text(label, fontSize = 11.sp, color = cs.onBackground.copy(alpha = 0.45f), fontFamily = Poppins)
        }
    }
}

@Composable
private fun MenuRow(emoji: String, label: String, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Surface(modifier = Modifier.fillMaxWidth(), onClick = onClick, color = Color.Transparent) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 18.sp)
            Spacer(Modifier.width(14.dp))
            Text(
                label, fontSize = 15.sp, color = cs.onBackground,
                modifier = Modifier.weight(1f), fontFamily = Poppins
            )
            Text("›", fontSize = 20.sp, color = cs.onBackground.copy(alpha = 0.3f))
        }
    }
}
