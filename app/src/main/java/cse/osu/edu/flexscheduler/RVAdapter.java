package cse.osu.edu.flexscheduler;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.EventViewHolder> {

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        CardView cv;

        TextView event_title;
        TextView start_date;
        TextView start_time;
        TextView deadline_date;
        TextView deadline_time;

        EventViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            event_title = (TextView)itemView.findViewById(R.id.event_title);
            start_date = (TextView)itemView.findViewById(R.id.start_date);
            start_time = (TextView)itemView.findViewById(R.id.start_time);
            deadline_date = (TextView)itemView.findViewById(R.id.deadline_date);
            deadline_time = (TextView)itemView.findViewById(R.id.deadline_time);
        }
    }

    List<SingleEvent> events;

    RVAdapter(List<SingleEvent> events){
        this.events = events;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_event, viewGroup, false);
        EventViewHolder pvh = new EventViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {
        eventViewHolder.event_title.setText(events.get(i).mEventTitle);
        eventViewHolder.start_date.setText(events.get(i).mEventStartDate);
        eventViewHolder.start_time.setText(events.get(i).mEventStartTime);
        eventViewHolder.deadline_date.setText(events.get(i).mEventDeadlineDate);
        eventViewHolder.deadline_time.setText(events.get(i).mEventDeadlineTime);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
