package de.schiewe.volker.bgjugend2016.helper

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.getByteArrayFromBase64
import de.schiewe.volker.bgjugend2016.models.Contact
import kotlinx.android.synthetic.main.item_youth_worker.view.*

class YouthWorkerRecyclerViewAdapter(
        private var mValues: List<Contact>
) : RecyclerView.Adapter<YouthWorkerRecyclerViewAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.name.text = item.name
        holder.address.text = item.formattedAddress()
        holder.phone.text = item.telephone
        holder.mail.text = item.mail
        if (item.image != "")
            Glide.with(holder.view)
                    .load(getByteArrayFromBase64(item.image))
                    .into(holder.image)
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
        val image: ImageView = view.youth_worker_image
        val name: TextView = view.youth_worker_name
        val address: TextView = view.youth_worker_address
        val phone: TextView = view.youth_worker_phone
        val mail: TextView = view.youth_worker_mail
    }
}