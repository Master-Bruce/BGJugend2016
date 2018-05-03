package de.schiewe.volker.bgjugend2016.models

import java.util.*

class Event(
        override var title: String,
        override var startDate: Date,
        override var endDate: Date,
        override var place: String,

        var header: String,
        var text: String,
        var minAge: Int,
        var maxAge: Int,
        var peopleNumber: Int,
        var cost: Int,
        var deadline: Date,
        var team: String,
//        val contact: Contact,
        var imagePath: String,
        var url: String
) : BaseEvent(title, startDate, endDate, place)
