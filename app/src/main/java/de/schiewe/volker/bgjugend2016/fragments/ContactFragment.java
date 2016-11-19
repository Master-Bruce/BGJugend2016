package de.schiewe.volker.bgjugend2016.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import de.schiewe.volker.bgjugend2016.R;
import de.schiewe.volker.bgjugend2016.activities.ImageActivity;
import de.schiewe.volker.bgjugend2016.activities.MainActivity;
import de.schiewe.volker.bgjugend2016.data_models.Contact;
import de.schiewe.volker.bgjugend2016.helper.FirebaseHandler;
import de.schiewe.volker.bgjugend2016.helper.Util;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    private TextView  mPTText;
    private MainActivity activity;
    private TableRow row1;
    private TableRow row2;
    private ImageView overView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        activity = (MainActivity) getActivity();


        row1 = (TableRow) view.findViewById(R.id.jbRow1);
        row2 = (TableRow) view.findViewById(R.id.jbRow2);
        overView = (ImageView) view.findViewById(R.id.ivOverView);

        mPTText = (TextView) view.findViewById(R.id.tvPTText);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseHandler fireDB = FirebaseHandler.getInstance(activity);
        ArrayList<Contact> jbs = fireDB.getJbs();
        if (jbs == null) return;
        for (int i = 0; i < jbs.size(); i++) {
            if (i < 2) {
                View jbView =((ViewGroup) LayoutInflater.from(activity)
                        .inflate(R.layout.jb_view, row1)).getChildAt(i);
                fillTextViews(jbView, jbs.get(i));
            } else if (i > 1 && i < 4) {
                View jbView =((ViewGroup) LayoutInflater.from(activity)
                        .inflate(R.layout.jb_view, row2)).getChildAt(i-2);
                fillTextViews(jbView, jbs.get(i));
            }
        }
        overView.setImageBitmap(Util.getImage(activity,MainActivity.OVERVIEW_FILENAME));
        overView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ImageActivity.class);
                intent.putExtra(ImageActivity.IMAGE_NAME, MainActivity.OVERVIEW_FILENAME);
                startActivity(intent);
                activity.overridePendingTransition(0, 0);
            }
        });
        mPTText.setText(fireDB.getPlanungsteam());
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setTitle(getString(R.string.contact_header));
    }

    private void fillTextViews(View jbView, Contact c) {
        TextView tvName = (TextView) jbView.findViewById(R.id.tvJBName);
        TextView tvAddress = (TextView) jbView.findViewById(R.id.tvJBAddress);
        TextView tvMail = (TextView) jbView.findViewById(R.id.tvJBMail);
        TextView tvTelephone = (TextView) jbView.findViewById(R.id.tvJBTelephone);
        try {
            tvName.setText(c.getName());
            tvAddress.setText(c.getAddress());
            tvMail.setText(c.getMail());
            tvTelephone.setText(c.getTelephone());
        } catch (Exception ignored) {
        }
    }
}
