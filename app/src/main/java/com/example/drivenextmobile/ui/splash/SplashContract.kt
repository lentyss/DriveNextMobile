package com.example.drivenextmobile.ui.splash

interface SplashContract {
    sealed class Event {
        data object InitialCheckCompleted : Event()
        data object LoginClicked : Event()
        data object RegisterClicked : Event()
    }

    sealed class Effect {
        data object NavigateToLogin : Effect()
        data object NavigateToRegister : Effect()
        data object NavigateToOnboarding : Effect()
    }
}