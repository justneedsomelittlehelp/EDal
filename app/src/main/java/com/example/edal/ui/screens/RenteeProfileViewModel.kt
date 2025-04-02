package com.example.edal.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


class RenteeProfileViewModel : ViewModel() {

    val firstName = MutableStateFlow("")
    val lastName = MutableStateFlow("")
    val age = MutableStateFlow("")
    val gender = MutableStateFlow("")

    val selectedState = MutableStateFlow("")
    val selectedCity = MutableStateFlow("")
    val minBudget = MutableStateFlow(1000f)
    val maxBudget = MutableStateFlow(3000f)

    val isSaving = MutableStateFlow(false)

    fun saveProfile(onProfileSaved: () -> Unit) {
        isSaving.value = true

        CoroutineScope(Dispatchers.Main).launch {
            println("Saving: ${firstName.value} ${lastName.value}, ${selectedState.value} - ${selectedCity.value}, \$${minBudget.value} - \$${maxBudget.value}")
            delay(1000)
            isSaving.value = false
            onProfileSaved()
        }
    }
}


