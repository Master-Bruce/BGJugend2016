package de.schiewe.volker.bgjugend2016

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import de.schiewe.volker.bgjugend2016.models.Contact
import de.schiewe.volker.bgjugend2016.models.Event
import de.schiewe.volker.bgjugend2016.models.UserData
import org.junit.Assert
import org.junit.Test
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
    fun test_get_id_from_string() {
        var id = getIdFromString("Fußball Österreich")
        Assert.assertEquals("FUSSBALL_OESTERREICH", id)
        id = getIdFromString("Älterer Übergang")
        Assert.assertEquals("AELTERER_UEBERGANG", id)
    }

    @Test
    fun test_is_new_version() {
        val sharedPrefs: SharedPreferences = mock()
        whenever(sharedPrefs.getInt(any(), any())).thenReturn(BuildConfig.VERSION_CODE)
        val value = isNewVersion(sharedPrefs, "")
        Assert.assertFalse(value)
    }

    @Test
    fun test_generate_mail_text() {
        val event: Event = mock()
        val contact: Contact = mock()
        val user: UserData = mock()

        whenever(event.title).thenReturn("Test test")
        whenever(event.dateString()).thenReturn("Test test")
        whenever(contact.name).thenReturn("Test test")
        whenever(event.contact).thenReturn(contact)
        whenever(user.name).thenReturn("Test test")
        whenever(user.street).thenReturn("Test test")
        whenever(user.place).thenReturn("Test test")
        whenever(user.birthday).thenReturn("Test test")
        whenever(user.telephone).thenReturn("Test test")

        val mailText = "Hallo Test, \n" +
                "Ich möchte mich für die Veranstaltung Test test vom Test test anmelden.\n\n" +
                "Test test\n" +
                "Test test\n" +
                "Test test\n" +
                "Test test\n" +
                "Test test\n\n" +
                "Viele Grüße\n Test"

        Assert.assertEquals(mailText, generateMailText(event, user))
    }

    @Test
    fun test_validate_date_string_true() {
        val string = "12.12.2018"
        val result = validateDateString(string)
        Assert.assertTrue(result)
    }

    @Test
    fun test_validate_date_string_false() {
        val string = "12. Mai 2018"
        val result = validateDateString(string)
        Assert.assertFalse(result)
    }

    @Test
    fun test_validate_date_string_empty() {
        val string = ""
        val result = validateDateString(string)
        Assert.assertFalse(result)
    }
}
