package de.schiewe.volker.bgjugend2016.models

import android.content.Context
import android.content.SharedPreferences
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.validateDateString
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class UserData(val context: Context, val sharedPrefs: SharedPreferences) {
    var name: String
        get() = sharedPrefs.getString(context.getString(R.string.name_key), "")
        set(value) {
            with(sharedPrefs.edit()) {
                putString(context.getString(R.string.name_key), value)
                apply()
            }
        }

    var street: String
        get() = sharedPrefs.getString(context.getString(R.string.street_key), "")
        set(value) {
            with(sharedPrefs.edit()) {
                putString(context.getString(R.string.street_key), value)
                apply()
            }
        }

    var place: String
        get() = sharedPrefs.getString(context.getString(R.string.place_key), "")
        set(value) {
            with(sharedPrefs.edit()) {
                putString(context.getString(R.string.place_key), value)
                apply()
            }
        }

    var birthday: String
        get() = sharedPrefs.getString(context.getString(R.string.birthday_key), "")
        set(value) {
            with(sharedPrefs.edit()) {
                putString(context.getString(R.string.birthday_key), value)
                apply()
            }
        }

    var telephone: String
        get() = sharedPrefs.getString(context.getString(R.string.telephone_key), "")
        set(value) {
            with(sharedPrefs.edit()) {
                putString(context.getString(R.string.telephone_key), value)
                apply()
            }
        }

    fun getAge(today:Calendar): Int? {
        if (this.birthday == "")
            return null
        return try {
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY)
            val date: Date = sdf.parse(this.birthday)
            calendar.time = date
            var age = today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < calendar.get(Calendar.DAY_OF_YEAR))
                age--
            age
        } catch (e: ParseException) {
            null
        }
    }

    companion object {
        fun validateName(name: String): Boolean {
            return name != ""
        }

        fun validateStreet(street: String): Boolean {
            return street != ""
        }

        fun validatePlace(place: String): Boolean {
            return if (place == "") {
                false
            } else {
                val regex = """^\d+\s\w+$""".toRegex()
                return regex matches place
            }
        }

        fun validateBirthday(birthday: String): Boolean {
            return if (birthday == "")
                true
            else {
                return validateDateString(birthday)
            }
        }

        fun validateTelephone(telephone: String): Boolean {
            return if (telephone == "")
                true
            else {
                val regex = """^[\d/\s+-]+$""".toRegex()
                return regex matches telephone
            }
        }
    }
}