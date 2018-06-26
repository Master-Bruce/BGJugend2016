package de.schiewe.volker.bgjugend2016.fragments


import android.app.TimePickerDialog
import android.arch.lifecycle.Observer
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TimePicker
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.database.DatabaseHelper
import de.schiewe.volker.bgjugend2016.helper.NotificationHelper
import de.schiewe.volker.bgjugend2016.models.Event
import de.schiewe.volker.bgjugend2016.validateDateString
import de.schiewe.volker.bgjugend2016.views.InfoDialogPreference
import de.schiewe.volker.bgjugend2016.views.TimePreference
import kotlinx.android.synthetic.main.pref_screen.*


class PreferenceFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private val events: List<Event> = listOf()

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
        preferenceScreen.findPreference(getString(R.string.deadline_notification_key)).onPreferenceChangeListener = this
        preferenceScreen.findPreference(getString(R.string.feedback_key)).onPreferenceClickListener = this
        preferenceScreen.findPreference(getString(R.string.version_key)).summary = activity?.packageManager?.getPackageInfo(activity?.packageName, 0)?.versionName ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (toolbar as Toolbar).title = getString(R.string.title_settings)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (activity == null || preference == null)
            return
        when (preference) {
            is InfoDialogPreference -> {
                val builder = AlertDialog.Builder(activity!!)
                builder.setMessage(preference.dialogMessage)
                        .setTitle(preference.title)
                        .setPositiveButton(preference.positiveButtonText)
                        { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
                val dialog = builder.create()
                dialog.show()
            }
            is TimePreference -> {
                val onTimeSetListener = TimePickerDialog.OnTimeSetListener { _: TimePicker?, hourOfDay: Int, minute: Int ->
                    preference.setTime(hourOfDay, minute)
                }
                val dialog = TimePickerDialog(activity, onTimeSetListener, preference.getHour(), preference.getMinute(), true)
                dialog.show()
            }
            else -> {
                super.onDisplayPreferenceDialog(preference)
            }
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        if (preference == null)
            return true

        when (preference.key) {
            getString(R.string.birthday_key) -> {
                return if (validateDateString(newValue as String)) {
                    preference.summary = newValue
                    true
                } else {
                    if (view != null)
                        Snackbar.make(view!!, getString(R.string.wrong_date_format), Snackbar.LENGTH_LONG).show()
                    false
                }
            }
            getString(R.string.deadline_notification_key) -> {
                resetNotifications()
            }
            getString(R.string.notification_day_key) -> {
                val dayString = if (newValue == "1") "Tag" else "Tage"
                preference.summary = "$newValue $dayString vorher"
                resetNotifications()
            }
            getString(R.string.notification_time_key) -> {
                preference.summary = newValue as CharSequence?
                resetNotifications()
            }
            else -> {
                preference.summary = newValue as CharSequence?
            }
        }
        return true
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        if (preference == null)
            return false

        when (preference.key) {
            getString(R.string.feedback_key) -> {
                val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "volker.s1994@gmail.com", null))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback zur EBU Jugend App")
                startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail)))
                return true
            }
        }
        return true
    }


    private fun resetNotifications() {
        if (activity == null)
            return

        val notificationHelper = NotificationHelper(activity!!)
        val databaseHelper = DatabaseHelper(activity!!)
        if (events.isEmpty()) {
            databaseHelper.getEvents().observe(activity!!, Observer { baseEvents ->
                if (baseEvents != null) {
                    val events = baseEvents.filter { baseEvent -> baseEvent is Event }.map { baseEvent -> baseEvent as Event }
                    notificationHelper.restoreNotifications(events, true)
                }
            })
        } else {
            notificationHelper.restoreNotifications(events)
        }
    }

    companion object {
        fun newInstance(): PreferenceFragment = PreferenceFragment()
    }
}
