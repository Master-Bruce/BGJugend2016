package de.schiewe.volker.bgjugend2016

import de.schiewe.volker.bgjugend2016.models.Event
import junit.framework.Assert
import org.junit.Test

class EventUnitTest{
    @Test
    fun test_age_string_min_max(){
        val event = Event()
        event.minAge = 12
        event.maxAge = 13
        val expected = "von 12 bis 13 Jahren"
        Assert.assertEquals(expected, event.ageString())
    }

    @Test
    fun test_age_string_min(){
        val event = Event()
        event.minAge = 12
        val expected = "ab 12 Jahren"
        Assert.assertEquals(expected, event.ageString())
    }

    @Test
    fun test_age_string_with_age_text(){
        val event = Event()
        event.minAge = 12
        event.maxAge = 16
        event.ageText = "Alle können kommen"
        val expected = event.ageText
        Assert.assertEquals(expected, event.ageString())
    }

    @Test
    fun test_age_string_empty(){
        val event = Event()
        val expected = ""
        Assert.assertEquals(expected, event.ageString())
    }

    @Test
    fun test_cost_string_only_cost(){
        val event = Event()
        event.cost = 30
        val expected = "30 €"
        Assert.assertEquals(expected, event.costString())
    }

    @Test
    fun test_cost_string_cost_and_cost_text(){
        val event = Event()
        event.cost = 30
        event.costText = "something else"
        val expected = "30 € something else"
        Assert.assertEquals(expected, event.costString())
    }

    @Test
    fun test_cost_string_empty(){
        val event = Event()
        val expected = ""
        Assert.assertEquals(expected, event.costString())
    }

    @Test
    fun test_deadline_string(){
        val event = Event()
        event.deadline = 1518735600000
        val expected = "16.02.2018"
        Assert.assertEquals(expected, event.deadlineString())
    }

    @Test
    fun test_deadline_string_empty(){
        val event = Event()
        val expected = ""
        Assert.assertEquals(expected, event.deadlineString())
    }

}