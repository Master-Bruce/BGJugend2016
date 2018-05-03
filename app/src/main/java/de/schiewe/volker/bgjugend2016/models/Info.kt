package de.schiewe.volker.bgjugend2016.models

import java.util.*

class Info(
        override var title: String,
        override var startDate: Date,
        override var endDate: Date,
        override var place: String,

        var who: String,
        var regestration: String

) : BaseEvent(title, startDate, endDate, place)