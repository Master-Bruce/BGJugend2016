package de.schiewe.volker.bgjugend2016.adapter;

import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.schiewe.volker.bgjugend2016.R;
import de.schiewe.volker.bgjugend2016.data_models.Info;
import de.schiewe.volker.bgjugend2016.helper.AppPersist;
import de.schiewe.volker.bgjugend2016.helper.Util;

/**
 * Adapter for Info Fragment
 */
public class InfoListAdapter extends  RecyclerView.Adapter<InfoListAdapter.ViewHolder> {

    private ArrayList<Info> mInfos;
    private SharedPreferences prefs;
    private AppPersist app;

    public InfoListAdapter(SharedPreferences prefs) {
        this.prefs = prefs;
        app = AppPersist.getInstance();
    }

    public void setData(){
        mInfos = new ArrayList<>();
        for (Info currInfo : app.getInfos()) {
            String[] array = currInfo.getWannWo().split(", ");
            if (Util.checkDate(array[0], prefs))
                mInfos.add(currInfo);
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_info_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Info currInfo = mInfos.get(position);

        holder.Header.setText(currInfo.getHeadline());
        holder.Wer.setText(Html.fromHtml("<b>Wer: </b>" + currInfo.getWer()));
        holder.Wo_Wann.setText(Html.fromHtml("<b>Wann & Wo: </b>" + currInfo.getWannWo()));
        holder.Info_Anmeldung.setText(Html.fromHtml("<b>Infos & Anmeldung: </b>" + currInfo.getAnmeldung()));

    }

    @Override
    public int getItemCount() {
        return mInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView Header, Wer, Wo_Wann, Info_Anmeldung;

        public ViewHolder(View itemView) {
            super(itemView);

            Header = (TextView) itemView.findViewById(R.id.tvHeader);
            Wer = (TextView) itemView.findViewById(R.id.tvWer);
            Wo_Wann = (TextView) itemView.findViewById(R.id.tvWannWo);
            Info_Anmeldung = (TextView) itemView.findViewById(R.id.tvInfoAnmeldung);
            Info_Anmeldung.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
