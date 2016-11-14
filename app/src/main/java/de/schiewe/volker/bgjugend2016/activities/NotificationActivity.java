package de.schiewe.volker.bgjugend2016.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;

public class NotificationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setItems(new String[]{"10 min", "30 min", "1 Stunde"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: {
                                Toast.makeText(getApplicationContext(), "10 min", Toast.LENGTH_SHORT).show();
                                setNewAlarm(600000);
                                break;
                            }
                            case 1: {
                                Toast.makeText(getApplicationContext(), "30 min", Toast.LENGTH_SHORT).show();
                                setNewAlarm(1800000);

                                break;
                            }
                            case 2: {
                                Toast.makeText(getApplicationContext(), "1 Stunde", Toast.LENGTH_SHORT).show();
                                setNewAlarm(3600000);
                                break;
                            }
                        }
                    }
                })
                .setTitle("Später erinnern");
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void setNewAlarm(long time){
//TODO
        Calendar calendar = Calendar.getInstance();

        finish();
    }
}
