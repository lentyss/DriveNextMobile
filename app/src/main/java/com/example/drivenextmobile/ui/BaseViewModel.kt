package com.example.drivenextmobile.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<Event, State, Effect> : ViewModel() {
    private val initialState: State by lazy { createInitialState() }

    private val _state: MutableStateFlow<State> = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state

    private val _effect: MutableSharedFlow<Effect> = MutableSharedFlow()
    val effect: SharedFlow<Effect> = _effect

    abstract fun createInitialState(): State
    abstract fun handleEvent(event: Event)

    fun onEvent(event: Event) {
        handleEvent(event)
    }

    protected fun setState(reducer: State.() -> State) {
        _state.update(reducer)
    }

    protected suspend fun setEffect(builder: () -> Effect) {
        _effect.emit(builder())
    }
}