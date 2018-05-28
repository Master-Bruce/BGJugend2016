package de.schiewe.volker.bgjugend2016.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import de.schiewe.volker.bgjugend2016.R
import kotlinx.android.synthetic.main.fragment_filter_bottom_sheet.*

class FilterModalBottomSheet : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_filter_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val filterInfos = sharedPref.getBoolean(getString(R.string.filter_infos_key), false)
        val filterAge = sharedPref.getBoolean(getString(R.string.filter_age_key), false)
        val filterOldEvents = sharedPref.getBoolean(getString(R.string.filter_old_events_key), true)

        switch_info_filter.isChecked = filterInfos
        switch_age_filter.isChecked = filterAge
        switch_old_events_filter.isChecked = filterOldEvents

        container_info_filer.setOnClickListener { _: View ->
            this.handleFilterClick(sharedPref, switch_info_filter, getString(R.string.filter_infos_key))
        }

        container_age_filter.setOnClickListener { _: View ->
            if (sharedPref.getInt(getString(R.string.birthday_key), -1) != -1)
                this.handleFilterClick(sharedPref, switch_age_filter, getString(R.string.filter_age_key))
            else
                Toast.makeText(activity, getString(R.string.missing_birthday), Toast.LENGTH_LONG).show()
                // TODO Fix SnackBar
                // Snackbar.make(view, getString(R.string.missing_birthday), Snackbar.LENGTH_LONG).show()
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

}