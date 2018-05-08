package de.schiewe.volker.bgjugend2016.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.schiewe.volker.bgjugend2016.R

import de.schiewe.volker.bgjugend2016.helper.EventRecyclerViewAdapter
import de.schiewe.volker.bgjugend2016.models.BaseEvent
import de.schiewe.volker.bgjugend2016.viewModels.EventViewModel
import de.schiewe.volker.bgjugend2016.viewModels.SharedViewModel
import kotlinx.android.synthetic.main.fragment_event_list.*


class EventListFragment : Fragment() {
    private var itemSelectedListener: OnListItemSelectedListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
        val sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
        filter_button.setOnClickListener({ _ -> itemSelectedListener?.onFilterButtonClicked() })
        // Set the adapter
        with(list) {
            layoutManager = LinearLayoutManager(context)
            adapter = EventRecyclerViewAdapter(listOf(), itemSelectedListener, sharedViewModel)
            eventViewModel.getEvents().observe(this@EventListFragment, Observer<List<BaseEvent>> { events ->
                (adapter as EventRecyclerViewAdapter).setEvents(events!!)
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
