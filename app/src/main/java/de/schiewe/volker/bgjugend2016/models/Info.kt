package de.schiewe.volker.bgjugend2016.models

class Info: BaseEvent(){
    override var title: String = ""
    override var startDate: Long? = null
    override var endDate: Long? = null
    override var place: String = ""

    var who: String = ""
    var regestration: String = ""

}