package de.schiewe.volker.bgjugend2016.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.schiewe.volker.bgjugend2016.helper.AppPersist;
import de.schiewe.volker.bgjugend2016.R;
import de.schiewe.volker.bgjugend2016.activities.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    private MainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        activity = (MainActivity) getActivity();

        RecyclerView rvEvents = (RecyclerView) view.findViewById(R.id.rvInfo);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvEvents.setLayoutManager(layoutManager);

        rvEvents.setAdapter(AppPersist.getInstance().getInfoAdapter());

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        activity.setTitle(getString(R.string.info_header));
    }

}
