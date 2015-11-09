package cse.osu.edu.flexscheduler;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.ContentValues;
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

    int startYear, startMonth, startDay, startHour, startMinute;
    int deadlineYear, deadlineMonth, deadlineDay, deadlineHour,  deadlineMinute;
    int mYear, mMonth, mDay, mHour, mMinute;

    TextView startTxtDate;
    TextView startTxtTime;
    TextView deadlineTxtDate;
    TextView deadlineTxtTime;
    TextView durationTxtMinutes;
    TextView durationTxtHours;


    ContentValues values = new ContentValues();
    EventDatabase mydb = null;

    private static final int REQUEST_PLACE_PICKER = 1;

    public static final String TAG = "SampleActivityBase";

    // For picking a place
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;
    private TextView mTitleView;
    private TextView mNoteView;
    private TextView mParticipantView;
    private TextView mPlaceDetailsText;
    private TextView mPlaceDetailsAttribution;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    String detailListMode;
    String eventID;

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

        //mAutocompleteView = (AutoCompleteTextView)findViewById(R.id.autocomplete_places);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        // Retrieve the TextViews that will display details and attributions of the selected place.
        //mPlaceDetailsText = (TextView) findViewById(R.id.place_details);
        //mPlaceDetailsAttribution = (TextView) findViewById(R.id.place_attribution);

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

                    Intent i = new Intent(DetailList.this, EventListActivity.class);
                    i.putExtra("accountID", getIntent().getStringExtra("accountID"));
                    startActivity(i);
                }
            }
        });

        directionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteView.setText("");
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
                values.put("account_id", getIntent().getStringExtra("accountID"));
                values.put("title", mTitleView.getText().toString());
                values.put("start_date", startTxtDate.getText().toString());
                values.put("start_time", startTxtTime.getText().toString());
                values.put("duration", durationTxtHours.getText().toString() + ' ' + durationTxtMinutes.getText().toString());
                values.put("deadline_date", deadlineTxtDate.getText().toString());
                values.put("deadline_time", deadlineTxtTime.getText().toString());
                values.put("participants", mParticipantView.getText().toString());
                values.put("note", mNoteView.getText().toString());

                addEntry(values);

                Intent i = new Intent(DetailList.this, EventListActivity.class);
                i.putExtra("accountID", getIntent().getStringExtra("accountID"));
                startActivity(i);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                values.put("account_id", getIntent().getStringExtra("accountID"));
                values.put("title", mTitleView.getText().toString());
                values.put("start_date", startTxtDate.getText().toString());
                values.put("start_time", startTxtTime.getText().toString());
                values.put("duration", durationTxtHours.getText().toString() + ' ' + durationTxtMinutes.getText().toString());
                values.put("deadline_date", deadlineTxtDate.getText().toString());
                values.put("deadline_time", deadlineTxtTime.getText().toString());
                values.put("participants", mParticipantView.getText().toString());
                values.put("note", mNoteView.getText().toString());

                addEntry(values);

                Intent i = new Intent(DetailList.this, EventListActivity.class);
                i.putExtra("accountID", getIntent().getStringExtra("accountID"));
                startActivity(i);
            }
        });



        detailListMode = getIntent().getExtras().getString("detailListMode");

        if (detailListMode.equals("1")) {
            directionButton.setVisibility(View.GONE);
            postponeButton.setVisibility(View.GONE);
            doneButton.setVisibility(View.GONE);

            Calendar cal = new GregorianCalendar();
            startYear =  deadlineYear = mYear = cal.get(Calendar.YEAR);
            startMonth =  deadlineMonth = mMonth = cal.get(Calendar.MONTH);
            startDay =  deadlineDay = mDay = cal.get(Calendar.DAY_OF_MONTH);
            startHour =  deadlineHour = mHour = cal.get(Calendar.HOUR_OF_DAY);
            startMinute =  deadlineMinute = mMinute = cal.get(Calendar.MINUTE);

            startTxtDate.setText(String.format("%d/%d/%d", mMonth+1, mDay, mYear));
            startTxtTime.setText(String.format("%02d:%02d", mHour, mMinute));
            deadlineTxtDate.setText(String.format("%d/%d/%d", mMonth + 1, mDay, mYear));
            deadlineTxtTime.setText(String.format("%02d:%02d", mHour, mMinute));

        }
        else if (detailListMode.equals("2")) {
            addButton.setVisibility(View.GONE);

            mydb = new EventDatabase(this);
            SQLiteDatabase db = mydb.getReadableDatabase();

            //WHERE clause arguments
            String[] selectionArg = {getIntent().getStringExtra("accountID"), getIntent().getStringExtra("eventID")};

            String[] columns = {"*"};

            String selection = "account_id=? AND event_id=?";

            Cursor cursor = null;
            try {
                cursor = db.query(EventDatabase.FLEX_SCHEDULER_TABLE_NAME, columns, selection, selectionArg, null, null, null);

                if (cursor.moveToFirst()) {
                    mTitleView.setText(cursor.getString(cursor.getColumnIndex("title")));
                    startTxtDate.setText(cursor.getString(cursor.getColumnIndex("start_date")));
                    startTxtTime.setText(cursor.getString(cursor.getColumnIndex("start_time")));
                    deadlineTxtDate.setText(cursor.getString(cursor.getColumnIndex("deadline_date")));
                    deadlineTxtTime.setText(cursor.getString(cursor.getColumnIndex("deadline_time")));
                    mParticipantView.setText(cursor.getString(cursor.getColumnIndex("participants")));
                    mNoteView.setText(cursor.getString(cursor.getColumnIndex("note")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            db.close();
            //String eventID = getIntent().getExtras().getString("eventID");
        }

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
                new DatePickerDialog(DetailList.this,startDateSetListener, startYear, startMonth, startDay).show();
                break;
            case R.id.start_time:
                new TimePickerDialog(DetailList.this,startTimeSetListener, startHour, startMinute, true).show();
                break;
            case R.id.deadline_date:
                new DatePickerDialog(DetailList.this,deadlineDateSetListener, deadlineYear, deadlineMonth, deadlineDay).show();
                break;
            case R.id.deadline_time:
                new TimePickerDialog(DetailList.this,deadlineTimeSetListener, deadlineHour, deadlineMinute, true).show();
                break;
            /*case R.id.duration_hours:
                durationHoursNow();
                break;
            case R.id.duration_minutes:
                durationMinutesNow();
                break;*/
            /*case R.id.test_place:
                mapClick();
                break;*/
        }
    }

    DatePickerDialog.OnDateSetListener startDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                {
                    startYear = year;
                    startMonth = monthOfYear;
                    startDay = dayOfMonth;

                    startUpdateNow();
                }
            };

    TimePickerDialog.OnTimeSetListener startTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    startHour = hourOfDay;
                    startMinute = minute;
                    startUpdateNow();
                }
            };

    DatePickerDialog.OnDateSetListener deadlineDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                {
                    deadlineYear = year;
                    deadlineMonth = monthOfYear;
                    deadlineDay = dayOfMonth;
                    deadlineUpdateNow();
                }
            };

    TimePickerDialog.OnTimeSetListener deadlineTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    deadlineHour = hourOfDay;
                    deadlineMinute = minute;

                    deadlineUpdateNow();
                }
            };


    void startUpdateNow(){
            startTxtDate.setText(String.format("%d/%d/%d", startMonth + 1, startDay, startYear));
            startTxtTime.setText(String.format("%02d:%02d", startHour, startMinute));
    }

    void deadlineUpdateNow(){
            deadlineTxtDate.setText(String.format("%d/%d/%d", deadlineMonth+1, deadlineDay, deadlineYear));
            deadlineTxtTime.setText(String.format("%02d:%02d", deadlineHour, deadlineMinute));
    }


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


            //Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
             //       Toast.LENGTH_SHORT).show();
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
                values.put("place_longitude", String.valueOf(place.getLatLng().longitude));
            }
            else {
                Toast.makeText(getApplicationContext(), "Clicked: " + place.getLatLng(),
                        Toast.LENGTH_SHORT).show();
            }
            // Format details of the place for display and show it in a TextView.
            /*mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
                    place.getId(), place.getAddress(), place.getPhoneNumber(),
                    place.getWebsiteUri()));
              */
            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            /*if (thirdPartyAttribution == null) {
                mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }*/

            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

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

    public void addEntry(ContentValues values){
        mydb = new EventDatabase(this);
        SQLiteDatabase db = mydb.getWritableDatabase();

        try{
            db.insert(EventDatabase.FLEX_SCHEDULER_TABLE_NAME, null, values);
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

}
