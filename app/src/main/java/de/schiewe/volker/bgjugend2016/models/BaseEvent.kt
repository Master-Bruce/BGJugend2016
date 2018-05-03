package de.schiewe.volker.bgjugend2016.models

import java.util.*

open class BaseEvent(
       open var title: String,
       open var startDate: Date,
       open  var endDate: Date,
       open var place: String
)