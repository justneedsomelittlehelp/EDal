package com.example.edal.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.*



class RenteeProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Personal info
    val firstName = MutableStateFlow("")
    val lastName = MutableStateFlow("")
    val age = MutableStateFlow("")
    val gender = MutableStateFlow("")

    // Location
    val selectedState = MutableStateFlow("")
    val selectedCity = MutableStateFlow("")
    val minBudget = MutableStateFlow(1000f)
    val maxBudget = MutableStateFlow(3000f)

    // Move-in date and stay length
    val moveInDate = MutableStateFlow<LocalDate?>(null)
    val stayLengthMonths = MutableStateFlow(0)

    // UI states
    val isSaving = MutableStateFlow(false)


    // --- Save functions ---
    fun saveProfile(onProfileSaved: () -> Unit) {
        isSaving.value = true

        val uid = auth.currentUser?.uid ?: return
        val userRef = firestore.collection("users").document(uid)

        val data = mapOf(
            "firstName" to firstName.value,
            "lastName" to lastName.value,
            "age" to age.value,
            "gender" to gender.value,
            "state" to selectedState.value,
            "city" to selectedCity.value,
            "minBudget" to minBudget.value,
            "maxBudget" to maxBudget.value,
            "profileCreated" to true
        )

        viewModelScope.launch {
            userRef.update(data)
                .addOnSuccessListener { onProfileSaved() }
                .addOnFailureListener { isSaving.value = false }
        }
    }

    fun saveMoveInData(onSaved: () -> Unit) {
        isSaving.value = true

        val uid = auth.currentUser?.uid ?: return
        val userRef = firestore.collection("users").document(uid)

        val data = mapOf(
            "moveInDate" to moveInDate.value.toString(),
            "stayLengthMonths" to stayLengthMonths.value
        )

        viewModelScope.launch {
            userRef.update(data)
                .addOnSuccessListener {
                    isSaving.value = false
                    onSaved()
                }
                .addOnFailureListener {
                    isSaving.value = false
                }
        }
    }
}

