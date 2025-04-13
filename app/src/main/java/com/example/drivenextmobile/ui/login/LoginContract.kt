package com.example.drivenextmobile.ui.login

interface LoginContract {
    data class State(
        val isLoading: Boolean = false,
        val emailError: String? = null,
        val passwordError: String? = null
    )

    sealed class Event {
        data class LoginButtonClicked(val email: String, val password: String) : Event()
        data object RegistrationClicked : Event()
        data object ForgotPasswordClicked : Event()
        data object BackPressed : Event()
    }

    sealed class Effect {
        data object NavigateToMain : Effect()
        data object NavigateToRegistration : Effect()
        data object NavigateToForgotPassword : Effect()
        data object NavigateToSplash : Effect()
        data object NavigateToSuccess : Effect()
        data class ShowError(val message: String) : Effect()
    }
}