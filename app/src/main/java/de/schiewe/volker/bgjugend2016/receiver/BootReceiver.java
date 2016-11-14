package de.schiewe.volker.bgjugend2016.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.schiewe.volker.bgjugend2016.DatabaseListener;
import de.schiewe.volker.bgjugend2016.R;
import de.schiewe.volker.bgjugend2016.activities.MainActivity;
import de.schiewe.volker.bgjugend2016.data_models.Event;
import de.schiewe.volker.bgjugend2016.fragments.SettingsFragment;
import de.schiewe.volker.bgjugend2016.helper.FirebaseHandler;

public class BootReceiver extends BroadcastReceiver {
    private Context mContext;
    private SharedPreferences sharedPref;
    private Event currEvent;
    private FirebaseHandler firebaseHandler;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            mContext = context;
            sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            firebaseHandler = FirebaseHandler.getInstance(context);
            DatabaseListener listener = new DatabaseListener() {
                @Override
                public void onDataCreated() {
                    ArrayList<Event> events = firebaseHandler.getEvents();
                    for (int i = 0; i < events.size(); i++) {
                        currEvent = events.get(i);

                        boolean isNotificationEnabled = sharedPref.getBoolean("EventNotification" + currEvent.getId(), false);
                        if (isNotificationEnabled) {
                            setNotificationAlarm(currEvent.getDate(), currEvent.getTitle(), currEvent.getId());
                        }
                    }
                }
            };
            firebaseHandler.setDbListener(listener);
        }
    }

    private void setNotificationAlarm(String date, String title, int eventID) {
        String dayDifference = sharedPref.getString(SettingsFragment.NOTIFICATION_DAYS_BEFORE, "2");
        AlarmManager alarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        Calendar nowCal = Calendar.getInstance();
        int broadcastID = eventID * 10;
        String notiText = mContext.getString(R.string.event_noti_text) + " " + dayDifference + " Tagen";
        long dateInMillis = MainActivity.getDateObject(date).getTime();

        for (int i = 0; i < 2; i++) {
            Date d = new Date(dateInMillis - Integer.parseInt(dayDifference) * 24 * 3600 * 1000);
            d = addPrefTime(d);
            calendar.setTime(d);

            if (calendar.before(nowCal)) {
                return;
            }
            Intent intent = new Intent(mContext, AlarmReceiver.class);
            intent.putExtra(MainActivity.NOTIFY_TITLE, title);
            intent.putExtra(MainActivity.NOTIFY_TEXT, notiText);
            intent.putExtra(MainActivity.NOTIFY_EVENT_ID, currEvent.getId());
            PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, broadcastID, intent, PendingIntent.FLAG_ONE_SHOT);

            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

            broadcastID += 5;
            notiText = "Der Anmeldeschluss läuft heut ab!";
            dateInMillis = currEvent.getdDeadline().getTime();

        }
    }

    private Date addPrefTime(Date date) {
        int hour = Integer.parseInt(sharedPref.getString(SettingsFragment.NOTIFICATION_TIME, "9"));
        date = new Date(date.getTime() + hour * 3600 * 1000);
        return date;
    }
}
