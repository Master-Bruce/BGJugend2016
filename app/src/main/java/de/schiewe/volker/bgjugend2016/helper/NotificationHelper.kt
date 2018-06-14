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
import de.schiewe.volker.bgjugend2016.models.Event
import java.util.*


const val NOTIFICATION_KEY_PREFIX: String = "NOTIFICATION_FOR_"
const val NOTIFICATION_CHANNEL: String = "Erinnerungen"
const val NOTIFICATION_INTENT_KEY: String = "NOTIFICATION"
const val NOTIFICATION_ID_KEY: String = "NOTIFICATION_ID"
const val DEADLINE_NOTIFICATION: Int = 0
const val DATE_NOTIFICATION: Int = 1

class NotificationHelper(val context: Context) {
    private val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

    fun restoreNotifactions(eventList: List<Event>) {
        for (event in eventList){
            if (sharedPref.getBoolean(NOTIFICATION_KEY_PREFIX + event.pk, false)){
                setNotification(event, true)
            }
        }
    }

    fun setNotification(event: Event, value: Boolean): Boolean {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val deadlineNotification: Boolean = sharedPref.getBoolean(context.getString(R.string.deadline_notification_key), false)
        val returnValue = if (value) {
            val notificationSet = this.scheduleNotification(event, DATE_NOTIFICATION)
            if (deadlineNotification)
                this.scheduleNotification(event, DEADLINE_NOTIFICATION) || notificationSet
            else
                notificationSet
        } else {
            this.cancelNotification(event)
            true
        }
        if (returnValue)
            with(sharedPref.edit()) {
                putBoolean(NOTIFICATION_KEY_PREFIX + event.pk, value)
                apply()
            }
        return returnValue
    }

    fun isNotificationEnabled(eventId: Int): Boolean {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPref.getBoolean(NOTIFICATION_KEY_PREFIX + eventId, false)
    }

    private fun scheduleNotification(event: Event, type: Int): Boolean {
        if (event.startDate == null || event.deadline == null)
            return false

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val receiverIntent = Intent(context, NotificationReceiver::class.java)
        val broadcastId = if (type == DATE_NOTIFICATION) event.pk else 100 + event.pk
        val notification = this.buildNotification(event, type)

        receiverIntent.putExtra(NOTIFICATION_INTENT_KEY, notification)
        receiverIntent.putExtra(NOTIFICATION_ID_KEY, broadcastId)
        val pendingIntent = PendingIntent.getBroadcast(context, broadcastId, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmTime = if (type == DATE_NOTIFICATION)
            getAlarmTime(event.startDate)
        else
            getAlarmTime(event.deadline)

        if (alarmTime != null && alarmTime.after(Calendar.getInstance())) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.timeInMillis, pendingIntent)
            return true
        }
        return false
    }

    private fun cancelNotification(event: Event) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val receiverIntent = Intent(context, NotificationReceiver::class.java)

        // cancel notification on date
        var pendingIntent = PendingIntent.getBroadcast(context, event.pk, receiverIntent, 0)
        alarmManager.cancel(pendingIntent)

        // cancel notification on deadline
        pendingIntent = PendingIntent.getBroadcast(context, 100 + event.pk, receiverIntent, 0)
        alarmManager.cancel(pendingIntent)

    }

    private fun getAlarmTime(eventStartDate: Long?): Calendar? {
        val notificationDay = sharedPref.getString(context.getString(R.string.notification_day_key), "7")
        val notificationTime = sharedPref.getString(context.getString(R.string.notification_time_key), "18:00")
        if (notificationDay == "" || notificationTime == "" || eventStartDate == null)
            return null
        val hourOfDay = notificationTime.split(":".toRegex())[0].toInt()
        val minute = notificationTime.split(":".toRegex())[1].toInt()
        val date = Calendar.getInstance()
        date.timeInMillis = eventStartDate
        date.add(Calendar.HOUR_OF_DAY, hourOfDay)
        date.add(Calendar.MINUTE, minute)
        date.add(Calendar.DAY_OF_YEAR, -notificationDay.toInt())
        return date
    }

    private fun createNotificationText(type: Int): String {
        val daysLeft = sharedPref.getString(context.getString(R.string.notification_day_key), "7")
        when (type) {
            DATE_NOTIFICATION -> {
                return if (daysLeft == "1") {
                    "Morgen gehts los!"
                } else {
                    "Nur noch $daysLeft Tage!"
                }
            }
            DEADLINE_NOTIFICATION -> {
                return if (daysLeft == "1") {
                    "Bis morgen anmelden!"
                } else {
                    "Noch $daysLeft Tage um Dich anzumelden!"
                }
            }
        }
        throw IllegalArgumentException("Wrong Notification Type")
    }

    private fun buildNotification(event: Event, type: Int): Notification {
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                .setContentTitle(event.title)
                .setContentText(createNotificationText(type))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notication)
                .setLargeIcon((ResourcesCompat.getDrawable(context.resources, R.drawable.youth_sheep, null) as BitmapDrawable).bitmap)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        return builder.build()
    }

}