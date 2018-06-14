package de.schiewe.volker.bgjugend2016.fragments


import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TimePicker
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.helper.InfoDialogPreference
import de.schiewe.volker.bgjugend2016.helper.TimePreference
import kotlinx.android.synthetic.main.pref_screen.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class PreferenceFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val keys = listOf(
                getString(R.string.name_key),
                getString(R.string.street_key),
                getString(R.string.place_key),
                getString(R.string.birthday_key),
                getString(R.string.telephone_key),
                getString(R.string.notification_time_key),
                getString(R.string.notification_day_key)
        )
        for (key in keys) {
            val preference = preferenceScreen.findPreference(key)
            preference.onPreferenceChangeListener = this
            preference.summary = preference.sharedPreferences.getString(key, "")
            if (key == getString(R.string.notification_day_key)) {
                val value = preference.sharedPreferences.getString(key, "")
                if (value != "") {
                    val dayString = if (value == "1") "Tag" else "Tage"
                    preference.summary = "$value $dayString vorher"
                }
            }
        }
        preferenceScreen.findPreference(getString(R.string.version_key)).summary = activity?.packageManager?.getPackageInfo(activity?.packageName, 0)?.versionName ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (toolbar as Toolbar).title = getString(R.string.title_settings)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (activity != null && preference != null) {
            when (preference) {
                is InfoDialogPreference -> {
                    val builder = AlertDialog.Builder(activity!!)
                    builder.setMessage(preference.dialogMessage)
                            .setTitle(preference.title)
                            .setPositiveButton(preference.positiveButtonText, { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() })
                    val dialog = builder.create()
                    dialog.show()
                }
                is TimePreference -> {
                    val onTimeSetListener = TimePickerDialog.OnTimeSetListener(
                            { _: TimePicker?, hourOfDay: Int, minute: Int ->
                                preference.setTime(hourOfDay, minute)
                            })
                    val dialog = TimePickerDialog(activity, onTimeSetListener, preference.getHour(), preference.getMinute(), true)
                    dialog.show()
                }
                else -> {
                    super.onDisplayPreferenceDialog(preference)
                }
            }
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        if (preference != null) {
            when (preference.key) {
                getString(R.string.birthday_key) -> {
                    return try {
                        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY)
                        sdf.parse(newValue as String?)
                        preference.summary = newValue
                        true
                    } catch (pe: ParseException) {
                        if (view != null)
                            Snackbar.make(view!!, getString(R.string.wrong_date_format), Snackbar.LENGTH_LONG).show()
                        false
                    }
                }
                getString(R.string.notification_day_key) -> {
                    val dayString = if (newValue == "1") "Tag" else "Tage"
                    preference.summary = "$newValue $dayString vorher"
                }
                else -> {
                    preference.summary = newValue as CharSequence?
                }
            }
        }
        return true
    }

    companion object {
        fun newInstance(): PreferenceFragment = PreferenceFragment()
    }
}
