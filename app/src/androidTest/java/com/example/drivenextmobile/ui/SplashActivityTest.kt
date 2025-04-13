package com.example.drivenextmobile.ui

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.drivenextmobile.R
import com.example.drivenextmobile.ui.splash.SplashActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashActivityTest {

    @Test
    fun whenShowButtonsIsTrueButtonsShouldBeVisible() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), SplashActivity::class.java)
        intent.putExtra("SHOW_BUTTONS", true)

        val scenario = ActivityScenario.launch<SplashActivity>(intent)

        onView(withId(R.id.button_login)).check(matches(isDisplayed()))
        onView(withId(R.id.button_registration)).check(matches(isDisplayed()))

        scenario.close()
    }
}