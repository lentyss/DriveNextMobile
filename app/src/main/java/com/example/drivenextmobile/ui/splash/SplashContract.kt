package com.example.drivenextmobile.ui.splash

/**
    Контракт MVI для Splash-экрана
 */
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