package de.schiewe.volker.bgjugend2016

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import de.schiewe.volker.bgjugend2016.activities.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InfoFragmentEspressoTest {
    @Rule
    @JvmField
    val activity = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Before
    fun openInfoFragment(){
        onView(withId(R.id.navigation_info)).perform(click())
        onView(withId(R.id.fragment_info)).check(matches(isDisplayed()))
    }

    @Test
    fun testYouthWorkers(){
        onView(withId(R.id.list_youth_worker)).check(matches(isDisplayed()))
//        onView(withId(R.id.youth_worker_name)).check(matches(isDisplayed())) // TODO Match multiple nodes
//        onView(withId(R.id.youth_team)).check(matches(withText("Bla")))  // TODO check text is displayed

    }
}