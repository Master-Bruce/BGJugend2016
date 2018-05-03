package de.schiewe.volker.bgjugend2016.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import de.schiewe.volker.bgjugend2016.R

class InfoFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = InfoFragment()
    }
}
