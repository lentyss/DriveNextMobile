package com.example.drivenextmobile.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.drivenextmobile.R
import com.example.drivenextmobile.ui.onboarding.OnboardingActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingActivityTest {

    @Test
    fun initialUiShouldDisplayFirstOnboardingScreen() {
        val scenario = ActivityScenario.launch(OnboardingActivity::class.java)

        onView(withId(R.id.textView)).check(matches(withText(R.string.onboarding_title1.toString())))
        onView(withId(R.id.textView1)).check(matches(withText(R.string.onboarding_first.toString())))

        scenario.close()
    }

    @Test
    fun onNextButtonClickedUiShouldUpdateToNextScreen() {
        val scenario = ActivityScenario.launch(OnboardingActivity::class.java)

        onView(withId(R.id.nextButton)).perform(click())
        onView(withId(R.id.textView)).check(matches(withText(R.string.onboarding_title2.toString())))
        onView(withId(R.id.textView1)).check(matches(withText(R.string.onboarding_second.toString())))

        scenario.close()
    }
}