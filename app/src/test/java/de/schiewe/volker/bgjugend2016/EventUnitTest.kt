package de.schiewe.volker.bgjugend2016

import android.content.Context
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import de.schiewe.volker.bgjugend2016.models.Event
import junit.framework.Assert
import org.junit.Test

class EventUnitTest{
    private val event = Event()
    @Test
    fun testAgeStringMinMax(){
        val context:Context = mock()
        whenever(context.getString(R.string.from_x_until_y_years, 12, 13)).thenReturn("von 12 bis 13 Jahren")
        event.minAge = 12
        event.maxAge = 13
        val expected = "von 12 bis 13 Jahren"
        Assert.assertEquals(expected, event.ageString(context))
    }

    @Test
    fun testAgeStringMin(){
        val context:Context = mock()
        whenever(context.getString(R.string.from_x_years, 12)).thenReturn("ab 12 Jahren")
        event.minAge = 12
        val expected = "ab 12 Jahren"
        Assert.assertEquals(expected, event.ageString(context))
    }

    @Test
    fun testAgeStringWithAgeText(){
        val context:Context = mock()
        event.minAge = 12
        event.maxAge = 16
        event.ageText = "Alle können kommen"
        val expected = event.ageText
        Assert.assertEquals(expected, event.ageString(context))
    }

    @Test
    fun testAgeStringEmpty(){
        val expected = ""
//        Assert.assertEquals(expected, event.ageString())
    }

    @Test
    fun testCostStringOnlyCost(){
        event.cost = 30
        val expected = "30 €"
        Assert.assertEquals(expected, event.costString())
    }

    @Test
    fun testCostStringCostAndCostText(){
        event.cost = 30
        event.costText = "something else"
        val expected = "30 € something else"
        Assert.assertEquals(expected, event.costString())
    }

    @Test
    fun testCostStringEmpty(){
        val expected = ""
        Assert.assertEquals(expected, event.costString())
    }

    @Test
    fun testDeadlineString(){
        event.deadline = 1518735600000
        val expected = "16.02.2018"
        Assert.assertEquals(expected, event.deadlineString())
    }

    @Test
    fun testDeadlineStringEmpty(){
        val expected = ""
        Assert.assertEquals(expected, event.deadlineString())
    }

}