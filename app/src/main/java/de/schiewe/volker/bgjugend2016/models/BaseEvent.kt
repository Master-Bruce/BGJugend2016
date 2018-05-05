package de.schiewe.volker.bgjugend2016.models

open class BaseEvent : Comparable<BaseEvent> {
    override fun compareTo(other: BaseEvent): Int = compareValuesBy(this, other, { it.startDate }, { it.endDate }, { it.title })

    open var title: String = ""
    open var startDate: Long? = null
    open var endDate: Long? = null
    open var place: String = ""

    companion object Factory {
        fun create(): BaseEvent = BaseEvent()
    }
}
