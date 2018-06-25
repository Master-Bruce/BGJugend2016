package de.schiewe.volker.bgjugend2016.helper

import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.schiewe.volker.bgjugend2016.R


import de.schiewe.volker.bgjugend2016.fragments.EventListFragment.OnListItemSelectedListener
import de.schiewe.volker.bgjugend2016.getAge
import de.schiewe.volker.bgjugend2016.models.BaseEvent
import de.schiewe.volker.bgjugend2016.models.Event
import de.schiewe.volker.bgjugend2016.models.Info
import de.schiewe.volker.bgjugend2016.viewModels.SharedViewModel

import kotlinx.android.synthetic.main.item_event.view.*
import java.util.*

private const val EVENT_VIEW_TYPE = 0
private const val INFO_VIEW_TYPE = 1

class EventRecyclerViewAdapter(
        private var mValues: List<BaseEvent>,
        private val mListener: OnListItemSelectedListener?,
        private val mViewModel: SharedViewModel,
        private val sharedPref: SharedPreferences,

        private val filterInfoKey: String,
        private val filterAgeKey: String,
        private val filterOldEventsKey: String,
        private val birthdayKey: String
) : RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder>(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (preferencesKeys.contains(key))
            this.filterItems()
    }

    private val mOnClickListener: View.OnClickListener
    private var mFilteredValues: MutableList<BaseEvent> = mutableListOf()
    private val preferencesKeys: List<String> = listOf(filterInfoKey, filterAgeKey, filterOldEventsKey)

    init {
        mOnClickListener = View.OnClickListener { v ->
            if (v.tag is Event) {
                mViewModel.select(v.tag as Event)
                mListener?.onEventSelected()
            }
        }
        for (item in mValues)
            mFilteredValues.add(item)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
    }

    override fun getItemViewType(position: Int): Int {
        val item = mFilteredValues[position]
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

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        sharedPref.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mFilteredValues[position]
        holder.title.text = item.title
        holder.place.text = item.place
        holder.date.text = item.dateString()
        if (item is Info && item.dateText != null)
            holder.date.text = item.dateText
        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mFilteredValues.size

    fun setEvents(data: List<BaseEvent>) {
        mValues = data.sorted()
        filterItems()
    }

    private fun filterItems() {
        mFilteredValues = mutableListOf()
        for (item in mValues) {
            var shouldAddItem = true
            if (sharedPref.getBoolean(filterInfoKey, false) && item is Info)
                shouldAddItem = false
            if (sharedPref.getBoolean(filterAgeKey, false) && item is Event) {
                val age = getAge(sharedPref.getString(birthdayKey, ""), Calendar.getInstance())
                if (age != -1 && item.minAge!! > age || item.maxAge!! < age)
                    shouldAddItem = false
            }
            if (!sharedPref.getBoolean(filterOldEventsKey, true) && item.endDate != null && item.endDate!! < Date().time)
                shouldAddItem = false

            if (shouldAddItem)
                mFilteredValues.add(item)
        }
        notifyDataSetChanged()

    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val title: TextView = mView.title
        val place: TextView = mView.place
        val date: TextView = mView.date

    }
}
