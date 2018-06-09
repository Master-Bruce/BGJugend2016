package de.schiewe.volker.bgjugend2016

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.support.v7.preference.PreferenceManager
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


fun formatDate(timestamp: Long?, simpleDateFormat: SimpleDateFormat): String {
    if (timestamp == null)
        return ""
    return simpleDateFormat.format(Date(timestamp))
}

fun getAge(dateString: String, today: Calendar): Int {
    if (dateString == "")
        return -1
    return try {
        val sdf = SimpleDateFormat("dd.MM.YYYY", Locale.GERMANY)
        val date: Date = sdf.parse(dateString)
        val calendar = Calendar.getInstance()
        calendar.time = date
        var age = today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < calendar.get(Calendar.DAY_OF_YEAR))
            age--
        age
    } catch (e: ParseException) {
        -1
    }
}

fun getByteArrayFromBase64(base64String: String): ByteArray {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Base64.getDecoder().decode(base64String)
    } else {
        android.util.Base64.decode(base64String, android.util.Base64.DEFAULT)
    }
}

fun getIdFromString(string: String): String {
    return string
            .toLowerCase()
            .replace("ö", "oe")
            .replace("ä", "ae")
            .replace("ü", "ue")
            .replace("ß", "ss")
            .replace(" ", "_")
            .toUpperCase()
}

fun isNewVersion(sharedPrefs: SharedPreferences, lastVersionKey: String): Boolean {
    if (BuildConfig.VERSION_CODE != sharedPrefs.getInt(lastVersionKey, 0))
        return true
    return false
}

fun migrateToCurrentVersion(context: Context) {
    val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
    // migrate user data
    with(sharedPref.edit()) {
        putString(context.getString(R.string.deadline_notification_key), sharedPref.getString("notifications_deadline", ""))
        putString(context.getString(R.string.date_notification_key), sharedPref.getString("notifications_date", ""))
        putString(context.getString(R.string.notification_day_key), sharedPref.getString("notification_list", ""))
        val hourOfDay: String = sharedPref.getString("notification_time", "")
        putString(context.getString(R.string.notification_time_key),"$hourOfDay:00")

        putString(context.getString(R.string.name_key), sharedPref.getString("pref_name", ""))
        putString(context.getString(R.string.street_key), sharedPref.getString("pref_street", ""))
        putString(context.getString(R.string.place_key), sharedPref.getString("pref_city", ""))
        putString(context.getString(R.string.birthday_key), sharedPref.getString("pref_birthday", ""))
        putString(context.getString(R.string.telephone_key), sharedPref.getString("pref_telephone", ""))
        apply()
    }
}