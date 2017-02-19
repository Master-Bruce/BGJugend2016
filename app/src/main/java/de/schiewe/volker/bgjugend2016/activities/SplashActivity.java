package de.schiewe.volker.bgjugend2016.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

import de.schiewe.volker.bgjugend2016.DatabaseListener;
import de.schiewe.volker.bgjugend2016.R;
import de.schiewe.volker.bgjugend2016.data_models.Event;
import de.schiewe.volker.bgjugend2016.helper.AppPersist;
import de.schiewe.volker.bgjugend2016.helper.FirebaseHandler;

public class SplashActivity extends AppCompatActivity implements DatabaseListener {
    public static final String URI_KEY = "deep_link";
    private static final String TAG = "SplashActivity";
    private String uri;
    private AppPersist app;
    private boolean started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "---------- onCreate ----------");
        started = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseHandler fbHandler = FirebaseHandler.getInstance(this);
        fbHandler.setDbListener(this);
        app = AppPersist.getInstance();

        Intent intent = getIntent();
        if (intent.getData() != null)
            uri = String.valueOf(intent.getData());

        if (app.getEvents() != null && app.getEvents().size() != 0)
            startMain();
    }

    @Override
    public void onDataCreated() {
        Log.i(TAG, "------------ onDataCreated --------------");
        app = AppPersist.getInstance();
        startMain();
    }

    private void startMain() {
        if (started)
            return;
        Log.i(TAG, "------------ startMain --------------");

        app = AppPersist.getInstance();

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);

        if (uri != null) {
            // Deeplink handling
            int event_id = -1;
            ArrayList<Event> list = app.getEvents();
            for (Event event : list) {
                if (event.getUrl().equals(uri)) {
                    event_id = event.getId();
                }
            }

            if (event_id == -1) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                browserIntent.setPackage("com.android.chrome");
                startActivity(browserIntent);
                finish();
                return;
            }
            intent.putExtra(URI_KEY, event_id);
        }
        started = true;
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}
