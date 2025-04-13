package com.example.drivenextmobile.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.drivenextmobile.app.usecase.RegistrationUseCase

class RegistrationViewModelFactory(
    private val registrationUseCase: RegistrationUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistrationViewModel<Any>(registrationUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}