package de.schiewe.volker.bgjugend2016.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.schiewe.volker.bgjugend2016.helper.AppPersist;
import de.schiewe.volker.bgjugend2016.views.PreferenceFragment;
import de.schiewe.volker.bgjugend2016.R;
import de.schiewe.volker.bgjugend2016.activities.MainActivity;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    //region Konstanten
    public static final String PREF_SHOW_PASTEVENTS = "pref_show_pastevents";
    public static final String SHOW_PASTEVENTS = "show_pastevents";
    public static final String PREF_AGE = "pref_age";
    public static final String SHOW_AGE_EVENTS = "show_age";
    public static final String AGE = "age";
    public static final String PREF_NAME = "pref_name";
    public static final String PREF_STREET = "pref_street";
    public static final String PREF_CITY = "pref_city";
    public static final String PREF_BIRTHDAY = "pref_birthday";
    public static final String PREF_TELEPHONE = "pref_telephone";
    public static final String PREF_TEST_INT = "pref_test_int";
    public static final String NOTIFICATION_TIME = "notification_time";
    public static final String NOTIFICATION_DAYS_BEFORE = "notification_list";
    private static final String PREF_INFO = "pref_info";
    private static final String PREF_FEEDBACK = "pref_feedback";
    //endregion
    private static String version;
    private int pref_int = 0;
    private MainActivity activity;
    private AppPersist app;
    private SharedPreferences prefs;
    /**
     * A preference value change listener that updates the preference's summary to reflect its new value.
     */
    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference.getKey().equals(PREF_BIRTHDAY)) {
                int age;
                try {
                    age = getAge(stringValue);
                } catch (Exception e) {
                    if (!prefs.getString(PREF_BIRTHDAY, "").equals(""))
                        Toast.makeText(activity, "Datum erneut eingeben! z.B. TT.MM.JJJJ", Toast.LENGTH_LONG).show();
                    return false;
                }
                prefs.edit().putInt(AGE, age).apply();
            }

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

            } else if (preference instanceof EditTextPreference) {
                // For EditText preferences, set the summary to the value's simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        app = AppPersist.getInstance();
        try {
            version = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        pref_int = prefs.getInt(PREF_TEST_INT, 0);

        addPreferencesFromResource(R.xml.preferences);

        bindPreferenceSummaryToValue(findPreference(PREF_NAME));
        bindPreferenceSummaryToValue(findPreference(PREF_STREET));
        bindPreferenceSummaryToValue(findPreference(PREF_CITY));
        bindPreferenceSummaryToValue(findPreference(PREF_BIRTHDAY));
        bindPreferenceSummaryToValue(findPreference(PREF_TELEPHONE));

        findPreference(PREF_INFO).setSummary(version);
        findPreference(PREF_INFO).setOnPreferenceClickListener(this);

        findPreference(PREF_SHOW_PASTEVENTS).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                prefs.edit().putBoolean(SHOW_PASTEVENTS, (Boolean) newValue).apply();
                app.getEventAdapter().setData(null);

                app.getInfoAdapter().setData();
                return true;
            }
        });

        findPreference(PREF_AGE).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                prefs.edit().putBoolean(SHOW_AGE_EVENTS, (Boolean) newValue).apply();

                app.getEventAdapter().setData(null);
                return true;
            }
        });
        findPreference(PREF_FEEDBACK).setOnPreferenceClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setTitle(getString(R.string.settings_header));
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));

    }

    private int getAge(String birthday) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy");
        DateTime birthdate;
        birthdate = formatter.parseDateTime(birthday);
        DateTime todayDate = DateTime.now();
        Period period = new Period(birthdate, todayDate);

        return period.getYears();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case PREF_INFO: {
                pref_int++;
                if (pref_int < 6 && pref_int > 2) {
                    Toast.makeText(activity, String.valueOf(pref_int), Toast.LENGTH_SHORT).show();
                } else if (pref_int == 6) {
                    Toast.makeText(activity, "Test Optionen aktiviert", Toast.LENGTH_SHORT).show();
                    app.getMenu().findItem(R.id.menuTest).setVisible(true);
                    prefs.edit().putInt(PREF_TEST_INT, pref_int).apply();
                    app.getEventAdapter().setData(null);
                } else if (pref_int > 6) {
                    Toast.makeText(activity, "Der Test ist doch schon aktiviert ;)", Toast.LENGTH_SHORT).show();
                }
                if (pref_int == 8) {
                    new AlertDialog.Builder(activity)
                            .setTitle("Test beenden")
                            //   .setMessage("Drbr")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    pref_int = 0;
                                    prefs.edit().putInt(PREF_TEST_INT, pref_int).apply();
                                    app.getEventAdapter().setData(null);
                                    app.getMenu().findItem(R.id.menuTest).setVisible(false);
                                    Log.d("AlertDialog", "Positive");
                                }
                            })
                            .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("AlertDialog", "Negative");
                                    pref_int = 6;
                                }
                            }).show();
                }
                return true;
            }
            case PREF_FEEDBACK: {
                activity.sendEmail("volker.schiewe@gmx.de", "Feedback Jugendarbeit BG - App", "");
                return true;
            }
        }
        return false;
    }
}
