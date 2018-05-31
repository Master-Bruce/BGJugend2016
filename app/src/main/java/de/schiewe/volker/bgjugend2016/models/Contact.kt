package de.schiewe.volker.bgjugend2016.models

import android.os.Build
import android.text.Html
import android.text.Spanned


class Contact {
    val name: String = ""
    val address: String = ""
    val telephone: String = ""
    val mail: String = ""
    val image: String = ""

    fun formattedAddress():Spanned{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Html.fromHtml(this.address, Html.FROM_HTML_MODE_COMPACT)
        return Html.fromHtml(this.address)
    }
}


