package de.schiewe.volker.bgjugend2016.models

open class BaseEvent {
    open var title: String = ""
    open var startDate: Long? = null
    open var endDate: Long? = null
    open var place: String = ""

    companion object Factory {
        fun create(): BaseEvent = BaseEvent()
    }
}
