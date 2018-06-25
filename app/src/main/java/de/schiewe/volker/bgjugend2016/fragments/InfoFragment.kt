package de.schiewe.volker.bgjugend2016.fragments

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.helper.DatabaseHelper
import de.schiewe.volker.bgjugend2016.helper.StaticGridLayoutManager
import de.schiewe.volker.bgjugend2016.helper.YouthWorkerRecyclerViewAdapter
import de.schiewe.volker.bgjugend2016.models.GeneralData
import kotlinx.android.synthetic.main.fragment_info.*

class InfoFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (toolbar as Toolbar).title = getString(R.string.title_informations)
        val databaseHelper = DatabaseHelper(activity!!)

        val adapter = YouthWorkerRecyclerViewAdapter(listOf(), context)
        with(list_youth_worker) {
            this.layoutManager = StaticGridLayoutManager(context, 2)

            this.adapter = adapter
            databaseHelper.getGeneralData().observe(this@InfoFragment, Observer { generalData: GeneralData? ->
                if (generalData != null) {
                    (adapter).setYouthWorkers(generalData.professionals)
                    youth_team.text = generalData.youthTeam
                }
            })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = InfoFragment()
    }
}
