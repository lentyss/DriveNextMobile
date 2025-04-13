package com.example.drivenextmobile.ui

import com.example.drivenextmobile.ui.onboarding.OnboardingEvent
import com.example.drivenextmobile.ui.onboarding.OnboardingViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class OnboardingViewModelTest {

    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setUp() {
        viewModel = OnboardingViewModel()
    }

    @Test
    fun initialState_shouldBeFirstOnboardingScreen() = runTest {
        val initialState = viewModel.state.value
        assertEquals(0, initialState?.currentPage)
        assertEquals(456, initialState?.titleRes) // Фиктивное значение вместо R.string.onboarding_title1
    }

    @Test
    fun onNextButtonClicked_stateShouldUpdateToNextPage() = runTest {
        viewModel.onEvent(OnboardingEvent.NextButtonClicked)
        val state = viewModel.state.value
        assertEquals(1, state?.currentPage)
        assertEquals(789, state?.titleRes) // Фиктивное значение вместо R.string.onboarding_title2
    }
}