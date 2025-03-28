package com.example.edal.ui.screens


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isLogin = MutableStateFlow(true)
    val isLogin: StateFlow<Boolean> = _isLogin

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun toggleMode() {
        _isLogin.value = !_isLogin.value
        _error.value = null
    }

    fun login(onSuccess: () -> Unit) {
        _isLoading.value = true
        _error.value = null

        auth.signInWithEmailAndPassword(email.value, password.value)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    _error.value = task.exception?.message ?: "Login failed"
                }
            }
    }

    fun register(onSuccess: () -> Unit) {
        _isLoading.value = true
        _error.value = null

        auth.createUserWithEmailAndPassword(email.value, password.value)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(uid)
                            .set(mapOf("role" to "", "profileCreated" to false))
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                _error.value = "Failed to initialize user: ${e.localizedMessage}"
                            }
                    } else {
                        _error.value = "Could not retrieve user ID"
                    }
                } else {
                    _error.value = task.exception?.message ?: "Registration failed"
                }
            }
    }

}
