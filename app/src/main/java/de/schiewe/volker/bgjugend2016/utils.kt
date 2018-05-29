package de.schiewe.volker.bgjugend2016

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