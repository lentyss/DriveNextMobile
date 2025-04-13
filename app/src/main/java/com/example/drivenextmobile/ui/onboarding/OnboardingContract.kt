package com.example.drivenextmobile.ui.onboarding

interface OnboardingContract {
    data class State(
        val imageRes: Int,
        val progressRes: Int,
        val titleRes: Int,
        val descriptionRes: Int,
        val currentPage: Int,
        val isLastPage: Boolean
    )

    sealed class Event {
        data object NextButtonClicked : Event()
        data object SkipButtonClicked : Event()
    }

    sealed class Effect {
        data object NavigateToSplashWithButtons : Effect()
    }
}