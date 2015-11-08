package cse.osu.edu.flexscheduler;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class DetailList extends AppCompatActivity {

    int startYear, startMonth, startDay, startHour, startMinute;
    int deadlineYear, deadlineMonth, deadlineDay, deadlineHour,  deadlineMinute;
    int mYear, mMonth, mDay, mHour, mMinute;

    TextView startTxtDate;
    TextView startTxtTime;
    TextView deadlineTxtDate;
    TextView deadlineTxtTime;

    private static final int REQUEST_PLACE_PICKER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_list);

      /*  FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new DetailListFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }*/

        startTxtDate = (TextView)findViewById(R.id.start_date);
        startTxtTime = (TextView)findViewById(R.id.start_time);
        deadlineTxtDate = (TextView)findViewById(R.id.deadline_date);
        deadlineTxtTime = (TextView)findViewById(R.id.deadline_time);

        Calendar cal = new GregorianCalendar();
        startYear =  deadlineYear = mYear = cal.get(Calendar.YEAR);
        startMonth =  deadlineMonth = mMonth = cal.get(Calendar.MONTH);
        startDay =  deadlineDay = mDay = cal.get(Calendar.DAY_OF_MONTH);
        startHour =  deadlineHour = mHour = cal.get(Calendar.HOUR_OF_DAY);
        startMinute =  deadlineMinute = mMinute = cal.get(Calendar.MINUTE);

        startTxtDate.setText(String.format("%d/%d/%d", mMonth+1, mDay, mYear));
        startTxtTime.setText(String.format("%d:%d", mHour, mMinute));
        deadlineTxtDate.setText(String.format("%d/%d/%d", mMonth+1, mDay, mYear));
        deadlineTxtTime.setText(String.format("%d:%d", mHour, mMinute));
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
            case R.id.test_place:
                mapClick();
                //Intent i = new Intent(DetailList.this, MapsActivity.class);// jihoon: temp change
                //startActivity(i);
                break;
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
            startTxtTime.setText(String.format("%d:%d", startHour, startMinute));
    }

    void deadlineUpdateNow(){
            deadlineTxtDate.setText(String.format("%d/%d/%d", deadlineMonth+1, deadlineDay, deadlineYear));
            deadlineTxtTime.setText(String.format("%d:%d", deadlineHour, deadlineMinute));
    }

   /* void mapClick() {
     //   LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Create a criteria object to retrieve provider
      //  Criteria criteria = new Criteria();

    //    String provider = locationManager.getBestProvider(criteria, true);

     //   Location myLocation = locationManager.getLastKnownLocation(provider);

        // Get latitude of the current location
       // double latitude = myLocation.getLatitude();

        // Get longitude of the current location
       // double longitude = myLocation.getLongitude();

        Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345");

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // BEGIN_INCLUDE(activity_result)
        if (requestCode == REQUEST_PLACE_PICKER) {
            // This result is from the PlacePicker dialog.

            // Enable the picker option
            //showPickAction(true);

            if (resultCode == Activity.RESULT_OK) {
                /* User has picked a place, extract data.
                   Data is extracted from the returned intent by retrieving a Place object from
                   the PlacePicker.
                 */
                final Place place = PlacePicker.getPlace(data,this);
                /* A Place object contains details about that place, such as its name, address
                and phone number. Extract the name, address, phone number, place ID and place types.
                 */
                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
                final CharSequence phone = place.getPhoneNumber();
                final String placeId = place.getId();
                String attribution = PlacePicker.getAttributions(data);
                if(attribution == null){
                    attribution = "";
                }
/*
                // Update data on card.
                getCardStream().getCard(CARD_DETAIL)
                        .setTitle(name.toString())
                        .setDescription(getString(R.string.detail_text, placeId, address, phone,
                                attribution));

                // Print data to debug log
                Log.d(TAG, "Place selected: " + placeId + " (" + name.toString() + ")");

                // Show the card.
                getCardStream().showCard(CARD_DETAIL);

            } else {
                // User has not selected a place, hide the card.
                getCardStream().hideCard(CARD_DETAIL);*/
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        // END_INCLUDE(activity_result)
    }

    void mapClick() {
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            // Start the Intent by requesting a result, identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER);

            // Hide the pick option in the UI to prevent users from starting the picker
            // multiple times.
            // showPickAction(false);

        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil
                    .getErrorDialog(e.getConnectionStatusCode(), this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
}
