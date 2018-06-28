package de.schiewe.volker.bgjugend2016.models

import de.schiewe.volker.bgjugend2016.formatDate
import java.text.SimpleDateFormat
import java.util.*

open class BaseEvent : Comparable<BaseEvent> {
    override fun compareTo(other: BaseEvent): Int = compareValuesBy(this, other, { it.startDate }, { it.endDate }, { it.title })

    open var title: String = ""
    open var startDate: Long? = null
    open var endDate: Long? = null
    open var place: String = ""
    open var dateText: String? = null

    fun dateString(): String {
        if (this.dateText != null)
            return this.dateText!!
        if (startDate != null && endDate != null){
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY)
            return "${formatDate(this.startDate, sdf)} - ${formatDate(this.endDate, sdf)}"
        }
        return ""
    }

    companion object Factory {
        fun create(): BaseEvent = BaseEvent()
    }
}
