package cse.osu.edu.flexscheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * An activity to display a list view of detail information about event
 *
 */
public class DetailList extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener{

    TextView startTxtDate, startTxtTime, deadlineTxtDate, deadlineTxtTime, durationTxtMinutes, durationTxtHours;

    ContentValues values = new ContentValues();
    EventDatabase mydb = null;

    public static final String TAG = "SampleActivityBase";

    // For picking a place
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;
    private TextView mTitleView;
    private TextView mNoteView;
    private TextView mParticipantView;
    private static final LatLngBounds BOUNDS_COLUMBUS = new LatLngBounds(
            new LatLng(39.888694, -83.149669), new LatLng(40.146367, -82.857158));

    double latitude = 40.001626; // if GPS is not working, the current latitude and longitude are set up near Ohio Stadium
    double longitude = -83.019456;

    String detailListMode;
    long event_ID = -1;

    PendingIntent pendingIntent;

    Calendar current_calendar = new GregorianCalendar();
    Calendar start_date_calendar = Calendar.getInstance();
    Calendar deadline_date_calendar = Calendar.getInstance();

    int start_year, start_month, start_day, start_hour, start_minute;
    int deadline_year, deadline_month, deadline_day, deadline_hour,  deadline_minute;
    int current_year, current_month, current_day, current_hour, current_minute;
    int duration_hour, duration_minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_list);

        // For picking a place
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)findViewById(R.id.autocomplete_places);
        mTitleView = (TextView)findViewById(R.id.title);
        mNoteView= (TextView)findViewById(R.id.note);
        mParticipantView = (TextView)findViewById(R.id.participant);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_COLUMBUS,
                null);
        mAutocompleteView.setAdapter(mAdapter);

        // Set up the buttons that will display on DetailListActivity
        Button clearPlaceButton = (Button) findViewById(R.id.autocomplete_clear);
        Button clearTitleButton = (Button) findViewById(R.id.clear_title);
        Button clearNoteButton = (Button) findViewById(R.id.clear_note);
        Button clearParticipantButton = (Button) findViewById(R.id.clear_participant);
        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        Button directionButton = (Button) findViewById(R.id.direction_button);
        Button postponeButton = (Button) findViewById(R.id.postpone_button);
        Button doneButton = (Button) findViewById(R.id.done_button);
        Button addButton = (Button) findViewById(R.id.Add_button);

        startTxtDate = (TextView)findViewById(R.id.start_date);
        startTxtTime = (TextView)findViewById(R.id.start_time);
        deadlineTxtDate = (TextView)findViewById(R.id.deadline_date);
        deadlineTxtTime = (TextView)findViewById(R.id.deadline_time);
        durationTxtHours = (TextView)findViewById(R.id.duration_hours);
        durationTxtMinutes = (TextView)findViewById(R.id.duration_minutes);

        // Set up the current date and time to Calendar values
        current_year = current_calendar.get(Calendar.YEAR);
        current_month = current_calendar.get(Calendar.MONTH);
        current_day = current_calendar.get(Calendar.DAY_OF_MONTH);
        current_hour = current_calendar.get(Calendar.HOUR_OF_DAY);
        current_minute = current_calendar.get(Calendar.MINUTE);

        //detailListMode = getIntent().getExtras().getString("detailListMode");

        // The following code is only for unit testing please uncomment the code and
        // comment the code "detailListMode = getIntent().getExtras().getString("detailListMode");"
        // above to proceed the test
        detailListMode = "2";

                mydb = new EventDatabase(this);
        final SQLiteDatabase db = mydb.getReadableDatabase();

        /* Consider to different modes
        * First, if new event will be added, detailListMode will be 1
        * and direction, postpone, done buttons will be invisible.
        * Second, if current event will be updated, detailListMode will be 2
        * and add button will be invisible
         */
        if (detailListMode.equals("1")) {
            directionButton.setVisibility(View.GONE);
            postponeButton.setVisibility(View.GONE);
            doneButton.setVisibility(View.GONE);
            initializeNewDetailList();
        }
        else if (detailListMode.equals("2")) {
            addButton.setVisibility(View.GONE);
            initializeExistedDetailList();
        }

        // Set up the 'clear text' button that clears the text in the autocomplete view
        clearPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteView.setText("");
            }
        });

        // Set up the 'clear text' button that clears the text in the title
        clearTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitleView.setText("");
            }
        });

        // Set up the 'clear text' button that clears the text in the note
        clearNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNoteView.setText("");
            }
        });

        // Set up the 'clear text' button that clears the text in the participant
        clearParticipantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParticipantView.setText("");
            }
        });

        /* Set up the 'cancel' button
        * If detailListMode is 1 (new event), move to EventListActivity without action
        * If detailListMode is 2 (existing event), remove the event in Database
        */
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detailListMode.equals("1")) {
                    Intent i = new Intent(DetailList.this, EventListActivity.class);
                    i.putExtra("accountID", getIntent().getStringExtra("accountID"));
                    startActivity(i);
                }
                else if (detailListMode.equals("2")) {
                    deleteEntry();
                    cancelAlarm();
                    Intent i = new Intent(DetailList.this, EventListActivity.class);
                    i.putExtra("accountID", getIntent().getStringExtra("accountID"));
                    startActivity(i);
                }
            }
        });

        // Set up 'direction' button that move to google map
        // with the latitude and longitude of event place
        directionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude +"," + longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        // Set up 'postpone' button that find the next available time
        // to do event
        postponeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] selectionArg = {getIntent().getStringExtra("accountID")};
                String[] columns = {"*"};
                String selection = "account_id=?";
                Cursor cursor;

                ArrayList<String> db_st_times = new ArrayList<String>();
                ArrayList<String> db_durations = new ArrayList<String>();
                ArrayList<String> db_dl_times = new ArrayList<String>();
                Calendar best_st_time;

                try {
                    cursor = db.query(EventDatabase.FLEX_SCHEDULER_TABLE_NAME, columns, selection,
                            selectionArg, null, null, "start_date_time ASC");
                    if(cursor.moveToFirst()){
                        while(cursor.isAfterLast() == false){
                            db_st_times.add(cursor.getString(cursor.getColumnIndex("start_date_time")));
                            db_durations.add(cursor.getString(cursor.getColumnIndex("duration")));
                            db_dl_times.add(cursor.getString(cursor.getColumnIndex("deadline_date_time")));
                            cursor.moveToNext();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                duration_hour = Integer.parseInt(durationTxtHours.getText().toString());
                duration_minute = Integer.parseInt(durationTxtMinutes.getText().toString());

                best_st_time = findBestSpot(db_st_times, db_durations, db_dl_times, start_month+1, start_day,
                        start_hour, start_year, start_minute, duration_hour, duration_minute,
                        deadline_month+1, deadline_day, deadline_year, deadline_hour, deadline_minute);

                if (best_st_time != null){
                    start_month = best_st_time.get(Calendar.MONTH);
                    start_day = best_st_time.get(Calendar.DAY_OF_MONTH);
                    start_year = best_st_time.get(Calendar.YEAR);
                    start_hour = best_st_time.get(Calendar.HOUR_OF_DAY);
                    start_minute = best_st_time.get(Calendar.MINUTE);
                    updateStartDateTime();
                }
            }
        });

        // Set up 'done' button that update the event in database
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateValuesToDatabase();
            }
        });

        // Set up 'add' button that insert the event in database
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addValuesToDatabase();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_list, menu);
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

    // Find the best available spot for postpone event
    public Calendar findBestSpot(ArrayList<String> db_st_times, ArrayList<String> db_durations,
                                 ArrayList<String> db_dl_times, int start_month, int start_day,
                                 int start_hour, int start_year, int start_minute, int duration_hour,
                                 int duration_minute, int deadline_month, int deadline_day,
                                 int deadline_year, int deadline_hour, int deadline_minute) {

        int adj_duration = this.duration_hour + 1;

        SimpleDateFormat date_format = new SimpleDateFormat("MM dd yyyy hh mm");
        SimpleDateFormat duration_form = new SimpleDateFormat("hh mm");
        SimpleDateFormat db_date_format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");

        String st_time_str = Integer.toString(start_month) + " " + Integer.toString(start_day) + " "
                + Integer.toString(start_year) + " " + Integer.toString(start_hour) + " "
                + Integer.toString(start_minute);
        String dl_time_str = Integer.toString(deadline_month) + " " + Integer.toString(deadline_day) + " "
                + Integer.toString(deadline_year) + " " + Integer.toString(deadline_hour) + " "
                + Integer.toString(deadline_minute);
        String dura_str = Integer.toString(adj_duration) + " " + Integer.toString(duration_minute);

        Date st_time = new Date();
        Date dl_time = new Date();
        Date dura_time = new Date();

        try {
            st_time = date_format.parse(st_time_str);
            dl_time = date_format.parse(dl_time_str);
            dura_time = duration_form.parse(dura_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < db_st_times.size()-1; i++){
            try {
                Date db_st_time = db_date_format.parse(db_st_times.get(i));
                Date db_next_st_time = db_date_format.parse(db_st_times.get(i+1));
                Date db_duration = duration_form.parse(db_durations.get(i));

                if(st_time.getTime() >= db_st_time.getTime()){
                    continue;
                }

                if (db_next_st_time.getTime() - (db_st_time.getTime() + db_duration.getTime())
                        > dura_time.getTime()){
                    Calendar cal_st = Calendar.getInstance();
                    cal_st.setTime(db_st_time);
                    Calendar cal_du = Calendar.getInstance();
                    cal_du.setTime(db_duration);
                    Calendar cal_dl = Calendar.getInstance();
                    cal_dl.setTime(dl_time);

                    int st_hours = cal_st.get(Calendar.HOUR_OF_DAY);
                    int du_hours = cal_du.get(Calendar.HOUR_OF_DAY);
                    st_hours += (du_hours + 1);
                    int st_mins = cal_st.get(Calendar.MINUTE);
                    int du_mins = cal_du.get(Calendar.MINUTE);
                    st_mins += du_mins;

                    cal_st.set(Calendar.HOUR_OF_DAY, st_hours);
                    cal_st.set(Calendar.MINUTE, st_mins);

                    if (cal_st.after(cal_dl)){
                        Toast.makeText(getApplicationContext(),
                                "Cannot postpone beyond the deadline",
                                Toast.LENGTH_LONG).show();
                        cal_st.setTime(db_st_time);
                        return null;
                    }

                    return cal_st;
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    // Set up when start date, start time, deadline date, deadline time are clicked
    public void mOnClick(View v) {
        switch(v.getId()) {
            case R.id.start_date:
                new DatePickerDialog(DetailList.this,startDateSetListener, start_year, start_month, start_day).show();
                break;
            case R.id.start_time:
                new TimePickerDialog(DetailList.this,startTimeSetListener, start_hour, start_minute, true).show();
                break;
            case R.id.deadline_date:
                new DatePickerDialog(DetailList.this,deadlineDateSetListener, deadline_year, deadline_month, deadline_day).show();
                break;
            case R.id.deadline_time:
                new TimePickerDialog(DetailList.this,deadlineTimeSetListener, deadline_hour, deadline_minute, true).show();
                break;
        }
    }

    // when start date is selected on dialog, the start date and time will be updated
    DatePickerDialog.OnDateSetListener startDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                {
                    start_year = year;
                    start_month = monthOfYear;
                    start_day = dayOfMonth;
                    updateStartDateTime();
                }
            };

    // when start time is selected on dialog, the start date and time will be updated
    TimePickerDialog.OnTimeSetListener startTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    start_hour = hourOfDay;
                    start_minute = minute;
                    updateStartDateTime();
                }
            };

    // when deadline date is selected on dialog, the deadline date and time will be updated
    DatePickerDialog.OnDateSetListener deadlineDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                {
                    deadline_year = year;
                    deadline_month = monthOfYear;
                    deadline_day = dayOfMonth;
                    updateDeadlineDateTime();
                }
            };

    // when deadline time is selected on dialog, the deadline date and time will be updated
    TimePickerDialog.OnTimeSetListener deadlineTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    deadline_hour = hourOfDay;
                    deadline_minute = minute;
                    updateDeadlineDateTime();
                }
            };

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            FragmentManager fm = getSupportFragmentManager();
            DetailListFragment detailFragment = (DetailListFragment)fm.findFragmentById(R.id.detail_list_fragment);

            // Update the place information
            if (detailFragment != null) {
                detailFragment.changeMap(place.getLatLng());

                values.put("place", place.getName().toString());
                values.put("place_latitude", String.valueOf(place.getLatLng().latitude));
                values.put("place_longitude",String.valueOf(place.getLatLng().longitude));
            }
            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    // Set up the initial Date Time to current date and time
    public void initializeNewDetailList() {
            start_year =  deadline_year = current_year;
            start_month =  deadline_month = current_month;
            start_day =  deadline_day = current_day;
            start_hour =  deadline_hour = current_hour;
            start_minute =  deadline_minute = current_minute;

            updateStartDateTime();
            updateDeadlineDateTime();
    }

    // when current existing event is selected on EventLIstActivity,
    // the function loads the information of event in database
    public void initializeExistedDetailList() {
        //event_ID = Integer.valueOf(getIntent().getStringExtra("eventID"));

        // The following code is only for unit testing please uncomment the code and
        // comment the code "event_ID = Integer.valueOf(getIntent().getStringExtra("eventID"));"
        // above to proceed the test
        event_ID = 1;

                mydb = new EventDatabase(this);
        SQLiteDatabase db = mydb.getReadableDatabase();

        //WHERE clause arguments
        String[] selectionArg = {getIntent().getStringExtra("accountID"), getIntent().getStringExtra("eventID")};

        String[] columns = {"*"};

        String selection = "account_id=? AND event_id=?";

        Cursor cursor;

        try {
            cursor = db.query(EventDatabase.FLEX_SCHEDULER_TABLE_NAME, columns, selection, selectionArg, null, null, null);
            if (cursor.moveToFirst()) {
                extractValuesFromDatabase(cursor);
                extractStringToDateTimeFromDatabase(cursor);
                updateStartDateTime();
                updateDeadlineDateTime();

                LatLng latLng = new LatLng(latitude, longitude);
                FragmentManager fm = getSupportFragmentManager();
                DetailListFragment detailFragment = (DetailListFragment)fm.findFragmentById(R.id.detail_list_fragment);
                detailFragment.changeMap(latLng);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();
    }

    // update start date and time
    void updateStartDateTime(){
        startTxtDate.setText(String.format("%d-%02d-%02d", start_year, start_month+1, start_day));
        startTxtTime.setText(String.format("%02d:%02d", start_hour, start_minute));
    }

    // update deadline date and time
    void updateDeadlineDateTime(){
        deadlineTxtDate.setText(String.format("%d-%02d-%02d", deadline_year, deadline_month+1, deadline_day));
        deadlineTxtTime.setText(String.format("%02d:%02d", deadline_hour, deadline_minute));
    }

    // extract the string from data and time in database for alarm
    void extractStringToDateTimeFromDatabase(Cursor cursor) {
        String startDateTimeArray [] = cursor.getString(cursor.getColumnIndex("start_date_time")).split("[T]");
        String deadlineDateTimeArray [] = cursor.getString(cursor.getColumnIndex("deadline_date_time")).split("[T]");
        String startDateArray[] = startDateTimeArray[0].split("[-]");
        String startTimeArray[] = startDateTimeArray[1].split("[:]");
        String deadlineDateArray[] = deadlineDateTimeArray[0].split("[-]");
        String deadlineTimeArray[] = deadlineDateTimeArray[1].split("[:]");

        start_year = Integer.valueOf(startDateArray[0]);
        start_month =  Integer.valueOf(startDateArray[1]) -1;
        start_day =  Integer.valueOf(startDateArray[2]);
        start_hour = Integer.valueOf(startTimeArray[0]);
        start_minute = Integer.valueOf(startTimeArray[1]);

        deadline_year = Integer.valueOf(deadlineDateArray[0]);
        deadline_month = Integer.valueOf(deadlineDateArray[1]) -1;
        deadline_day = Integer.valueOf(deadlineDateArray[2]);
        deadline_hour = Integer.valueOf(deadlineTimeArray[0]);
        deadline_minute = Integer.valueOf(deadlineTimeArray[1]);
    }

    // extract each value in database and put it to view
    void extractValuesFromDatabase(Cursor cursor) {
        String textStr[] = cursor.getString(cursor.getColumnIndex("duration")).split("\\s+");
        mTitleView.setText(cursor.getString(cursor.getColumnIndex("title")));
        durationTxtHours.setText(textStr[0]);
        durationTxtMinutes.setText(textStr[1]);
        mParticipantView.setText(cursor.getString(cursor.getColumnIndex("participants")));
        mNoteView.setText(cursor.getString(cursor.getColumnIndex("note")));
        mAutocompleteView.setText(cursor.getString(cursor.getColumnIndex("place")));
        latitude = cursor.getDouble(cursor.getColumnIndex("place_latitude"));
        longitude = cursor.getDouble(cursor.getColumnIndex("place_longitude"));
    }

    // add the information of event to database and back to EventListActivity
    void addValuesToDatabase() {
        values.put("account_id", getIntent().getStringExtra("accountID"));
        values.put("title", mTitleView.getText().toString());
        values.put("start_date_time", startTxtDate.getText().toString() + 'T' + startTxtTime.getText().toString());
        values.put("duration", durationTxtHours.getText().toString() + ' ' + durationTxtMinutes.getText().toString());
        values.put("deadline_date_time", deadlineTxtDate.getText().toString() + 'T' + deadlineTxtTime.getText().toString());
        values.put("participants", mParticipantView.getText().toString());
        values.put("note", mNoteView.getText().toString());

        if (checkRangeBetweenStartAndDeadline()) {
            addEntry(values);
            addAlarm();
            Intent i = new Intent(DetailList.this, EventListActivity.class);
            i.putExtra("accountID", getIntent().getStringExtra("accountID"));
            startActivity(i);
        }
    }

    // update the information of event in database and back to EventListActivity
    void updateValuesToDatabase() {
        values.put("title", mTitleView.getText().toString());
        values.put("start_date_time", startTxtDate.getText().toString() + 'T' + startTxtTime.getText().toString());
        values.put("duration", durationTxtHours.getText().toString() + ' ' + durationTxtMinutes.getText().toString());
        values.put("deadline_date_time", deadlineTxtDate.getText().toString() + 'T' + deadlineTxtTime.getText().toString());
        values.put("participants", mParticipantView.getText().toString());
        values.put("note", mNoteView.getText().toString());

        if (checkRangeBetweenStartAndDeadline()) {
            updateEntry(values);
            updateAlarm();
            Intent i = new Intent(DetailList.this, EventListActivity.class);
            i.putExtra("accountID", getIntent().getStringExtra("accountID"));
            startActivity(i);
        }
    }

    // call the database to insert the values of the event
    public void addEntry(ContentValues values){
        mydb = new EventDatabase(this);
        SQLiteDatabase db = mydb.getWritableDatabase();

        try{
            event_ID = db.insert(EventDatabase.FLEX_SCHEDULER_TABLE_NAME, null, values);
        }catch(Exception e){
            e.printStackTrace();
        }

        db.close();
    }

    // call the database to update the values of the event
    public void updateEntry(ContentValues values){
        mydb = new EventDatabase(this);
        SQLiteDatabase db = mydb.getWritableDatabase();

        String[] selectionArg = {getIntent().getStringExtra("accountID"), getIntent().getStringExtra("eventID")};

        String selection = "account_id=? AND event_id=?";

        try{
            db.update(EventDatabase.FLEX_SCHEDULER_TABLE_NAME, values, selection, selectionArg);
        }catch(Exception e){
            e.printStackTrace();
        }
        db.close();
    }

    // call the database to delete the values of the event
    public void deleteEntry() {
        mydb = new EventDatabase(this);
        SQLiteDatabase db = mydb.getWritableDatabase();

        try{
            db.delete(EventDatabase.FLEX_SCHEDULER_TABLE_NAME, "account_id = " + getIntent().getStringExtra("accountID") + " and event_id = " + getIntent().getStringExtra("eventID"), null);
        }catch(Exception e){
            e.printStackTrace();
        }
        db.close();
    }

    // add alarm receiver based on values of event
    public void addAlarm() {
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra("accountID", getIntent().getStringExtra("accountID"));
        intent.putExtra("eventID", String.valueOf(event_ID));
        intent.putExtra("title", mTitleView.getText().toString());
        intent.putExtra("duration_hour", durationTxtHours.getText().toString());
        intent.putExtra("duration_minute", durationTxtMinutes.getText().toString());
        intent.putExtra("deadline_date", deadlineTxtDate.getText().toString());
        intent.putExtra("deadline_time", deadlineTxtTime.getText().toString());

        pendingIntent = PendingIntent.getBroadcast(getBaseContext(), (int)event_ID, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, start_date_calendar.getTimeInMillis(), pendingIntent);
    }

    // update alarm receiver based on values of event
    public void updateAlarm() {
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra("accountID", getIntent().getStringExtra("accountID"));
        intent.putExtra("eventID", String.valueOf(event_ID));
        intent.putExtra("title", mTitleView.getText().toString());
        intent.putExtra("duration_hour", durationTxtHours.getText().toString());
        intent.putExtra("duration_minute", durationTxtMinutes.getText().toString());
        intent.putExtra("deadline_date", deadlineTxtDate.getText().toString());
        intent.putExtra("deadline_time", deadlineTxtTime.getText().toString());

        pendingIntent = PendingIntent.getBroadcast(getBaseContext(), (int)event_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, start_date_calendar.getTimeInMillis(), pendingIntent);
    }

    // cancel alarm receiver based on values of event
    public void cancelAlarm() {
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getBaseContext(), (int)event_ID, intent,  PendingIntent.FLAG_CANCEL_CURRENT) ;
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
    }

    // check whether start date and time is later than deadline date and time
    // and start date time is earlier than current date and time
    public boolean checkRangeBetweenStartAndDeadline() {
        start_date_calendar.set(start_year, start_month, start_day, start_hour, start_minute, 00);
        deadline_date_calendar.set(deadline_year, deadline_month, deadline_day, deadline_hour, deadline_minute, 00);

        if(start_date_calendar.compareTo(current_calendar) < 0){
            //The set Date/Time already passed
            Toast.makeText(getApplicationContext(),
                    "Invalid Date/Time",
                    Toast.LENGTH_LONG).show();
        }else if (deadline_date_calendar.compareTo(start_date_calendar) < 0) {
            Toast.makeText(getApplicationContext(),
                    "Deadline is earlier than start",
                    Toast.LENGTH_LONG).show();
        }else {
            return true;
        }
        return false;
    }
}
