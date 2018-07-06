package de.schiewe.volker.bgjugend2016

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import de.schiewe.volker.bgjugend2016.activities.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityEspressoTest{

    @Rule
    @JvmField
    val activity = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    fun testNavigationEvents() {
        onView(withId(R.id.navigation_events)).perform(click())
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.fragment_event_list)).check(matches(isDisplayed()))
    }

    @Test
    fun testNavigationInfo() {
        onView(withId(R.id.navigation_info)).perform(click())
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.fragment_info)).check(matches(isDisplayed()))
    }

    @Test
    fun testNavigationSettings() {
        onView(withId(R.id.navigation_settings)).perform(click())
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.fragment_settings)).check(matches(isDisplayed()))
    }

    @Test
    fun testOpenEventDetails() {
       //TODO
    }

}