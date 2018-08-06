package de.schiewe.volker.bgjugend2016.models

import android.content.Context
import android.text.Spanned
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.fromHtml

class Info : BaseEvent() {
    override var title: String = ""
    override var startDate: Long? = null
    override var endDate: Long? = null
    override var place: String = ""
    override var dateText: String? = null

    var who: String = ""
    var registration: String = ""

    fun dialogText(context: Context): Spanned {
        var message = context.getString(R.string.info_when_and_where, this.dateString(), this.place)

        if (this.who != "") {
            message += context.getString(R.string.info_who, this.who)
        }
        message += context.getString(R.string.info_registration, this.registration)

        return fromHtml(message)
    }

    fun getMail(): String? {
        val regex = "\\S+@\\S+\\.\\S+".toRegex()
        return regex.find(this.registration)?.value
    }
}
