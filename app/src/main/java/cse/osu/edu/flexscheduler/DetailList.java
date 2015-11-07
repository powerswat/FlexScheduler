package cse.osu.edu.flexscheduler;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

public class DetailList extends AppCompatActivity {

    int startYear, startMonth, startDay, startHour, startMinute;
    int deadlineYear, deadlineMonth, deadlineDay, deadlineHour,  deadlineMinute;
    int mYear, mMonth, mDay, mHour, mMinute;

    TextView startTxtDate;
    TextView startTxtTime;
    TextView deadlineTxtDate;
    TextView deadlineTxtTime;

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



    void durationHoursNow() {
        final CharSequence[] items = {"0", "1", "2", "3", "4", "5", "6", "7"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);     // 여기서 this는 Activity의 this

        // 여기서 부터는 알림창의 속성 설정
        builder.setTitle("Hours")        // 제목 설정
                .setItems(items, new DialogInterface.OnClickListener() {    // 목록 클릭시 설정
                    public void onClick(DialogInterface dialog, int index) {
                        Toast.makeText(getApplicationContext(), items[index], Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기
    }

    void durationMinutesNow() {
        final CharSequence[] items = {"0", "1", "2", "3", "4", "5", "6", "7"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);     // 여기서 this는 Activity의 this

        // 여기서 부터는 알림창의 속성 설정
        builder.setTitle("Minutes")        // 제목 설정
                .setItems(items, new DialogInterface.OnClickListener() {    // 목록 클릭시 설정
                    public void onClick(DialogInterface dialog, int index) {
                        Toast.makeText(getApplicationContext(), items[index], Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기
    }

    void startUpdateNow(){
            startTxtDate.setText(String.format("%d/%d/%d", startMonth+1, startDay, startYear));
            startTxtTime.setText(String.format("%d:%d", startHour, startMinute));
    }

    void deadlineUpdateNow(){
            deadlineTxtDate.setText(String.format("%d/%d/%d", deadlineMonth+1, deadlineDay, deadlineYear));
            deadlineTxtTime.setText(String.format("%d:%d", deadlineHour, deadlineMinute));
    }

}
