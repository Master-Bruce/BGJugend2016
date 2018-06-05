package de.schiewe.volker.bgjugend2016.activities

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import de.schiewe.volker.bgjugend2016.helper.NOTIFICATION_CHANNEL
import de.schiewe.volker.bgjugend2016.helper.NOTIFICATION_ID_KEY
import de.schiewe.volker.bgjugend2016.helper.NOTIFICATION_INTENT_KEY


class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // create required notification channel on Android 8 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(mChannel)
        }

        val notification = intent.getParcelableExtra(NOTIFICATION_INTENT_KEY) as Notification
        val notificationId = intent.getIntExtra(NOTIFICATION_ID_KEY, -1)
        if (notificationId == -1) {
            Log.e("NotificationReceiver", "No valid notification ID was submitted!")
            return
        }
        notificationManager.notify(notificationId, notification)
    }
}
