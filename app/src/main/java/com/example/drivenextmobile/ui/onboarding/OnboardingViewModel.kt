package com.example.drivenextmobile.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivenextmobile.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class OnboardingViewModel : ViewModel() {
    private val _state = MutableStateFlow(createInitialState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<OnboardingContract.Effect>()
    val effect = _effect.asSharedFlow()

    private fun createInitialState(): OnboardingContract.State {
        val pages = listOf(
            Page(R.drawable.onboarding_image1, R.drawable.progress_indicator1, R.string.onboarding_title1, R.string.onboarding_first),
            Page(R.drawable.onboarding_image2, R.drawable.progress_indicator2, R.string.onboarding_title2, R.string.onboarding_second),
            Page(R.drawable.onboarding_image3, R.drawable.progress_indicator3, R.string.onboarding_title3, R.string.onboarding_last)
        )
        return createStateForPage(0, pages)
    }

    fun onEvent(event: OnboardingContract.Event) {
        when (event) {
            OnboardingContract.Event.NextButtonClicked -> handleNext()
            OnboardingContract.Event.SkipButtonClicked -> skipOnboarding()
        }
    }

    private fun handleNext() {
        val current = _state.value
        if (current.isLastPage) {
            viewModelScope.launch {
                _effect.emit(OnboardingContract.Effect.NavigateToSplashWithButtons)
            }
        } else {
            updateToPage(current.currentPage + 1)
        }
    }

    private fun skipOnboarding() {
        viewModelScope.launch {
            _effect.emit(OnboardingContract.Effect.NavigateToSplashWithButtons)
        }
    }

    private fun updateToPage(page: Int) {
        val pages = listOf(
            Page(R.drawable.onboarding_image1, R.drawable.progress_indicator1, R.string.onboarding_title1, R.string.onboarding_first),
            Page(R.drawable.onboarding_image2, R.drawable.progress_indicator2, R.string.onboarding_title2, R.string.onboarding_second),
            Page(R.drawable.onboarding_image3, R.drawable.progress_indicator3, R.string.onboarding_title3, R.string.onboarding_last)
        )
        _state.value = createStateForPage(page, pages)
    }

    private fun createStateForPage(page: Int, pages: List<Page>): OnboardingContract.State {
        return OnboardingContract.State(
            imageRes = pages[page].imageRes,
            progressRes = pages[page].progressRes,
            titleRes = pages[page].titleRes,
            descriptionRes = pages[page].descriptionRes,
            currentPage = page,
            isLastPage = page == pages.size - 1
        )
    }
    // Хранение данных страницы
    private data class Page(
        val imageRes: Int,
        val progressRes: Int,
        val titleRes: Int,
        val descriptionRes: Int
    )
}