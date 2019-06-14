package de.schiewe.volker.bgjugend2016

import android.app.PendingIntent
import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.preference.PreferenceManager
import de.schiewe.volker.bgjugend2016.activities.NotificationReceiver
import de.schiewe.volker.bgjugend2016.helper.NOTIFICATION_KEY_PREFIX
import de.schiewe.volker.bgjugend2016.helper.NotificationHelper
import de.schiewe.volker.bgjugend2016.models.Event
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 */
@RunWith(AndroidJUnit4::class)
class NotificationTest {
    private val event = Event()
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun initEvent() {
        val calender = Calendar.getInstance()
        calender.add(Calendar.DATE, 30)

        event.pk = 1
        event.startDate = calender.timeInMillis
        event.deadline = calender.timeInMillis
    }


    @Test
    fun testSetNotification() {
        // Context of the app under test.

        val notifications = NotificationHelper(context)

        // Notification is not set before
        var enabled = notifications.isNotificationEnabled(event.pk)
        Assert.assertEquals(false, enabled)
        Assert.assertFalse(isAlarmSet(event))

        notifications.setNotification(event, true)

        // Test Notification is set after call
        enabled = notifications.isNotificationEnabled(event.pk)
        Assert.assertEquals(true, enabled)
        Assert.assertTrue(isAlarmSet(event))
    }

    @Test
    fun testCancelNotification() {
        val notifications = NotificationHelper(context)
        notifications.setNotification(event, true)
        Assert.assertTrue(isAlarmSet(event))

        notifications.setNotification(event, false)
        Assert.assertFalse(isAlarmSet(event))
    }

    @Test
    fun testRestoreNotificationAfterRestart() {
        val notifications = NotificationHelper(context)
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        with(sharedPrefs.edit()) {
            putBoolean(NOTIFICATION_KEY_PREFIX + event.pk, true)
            apply()
        }
        notifications.restoreNotifications(listOf(event), false)
        Assert.assertTrue(isAlarmSet(event, true))
    }

    @Test
    fun testRestoreNotificationAfterChange() {
        val notifications = NotificationHelper(context)
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        notifications.setNotification(event, true)

        with(sharedPrefs.edit()) {
            putBoolean(context.getString(R.string.deadline_notification_key), true)
            apply()
        }
        notifications.restoreNotifications(listOf(event), true)
        Assert.assertTrue(isAlarmSet(event, true))
    }

    @After
    fun cleanAlarms() {
        val notifications = NotificationHelper(context)
        notifications.setNotification(event, false)
    }


    private fun isAlarmSet(event: Event, check_deadline: Boolean = false): Boolean {
        val intent = Intent(context, NotificationReceiver::class.java)
        val date = PendingIntent.getBroadcast(
                context,
                event.pk,
                intent,
                PendingIntent.FLAG_NO_CREATE
        )
        var deadline: PendingIntent? = null
        if (check_deadline) {
            deadline = PendingIntent.getBroadcast(
                    context,
                    event.pk,
                    intent,
                    PendingIntent.FLAG_NO_CREATE
            )
        }
        if (check_deadline)
            return date != null && deadline != null
        return date != null
    }
}
