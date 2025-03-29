package com.example.edal.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import android.util.Log

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun register(onSuccess: () -> Unit) {
        _isLoading.value = true
        _error.value = null

        auth.createUserWithEmailAndPassword(email.value, password.value)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verifyTask ->
                            if (verifyTask.isSuccessful) {
                                _error.value = "Verification email sent. Please check your inbox."
                            } else {
                                _error.value = "Failed to send verification email."
                            }
                        }
                } else {
                    _error.value = task.exception?.message ?: "Registration failed"
                }
            }

    }

    fun login(onSuccess: () -> Unit) {
        _isLoading.value = true
        _error.value = null

        // ✅ Validate input
        if (email.value.isBlank() || password.value.isBlank()) {
            _isLoading.value = false
            _error.value = "Email and password must not be empty"
            return
        }

        auth.signInWithEmailAndPassword(email.value, password.value)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        onSuccess()
                    } else {
                        _error.value = "Please verify your email before logging in."
                        auth.signOut()
                    }
                } else {
                    _error.value = task.exception?.message ?: "Login failed"
                }
            }
    }


    fun signInWithGoogleCredential(credential: AuthCredential, onSuccess: () -> Unit) {
        _isLoading.value = true
        _error.value = null

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    _error.value = task.exception?.message ?: "Google Sign-In failed"
                }
            }
    }
}

