package de.schiewe.volker.bgjugend2016.fragments


import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat

import de.schiewe.volker.bgjugend2016.R


class PreferenceFragment : PreferenceFragmentCompat() {


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    companion object {
        fun newInstance(): PreferenceFragment = PreferenceFragment()
    }
}
