package de.schiewe.volker.bgjugend2016.fragments


import android.app.TimePickerDialog
import androidx.lifecycle.Observer
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.TimePicker
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.database.DatabaseHelper
import de.schiewe.volker.bgjugend2016.helper.Analytics
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
            val preference = preferenceScreen.findPreference<Preference>(key)
            preference?.onPreferenceChangeListener = this
            preference?.summary = preference?.sharedPreferences?.getString(key, "")
            if (key == getString(R.string.notification_day_key)) {
                val value = preference?.sharedPreferences?.getString(key, "")
                if (!value.isNullOrEmpty()){
                    preference.summary = resources.getQuantityString(R.plurals.days_previously, value.toInt(), value)
                }
            }
        }
        preferenceScreen.findPreference<Preference>(getString(R.string.deadline_notification_key))?.onPreferenceChangeListener = this
        preferenceScreen.findPreference<Preference>(getString(R.string.feedback_key))?.onPreferenceClickListener = this
        preferenceScreen.findPreference<Preference>(getString(R.string.version_key))?.summary = activity?.packageName?.let { activity?.packageManager?.getPackageInfo(it, 0)?.versionName }
                ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (toolbar as Toolbar).title = getString(R.string.title_settings)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        activity?.let {
            when (preference) {
                is InfoDialogPreference -> {
                    val builder = AlertDialog.Builder(it)
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
                    val dialog = TimePickerDialog(it, onTimeSetListener, preference.getHour(), preference.getMinute(), true)
                    dialog.show()
                }
                else -> {
                    super.onDisplayPreferenceDialog(preference)
                }
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
                    view?.let { Snackbar.make(it, getString(R.string.wrong_date_format), Snackbar.LENGTH_LONG).show() }
                    false
                }
            }
            getString(R.string.deadline_notification_key) -> {
                resetNotifications()
            }
            getString(R.string.notification_day_key) -> {
                preference.summary = resources.getQuantityString(R.plurals.days_previously, (newValue as String).toInt(), newValue)
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
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_to_app))
                startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail)))
                return true
            }
        }
        return true
    }

    override fun onAttach(context: Context) {
        activity?.let { Analytics.setScreen(it, javaClass.simpleName) }
        super.onAttach(context)
    }

    private fun resetNotifications() {
        activity?.let {
            val notificationHelper = NotificationHelper(it)
            val databaseHelper = DatabaseHelper(it)
            if (events.isEmpty()) {
                databaseHelper.getEvents().observe(it, Observer { baseEvents ->
                    if (baseEvents != null) {
                        val events = baseEvents.filter { baseEvent -> baseEvent is Event }.map { baseEvent -> baseEvent as Event }
                        notificationHelper.restoreNotifications(events, true)
                    }
                })
            } else {
                notificationHelper.restoreNotifications(events)
            }
        }
    }

    companion object {
        fun newInstance(): PreferenceFragment = PreferenceFragment()
    }
}
