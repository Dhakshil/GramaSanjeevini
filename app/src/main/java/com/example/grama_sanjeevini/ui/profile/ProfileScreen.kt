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
import com.example.grama_sanjeevini.constants.theme.PrimaryColor
import com.example.grama_sanjeevini.constants.theme.PrimaryDark
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun ProfileScreen(onLogOut: () -> Unit) {
    val auth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }

    var displayName by remember { mutableStateOf("") }
    var displayPhone by remember { mutableStateOf("") }
    var displayRole by remember { mutableStateOf("Customer") }
    var isLoading by remember { mutableStateOf(true) }

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
            } catch (_: Exception) {
                displayName  = "User"
                displayPhone = auth.currentUser?.phoneNumber ?: ""
            }
        }
        isLoading = false
    }

    val initials = displayName.trim().split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2).joinToString("")
        .ifEmpty { "U" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7F5))
            .verticalScroll(rememberScrollState())
    ) {
        // ── Gradient header ────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(PrimaryDark, PrimaryColor)))
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
                        Text(initials, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(displayName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    Text(displayPhone, fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
                    Spacer(Modifier.height(8.dp))
                    Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.2f)) {
                        Text(displayRole, fontSize = 12.sp, color = Color.White,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Health Vitals Cards ────────────────────────────────────
        Text("HEALTH PROFILE", fontSize = 11.sp, fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp, color = Color.Gray,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            VitalCard("Gender", "Male", "👤", Modifier.weight(1f))
            VitalCard("Age", "28", "🎂", Modifier.weight(1f))
            VitalCard("Blood Type", "O+", "🩸", Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))

        // ── Address Card ───────────────────────────────────────────
        Text("ADDRESS", fontSize = 11.sp, fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp, color = Color.Gray,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
        Spacer(Modifier.height(8.dp))

        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                    .background(PrimaryColor.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                    Icon(painterResource(R.drawable.ic_location), null,
                        tint = PrimaryColor, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Home", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                    Text("Koramangala, Bengaluru – 560034", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Menu Items ─────────────────────────────────────────────
        Text("ACCOUNT", fontSize = 11.sp, fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp, color = Color.Gray,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
        Spacer(Modifier.height(8.dp))

        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
            Column {
                MenuRow(emoji = "📦", label = "My Orders", onClick = {})
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.Gray.copy(alpha = 0.1f))
                MenuRow(emoji = "⚙️", label = "Settings", onClick = {})
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.Gray.copy(alpha = 0.1f))
                MenuRow(emoji = "🛡️", label = "Privacy Policy", onClick = {})
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Log Out ────────────────────────────────────────────────
        Button(
            onClick = {
                auth.signOut()
                onLogOut()
            },
            modifier = Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
        ) {
            Text("Log Out", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun VitalCard(label: String, value: String, emoji: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 24.sp)
            Spacer(Modifier.height(6.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
            Text(label, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun MenuRow(emoji: String, label: String, onClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), onClick = onClick, color = Color.Transparent) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 18.sp)
            Spacer(Modifier.width(14.dp))
            Text(label, fontSize = 15.sp, color = Color(0xFF1A1A1A), modifier = Modifier.weight(1f))
            Text("›", fontSize = 20.sp, color = Color.Gray)
        }
    }
}
