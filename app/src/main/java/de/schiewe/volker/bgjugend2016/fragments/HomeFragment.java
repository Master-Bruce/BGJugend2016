package de.schiewe.volker.bgjugend2016.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;

import de.schiewe.volker.bgjugend2016.R;
import de.schiewe.volker.bgjugend2016.activities.MainActivity;
import de.schiewe.volker.bgjugend2016.data_models.Event;
import de.schiewe.volker.bgjugend2016.helper.FirebaseHandler;
import de.schiewe.volker.bgjugend2016.helper.Util;


/**
 * Start screen
 */
public class HomeFragment extends Fragment {

    private FirebaseHandler firebaseHandler;
    private View cardView;
    private ProgressBar loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        firebaseHandler = FirebaseHandler.getInstance(getActivity());
        loading = (ProgressBar) view.findViewById(R.id.pbLoading);
        Button btnEvents = (Button) view.findViewById(R.id.btnEvents);
        btnEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment eventsFragment = new EventsListFragment();
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(MainActivity.ANIM_IN, MainActivity.ANIM_OUT, MainActivity.ANIM_IN, MainActivity.ANIM_OUT)
                        .replace(R.id.container, eventsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        Button btnContact = (Button) view.findViewById(R.id.btnContact);
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment contactFragment = new ContactFragment();
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(MainActivity.ANIM_IN, MainActivity.ANIM_OUT, MainActivity.ANIM_IN, MainActivity.ANIM_OUT)
                        .replace(R.id.container, contactFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // CardView setup
        cardView = view.findViewById(R.id.includeCard);
        cardView.setVisibility(View.GONE);
        loading.setIndeterminate(true);
        if (firebaseHandler.getEvents().size() > 0) {
            setupCard();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.app_name));
    }

    public void setupCard() {
        if (cardView == null) return;
        cardView.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
        Date today = new Date();
        TextView header = (TextView) cardView.findViewById(R.id.tvHeader);
        TextView date = (TextView) cardView.findViewById(R.id.tvDate);
        ImageView imgWarning = (ImageView) cardView.findViewById(R.id.ivWarnig);
        final Event nextEvent = getNextEvent();
        header.setText(nextEvent.getTitle());
        String placeDate = nextEvent.getPlace() + "\n" + nextEvent.getDate();
        date.setText(placeDate);
        if (today.after(nextEvent.getdDeadline())) {
            imgWarning.setVisibility(View.VISIBLE);
        } else {
            imgWarning.setVisibility(View.INVISIBLE);
        }
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeEventFragment eventFrag = new SwipeEventFragment();
                eventFrag.setItem(nextEvent.getId());
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.push_left_in, R.anim.fade_out, R.anim.fade_in, R.anim.push_right_out)
                        .replace(R.id.container, eventFrag)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private Event getNextEvent() {
        Event reEvent = null;
        Date today = new Date();
        int i = 0;

        while (reEvent == null && i < firebaseHandler.getEvents().size()) {
            String eventTime = firebaseHandler.getEvents().get(i).getDate().split("–")[0].trim();
            Date dEventTime = Util.parseDate(eventTime);

            assert dEventTime != null;
            if (dEventTime.after(today)) {
                reEvent = firebaseHandler.getEvents().get(i);
            }
            i++;
        }
        if (reEvent == null)
            return new Event("Keine Veranstaltungen", "", "nächstes Jahr mehr", new Date());

        return reEvent;
    }
}
