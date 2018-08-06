package de.schiewe.volker.bgjugend2016.models

import android.text.Spanned
import de.schiewe.volker.bgjugend2016.fromHtml


class Contact {
    val name: String = ""
    val address: String = ""
    val telephone: String = ""
    val mail: String = ""
    val image: String = ""


    fun formattedAddress():Spanned{
        return fromHtml(this.address)
    }
}


