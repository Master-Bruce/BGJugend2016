package de.schiewe.volker.bgjugend2016.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

public class DeepActivity extends AppCompatActivity {

    public static final String ID_KEY = "EVENT_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        if (intent == null || intent.getData() == null) {
            finish();
            return;
        }
        List<String> pathSegments = intent.getData().getPathSegments();
        if (pathSegments.size() > 0 && pathSegments.get(0).equals("2017")) {
            int id = Integer.parseInt(pathSegments.get(1));

            Intent next = new Intent(this, MainActivity.class);
            Bundle b = new Bundle();
            b.putInt(ID_KEY, id);
            next.putExtras(b);
            startActivity(next);
        } else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.jugend.ebu.de/veranstaltungen"));
            startActivity(browserIntent);
        }

        // Finish this activity
        finish();
        super.onCreate(savedInstanceState);
    }
}
