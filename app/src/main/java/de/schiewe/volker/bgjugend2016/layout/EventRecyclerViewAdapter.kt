package de.schiewe.volker.bgjugend2016.layout

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.schiewe.volker.bgjugend2016.R

import de.schiewe.volker.bgjugend2016.interfaces.OnListItemSelectedListener
import de.schiewe.volker.bgjugend2016.models.BaseEvent
import de.schiewe.volker.bgjugend2016.models.Event
import de.schiewe.volker.bgjugend2016.models.Info
import de.schiewe.volker.bgjugend2016.models.UserData
import de.schiewe.volker.bgjugend2016.views.SharedViewModel

import kotlinx.android.synthetic.main.item_event.view.*
import java.util.*

private const val EVENT_VIEW_TYPE = 0
private const val INFO_VIEW_TYPE = 1

class EventRecyclerViewAdapter(
        private val itemSelectedListener: OnListItemSelectedListener?,
        private val sharedViewModel: SharedViewModel,
        private val context: Context
) : RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder>(), SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {
    private var mValues: List<BaseEvent> = listOf()
    private var mFilteredValues: MutableList<BaseEvent> = mutableListOf()
    private val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    init {
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
            setOnClickListener(this@EventRecyclerViewAdapter)
        }
    }

    override fun getItemCount(): Int = mFilteredValues.size

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val preferencesKeys: List<String> = listOf(
                context.getString(R.string.filter_infos_key),
                context.getString(R.string.filter_age_key),
                context.getString(R.string.filter_old_events_key)
        )
        if (preferencesKeys.contains(key))
            this.filterItems()
    }

    override fun onClick(v: View?) {
        if (v != null && v.tag is Event) {
            sharedViewModel.select(v.tag as Event)
            itemSelectedListener?.onEventSelected()
        }
    }

    fun setEvents(data: List<BaseEvent>) {
        mValues = data.sorted()
        filterItems()
    }

    private fun filterItems() {
        mFilteredValues = mutableListOf()
        for (item in mValues) {
            var shouldAddItem = true
            if (sharedPref.getBoolean(context.getString(R.string.filter_infos_key), false) && item is Info)
                shouldAddItem = false
            if (sharedPref.getBoolean(context.getString(R.string.filter_age_key), false) && item is Event) {
                val user = UserData(context, sharedPref)
                val age = user.getAge(Calendar.getInstance())
                if (age != null && (item.minAge > age || item.maxAge < age))
                    shouldAddItem = false
            }
            val eventDate = item.endDate ?: (item.startDate ?: Date().time)
            if (!sharedPref.getBoolean(context.getString(R.string.filter_old_events_key), false) && eventDate < Date().time)
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
