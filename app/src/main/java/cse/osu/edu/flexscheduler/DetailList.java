package cse.osu.edu.flexscheduler;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
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
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

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
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);
        mAutocompleteView.setAdapter(mAdapter);

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

        current_year = current_calendar.get(Calendar.YEAR);
        current_month = current_calendar.get(Calendar.MONTH);
        current_day = current_calendar.get(Calendar.DAY_OF_MONTH);
        current_hour = current_calendar.get(Calendar.HOUR_OF_DAY);
        current_minute = current_calendar.get(Calendar.MINUTE);

        detailListMode = getIntent().getExtras().getString("detailListMode");

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

        clearTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitleView.setText("");
            }
        });

        clearNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNoteView.setText("");
            }
        });

        clearParticipantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParticipantView.setText("");
            }
        });

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

        directionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude +"," + longitude);
                //Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + latitude +"," + longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        postponeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteView.setText("");
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateValuesToDatabase();
            }
        });

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

    TimePickerDialog.OnTimeSetListener startTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    start_hour = hourOfDay;
                    start_minute = minute;
                    updateStartDateTime();
                }
            };

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
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
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



    public void initializeNewDetailList() {
            start_year =  deadline_year = current_year;
            start_month =  deadline_month = current_month;
            start_day =  deadline_day = current_day;
            start_hour =  deadline_hour = current_hour;
            start_minute =  deadline_minute = current_minute;

            updateStartDateTime();
            updateDeadlineDateTime();
    }

    public void initializeExistedDetailList() {
        event_ID = Integer.valueOf(getIntent().getStringExtra("eventID"));

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

    void updateStartDateTime(){
        startTxtDate.setText(String.format("%d-%02d-%02d", start_year, start_month+1, start_day));
        startTxtTime.setText(String.format("%02d:%02d", start_hour, start_minute));
    }

    void updateDeadlineDateTime(){
        deadlineTxtDate.setText(String.format("%d-%02d-%02d", deadline_year, deadline_month+1, deadline_day));
        deadlineTxtTime.setText(String.format("%02d:%02d", deadline_hour, deadline_minute));
    }

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

    public void addAlarm() {
        Toast.makeText(getApplicationContext(),
                startTxtDate.getText() + " " + event_ID,
        Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra("accountID", getIntent().getStringExtra("accountID"));
        intent.putExtra("eventID",  String.valueOf(event_ID));
        pendingIntent = PendingIntent.getBroadcast(getBaseContext(), (int)event_ID, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, start_date_calendar.getTimeInMillis(), pendingIntent);
    }

    public void updateAlarm() {
        Toast.makeText(getApplicationContext(),
                startTxtDate.getText() + " " + event_ID,
                Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra("accountID", getIntent().getStringExtra("accountID"));
        intent.putExtra("eventID",  String.valueOf(event_ID));
        pendingIntent = PendingIntent.getBroadcast(getBaseContext(), (int)event_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, start_date_calendar.getTimeInMillis(), pendingIntent);
    }

    public void cancelAlarm() {
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getBaseContext(), (int)event_ID, intent,  PendingIntent.FLAG_CANCEL_CURRENT) ;
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }

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
