package com.example.edal.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

class RenteeMoveInViewModel : ViewModel() {

    private val _moveInDate = MutableStateFlow<LocalDate?>(null)
    val moveInDate: StateFlow<LocalDate?> = _moveInDate

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    fun saveMoveInData(stayLength: Int, onNext: () -> Unit) {
        _isSaving.value = true

        println("Saving move-in date: ${_moveInDate.value}, Stay length: $stayLength months")

        viewModelScope.launch {
            delay(1000)
            _isSaving.value = false
            onNext()
        }
    }

    fun setMoveInDate(date: LocalDate) {
        _moveInDate.value = date
    }
}
