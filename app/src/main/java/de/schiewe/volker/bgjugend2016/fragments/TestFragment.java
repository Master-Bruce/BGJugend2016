package de.schiewe.volker.bgjugend2016.fragments;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.schiewe.volker.bgjugend2016.R;
import de.schiewe.volker.bgjugend2016.activities.MainActivity;
import de.schiewe.volker.bgjugend2016.activities.NotificationActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestFragment extends Fragment {

    private Button setAlarm;
    private Context context;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);


        context = getActivity();

        setAlarm = (Button) view.findViewById(R.id.setAlarm);

        return view;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Prepare intent which is triggered if the
                // notification is selected
                Intent intent = new Intent(context, NotificationActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

                // Build notification
                // Actions are just fake
                NotificationManager mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_notification)
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                                .setContentTitle("Title")
                                .setContentText("Text")
                                .addAction(0, "Später erinnern", pIntent)
                                .setDefaults(NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_SOUND)
                                .setAutoCancel(true);

                Intent resultIntent = new Intent(context, MainActivity.class);
                resultIntent.putExtra(MainActivity.NOTIFY_EVENT_ID, 0);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT);
                mBuilder.setContentIntent(resultPendingIntent);

                mManager.notify(0, mBuilder.build());

            }
        });

    }
}
