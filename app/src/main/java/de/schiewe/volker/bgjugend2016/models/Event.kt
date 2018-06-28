package de.schiewe.volker.bgjugend2016.models

import android.os.Build
import android.text.Html
import android.text.Spanned
import de.schiewe.volker.bgjugend2016.formatDate
import java.text.SimpleDateFormat
import java.util.*


class Event : BaseEvent() {
    override var title: String = ""
    override var startDate: Long? = null
    override var endDate: Long? = null
    override var place: String = ""

    var pk: Int = 0
    var header: String = ""
    var text: String = ""
    var minAge: Int? = null
    var maxAge: Int? = null
    var ageText: String? = null
    var peopleNumber: Int? = null
    var cost: Int? = null
    var costText: String? = null
    var deadline: Long? = null
    var team: String = ""
    val contact: Contact? = null
    var imagePath: String = ""
    var url: String = ""

    fun ageString(): String {
        if (this.ageText != null)
            return this.ageText!!
        if (this.maxAge == null && this.minAge != null)
            return "ab ${this.minAge} Jahren"
        else if (this.maxAge != null && this.minAge != null)
            return "von ${this.minAge} bis ${this.maxAge} Jahren"
        return ""
    }

    fun costString(): String {
        if (this.cost == null)
            return ""
        var text = "${this.cost} €"
        if (this.costText != null)
            text += " " + this.costText
        return text
    }

    fun deadlineString(): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY)
        return formatDate(this.deadline, sdf)
    }

    fun formattedText(): Spanned {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Html.fromHtml(this.text, Html.FROM_HTML_MODE_COMPACT)
        return Html.fromHtml(this.text)
    }

    companion object Factory {
        fun create(): Event = Event()
    }

}