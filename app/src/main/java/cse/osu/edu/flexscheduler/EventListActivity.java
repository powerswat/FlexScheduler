package cse.osu.edu.flexscheduler;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends AppCompatActivity {

    private List<SingleEventForList> events;
    private RecyclerView rv;

    EventDatabase mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        Toast.makeText(getApplicationContext(), String.valueOf(getIntent().getStringExtra("accountID")), Toast.LENGTH_SHORT).show();


        FloatingActionButton addNewEvent = (FloatingActionButton) findViewById(R.id.add_new_event);

        addNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventListActivity.this, DetailList.class);
                i.putExtra("detailListMode", String.valueOf(1));
                i.putExtra("accountID", getIntent().getStringExtra("accountID"));
                startActivity(i);
            }
        });

        rv = (RecyclerView) findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        initializeData();
        initializeAdapter();
    }

    private void initializeData() {
        events = new ArrayList<>();
        mydb = new EventDatabase(this);

/*
        SQLiteDatabase db = mydb.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("account_id", getIntent().getStringExtra("accountID"));
        values.put("start_date_time", "2015-11-17T10:00");
        values.put("duration", "10 00");
        values.put("deadline_date_time", "2015-11-17T10:00");
       // values.put("deadline_time", "11:00");
        values.put("title", "Test1");

        try{
            db.insert(EventDatabase.FLEX_SCHEDULER_TABLE_NAME, null, values);
        }catch(Exception e){
            e.printStackTrace();
        }
*/
        //mydb = new EventDatabase(this);
        SQLiteDatabase db = mydb.getReadableDatabase();

        //WHERE clause arguments
        String[] selectionArg = {getIntent().getStringExtra("accountID")};

        String[] columns = {"*"};

        String selection = "account_id=?";

        Cursor cursor = null;
        try {
            cursor = db.query(EventDatabase.FLEX_SCHEDULER_TABLE_NAME, columns, selection, selectionArg, null, null,  "start_date_time  ASC");

            if (cursor.moveToFirst()) {

                while (cursor.isAfterLast() == false) {
                    String temp_event_id = cursor.getString(cursor.getColumnIndex("event_id"));
                    String temp_title = cursor.getString(cursor.getColumnIndex("title"));
                    String temp_start_date_time[] = cursor.getString(cursor.getColumnIndex("start_date_time")).split("[T]");
                    String temp_deadline_date_time[] = cursor.getString(cursor.getColumnIndex("deadline_date_time")).split("[T]");

                    events.add(new SingleEventForList(getIntent().getStringExtra("accountID"), temp_event_id, temp_title, temp_start_date_time[0], temp_start_date_time[1], temp_deadline_date_time[0], temp_deadline_date_time[1]));
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();

    }

    private void initializeAdapter() {
        RVAdapter adapter = new RVAdapter(events);
        rv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
}
