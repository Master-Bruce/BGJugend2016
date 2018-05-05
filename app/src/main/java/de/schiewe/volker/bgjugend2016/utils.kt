package de.schiewe.volker.bgjugend2016

import java.text.SimpleDateFormat
import java.util.*

fun formatDate(timestamp: Long?, simpleDateFormat: SimpleDateFormat): String {
    if (timestamp == null)
        return ""
    return simpleDateFormat.format(Date(timestamp))
}