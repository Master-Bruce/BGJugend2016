package de.schiewe.volker.bgjugend2016

import android.content.Context
import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import de.schiewe.volker.bgjugend2016.models.UserData
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*


class UserDataUnitTest {
    private var context: Context = mock()
    private val sharedPrefs: SharedPreferences = mock()

    @Before fun mock_context(){
        whenever(context.getString(any())).thenReturn("")
    }
    @Test
    fun test_get_age_empty_string() {
        val user = UserData(context, sharedPrefs)
        whenever(sharedPrefs.getString(any(), any())).thenReturn("")
        Assert.assertEquals(null, user.getAge(Calendar.getInstance()))
    }

    @Test
    fun test_get_age_correct_string() {
        val user = UserData(context, sharedPrefs)
        val calendar = Calendar.getInstance()
        calendar.set(2018, 1, 1)

        whenever(sharedPrefs.getString(any(), any())).thenReturn("01.01.1997")
        Assert.assertEquals(21, user.getAge(calendar))
        whenever(sharedPrefs.getString(any(), any())).thenReturn("01.01.1999")
        Assert.assertEquals(19, user.getAge(calendar))
    }

    @Test
    fun test_get_age_wrong_string() {
        val user = UserData(context, sharedPrefs)
        val calendar = Calendar.getInstance()
        calendar.set(2018, 1, 1)

        whenever(sharedPrefs.getString(any(), any())).thenReturn("Test")
        Assert.assertEquals(null, user.getAge(calendar))
    }


    @Test
    fun test_name_validation() {
        Assert.assertEquals(false, UserData.validateName(""))
        Assert.assertEquals(true, UserData.validateName("FirstName Name"))
    }

    @Test
    fun test_street_validation() {
        Assert.assertEquals(false, UserData.validateStreet(""))
        Assert.assertEquals(true, UserData.validateStreet("Street"))
    }

    @Test
    fun test_place_validation() {
        Assert.assertEquals(false, UserData.validatePlace(""))
        Assert.assertEquals(false, UserData.validatePlace("Place"))
        Assert.assertEquals(false, UserData.validatePlace("12345"))
        Assert.assertEquals(true, UserData.validatePlace("12345 Place"))
    }

    @Test
    fun test_birthday_validation() {
        Assert.assertEquals(true, UserData.validateBirthday(""))
        Assert.assertEquals(false, UserData.validateBirthday("Birthday"))
        Assert.assertEquals(false, UserData.validateBirthday("12. May 1994"))
        Assert.assertEquals(true, UserData.validateBirthday("12.04.1994"))
    }

    @Test
    fun test_telephone_validation() {
        Assert.assertEquals(true, UserData.validateTelephone(""))
        Assert.assertEquals(false, UserData.validateTelephone("Telephone"))
        Assert.assertEquals(true, UserData.validateTelephone("0123456789"))
        Assert.assertEquals(true, UserData.validateTelephone("+49134/234234"))
    }

}
