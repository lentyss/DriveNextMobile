package com.example.drivenextmobile.ui.registration

import android.net.Uri

interface RegistrationContract {
    data class State(
        val currentStep: Int = 1,
        val isLoading: Boolean = false,
        val emailError: String? = null,
        val passwordError: String? = null,
        // Другие ошибки валидации
    )

    sealed class Event {
        // Навигация
        data object NextClicked : Event()
        data object BackClicked : Event()

        // Шаг 1
        data class EmailChanged(val email: String) : Event()
        data class PasswordChanged(val password: String) : Event()
        data class ConfirmPasswordChanged(val confirmPassword: String) : Event()
        data class AgreementChanged(val agreed: Boolean) : Event()

        // Шаг 2
        data class LastNameChanged(val lastName: String) : Event()
        data class FirstNameChanged(val firstName: String) : Event()
        data class MiddleNameChanged(val middleName: String) : Event()
        data class GenderChanged(val gender: String) : Event()
        data class BirthDateChanged(val date: String) : Event()

        // Шаг 3
        data class LicenseChanged(val license: String) : Event()
        data class LicenseDateChanged(val date: String) : Event()
        data class ProfilePhotoSelected(val uri: Uri?) : Event()
        data class LicensePhotoSelected(val uri: Uri?) : Event()
        data class PassportPhotoSelected(val uri: Uri?) : Event()

        data object RegisterClicked : Event()
    }

    sealed class Effect {
        data object NavigateToNextStep : Effect()
        data object NavigateToPreviousStep : Effect()
        data object NavigateToLogin : Effect()
        data object NavigateToSuccess : Effect()
        data class ShowError(val message: String) : Effect()
        data class UpdatePhotoView(val type: PhotoType, val uri: Uri?) : Effect()
    }

    enum class PhotoType {
        PROFILE, DRIVER_LICENSE, PASSPORT
    }
}