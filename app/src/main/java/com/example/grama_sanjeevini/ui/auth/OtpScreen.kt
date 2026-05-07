package com.example.grama_sanjeevini.ui.auth

import androidx.activity.ComponentActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grama_sanjeevini.constants.theme.Poppins
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@Composable
fun OtpScreen(
    phone: String,
    onOtpVerified: (FirebaseUser) -> Unit
) {
    val cs = MaterialTheme.colorScheme

    // ── State ─────────────────────────────────────────────────────
    var otpValue       by remember { mutableStateOf("") }
    var isLoading      by remember { mutableStateOf(false) }
    var errorMessage   by remember { mutableStateOf("") }
    var resendTimer    by remember { mutableStateOf(30) }
    var canResend      by remember { mutableStateOf(false) }
    var verificationId by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    val auth           = remember { FirebaseAuth.getInstance() }

    val context  = LocalContext.current
    val activity = context as ComponentActivity

    fun signInWithCredential(credential: PhoneAuthCredential) {
        isLoading = true
        errorMessage = ""
        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                isLoading = false
                result.user?.let { onOtpVerified(it) }
            }
            .addOnFailureListener {
                isLoading = false
                otpValue = ""
                errorMessage = "Invalid OTP. Please try again."
            }
    }

    fun sendOtp() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phone")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential)
                }
                override fun onVerificationFailed(e: FirebaseException) {
                    errorMessage = e.message ?: "Failed to send OTP"
                }
                override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                    verificationId = id
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    LaunchedEffect(phone) {
        sendOtp()
        delay(500)
        focusRequester.requestFocus()
    }

    LaunchedEffect(Unit) {
        while (resendTimer > 0) {
            delay(1000L)
            resendTimer--
        }
        canResend = true
    }

    LaunchedEffect(otpValue) {
        if (otpValue.length == 6 && verificationId.isNotEmpty()) {
            val credential = PhoneAuthProvider.getCredential(verificationId, otpValue)
            signInWithCredential(credential)
        }
    }

    // ── UI ────────────────────────────────────────────────────────
    Column(modifier = Modifier.fillMaxSize()) {

        // Gradient header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cs.primaryContainer)
                .padding(top = 48.dp, bottom = 32.dp, start = 16.dp, end = 16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Verify Number",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "+91 $phone",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    fontFamily = Poppins
                )
            }
        }

        // Card
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = cs.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Enter the 6-digit code",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = cs.onBackground,
                    fontFamily = Poppins
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Sent to your mobile number via SMS",
                    fontSize = 14.sp,
                    color = cs.onBackground.copy(alpha = 0.5f),
                    fontFamily = Poppins
                )

                Spacer(Modifier.height(40.dp))

                OtpInputRow(
                    otpValue = otpValue,
                    onValueChange = {
                        if (it.length <= 6 && it.all(Char::isDigit)) {
                            otpValue = it
                            errorMessage = ""
                        }
                    },
                    focusRequester = focusRequester,
                    isError = errorMessage.isNotEmpty()
                )

                Spacer(Modifier.height(16.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(
                        errorMessage,
                        color = cs.error,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = Poppins
                    )
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (otpValue.length == 6 && verificationId.isNotEmpty()) {
                            val credential = PhoneAuthProvider.getCredential(verificationId, otpValue)
                            signInWithCredential(credential)
                        }
                    },
                    enabled = otpValue.length == 6 && !isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = cs.primary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = cs.onPrimary,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Verify & Continue",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Poppins,
                            color = cs.onPrimary
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Didn't receive the code? ",
                        fontSize = 14.sp,
                        color = cs.onBackground.copy(alpha = 0.5f),
                        fontFamily = Poppins
                    )
                    if (canResend) {
                        TextButton(onClick = {
                            canResend = false
                            resendTimer = 30
                            otpValue = ""
                            errorMessage = ""
                            sendOtp()
                        }) {
                            Text("Resend", color = cs.primary, fontWeight = FontWeight.SemiBold, fontFamily = Poppins)
                        }
                    } else {
                        Text(
                            "Resend in ${resendTimer}s",
                            fontSize = 14.sp,
                            color = cs.primary,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins
                        )
                    }
                }
            }
        }
    }
}

// ── 6-box OTP row ─────────────────────────────────────────────────────────────
@Composable
private fun OtpInputRow(
    otpValue: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    isError: Boolean
) {
    val cs = MaterialTheme.colorScheme
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.clickable { focusRequester.requestFocus() }
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(6) { index ->
                val char      = otpValue.getOrNull(index)
                val isFocused = otpValue.length == index

                val borderColor = when {
                    isError      -> cs.error
                    isFocused    -> cs.primary
                    char != null -> cs.primary.copy(alpha = 0.5f)
                    else         -> cs.onBackground.copy(alpha = 0.25f)
                }
                val bgColor = when {
                    isError      -> cs.error.copy(alpha = 0.05f)
                    char != null -> cs.surface
                    else         -> cs.surface
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor)
                        .border(
                            width = if (isFocused) 2.dp else 1.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (char != null) {
                        Text(
                            text = char.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = cs.primary,
                            fontFamily = Poppins
                        )
                    } else if (isFocused) {
                        val alpha by animateFloatAsState(1f, label = "cursor")
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(28.dp)
                                .background(cs.primary.copy(alpha = alpha))
                        )
                    }
                }
            }
        }

        BasicTextField(
            value = otpValue,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            cursorBrush = SolidColor(cs.primary),
            modifier = Modifier
                .focusRequester(focusRequester)
                .matchParentSize()
                .alpha(0f)
        )
    }
}