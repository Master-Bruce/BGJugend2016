package de.schiewe.volker.bgjugend2016.models


class Event : BaseEvent() {
    override var title: String = ""
    override var startDate: Long? = null
    override var endDate: Long? = null
    override var place: String = ""

    var header: String = ""
    var text: String = ""
    var minAge: Int? = null
    var maxAge: Int? = null
    var peopleNumber: Int? = null
    var cost: Int? = null
    var deadline: Long? = null
    var team: String = ""
    //        val contact: Contact,
    var imagePath: String = ""
    var url: String = ""

    companion object Factory {
        fun create(): Event = Event()
    }

}