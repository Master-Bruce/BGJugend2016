package de.schiewe.volker.bgjugend2016.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.helper.Analytics
import kotlinx.android.synthetic.main.fragment_filter_bottom_sheet.*

class FilterModalBottomSheet : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_filter_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        val filterInfos = sharedPref.getBoolean(getString(R.string.filter_infos_key), false)
        val filterAge = sharedPref.getBoolean(getString(R.string.filter_age_key), false)
        val filterOldEvents = sharedPref.getBoolean(getString(R.string.filter_old_events_key), false)

        switch_info_filter.isChecked = filterInfos
        switch_age_filter.isChecked = filterAge
        switch_old_events_filter.isChecked = filterOldEvents

        container_info_filer.setOnClickListener {
            this.handleFilterClick(sharedPref, switch_info_filter, getString(R.string.filter_infos_key))
        }

        container_age_filter.setOnClickListener { _: View ->
            if (sharedPref.getString(getString(R.string.birthday_key), "") != "")
                this.handleFilterClick(sharedPref, switch_age_filter, getString(R.string.filter_age_key))
            else
                Toast.makeText(activity, getString(R.string.missing_birthday), Toast.LENGTH_SHORT).show()
        }

        container_old_events_filer.setOnClickListener { _: View ->
            this.handleFilterClick(sharedPref, switch_old_events_filter, getString(R.string.filter_old_events_key))
        }
    }

    private fun handleFilterClick(sharedPref: SharedPreferences, switch: Switch, preferencesKey: String) {
        val currentValue: Boolean = switch.isChecked
        with(sharedPref.edit())
        {
            putBoolean(preferencesKey, !currentValue)
            apply()
        }
        switch.isChecked = !currentValue
    }

    override fun onAttach(context: Context) {
        activity?.let {
            Analytics.setScreen(it, javaClass.simpleName)
        }
        super.onAttach(context)
    }

}