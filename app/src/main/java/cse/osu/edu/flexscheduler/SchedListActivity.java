package cse.osu.edu.flexscheduler;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SchedListActivity extends AppCompatActivity {
    private final static String  TAG = "SchedListAcitivity";
    private ArrayList<NoticeData> notice_data = new ArrayList<NoticeData>();
    Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sched_list);

        Log.d(TAG, "onCreate()");

        notice_data.add(new NoticeData("공지사항내용1", "2015-03-22"));
        notice_data.add(new NoticeData("공지사항내용2", "2015-03-25"));
        notice_data.add(new NoticeData("공지사항내용3", "2015-03-28"));

        ListView notice_list = (ListView) findViewById(R.id.listView);
        NoticeListAdapter adapter = new NoticeListAdapter(mContext,0,notice_data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }


    private class NoticeListAdapter extends ArrayAdapter<NoticeData> {

        private ArrayList<NoticeData> mNoticeData;

        public NoticeListAdapter(Context context, int resource, ArrayList<NoticeData> notice_data){
            super(context, resource, notice_data);
            mNoticeData = notice_data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
            View row_view = inflater.inflate(R.layout.list_notice, null, true);

            TextView txtTitle = (TextView) row_view.findViewById(R.id.title);
            TextView txtDate = (TextView) row_view.findViewById(R.id.date);

            txtTitle.setText(mNoticeData.get(position).getTitle());
            txtDate.setText(mNoticeData.get(position).getData());

            return row_view;
        }
    }
}
