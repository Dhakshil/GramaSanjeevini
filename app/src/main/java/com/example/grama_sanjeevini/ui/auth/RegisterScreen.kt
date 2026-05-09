package com.example.grama_sanjeevini.ui.auth

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grama_sanjeevini.R
import com.example.grama_sanjeevini.constants.theme.Poppins
import com.example.grama_sanjeevini.viewmodel.UserRole

@Composable
fun RegisterScreen(
    onNavigateToOtp: (phone: String, role: UserRole, name: String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    var name       by remember { mutableStateOf("") }
    var phone      by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.CUSTOMER) }
    var nameError  by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Gradient header ───────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cs.primaryContainer)
                .padding(top = 56.dp, bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = Poppins
                    )
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    "Create Account",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins
                )
                Text(
                    "Join GramaSanjeevini",
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 13.sp,
                    fontFamily = Poppins
                )
            }
        }

        // ── Scrollable card ──────────────────────────────────────
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = cs.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Text(
                    "Your details",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = cs.onBackground,
                    fontFamily = Poppins
                )
                Text(
                    "Fill in the details below to get started.",
                    fontSize = 14.sp,
                    color = cs.onBackground.copy(alpha = 0.5f),
                    fontFamily = Poppins
                )

                Spacer(Modifier.height(28.dp))

                // Role chips
                Text(
                    "I AM A",
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = cs.onBackground.copy(alpha = 0.5f),
                    fontFamily = Poppins
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    UserRole.entries.forEach { role ->
                        RegisterRoleChip(
                            role = role,
                            isSelected = selectedRole == role,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedRole = role }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Full name field
                Text(
                    "FULL NAME",
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = cs.onBackground.copy(alpha = 0.5f),
                    fontFamily = Poppins
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = "" },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. Ravi Kumar", fontFamily = Poppins) },
                    singleLine = true,
                    isError = nameError.isNotEmpty(),
                    supportingText = if (nameError.isNotEmpty()) {
                        { Text(nameError, color = cs.error, fontFamily = Poppins) }
                    } else null,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cs.primary,
                        unfocusedBorderColor = cs.onBackground.copy(alpha = 0.25f)
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Phone field
                Text(
                    "MOBILE NUMBER",
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = cs.onBackground.copy(alpha = 0.5f),
                    fontFamily = Poppins
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
                    placeholder = { Text("10-digit number", fontFamily = Poppins) },
                    prefix = {
                        Text(
                            "+91  ",
                            color = cs.onBackground,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = phoneError.isNotEmpty(),
                    supportingText = if (phoneError.isNotEmpty()) {
                        { Text(phoneError, color = cs.error, fontFamily = Poppins) }
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cs.primary,
                        unfocusedBorderColor = cs.onBackground.copy(alpha = 0.25f)
                    )
                )

                Spacer(Modifier.height(32.dp))

                // Create account button
                Button(
                    onClick = {
                        var valid = true
                        if (name.isBlank() || name.trim().length < 2) {
                            nameError = "Please enter your full name"
                            valid = false
                        }
                        if (phone.length != 10) {
                            phoneError = "Please enter a valid 10-digit number"
                            valid = false
                        }
                        if (valid) {
                            onNavigateToOtp(phone, selectedRole, name.trim())
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = cs.primary)
                ) {
                    Text(
                        "Send OTP",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = cs.onPrimary,
                        fontFamily = Poppins
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Already have account
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Already have an account? ",
                        fontSize = 14.sp,
                        color = cs.onBackground.copy(alpha = 0.5f),
                        fontFamily = Poppins
                    )
                    Text(
                        "Sign In",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = cs.primary,
                        modifier = Modifier.clickable { onNavigateToLogin() },
                        fontFamily = Poppins
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    "Your information is saved securely on Firebase.",
                    fontSize = 12.sp,
                    color = cs.onBackground.copy(alpha = 0.35f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    fontFamily = Poppins
                )
            }
        }
    }
}

@Composable
private fun RegisterRoleChip(
    role: UserRole,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) cs.primary else cs.onBackground.copy(alpha = 0.25f),
        animationSpec = tween(200), label = "border"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) cs.primary.copy(alpha = 0.08f) else Color.Transparent,
        animationSpec = tween(200), label = "bg"
    )
    val contentColor = if (isSelected) cs.primary else cs.onBackground.copy(alpha = 0.5f)

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
                if (role == UserRole.CUSTOMER) R.drawable.profile
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
            color = contentColor,
            fontFamily = Poppins
        )
    }
}