package com.example.drivenextmobile.ui.login

import androidx.lifecycle.*
import com.example.drivenextmobile.app.usecase.LoginUseCase
import com.example.drivenextmobile.ui.BaseViewModel
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : BaseViewModel<LoginContract.Event, LoginContract.State, LoginContract.Effect>() {

    override fun createInitialState(): LoginContract.State = LoginContract.State()

    override fun handleEvent(event: LoginContract.Event) {
        when (event) {
            is LoginContract.Event.LoginButtonClicked -> login(event.email, event.password)
            LoginContract.Event.RegistrationClicked -> navigateToRegistration()
            LoginContract.Event.ForgotPasswordClicked -> navigateToForgotPassword()
            LoginContract.Event.BackPressed -> handleBackPressed()
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            when (val result = loginUseCase.execute(email, password)) {
                is LoginUseCase.LoginResult.Success -> {
                    setEffect { LoginContract.Effect.NavigateToSuccess }
                }
                is LoginUseCase.LoginResult.Error -> {
                    setEffect { LoginContract.Effect.ShowError(result.message) }
                }
            }

            setState { copy(isLoading = false) }
        }
    }

    private fun navigateToRegistration() {
        viewModelScope.launch {
            setEffect { LoginContract.Effect.NavigateToRegistration }
        }
    }

    private fun navigateToSuccess() {
        viewModelScope.launch {
            setEffect { LoginContract.Effect.NavigateToSuccess }
        }
    }

    private fun navigateToForgotPassword() {
        viewModelScope.launch {
            setEffect { LoginContract.Effect.NavigateToForgotPassword }
        }
    }

    private fun handleBackPressed() {
        viewModelScope.launch {
            setEffect { LoginContract.Effect.NavigateToSplash }
        }
    }
}