package de.schiewe.volker.bgjugend2016.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.schiewe.volker.bgjugend2016.DatabaseListener;
import de.schiewe.volker.bgjugend2016.R;
import de.schiewe.volker.bgjugend2016.activities.MainActivity;
import de.schiewe.volker.bgjugend2016.data_models.Contact;
import de.schiewe.volker.bgjugend2016.data_models.Event;
import de.schiewe.volker.bgjugend2016.data_models.Info;

/**
 * Connection to firebase service and database
 */


public class FirebaseHandler {
    private static final String TAG = "FirebaseHandler";
    private static final String HEADER = "header";
    private static FirebaseHandler instance;

    //region fields
    private Context ctx;
    private SharedPreferences prefs;
    private DatabaseListener listener;

    private FirebaseDatabase database;
    private DatabaseReference dbReference;
    private DatabaseReference jbReference;
    private StorageReference stgReference;

    private ArrayList<Event> eventList = new ArrayList<>();
    private ArrayList<Info> infoList = new ArrayList<>();
    private ArrayList<Contact> jbList = new ArrayList<>();
    private String planungsTeam = "";
    //endregion

    public static FirebaseHandler getInstance(Context ctx) {
        if (instance == null)
            instance = new FirebaseHandler(ctx);
        return instance;
    }

    public static FirebaseHandler getInstance(){
        if (instance != null) return instance;
        return null;
    }

    private FirebaseHandler(final Context ctx) {
        this.ctx = ctx;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        if (database == null) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        database = FirebaseDatabase.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        dbReference = database.getReference("Database");
        jbReference = database.getReference("JB_Data");
        stgReference = storage.getReferenceFromUrl("gs://jugendarbeit-ebu.appspot.com").child("event_img");
        dbReference.keepSynced(true);
    }

    public void init() {
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (i == 0) {
                        //get EventsList
                        eventList.clear();
                        for (DataSnapshot child : d.getChildren()) {
                            Event value = child.getValue(Event.class);

                            //find image or download
                            downloadImage(value.getImage());

                            //parse Deadline-Date
                            Date date = null;
                            try {
                                date = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).parse(value.getDeadline());
                            } catch (ParseException e) {
                                Log.e(TAG, "onDataChange: Error parsing Deadline", e);
                            }
                            value.setdDeadline(date);
                            //set Id
                            value.setId(eventList.size());

                            eventList.add(value);
                            i++;
                        }
                    } else {
                        //get InfoList
                        infoList.clear();
                        for (DataSnapshot child : d.getChildren()) {
                            Info value = child.getValue(Info.class);
                            infoList.add(value);
                        }
                    }
                }

                listener.onDataCreated();
                cleanFiles();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error in Firebase Database update! Message: " + databaseError.getMessage());
            }
        });
        jbReference.child("Contact").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot value : dataSnapshot.getChildren()) {
                    jbList.add(value.getValue(Contact.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        database.getReference("HeaderImage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int value = Integer.parseInt(dataSnapshot.getValue(String.class));
                if (value != prefs.getInt(HEADER, 0)) {
                    downloadHeader();
                    prefs.edit().putInt(HEADER, value).apply();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        database.getReference("Planungsteam").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                planungsTeam = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public ArrayList<Event> getEvents() {
        return eventList;
    }

    public ArrayList<Info> getInfos() {
        return infoList;
    }

    public ArrayList<Contact> getJbs() {
        return jbList;
    }

    public void downloadHeader() {
        final File header = new File(ctx.getFilesDir(), MainActivity.HEADER_FILENAME + ".jpg");
        File overView = new File(ctx.getFilesDir(), MainActivity.OVERVIEW_FILENAME + ".jpg");
        //download from firebase an save to internal storage
        StorageReference navRef = stgReference.getRoot().child(MainActivity.HEADER_FILENAME + ".jpg");
        StorageReference overRef = stgReference.getRoot().child(MainActivity.OVERVIEW_FILENAME + ".jpg");
        navRef.getFile(header).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                ImageView NavImage = (ImageView) ((NavigationView) ((MainActivity) ctx)
                        .findViewById(R.id.nav_view)).getHeaderView(0)
                        .findViewById(R.id.drawerImageView);
                NavImage.setImageBitmap(Util.getImage(ctx, MainActivity.HEADER_FILENAME));
            }
        });
        overRef.getFile(overView).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "onSuccess: Übersicht gespeichert");
            }
        });
    }

    private void downloadImage(String fileName) {
        fileName += ".jpg";
        final File file = new File(ctx.getFilesDir(), fileName);
        if (!file.exists()) {
            //download from firebase an save to internal storage
            StorageReference imgRef = stgReference.child(fileName);
            imgRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //File had been created
                }
            });
        }
    }

    public void setDbListener(DatabaseListener listener) {
        this.listener = listener;
    }

    public String getPlanungsTeam() {
        return planungsTeam;
    }

    public void cleanFiles() {
        String[] fileList = ctx.fileList();
        boolean fileExist = false;
        for (String s : fileList) {
            String[] split = s.split("\\.");
            if (split.length > 1 && split[1].equals("jpg")) {
                for (Event e : getEvents()) {
                    if (fileExist) break;
                    if (e.getImage().equals(split[0]) || MainActivity.HEADER_FILENAME.equals(split[0])
                            || MainActivity.OVERVIEW_FILENAME.equals(split[0])) {
                        fileExist = true;
                    }
                }
                if (!fileExist) {
                    ctx.deleteFile(s);
                }
                fileExist = false;
            }
        }
    }
}
