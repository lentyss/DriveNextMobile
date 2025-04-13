package com.example.drivenextmobile.app.ui

import junit.framework.TestCase.assertEquals
import org.junit.Test

class OnboardingStateTest {

    @Test
    fun `OnboardingState should hold correct data`() {
        val state = OnboardingState(
            imageRes = 123,
            titleRes = 456,
            descriptionRes = 789,
            currentPage = 0
        )

        assertEquals(123, state.imageRes)
        assertEquals(456, state.titleRes)
        assertEquals(789, state.descriptionRes)
        assertEquals(0, state.currentPage)
    }
}