package com.example.edal.ui.screens

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RenteeProfileViewModel : ViewModel() {

    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> = _lastName

    private val _age = MutableStateFlow("")
    val age: StateFlow<String> = _age

    private val _gender = MutableStateFlow("")
    val gender: StateFlow<String> = _gender

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun onFirstNameChange(newValue: String) {
        _firstName.value = newValue
    }

    fun onLastNameChange(newValue: String) {
        _lastName.value = newValue
    }

    fun onAgeChange(newValue: String) {
        _age.value = newValue
    }

    fun onGenderChange(newValue: String) {
        _gender.value = newValue
    }

    fun submitProfile(onSuccess: () -> Unit) {
        _isLoading.value = true
        _error.value = null

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            _error.value = "User not logged in"
            _isLoading.value = false
            return
        }

        val profile = mapOf(
            "firstName" to firstName.value,
            "lastName" to lastName.value,
            "age" to age.value,
            "gender" to gender.value,
            "profileCreated" to true
        )

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.uid)
            .update(profile)
            .addOnSuccessListener {
                _isLoading.value = false
                onSuccess()
            }
            .addOnFailureListener {
                _error.value = "Failed to save profile: ${it.localizedMessage}"
                _isLoading.value = false
            }
    }
}

