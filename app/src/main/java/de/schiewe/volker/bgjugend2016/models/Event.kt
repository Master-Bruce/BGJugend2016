package de.schiewe.volker.bgjugend2016.models

import androidx.lifecycle.Observer
import android.content.Context
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import android.text.Spanned
import com.google.firebase.storage.FirebaseStorage
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.database.DatabaseHelper
import de.schiewe.volker.bgjugend2016.formatDate
import de.schiewe.volker.bgjugend2016.fromHtml
import de.schiewe.volker.bgjugend2016.interfaces.DownloadUrlListener

const val EVENT_IMG = "event_img"

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

    private var imageDownloadUrl: String? = null

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

    fun formattedText(): Spanned {
        return fromHtml(this.text)
    }

    fun downloadUrlListener(context: FragmentActivity, storage: FirebaseStorage, listener: DownloadUrlListener) {
        val imageDownloadUrl = this.imageDownloadUrl
        if (imageDownloadUrl != null)
            listener.onSuccess(imageDownloadUrl)
        else {
            val db = DatabaseHelper(context)
            db.getGeneralData().observe(context, Observer { generalData ->
                if (generalData != null){
                    val year = generalData.currentDatabaseName
                    storage.getReference("$EVENT_IMG/$year/${this.imagePath}").downloadUrl.addOnSuccessListener { uri: Uri? ->
                        this.imageDownloadUrl = uri.toString()
                        listener.onSuccess(uri.toString())
                    }
                }
            })
        }
    }

}