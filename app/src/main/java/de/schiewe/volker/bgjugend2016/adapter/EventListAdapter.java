package de.schiewe.volker.bgjugend2016.adapter;

import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import de.schiewe.volker.bgjugend2016.R;
import de.schiewe.volker.bgjugend2016.data_models.Event;
import de.schiewe.volker.bgjugend2016.helper.AppPersist;
import de.schiewe.volker.bgjugend2016.helper.Util;

/**
 * Adapter for RecyclerView
 */
public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    private OnItemClickListener mOnClickListener;
    private ArrayList<Event> mEvents;
    private Date today = new Date();
    private SharedPreferences prefs;
    private AppPersist app;

    public EventListAdapter(SharedPreferences prefs) {
        this.prefs = prefs;
        app = AppPersist.getInstance();
    }

    public void setData(String query) {
        mEvents = new ArrayList<>();
        for (Event currEvent : app.getEvents()) {
            if (Util.checkDate(currEvent.getDate(), prefs) && Util.checkAge(currEvent.getAge(), prefs)) {
                if (query == null) {
                    mEvents.add(currEvent);
                } else if (currEvent.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    mEvents.add(currEvent);
                }
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<Event> getEvents() {
        return mEvents;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false);
        return new ViewHolder(itemView);
    }

    public Event getEventById(int eventId) {
        for (Event e :
                mEvents) {
            if (e.getId() == eventId) {
                return e;
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Event currEvent = mEvents.get(position);
        String date = currEvent.getPlace() + "\n" + currEvent.getDate();
        viewHolder.Name.setText(currEvent.getTitle());
        viewHolder.Date.setText(date);

        if (today.after(currEvent.getdDeadline())) {
            viewHolder.warning.setVisibility(View.VISIBLE);
        } else {
            viewHolder.warning.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.mOnClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Name, Date;
        ImageView warning;

        ViewHolder(View itemView) {
            super(itemView);
            Name = (TextView) itemView.findViewById(R.id.tvHeader);
            Date = (TextView) itemView.findViewById(R.id.tvDate);
            warning = (ImageView) itemView.findViewById(R.id.ivWarnig);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnClickListener != null) {
                mOnClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

}
