package de.schiewe.volker.bgjugend2016

import android.content.Context
import android.content.SharedPreferences
import de.schiewe.volker.bgjugend2016.models.Contact
import de.schiewe.volker.bgjugend2016.models.Event
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import java.text.SimpleDateFormat
import java.util.*

class UtilsUnitTest {
    @Test
    fun test_format_date() {
        val simpleDateFormat = SimpleDateFormat("dd.MM.YYYY")
        val date = Calendar.getInstance()
        date.set(2018, 0, 1)
        val formattedDate = formatDate(date.timeInMillis, simpleDateFormat)
        Assert.assertEquals("01.01.2018", formattedDate)
    }

    @Test
    fun test_format_date_empty() {
        val simpleDateFormat = SimpleDateFormat("dd.MM.YYYY")
        val formattedDate = formatDate(null, simpleDateFormat)
        Assert.assertEquals("", formattedDate)
    }

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
    fun test_get_id_from_string() {
        var id = getIdFromString("Fußball Österreich")
        Assert.assertEquals("FUSSBALL_OESTERREICH", id)
        id = getIdFromString("Älterer Übergang")
        Assert.assertEquals("AELTERER_UEBERGANG", id)
    }

    @Test
    fun test_is_new_version() {
        val sharedPrefs = Mockito.mock(SharedPreferences::class.java)
        Mockito.`when`(sharedPrefs.getInt(anyString(), anyInt())).thenReturn(BuildConfig.VERSION_CODE)
        val value = isNewVersion(sharedPrefs, "")
        Assert.assertFalse(value)
    }

    @Test
    fun test_generate_mail_text(){
        val sharedPrefs = Mockito.mock(SharedPreferences::class.java)
        val context = Mockito.mock(Context::class.java)
        val event = Mockito.mock(Event::class.java)
        val contact = Mockito.mock(Contact::class.java)
        Mockito.`when`(context.getString(anyInt())).thenReturn("Test")
        Mockito.`when`(sharedPrefs.getString(anyString(), anyString())).thenReturn("Test test")
        Mockito.`when`(event.title).thenReturn("Test test")
        Mockito.`when`(event.dateString()).thenReturn("Test test")
        Mockito.`when`(contact.name).thenReturn("Test test")
        Mockito.`when`(event.contact).thenReturn(contact)

        val mailText = "Hallo Test, \n" +
                "Ich möchte mich für die Veranstaltung Test test vom Test test anmelden.\n\n" +
                "Test test\n" +
                "Test test\n" +
                "Test test\n" +
                "Test test\n" +
                "Test test\n\n" +
                "Viele Grüße\n Test"

        Assert.assertEquals(mailText, generateMailText(context, event, sharedPrefs))
    }

    @Test
    fun test_validate_date_string_true(){
        val string = "12.12.2018"
        val result = validateDateString(string)
        Assert.assertTrue(result)
    }
    @Test
    fun test_validate_date_string_false(){
        val string = "12. Mai 2018"
        val result = validateDateString(string)
        Assert.assertFalse(result)
    }
    @Test
    fun test_validate_date_string_empty(){
        val string = ""
        val result = validateDateString(string)
        Assert.assertFalse(result)
    }
}
