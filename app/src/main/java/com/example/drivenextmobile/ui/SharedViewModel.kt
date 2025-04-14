package com.example.drivenextmobile.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

enum class Screen {
    LOGIN, REGISTRATION, SUCCESS
}

/**
    Общая ViewModel для навигации:
    LiveData с текущим экраном
    Навигация между экранами
 */
class SharedViewModel : ViewModel() {

    private val _currentScreen = MutableLiveData<Screen>(Screen.LOGIN)
    val currentScreen: LiveData<Screen> get() = _currentScreen

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }
}