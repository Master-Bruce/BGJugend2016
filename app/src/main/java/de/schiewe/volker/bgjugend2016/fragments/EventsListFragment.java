package de.schiewe.volker.bgjugend2016.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.schiewe.volker.bgjugend2016.helper.AppPersist;
import de.schiewe.volker.bgjugend2016.R;
import de.schiewe.volker.bgjugend2016.activities.MainActivity;
import de.schiewe.volker.bgjugend2016.adapter.EventListAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsListFragment extends Fragment implements EventListAdapter.OnItemClickListener {

    private MainActivity activity;
    private AppPersist app;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events_list, container, false);
        activity = (MainActivity) getActivity();
        app = AppPersist.getInstance();

        RecyclerView rvEvents = (RecyclerView) view.findViewById(R.id.rvEvents);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvEvents.setLayoutManager(layoutManager);

        rvEvents.setAdapter(app.getEventAdapter());
        app.getEventAdapter().setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setTitle(getString(R.string.events_header));
        app.getEventAdapter().notifyDataSetChanged();
        app.getMenu().findItem(R.id.menuSearch).setVisible(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        app.getMenu().findItem(R.id.menuSearch).setVisible(false);
        MainActivity.hideSoftKeyboard(activity);
        SearchView item = (SearchView) app.getMenu().findItem(R.id.menuSearch).getActionView();
        item.setQuery("", false);
        item.clearFocus();
        item.setIconified(true);
    }

    @Override
    public void onItemClick(View view, int position) {
        int eventId = app.getEventAdapter().getEvents().get(position).getId();
        app.setCurrEvent(eventId);

        SwipeEventFragment swipeEventFragment = new SwipeEventFragment();
        swipeEventFragment.setData(app.getEventAdapter().getEvents(), position);
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.push_left_in, R.anim.fade_out, R.anim.fade_in, 0)
                .replace(R.id.container, swipeEventFragment, MainActivity.EVENT_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }
}
