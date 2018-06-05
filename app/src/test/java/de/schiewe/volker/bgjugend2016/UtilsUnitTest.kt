package de.schiewe.volker.bgjugend2016

import org.junit.Assert
import org.junit.Test
import java.util.*

class UtilsUnitTest {
    @Test
    fun test_get_age_empty_string() {
        Assert.assertEquals(-1, getAge("", Calendar.getInstance()))
    }

    @Test
    fun test_get_age_correct_string() {
        val calendar = Calendar.getInstance()
        calendar.set(2018, 1, 1)
        Assert.assertEquals(1, getAge("01.01.2017", calendar))
        Assert.assertEquals(21, getAge("01.01.1997", calendar))
    }
    @Test
    fun test_get_age_wrong_string() {
        val calendar = Calendar.getInstance()
        calendar.set(2018, 1, 1)
        Assert.assertEquals(-1, getAge("Test", calendar))
    }

    @Test
    fun test_get_id_from_string(){
        var id = getIdFromString("Fußball Österreich")
        Assert.assertEquals("FUSSBALL_OESTERREICH", id)
        id = getIdFromString("Älterer Übergang")
        Assert.assertEquals("AELTERER_UEBERGANG", id)
    }
}
