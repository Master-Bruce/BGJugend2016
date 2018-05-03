package de.schiewe.volker.bgjugend2016.fragments


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
import de.schiewe.volker.bgjugend2016.viewModels.SharedViewModel


class EventFragment : Fragment() {
    private var event: Event? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding: ViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_event, container, false)
        val view: View = binding.root
        val sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
        sharedViewModel.getSelected().observe(activity!!, Observer { item ->
            event = item
        });

        // setting values to model
        binding.setVariable(BR.event, event)

        return view
    }


    companion object {
        @JvmStatic
        fun newInstance(): EventFragment = EventFragment()
    }

}
