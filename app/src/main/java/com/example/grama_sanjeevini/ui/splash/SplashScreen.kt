package com.example.grama_sanjeevini.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grama_sanjeevini.R
import com.example.grama_sanjeevini.constants.theme.Poppins
import com.example.grama_sanjeevini.constants.theme.PrimaryColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

/**
 * Splash screen that:
 * 1. Shows the animated brand logo for at least 2 seconds.
 * 2. Simultaneously checks whether a Firebase session already exists.
 *    - If yes → reads the user's role from Firestore → calls [onFinished] with
 *      "customer_main" or "pharmacist_main".
 *    - If no  → calls [onFinished] with "login".
 *
 * The caller (NavGraph) uses the destination string to navigate without
 * showing the login screen to already-authenticated users.
 */
@Composable
fun SplashScreen(onFinished: (destination: String) -> Unit) {

    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Start fade-in animation immediately
        alpha.animateTo(
            targetValue  = 1f,
            animationSpec = tween(durationMillis = 800, easing = EaseOut)
        )

        // Resolve the auth destination in parallel with the visual hold time
        val destination = resolveStartDestination()

        // Ensure we show the splash for at least 2 s total
        delay(1400L)   // ~800ms already spent on animation; total ≥ 2 200ms
        onFinished(destination)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alpha.value)
        ) {

            // ── Logo ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gramasanjeevini_logo),
                    contentDescription = "GramaSanjeevini Logo",
                    modifier = Modifier.size(300.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── App Name ──────────────────────────────────────────────
            Text(
                text           = "GramaSanjeevini",
                color          = Color.White,
                fontSize       = 28.sp,
                fontWeight     = FontWeight.Bold,
                textAlign      = TextAlign.Center,
                letterSpacing  = 0.5.sp,
                fontFamily     = Poppins
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Tagline ───────────────────────────────────────────────
            Text(
                text          = "YOUR VILLAGE PHARMACY NETWORK",
                color         = Color.White.copy(alpha = 0.65f),
                fontSize      = 11.sp,
                fontWeight    = FontWeight.Medium,
                letterSpacing = 2.sp,
                textAlign     = TextAlign.Center,
                fontFamily    = Poppins
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Pulsing dots loader
            PulsingDots()
        }
    }
}

/**
 * Checks Firebase Auth for a current session and, if found, reads the user's
 * role from Firestore to determine the correct start destination.
 *
 * Returns one of: "customer_main" | "pharmacist_main" | "login"
 */
private suspend fun resolveStartDestination(): String {
    val currentUser = FirebaseAuth.getInstance().currentUser
        ?: return "login"          // No session → go to Login

    return try {
        val doc = FirebaseFirestore.getInstance()
            .collection("users")
            .document(currentUser.uid)
            .get()
            .await()

        if (!doc.exists()) {
            // Firebase token exists but no Firestore record → force re-login
            FirebaseAuth.getInstance().signOut()
            "login"
        } else {
            when (doc.getString("role")?.uppercase()) {
                "PHARMACIST" -> "pharmacist_main"
                else         -> "customer_main"
            }
        }
    } catch (_: Exception) {
        // Network failure during splash → safer to send to login
        // The user can re-authenticate; Firebase keeps the token cached so
        // OTP won't be needed again if the session is still valid on retry.
        "login"
    }
}

// ── Pulsing dots loader ────────────────────────────────────────────────────────
@Composable
private fun PulsingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    val alphas = (0..2).map { index ->
        infiniteTransition.animateFloat(
            initialValue  = 0.3f,
            targetValue   = 1f,
            animationSpec = infiniteRepeatable(
                animation     = tween(600, easing = EaseInOut),
                repeatMode    = RepeatMode.Reverse,
                initialStartOffset = StartOffset(index * 200)
            ),
            label = "dot_$index"
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        alphas.forEach { alphaState ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .alpha(alphaState.value)
                    .background(Color.White)
            )
        }
    }
}