package com.example.grama_sanjeevini.ui.auth

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grama_sanjeevini.R
import com.example.grama_sanjeevini.viewmodel.UserRole

private val GreenPrimary = Color(0xFF1B5E20)
private val GreenLight   = Color(0xFFE8F5E9)

@Composable
fun LoginScreen(
    onNavigateToOtp: (phone: String, role: UserRole) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var selectedRole by remember { mutableStateOf(UserRole.CUSTOMER) }
    var phone        by remember { mutableStateOf("") }
    var phoneError   by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Green header (fixed, doesn't scroll) ──────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenPrimary)
                .padding(top = 56.dp, bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(R.drawable.gramasanjeevini_logo),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "GramaSanjeevini",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "VILLAGE PHARMACY NETWORK",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    letterSpacing = 1.5.sp
                )
            }
        }

        // ── Scrollable white card ─────────────────────────────────
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)        // ← scrollable
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Text(
                    "Welcome back",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "Sign in to your account",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )

                Spacer(Modifier.height(28.dp))

                // Role chips
                Text(
                    "I AM A",
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    UserRole.entries.forEach { role ->
                        RoleChip(
                            role = role,
                            isSelected = selectedRole == role,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedRole = role }
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                // Phone field
                Text(
                    "MOBILE NUMBER",
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        if (it.length <= 10 && it.all(Char::isDigit)) {
                            phone = it
                            phoneError = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("10-digit number") },
                    prefix = {
                        Text(
                            "+91  ",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = phoneError.isNotEmpty(),
                    supportingText = if (phoneError.isNotEmpty()) {
                        { Text(phoneError, color = MaterialTheme.colorScheme.error) }
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                    )
                )

                Spacer(Modifier.height(32.dp))

                // Login button
                Button(
                    onClick = {
                        if (phone.length != 10) {
                            phoneError = "Please enter a valid 10-digit number"
                        } else {
                            onNavigateToOtp(phone, selectedRole)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("Send OTP", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(20.dp))

                // Divider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        "  OR  ",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(20.dp))

                // New user button
                OutlinedButton(
                    onClick = onNavigateToRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, GreenPrimary
                    )
                ) {
                    Text(
                        "New User? Create Account",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = GreenPrimary
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    "By continuing you agree to our Terms of Service",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun RoleChip(
    role: UserRole,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) GreenPrimary else Color.Gray.copy(alpha = 0.3f),
        animationSpec = tween(200), label = "border"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) GreenLight else Color.Transparent,
        animationSpec = tween(200), label = "bg"
    )
    val contentColor = if (isSelected) GreenPrimary else Color.Gray

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painter = painterResource(
                if (role == UserRole.CUSTOMER) R.drawable.ic_person
                else R.drawable.ic_medical_services
            ),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = if (role == UserRole.CUSTOMER) "Customer" else "Pharmacist",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor
        )
    }
}