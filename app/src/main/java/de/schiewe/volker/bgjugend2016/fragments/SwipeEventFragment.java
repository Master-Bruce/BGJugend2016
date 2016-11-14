package de.schiewe.volker.bgjugend2016.fragments;


import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.schiewe.volker.bgjugend2016.R;
import de.schiewe.volker.bgjugend2016.activities.MainActivity;
import de.schiewe.volker.bgjugend2016.helper.AppPersist;
import de.schiewe.volker.bgjugend2016.helper.FirebaseHandler;

/**
 * A simple {@link Fragment} subclass.
 */
public class SwipeEventFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private AppPersist app;
    private FirebaseHandler fbHandler;
    private MainActivity activity;
    private SharedPreferences sharedPrefs;
    private int startPos;

    public SwipeEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        app = AppPersist.getInstance();
        fbHandler = FirebaseHandler.getInstance(getActivity());
        View view = inflater.inflate(R.layout.fragment_swipe_event, container, false);
        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        EventPageAdapter eventPageAdapter = new EventPageAdapter(getActivity().getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(eventPageAdapter);
        viewPager.setCurrentItem(startPos);
        viewPager.addOnPageChangeListener(this);
        return view;
    }

    public void setItem(int position) {
        startPos = position;
    }

    @Override
    public void onResume() {
        super.onResume();
        app.getMenu().findItem(R.id.menuBenachrichtigung).setVisible(true);
        app.getMenu().findItem(R.id.menuApply).setVisible(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            app.getMenu().findItem(R.id.menuCalendar).setVisible(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        app.getMenu().findItem(R.id.menuBenachrichtigung).setVisible(false);
        app.getMenu().findItem(R.id.menuApply).setVisible(false);
        app.getMenu().findItem(R.id.menuCalendar).setVisible(false);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        app.setCurrEvent(position);
        activity.setTitle(app.getCurrEvent().getTitle());

        if (sharedPrefs.getBoolean("EventNotification" + app.getCurrEvent().getId(), false))
            app.getMenu().findItem(R.id.menuBenachrichtigung).setChecked(true);
        else
            app.getMenu().findItem(R.id.menuBenachrichtigung).setChecked(false);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class EventPageAdapter extends FragmentStatePagerAdapter {
        EventPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
//            app.setCurrEvent(i);
            return EventFragment.newInstance(app.getEventAdapter().getEvents().get(i));
        }


        @Override
        public int getCount() {
            return fbHandler.getEvents().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }
}
