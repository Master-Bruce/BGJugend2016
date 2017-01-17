package de.schiewe.volker.bgjugend2016.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.schiewe.volker.bgjugend2016.DatabaseListener;
import de.schiewe.volker.bgjugend2016.R;
import de.schiewe.volker.bgjugend2016.adapter.EventListAdapter;
import de.schiewe.volker.bgjugend2016.adapter.InfoListAdapter;
import de.schiewe.volker.bgjugend2016.fragments.ContactFragment;
import de.schiewe.volker.bgjugend2016.fragments.DonateFragment;
import de.schiewe.volker.bgjugend2016.fragments.EventFragment;
import de.schiewe.volker.bgjugend2016.fragments.EventsListFragment;
import de.schiewe.volker.bgjugend2016.fragments.HomeFragment;
import de.schiewe.volker.bgjugend2016.fragments.InfoFragment;
import de.schiewe.volker.bgjugend2016.fragments.SettingsFragment;
import de.schiewe.volker.bgjugend2016.helper.AppPersist;
import de.schiewe.volker.bgjugend2016.helper.FirebaseHandler;
import de.schiewe.volker.bgjugend2016.helper.Util;
import de.schiewe.volker.bgjugend2016.receiver.AlarmReceiver;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DatabaseListener {
    //region Members and Constants
    public static final String NOTIFY_TITLE = "NOTIFY_TITLE";
    public static final String NOTIFY_TEXT = "NOTIFY_TEXT";
    public static final String NOTIFY_EVENT_ID = "NOTIFY_ID";
    public static final String LAST_VERSION = "last_version";
    public static final String EVENT_FRAGMENT = "eventFragment";
    public static final String HEADER_FILENAME = "navigation_header";
    public static final String OVERVIEW_FILENAME = "overview";
    public static final int ANIM_IN = R.anim.fade_in;
    public static final int ANIM_OUT = R.anim.fade_out;
    private AppPersist app;
    private FirebaseHandler fireDB;

    private SharedPreferences sharedPref;
    private HomeFragment homeFragment;
    private EventsListFragment eventsListFragment;
    private EventFragment eventFragment;
    private InfoFragment infoFragment;
    private ContactFragment contactFragment;
    private DonateFragment donateFragment;
    private SettingsFragment settingsFragment;
    private FragmentManager fragManager;
    private DrawerLayout drawer;
    private AlarmManager alarmMgr;

    private ProgressDialog progressDialog;
    //endregion

    public static Date getDateObject(String date) {
        Date returnDate = null;
        String firstDate = date.split("–")[0];
        SimpleDateFormat sdf = new SimpleDateFormat("dd. MMMM yyyy", Locale.GERMAN);
        try {
            returnDate = sdf.parse(firstDate.trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnDate;
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() == null) return;
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init firebase;
        fireDB = FirebaseHandler.getInstance(this);
        fireDB.setDbListener(this);
        fireDB.init();

        app = AppPersist.getInstance();
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        newVersion();
//        Util.checkPlayServices(this);

        // init Fragments
        homeFragment = new HomeFragment();
        eventsListFragment = new EventsListFragment();
        eventFragment = new EventFragment();
        infoFragment = new InfoFragment();
        contactFragment = new ContactFragment();
        donateFragment = new DonateFragment();
        settingsFragment = new SettingsFragment();

        //NavDrawer init
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);
        ImageView headerView = (ImageView) navigationView.getHeaderView(0)
                .findViewById(R.id.drawerImageView);
        if (headerView != null) {
            Bitmap bitmap = Util.getImage(this, HEADER_FILENAME);
            if (bitmap == null)
                fireDB.downloadHeader();
            else {
                headerView.setImageBitmap(bitmap);
                headerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                        intent.putExtra(ImageActivity.IMAGE_NAME, HEADER_FILENAME);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                });
            }

        }

        //show home fragment
        if (fragManager == null) {
            fragManager = getSupportFragmentManager();
            fragManager.beginTransaction()
                    .add(R.id.container, homeFragment)
                    .commit();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//      handle Notification Click
        Intent alarmIntent = getIntent();
        int id = alarmIntent.getIntExtra(NOTIFY_EVENT_ID, -1);

        if (id != -1) {
            app.setCurrEvent(id);
            fragManager.beginTransaction()
                    .replace(R.id.container, eventFragment)
                    .commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home: {
                fragManager.beginTransaction()
                        .setCustomAnimations(ANIM_IN, ANIM_OUT, ANIM_IN, ANIM_OUT)
                        .replace(R.id.container, homeFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.nav_events: {
                fragManager.beginTransaction()
                        .setCustomAnimations(ANIM_IN, ANIM_OUT, ANIM_IN, ANIM_OUT)
                        .replace(R.id.container, eventsListFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.nav_info: {
                fragManager.beginTransaction()
                        .setCustomAnimations(ANIM_IN, ANIM_OUT, ANIM_IN, ANIM_OUT)
                        .replace(R.id.container, infoFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.nav_contact: {
                fragManager.beginTransaction()
                        .setCustomAnimations(ANIM_IN, ANIM_OUT, ANIM_IN, ANIM_OUT)
                        .replace(R.id.container, contactFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.nav_donate: {
                fragManager.beginTransaction()
                        .setCustomAnimations(ANIM_IN, ANIM_OUT, ANIM_IN, ANIM_OUT)
                        .replace(R.id.container, donateFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.nav_settings: {
                fragManager.beginTransaction()
                        .setCustomAnimations(ANIM_IN, ANIM_OUT, ANIM_IN, ANIM_OUT)
                        .replace(R.id.container, settingsFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            }
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawer.closeDrawer(GravityCompat.START);
            }
        }, 100);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        app.setMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);

        if (sharedPref.getInt(SettingsFragment.PREF_TEST_INT, 0) >= 6) {
            app.getMenu().findItem(R.id.menuTest).setVisible(true);
        }
//      handle Notification Click
        Intent alarmIntent = getIntent();
        int id = alarmIntent.getIntExtra(NOTIFY_EVENT_ID, -1);

        if (id != -1) {
            app.setCurrEvent(id);
            fragManager.beginTransaction()
                    .replace(R.id.container, eventFragment)
                    .commit();
        }

        //Config SearchView
        MenuItem searchItem = menu.findItem(R.id.menuSearch);

        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    app.getEventAdapter().setData(newText);
                    return true;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menuApply: {
                sendEmail(app.getCurrEvent().getContact().getMail(), "Anmeldung für " +
                        app.getCurrEvent().getTitle(), EventFragment.GetApplyString(app.getCurrEvent(), this));
                break;
            }
            case R.id.menuBenachrichtigung: {
                MenuItem menuItem = app.getMenu().findItem(R.id.menuBenachrichtigung);
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                    cancelNotification(app.getCurrEvent().getId());
                } else {
                    menuItem.setChecked(true);
                    setNotificationAlarm(app.getCurrEvent().getDate(), app.getCurrEvent().getTitle(),
                            app.getCurrEvent().getId());
                }
                break;
            }
            case R.id.menuCalendar: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    Intent intent = new Intent(Intent.ACTION_INSERT)
                            .setType("vnd.android.cursor.item/event")
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, getEventDates()[0].getTime())
                            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, getEventDates()[1].getTime())
                            .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                            .putExtra(CalendarContract.Events.TITLE, app.getCurrEvent().getTitle())
                            .putExtra(CalendarContract.Events.DESCRIPTION, app.getCurrEvent().getHeader())
                            .putExtra(CalendarContract.Events.EVENT_LOCATION, app.getCurrEvent().getPlace())
                            .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                            .putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
                    startActivity(intent);
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Fragment eventFrag = fragManager.findFragmentByTag(EVENT_FRAGMENT);
        if (eventFrag != null && eventFrag.isVisible()) {
            fragManager.beginTransaction()
                    .setCustomAnimations(0, R.anim.push_right_out)
                    .remove(eventFrag)
                    .commit();
        }
        if (fragManager.getBackStackEntryCount() > 0) {
            fragManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void sendEmail(String mailAddress, String subject, String text) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mailAddress, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(emailIntent, "E-Mail senden..."));
    }

    private void setNotificationAlarm(String date, String title, int eventID) {
        String dayDifference = sharedPref.getString(SettingsFragment.NOTIFICATION_DAYS_BEFORE, "2");
        //TODO verbessern
        Calendar calendar = Calendar.getInstance();
        Calendar nowCal = Calendar.getInstance();
        int broadcastID = eventID * 10;
        String notiText = getString(R.string.event_noti_text) + " " + remainingDays(Integer.parseInt(dayDifference));
        long dateInMillis = getDateObject(date).getTime();

        for (int i = 0; i < 2; i++) {
            Date d = new Date(dateInMillis - Integer.parseInt(dayDifference) * 24 * 3600 * 1000);
            d = addPrefTime(d);
            calendar.setTime(d);

            if (calendar.before(nowCal)) {
                Toast.makeText(MainActivity.this, "Veranstaltung hat bereits begonnen", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
            intent.putExtra(NOTIFY_TITLE, title);
            intent.putExtra(NOTIFY_TEXT, notiText);
            intent.putExtra(NOTIFY_EVENT_ID, app.getCurrEvent().getId());
            PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this, broadcastID, intent, PendingIntent.FLAG_ONE_SHOT);

            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

            broadcastID += 5;
            notiText = "Der Anmeldeschluss läuft " + " " + remainingDays(Integer.parseInt(dayDifference)) + " ab!";
            dateInMillis = app.getCurrEvent().getdDeadline().getTime();

        }
        sharedPref.edit().putBoolean("EventNotification" + eventID, true).apply();
    }

    private void cancelNotification(int ID) {
        ID *= 10;
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this, ID, intent, 0);
        alarmMgr.cancel(alarmIntent);

        sharedPref.edit().putBoolean("EventNotification" + ID / 10, false).apply();
    }

    private String remainingDays(int days) {
        String returnString;
        switch (days) {
            case 0: {
                returnString = "heute";
                break;
            }
            case 1: {
                returnString = " morgen";
                break;
            }
            default: {
                returnString = "in" + " " + days + " Tagen";
                break;
            }
        }
        return returnString;
    }

    private Date[] getEventDates() {
        String[] eventDates = app.getCurrEvent().getDate().split("–");
        Date[] dates = new Date[2];
        for (int i = 0; i < 2; i++) {
            try {
                dates[i] = new SimpleDateFormat("dd. MMMM yyyy", Locale.GERMAN).parse(eventDates[i].trim());
                dates[i].setHours(1); // for Calender insert
//                ToDo find a better way
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dates;
    }

    private Date addPrefTime(Date date) {
        int hour = Integer.parseInt(sharedPref.getString(SettingsFragment.NOTIFICATION_TIME, "9"));
        date = new Date(date.getTime() + hour * 3600 * 1000);
        return date;
    }

    public void mapsOnClick(View view) {
        String url = "http://maps.google.co.in/maps?q=" + app.getCurrEvent().getPlace();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onDataCreated() {
        app.setEventAdapter(new EventListAdapter(sharedPref, fireDB));
        app.getEventAdapter().setData(null);

        app.setInfoAdapter(new InfoListAdapter(sharedPref, fireDB));
        app.getInfoAdapter().setData();

        homeFragment.setupCard();
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private void newVersion() {
        int version = -1;
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        if (sharedPref.getInt(LAST_VERSION, 0) != version) {
            sharedPref.edit().putInt(LAST_VERSION, version).apply();
            progressDialog = ProgressDialog.show(this, "",
                    "Lade Daten...", true, false);
        }
    }
}
