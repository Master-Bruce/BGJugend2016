package de.schiewe.volker.bgjugend2016.helper

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.schiewe.volker.bgjugend2016.R


import de.schiewe.volker.bgjugend2016.fragments.EventListFragment.OnListItemSelectedListener
import de.schiewe.volker.bgjugend2016.models.BaseEvent
import de.schiewe.volker.bgjugend2016.models.Event
import de.schiewe.volker.bgjugend2016.models.Info
import de.schiewe.volker.bgjugend2016.viewModels.SharedViewModel

import kotlinx.android.synthetic.main.item_event.view.*
import java.text.SimpleDateFormat
import java.util.*

private const val EVENT_VIEW_TYPE = 0
private const val INFO_VIEW_TYPE = 1
class EventRecyclerViewAdapter(
        private var mValues: List<BaseEvent>,
        private val mListener: OnListItemSelectedListener?,
        private val mViewModel: SharedViewModel)
    : RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    private val sdf = SimpleDateFormat("dd.MM.YYYY", Locale.GERMANY)

    init {
        mOnClickListener = View.OnClickListener { v ->
            if (v.tag is Event){
                mViewModel.select(v.tag as Event)
                mListener?.onEventSelected()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = mValues[position]
        if (item is Event)
            return EVENT_VIEW_TYPE
        else if (item is Info)
            return INFO_VIEW_TYPE
        throw NotImplementedError("Item was neither Event nor Info object.")
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
                when (viewType) {
                    EVENT_VIEW_TYPE -> {
                        LayoutInflater.from(parent.context)
                                .inflate(R.layout.item_event, parent, false)
                    }
                    INFO_VIEW_TYPE -> {
                        LayoutInflater.from(parent.context)
                                .inflate(R.layout.item_info, parent, false)
                    }
                    else -> {
                        throw NotImplementedError("View type was neither Event nor Info object.")
                    }
                }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.title.text = item.title
        holder.place.text = item.place
        holder.date.text = item.dateString()

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    fun setEvents(data: List<BaseEvent>) {
        mValues = data.sorted()
        notifyDataSetChanged()
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val title: TextView = mView.title
        val place: TextView = mView.place
        val date: TextView = mView.date

    }
}
