package de.schiewe.volker.bgjugend2016.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.database.DatabaseHelper
import de.schiewe.volker.bgjugend2016.layout.EventRecyclerViewAdapter
import de.schiewe.volker.bgjugend2016.models.BaseEvent
import de.schiewe.volker.bgjugend2016.views.SharedViewModel
import kotlinx.android.synthetic.main.fragment_event_list.*


class EventListFragment : Fragment() {
    private var itemSelectedListener: OnListItemSelectedListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar = toolbar as Toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.title_events)
        val sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
        event_list_progress.visibility = View.VISIBLE
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        val adapter = EventRecyclerViewAdapter(listOf(), itemSelectedListener, sharedViewModel, sharedPref,
                getString(R.string.filter_infos_key),
                getString(R.string.filter_age_key),
                getString(R.string.filter_old_events_key),
                getString(R.string.birthday_key)
        )

        filter_button.setOnClickListener { _ -> itemSelectedListener?.onFilterButtonClicked() }
        // Set the adapter
        val databaseHelper = DatabaseHelper(activity!!)
        with(list) {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
            databaseHelper.getEvents().observe(this@EventListFragment, Observer<List<BaseEvent>> { events ->
                (adapter).setEvents(events!!)
                event_list_progress.visibility = View.GONE
            })
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListItemSelectedListener) {
            itemSelectedListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListItemSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        itemSelectedListener = null
    }

    interface OnListItemSelectedListener {
        fun onEventSelected()
        fun onFilterButtonClicked()
    }

    companion object {
        @JvmStatic
        fun newInstance(): EventListFragment = EventListFragment()
    }
}
