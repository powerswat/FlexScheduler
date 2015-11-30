package cse.osu.edu.flexscheduler;

import android.test.ActivityInstrumentationTestCase2;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Young Suk Cho on 11/24/2015.
 */
public class DetailListTest extends ActivityInstrumentationTestCase2<DetailList>{

    private DetailList mDetailList;

    public DetailListTest() {
        super(DetailList.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mDetailList = getActivity();
    }

    public void testPrecondition(){
        assertNotNull(mDetailList);
    }

    public void testFindBestSpot() {
        Calendar best_st_time = new Calendar() {
            @Override
            public void add(int field, int value) {}
            @Override
            protected void computeFields() {}
            @Override
            protected void computeTime() {}
            @Override
            public int getGreatestMinimum(int field) {return 0;}
            @Override
            public int getLeastMaximum(int field) {return 0;}
            @Override
            public int getMaximum(int field) {return 0;}
            @Override
            public int getMinimum(int field) {return 0;}
            @Override
            public void roll(int field, boolean increment) {}
        };

        ArrayList<String> db_st_times = new ArrayList<String>();
        String[] st_times_str = {"2015-11-23T20:43", "2015-11-24T13:22", "2015-11-24T14:23", "2015-11-24T18:23",
                                "2015-11-25T00:56", "2015-11-26T20:56", "2015-11-27T17:52"};
        for (int i = 0; i < st_times_str.length; i++) {
            db_st_times.add(st_times_str[i]);
        }

        ArrayList<String> db_durations = new ArrayList<String>();
        String[] dura_str = {"1 1", "2 0", "3 0", "2 0", "1 0", "3 0", "1 1"};
        for (int i = 0; i < st_times_str.length; i++) {
            db_durations.add(dura_str[i]);
        }

        ArrayList<String> db_dl_times = new ArrayList<String>();
        String[] dl_times_str = {"2015-11-30T22:41", "2015-11-24T18:25", "2015-11-25T19:23", "2015-11-30T17:20",
                "2015-11-27T12:44", "2015-11-27T10:56", "2015-11-30T18:51"};
        for (int i = 0; i < dl_times_str.length; i++) {
            db_dl_times.add(dl_times_str[i]);
        }

        int start_month = 10, start_day = 23, start_hour = 20, start_year = 2015, start_minute = 43;
        int duration_hour = 1, duration_minute = 1;
        int deadline_month = 11, deadline_day = 30, deadline_year = 2015, deadline_hour = 22, deadline_minute = 41;

        best_st_time = mDetailList.findBestSpot(db_st_times, db_durations, db_dl_times, start_month + 1, start_day,
                start_hour, start_year, start_minute, duration_hour, duration_minute,
                deadline_month+1, deadline_day, deadline_year, deadline_hour, deadline_minute);

        assertNotNull(best_st_time);
    }

    @Override
    protected void tearDown() throws Exception {
        mDetailList.finish();
    }
}
