package de.schiewe.volker.bgjugend2016.models

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.formatDate


class Event : BaseEvent() {
    override var title: String = ""
    override var startDate: Long? = null
    override var endDate: Long? = null
    override var place: String = ""

    var pk: Int = 0
    var header: String = ""
    var text: String = ""
    var minAge: Int = 0
    var maxAge: Int = 100
    var ageText: String? = null
    var peopleNumber: Int? = null
    var cost: Int? = null
    var costText: String? = null
    var deadline: Long? = null
    var team: String = ""
    val contact: Contact? = null
    var imagePath: String = ""
    var url: String = ""

    fun ageString(context: Context): String {
        val ageText = this.ageText
        if (ageText != null)
            return ageText
        if (this.maxAge == 100 && this.minAge != 0)
            return context.getString(R.string.from_x_years, this.minAge)
        else if (this.maxAge != 100 && this.minAge != 0)
            return context.getString(R.string.from_x_until_y_years, this.minAge, this.maxAge)
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
        return formatDate(this.deadline, sdf)
    }

    @SuppressWarnings("deprecation")
    fun formattedText(): Spanned {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Html.fromHtml(this.text, Html.FROM_HTML_MODE_COMPACT)
        return Html.fromHtml(this.text)
    }
}