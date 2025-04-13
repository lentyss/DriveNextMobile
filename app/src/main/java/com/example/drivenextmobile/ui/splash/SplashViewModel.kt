package com.example.drivenextmobile.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class SplashViewModel : ViewModel() {
    private val _effect = MutableSharedFlow<SplashContract.Effect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: SplashContract.Event) {
        when (event) {
            SplashContract.Event.InitialCheckCompleted -> checkInitialState()
            SplashContract.Event.LoginClicked -> navigateToLogin()
            SplashContract.Event.RegisterClicked -> navigateToRegister()
        }
    }

    private fun checkInitialState() {
        viewModelScope.launch {
            delay(1500)
            _effect.emit(SplashContract.Effect.NavigateToOnboarding)
        }
    }

    private fun navigateToLogin() {
        viewModelScope.launch {
            _effect.emit(SplashContract.Effect.NavigateToLogin)
        }
    }

    private fun navigateToRegister() {
        viewModelScope.launch {
            _effect.emit(SplashContract.Effect.NavigateToRegister)
        }
    }
}