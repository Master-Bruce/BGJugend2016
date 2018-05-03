package de.schiewe.volker.bgjugend2016.fragments


import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.schiewe.volker.bgjugend2016.BR

import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.models.Event

private const val ARG_EVENT_ID = "event_id"

class EventFragment : Fragment() {
    private var listener: EventFragment.GetEventListener? = null
    private var event: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val eventId = it.getInt(ARG_EVENT_ID)
            event = listener!!.getEventById(eventId)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding: ViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_event, container, false)
        val view: View = binding.root


        // setting values to model
        binding.setVariable(BR.event, event)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is GetEventListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListItemSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface GetEventListener {
        fun getEventById(event_id: Int): Event
    }

    companion object {
        @JvmStatic
        fun newInstance(event_id: Int) =
                EventFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_EVENT_ID, event_id)
                    }
                }
    }

}
