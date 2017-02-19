package de.schiewe.volker.bgjugend2016.helper;

import android.view.Menu;

import java.util.ArrayList;

import de.schiewe.volker.bgjugend2016.adapter.EventListAdapter;
import de.schiewe.volker.bgjugend2016.adapter.InfoListAdapter;
import de.schiewe.volker.bgjugend2016.data_models.Event;
import de.schiewe.volker.bgjugend2016.data_models.Info;

/**
 * For global variables
 */
public class AppPersist {
    private static AppPersist instance;
    private Menu menu;
    private int currEvent;
    private EventListAdapter eventAdapter;
    private InfoListAdapter infoAdapter;
    private FirebaseHandler fbHandler;

    private AppPersist() {
        fbHandler = FirebaseHandler.getInstance();
    }

    public static AppPersist getInstance() {
        if (instance == null)
            instance = new AppPersist();
        return instance;
    }

    public Event getCurrEvent() {
        return eventAdapter.getEventById(currEvent);
    }

    public void setCurrEvent(int currEvent) {
        this.currEvent = currEvent;
    }

    public EventListAdapter getEventAdapter() {
        return eventAdapter;
    }

    public void setEventAdapter(EventListAdapter eventAdapter) {
        this.eventAdapter = eventAdapter;
        this.eventAdapter.setData(null);
    }

    public InfoListAdapter getInfoAdapter() {
        return infoAdapter;
    }

    public void setInfoAdapter(InfoListAdapter infoAdapter) {
        this.infoAdapter = infoAdapter;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public ArrayList<Event> getEvents() {
        return fbHandler.getEvents();
    }

    public ArrayList<Info> getInfos() {
        return fbHandler.getInfos();
    }
}
