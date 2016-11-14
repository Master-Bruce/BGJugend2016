package de.schiewe.volker.bgjugend2016.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.schiewe.volker.bgjugend2016.R;
import de.schiewe.volker.bgjugend2016.activities.ImageActivity;
import de.schiewe.volker.bgjugend2016.activities.MainActivity;
import de.schiewe.volker.bgjugend2016.data_models.Event;
import de.schiewe.volker.bgjugend2016.helper.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {

    private TextView mTitle, mDate, mPlace, mHeader, mText, mAge, mPeopleNumber, mCost, mDeadline,
            mTeam, mContact, mContactPhone, mContactMail;
    private ImageView mImage;
    private Button mApply;
        private Event mEvent;
    private MainActivity activity;


    public static EventFragment newInstance(Event event) {
        EventFragment fragment = new EventFragment();
//        fragment.app = AppPersist.getInstance();
        fragment.mEvent = event;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_new, container, false);

        mTitle = (TextView) view.findViewById(R.id.tvTitle);
        mDate = (TextView) view.findViewById(R.id.tvDate);
        mPlace = (TextView) view.findViewById(R.id.tvPlace);
        mHeader = (TextView) view.findViewById(R.id.tvHeader);
        mText = (TextView) view.findViewById(R.id.tvText);
        mAge = (TextView) view.findViewById(R.id.tvAge);
        mPeopleNumber = (TextView) view.findViewById(R.id.tvPeopleNumber);
        mCost = (TextView) view.findViewById(R.id.tvCost);
        mDeadline = (TextView) view.findViewById(R.id.tvDeadline);
        mTeam = (TextView) view.findViewById(R.id.tvTeam);
        mContact = (TextView) view.findViewById(R.id.tvContact);
        mContactPhone = (TextView) view.findViewById(R.id.tvContactPhone);
        mContactMail = (TextView) view.findViewById(R.id.tvContactMail);

        mImage = (ImageView) view.findViewById(R.id.ivEvent);
        mApply = (Button) view.findViewById(R.id.btnApply);
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTitle.setText(mEvent.getTitle());
        mDate.setText(mEvent.getDate());
        mPlace.setText(mEvent.getPlace());
        mHeader.setText(mEvent.getHeader());
        mText.setText(Html.fromHtml(mEvent.getText()));
        mAge.setText(mEvent.getAge());
        mPeopleNumber.setText(mEvent.getPeopleNumber());
        mCost.setText(mEvent.getCost());
        mDeadline.setText(mEvent.getDeadline());
        mTeam.setText(Html.fromHtml(mEvent.getTeam()));
        String contact = mEvent.getContact().getName() + "\n" + mEvent.getContact().getAddress();
        mContact.setText(contact);
        mContactPhone.setText(mEvent.getContact().getTelephone());
        mContactMail.setText(mEvent.getContact().getMail());

        mApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.sendEmail(mEvent.getContact().getMail(), "Anmeldung für " + mEvent.getTitle(),
                        GetApplyString(mEvent, activity));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.getImage(activity, mEvent.getImage()) == null)
            mImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.loading));
        else {
            mImage.setImageBitmap(Util.getImage(activity, mEvent.getImage()));
            mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ImageActivity.class);
                    intent.putExtra(ImageActivity.IMAGE_NAME, mEvent.getImage());
                    startActivity(intent);
                    activity.overridePendingTransition(0, 0);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    public static String GetApplyString(Event currEvent, MainActivity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        String[] Name = prefs.getString(SettingsFragment.PREF_NAME, "").split(" ");
        StringBuilder sb = new StringBuilder();
        String[] ContactName = currEvent.getContact().getName().split(" ");
        String name = prefs.getString(SettingsFragment.PREF_NAME, "");
        String street = prefs.getString(SettingsFragment.PREF_STREET, "");
        String city = prefs.getString(SettingsFragment.PREF_CITY, "");
        String birthday = prefs.getString(SettingsFragment.PREF_BIRTHDAY, "");
        String telephone = prefs.getString(SettingsFragment.PREF_TELEPHONE, "");

        sb.append("Hallo ").append(ContactName[0]).append(",\n");
        sb.append("Ich möchte mich für die Veranstaltung ").append(currEvent.getTitle()).append(" vom ").append(currEvent.getDate()).append(" anmelden.\n\n");
        sb.append(name).append("\n");
        sb.append(street).append("\n");
        sb.append(city).append("\n");
        sb.append(birthday).append("\n");
        sb.append(telephone).append("\n\n");
        sb.append("Liebe Grüße\n").append(Name[0]);

        if (name.equals("") | street.equals("") | city.equals("") | birthday.equals("") | telephone.equals(""))
            Toast.makeText(activity, "Vergiss nicht Deine Daten vollständig anzugeben!", Toast.LENGTH_LONG).show();

        return sb.toString();
    }
}