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

import kotlinx.android.synthetic.main.item_event.view.*

class EventRecyclerViewAdapter(
        private val mValues: MutableList<BaseEvent>,
        private val mListener: OnListItemSelectedListener?)
    : RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private val EVENT_VIEW_TYPE = 0;
    private val INFO_VIEW_TYPE = 1;

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as BaseEvent
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListItemSelected(item)
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

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val title: TextView = mView.title
        val place: TextView = mView.place
        val date: TextView = mView.date

    }
}
