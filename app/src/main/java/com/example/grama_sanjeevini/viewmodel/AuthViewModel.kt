package com.example.grama_sanjeevini.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

enum class UserRole { CUSTOMER, PHARMACIST }

class AuthViewModel : ViewModel() {

    private val auth      = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Add inside AuthViewModel class
    var pendingName: String = ""
        private set

    fun setPendingName(name: String) {
        pendingName = name
    }

    sealed class AuthState {
        object Idle    : AuthState()
        object Loading : AuthState()
        data class Success(val role: UserRole) : AuthState()
        data class UserNotFound(val phone: String) : AuthState()
        data class AlreadyExists(val phone: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    var authState by mutableStateOf<AuthState>(AuthState.Idle)
        private set

    // Called after OTP verified on LOGIN flow
    fun onLoginOtpSuccess(user: FirebaseUser) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val doc = firestore.collection("users")
                    .document(user.uid).get().await()
                if (doc.exists()) {
                    val savedRole = when (doc.getString("role")) {
                        "PHARMACIST" -> UserRole.PHARMACIST
                        else -> UserRole.CUSTOMER
                    }
                    authState = AuthState.Success(savedRole)
                } else {
                    // Phone not registered — tell them to sign up
                    auth.signOut()
                    authState = AuthState.UserNotFound(user.phoneNumber ?: "")
                }
            } catch (e: Exception) {
                authState = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    // Called after OTP verified on REGISTER flow
    fun onRegisterOtpSuccess(user: FirebaseUser, name: String, role: UserRole) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val doc = firestore.collection("users")
                    .document(user.uid).get().await()
                if (doc.exists()) {
                    // Already registered — tell them to login
                    auth.signOut()
                    authState = AuthState.AlreadyExists(user.phoneNumber ?: "")
                } else {
                    firestore.collection("users").document(user.uid).set(
                        mapOf(
                            "name"      to name,
                            "phone"     to (user.phoneNumber ?: ""),
                            "role"      to role.name,
                            "createdAt" to System.currentTimeMillis()
                        )
                    ).await()
                    authState = AuthState.Success(role)
                }
            } catch (e: Exception) {
                authState = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun resetState() {
        authState = AuthState.Idle
    }
}