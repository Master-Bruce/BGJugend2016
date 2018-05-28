package de.schiewe.volker.bgjugend2016.fragments


import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.helper.InfoDialogPreference
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class PreferenceFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        if (preference != null) {
            when (preference.key) {
                getString(R.string.birthday_key) -> {
                    return try {
                        val sdf = SimpleDateFormat("dd.MM.YYYY", Locale.GERMANY)
                        sdf.parse(newValue as String?)
                        preference.summary = newValue
                        true
                    } catch (pe: ParseException) {
                        if (view != null)
                            Snackbar.make(view!!, getString(R.string.wrong_date_format), Snackbar.LENGTH_LONG).show()
                        false
                    }
                }
                else -> {
                    preference.summary = newValue as CharSequence?
                }
            }
        }
        return true
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val keys = listOf(
                getString(R.string.name_key),
                getString(R.string.street_key),
                getString(R.string.place_key),
                getString(R.string.birthday_key),
                getString(R.string.telephone_key)
        )
        for (key in keys) {
            val preference = preferenceScreen.findPreference(key)
            preference.onPreferenceChangeListener = this
            preference.summary = preference.sharedPreferences.getString(preference.key, "")
        }
        preferenceScreen.findPreference(getString(R.string.version_key)).summary = activity?.packageManager?.getPackageInfo(activity?.packageName, 0)?.versionName ?: ""
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (preference != null) {
            if (activity != null && preference is InfoDialogPreference) {
                val builder = AlertDialog.Builder(activity!!)
                builder.setMessage(preference.dialogMessage)
                        .setTitle(preference.title)
                        .setPositiveButton(preference.positiveButtonText, { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() })
                val dialog = builder.create()
                dialog.show()
            } else
                super.onDisplayPreferenceDialog(preference)
        }
    }

    companion object {
        fun newInstance(): PreferenceFragment = PreferenceFragment()
    }
}
