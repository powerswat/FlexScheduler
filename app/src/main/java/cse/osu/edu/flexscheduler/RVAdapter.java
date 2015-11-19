package cse.osu.edu.flexscheduler;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.EventViewHolder> {

    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cv;

        TextView event_title;
        TextView event_date;
        String  tempAccountID, tempEventID;
        private final Context context;

        EventViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            cv = (CardView)itemView.findViewById(R.id.cv);
            event_title = (TextView)itemView.findViewById(R.id.event_title);
            event_date = (TextView)itemView.findViewById(R.id.event_date);

            itemView.setOnClickListener(this);
        }

        public void setItem(String accountID, String eventID) {
           tempAccountID = accountID;
           tempEventID = eventID;
        }

        String TAG = "Test";
        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick " + tempAccountID + tempEventID);
            Intent intent =  new Intent(context, DetailList.class);
            intent.putExtra("detailListMode",String.valueOf(2));
            intent.putExtra("accountID",tempAccountID);
            intent.putExtra("eventID",tempEventID);
            context.startActivity(intent);
        }
    }

    List<SingleEventForList> events;

    RVAdapter(List<SingleEventForList> events){
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
        eventViewHolder.event_date.setText("Start :" + events.get(i).mEventStartDate + " " + events.get(i).mEventStartTime + " Deadline :"
                + events.get(i).mEventDeadlineDate + " " + events.get(i).mEventDeadlineTime );
        eventViewHolder.setItem(events.get(i).mAccountID, events.get(i).mEventID);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
