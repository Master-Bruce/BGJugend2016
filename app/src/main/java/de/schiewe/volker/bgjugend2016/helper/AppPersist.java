package de.schiewe.volker.bgjugend2016.helper;

import android.view.Menu;

import de.schiewe.volker.bgjugend2016.adapter.EventListAdapter;
import de.schiewe.volker.bgjugend2016.adapter.InfoListAdapter;
import de.schiewe.volker.bgjugend2016.data_models.Event;

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

    public static AppPersist getInstance() {
        if (instance == null)
            instance = new AppPersist();
        return instance;
    }

    private AppPersist(){
        fbHandler = FirebaseHandler.getInstance();
    }

    public Event getCurrEvent() {
        return fbHandler.getEvents().get(currEvent);
    }

    public void setCurrEvent(int currEvent) {
        this.currEvent = currEvent;
    }

    public EventListAdapter getEventAdapter() {
        return eventAdapter;
    }

    public void setEventAdapter(EventListAdapter eventAdapter) {
        this.eventAdapter = eventAdapter;
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
}
