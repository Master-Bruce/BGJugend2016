package de.schiewe.volker.bgjugend2016.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.schiewe.volker.bgjugend2016.fragments.SettingsFragment;

/**
 * Helper class
 */
public class Util {

    public static boolean checkDate(String date, SharedPreferences prefs) {
        boolean showPastEvents = prefs.getBoolean(SettingsFragment.SHOW_PASTEVENTS, false);
        Date today = new Date();
        if (!date.contains("–")) {
            return true;
        }
        String endDate = date.split("–")[1].trim();
        Date eventDate = parseDate(endDate);
        assert eventDate != null;
        return showPastEvents || eventDate.after(today);
    }

    public static Date parseDate(String date) {
        String[] formats = {"dd. MMM. yyyy", "dd. MMM yyyy", "dd.MM.yy"};
        for (String format : formats) {
            try {
                return new SimpleDateFormat(format, Locale.GERMANY).parse(date);
            } catch (ParseException ignored) {
            }
        }
        return null;
    }

    public static boolean checkAge(String age, SharedPreferences prefs) {
        boolean isAgeRelevant = prefs.getBoolean(SettingsFragment.SHOW_AGE_EVENTS, false);
        boolean returnValue;
        int userAge = prefs.getInt(SettingsFragment.AGE, 0);
        String[] eventAgeArray = age.split(" ");
        if (eventAgeArray[0].equals("ab")) {
            returnValue = Integer.parseInt(eventAgeArray[1]) < userAge;
        } else if (eventAgeArray[1].equals("–")) {
            returnValue = Integer.parseInt(eventAgeArray[0]) < userAge & Integer.parseInt(eventAgeArray[2]) > userAge;
        } else {
            returnValue = 11 < userAge & 15 > userAge;
        }

        return !isAgeRelevant || returnValue;
    }

    public static Bitmap getImage(Context ctx, String fileName) {
        fileName += ".jpg";
        Bitmap reImage = null;
        final File file = new File(ctx.getFilesDir(), fileName);
        if (file.exists()) {
            //read from internal storage
            try {
                reImage = BitmapFactory.decodeStream(new FileInputStream(file));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return reImage;
    }

    public static boolean checkPlayServices(Activity ctx) {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(ctx);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(ctx, result, 9000).show();
            }
            return false;
        }
        return true;
    }
}
