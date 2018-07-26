package de.schiewe.volker.bgjugend2016.layout

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.models.Contact
import kotlinx.android.synthetic.main.item_youth_worker.view.*

class YouthWorkerRecyclerViewAdapter: RecyclerView.Adapter<YouthWorkerRecyclerViewAdapter.ViewHolder>() {
    private var mValues = listOf<Contact>()
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.name.text = item.name
        holder.address.text = item.formattedAddress()
        holder.phone.text = item.telephone
        holder.mail.text = item.mail
        if (item.image != "") {
            holder.image.setImageURI(item.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_youth_worker, parent, false)

        return ViewHolder(view)
    }

    fun setYouthWorkers(data: List<Contact>) {
        mValues = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = mValues.count()

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.youth_worker_name
        val address: TextView = view.youth_worker_address
        val phone: TextView = view.youth_worker_phone
        val mail: TextView = view.youth_worker_mail
        val image: SimpleDraweeView = view.youth_worker_image

        init {
            val imageRounding = RoundingParams()
            imageRounding.roundAsCircle = true
            this.image.hierarchy.roundingParams = imageRounding
        }
    }
}