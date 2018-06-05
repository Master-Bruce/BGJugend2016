package de.schiewe.volker.bgjugend2016.helper

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.RingtoneManager
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.res.ResourcesCompat
import de.schiewe.volker.bgjugend2016.R
import de.schiewe.volker.bgjugend2016.activities.NotificationReceiver
import de.schiewe.volker.bgjugend2016.getIdFromString
import de.schiewe.volker.bgjugend2016.models.Event


const val NOTIFICATION_KEY_PREFIX: String = "NOTIFICATION_FOR_"
const val NOTIFICATION_CHANNEL: String = "Erinnerungen"
const val NOTIFICATION_INTENT_KEY: String = "NOTIFICATION"
const val NOTIFICATION_ID_KEY: String = "NOTIFICATION_ID"

class NotificationHelper(val context: Context) {
    fun restoreNotifactions() {
        TODO("Create all notifications based on data in sharedPreferences")
    }

    fun setNotification(event: Event, value: Boolean) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        with(sharedPref.edit()) {
            putBoolean(NOTIFICATION_KEY_PREFIX + getIdFromString(event.title), value)
            apply()
        }
        if (value)
            this.scheduleNotification(event)
        else
            this.cancelNotification(event)
    }

    fun isNotificationEnabled(eventId: Int): Boolean {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPref.getBoolean(NOTIFICATION_KEY_PREFIX + eventId, false)
    }

    private fun scheduleNotification(event: Event) {
        if (event.startDate == null)
            return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val receiverIntent = Intent(context, NotificationReceiver::class.java)
        receiverIntent.putExtra(NOTIFICATION_INTENT_KEY, this.buildNotification(event))
        receiverIntent.putExtra(NOTIFICATION_ID_KEY, event.pk)

        val pendingIntent = PendingIntent.getBroadcast(context, event.pk, receiverIntent, 0)
        alarmManager.set(AlarmManager.RTC_WAKEUP, getAlarmTime(event.startDate!!), pendingIntent)
    }

    private fun cancelNotification(event: Event) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val receiverIntent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, event.pk, receiverIntent, 0)

        alarmManager.cancel(pendingIntent)
    }

    private fun getAlarmTime(eventStartDate: Long): Long {
        // TODO get settings from sharedPreferences
        return eventStartDate + 5000
    }

    private fun buildNotification(event: Event): Notification {
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                .setContentTitle(event.title)
                .setContentText("Nur noch 10 Tage!")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_stat_ac_unit)
                .setLargeIcon((ResourcesCompat.getDrawable(context.resources, R.drawable.ic_launcher, null) as BitmapDrawable).bitmap)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        return builder.build()
    }

}