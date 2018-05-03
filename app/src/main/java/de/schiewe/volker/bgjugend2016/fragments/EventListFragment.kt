package de.schiewe.volker.bgjugend2016.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.schiewe.volker.bgjugend2016.R

import de.schiewe.volker.bgjugend2016.helper.EventRecyclerViewAdapter
import de.schiewe.volker.bgjugend2016.models.BaseEvent
import de.schiewe.volker.bgjugend2016.models.Event
import de.schiewe.volker.bgjugend2016.models.Info
import de.schiewe.volker.bgjugend2016.testContent
import java.util.*

class EventListFragment : Fragment() {
    private var listener: OnListItemSelectedListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_event_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = EventRecyclerViewAdapter(testContent, listener)
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListItemSelectedListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListItemSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnListItemSelectedListener {
        fun onListItemSelected(item: BaseEvent)
    }

    companion object {
        @JvmStatic
        fun newInstance(): EventListFragment = EventListFragment()
    }
}
