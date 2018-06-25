package de.schiewe.volker.bgjugend2016

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.provider.CalendarContract
import android.support.v4.widget.CircularProgressDrawable
import android.support.v7.preference.PreferenceManager
import de.schiewe.volker.bgjugend2016.models.BaseEvent
import de.schiewe.volker.bgjugend2016.models.Event
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
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY)
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
    if (BuildConfig.VERSION_CODE != sharedPrefs.getInt(lastVersionKey, 0)) {
        with(sharedPrefs.edit()) {
            putInt(lastVersionKey, BuildConfig.VERSION_CODE)
            apply()
        }
        return true
    }
    return false
}

fun migrateToCurrentVersion(context: Context) {
    val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
    with(sharedPref.edit()) {
        // migrate notification settings
        putBoolean(context.getString(R.string.deadline_notification_key), sharedPref.getBoolean("notifications_deadline", false))
        putBoolean(context.getString(R.string.date_notification_key), sharedPref.getBoolean("notifications_date", false))
        putString(context.getString(R.string.notification_day_key), sharedPref.getString("notification_list", "7"))
        val hourOfDay: String = sharedPref.getString("notification_time", "")
        if (hourOfDay != "")
            putString(context.getString(R.string.notification_time_key), "$hourOfDay:00")

        // migrate user data
        putString(context.getString(R.string.name_key), sharedPref.getString("pref_name", ""))
        putString(context.getString(R.string.street_key), sharedPref.getString("pref_street", ""))
        putString(context.getString(R.string.place_key), sharedPref.getString("pref_city", ""))
        putString(context.getString(R.string.birthday_key), sharedPref.getString("pref_birthday", ""))
        putString(context.getString(R.string.telephone_key), sharedPref.getString("pref_telephone", ""))
        apply()
    }
}

fun openPlaceOnMap(context: Context, place: String) {
    val url = "http://maps.google.co.in/maps?q=$place"
    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(mapIntent)
}

fun addEventToCalender(context: Context, event: BaseEvent) {
    val calenderIntent = Intent(Intent.ACTION_INSERT)
            .setType("vnd.android.cursor.item/event")
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.startDate)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.endDate)
            .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
            .putExtra(CalendarContract.Events.TITLE, event.title)
            .putExtra(CalendarContract.Events.EVENT_LOCATION, event.place)
            .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
            .putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
    context.startActivity(calenderIntent)
}

fun shareEvent(context: Context, event: Event) {
    val shareIntent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, event.title + ": " + event.url)
    context.startActivity(shareIntent)
}

fun generateMailText(context: Context, event: Event, sharedPrefs: SharedPreferences): String {
    val contactFirstName = event.contact?.name?.split(" ")!![0]
    val name = sharedPrefs.getString(context.getString(R.string.name_key), "")
    val street = sharedPrefs.getString(context.getString(R.string.street_key), "")
    val city = sharedPrefs.getString(context.getString(R.string.place_key), "")
    val birthday = sharedPrefs.getString(context.getString(R.string.birthday_key), "")
    val telephone = sharedPrefs.getString(context.getString(R.string.telephone_key), "")

    return "Hallo $contactFirstName, \n" +
            "Ich möchte mich für die Veranstaltung ${event.title} vom ${event.dateString()} anmelden.\n\n" +
            "$name\n" +
            "$street\n" +
            "$city\n" +
            "$birthday\n" +
            "$telephone\n\n" +
            "Viele Grüße\n ${name.split(" ")[0]}"
}

fun validateDateString(date: String): Boolean {
    return try {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY)
        sdf.parse(date)
        true
    } catch (e: ParseException) {
        false
    }
}

fun getProgressBar(context: Context, stroke: Float, radius: Float): CircularProgressDrawable {
    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.strokeWidth = stroke
    circularProgressDrawable.centerRadius = radius
    circularProgressDrawable.setColorSchemeColors(context.resources.getColor(R.color.white))
    circularProgressDrawable.start()

    return circularProgressDrawable
}