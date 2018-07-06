package de.schiewe.volker.bgjugend2016

import de.schiewe.volker.bgjugend2016.models.BaseEvent
import junit.framework.Assert
import org.junit.Test

class BaseEventUnitTest{
    @Test
    fun testDateStringEmpty(){
        val event = BaseEvent()
        val expected = ""
        Assert.assertEquals(expected, event.dateString())
    }

    @Test
    fun testDateStringStartEnd(){
        val event = BaseEvent()
        event.startDate = 1518562800000
        event.endDate = 1518908400000
        val expected = "14.02.2018 - 18.02.2018"
        Assert.assertEquals(expected, event.dateString())
    }

    @Test
    fun testDateStringDateText(){
        val event = BaseEvent()
        event.dateText = "Oktober 2018"
        val expected = "Oktober 2018"
        Assert.assertEquals(expected, event.dateString())
    }
}