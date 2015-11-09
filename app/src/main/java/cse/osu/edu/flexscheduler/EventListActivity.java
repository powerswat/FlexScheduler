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
        values.put("start_date", "01/01/2000");
        values.put("start_time", "10:00");
        values.put("deadline_date", "01/05/2000");
        values.put("deadline_time", "11:00");
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
            cursor = db.query(EventDatabase.FLEX_SCHEDULER_TABLE_NAME, columns, selection, selectionArg, null, null, null);

            if (cursor.moveToFirst()) {

                while (cursor.isAfterLast() == false) {
                    String temp_event_id = cursor.getString(cursor.getColumnIndex("event_id"));
                    String temp_title = cursor.getString(cursor.getColumnIndex("title"));
                    String temp_start_date = cursor.getString(cursor.getColumnIndex("start_date"));
                    String temp_start_time = cursor.getString(cursor.getColumnIndex("start_time"));
                    String temp_deadline_date = cursor.getString(cursor.getColumnIndex("deadline_date"));
                    String temp_deadline_time = cursor.getString(cursor.getColumnIndex("deadline_time"));

                    events.add(new SingleEventForList(getIntent().getStringExtra("accountID"), temp_event_id, temp_title, temp_start_date, temp_start_time, temp_deadline_date, temp_deadline_time));
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

    public void moveToDetailList(String i) {
        Intent intent = new Intent(EventListActivity.this, DetailList.class);
        intent.putExtra("detailListMode",String.valueOf(2));
        intent.putExtra("accountID",getIntent().getStringExtra("accountID"));
        intent.putExtra("eventID",i);
        startActivity(intent);
    }
}
