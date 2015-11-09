package cse.osu.edu.flexscheduler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jihoon Yun on 11/8/2015.
 */
public class EventDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "flextest2.db";
    private static final int DATABASE_VERSION = 1;
    public static final String FLEX_SCHEDULER_TABLE_NAME = "flextest2";
    private static final String FLEX_SCHEDULER_TABLE_CREATE =
            "CREATE TABLE " + FLEX_SCHEDULER_TABLE_NAME + "(" +
                    "event_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "account_id TEXT NOT NULL," +
                    "title TEXT NOT NULL, start_date TEXT NOT NULL," +
                    "start_time TEXT NOT NULL, duration," +
                    "deadline_date TEXT NOT NULL, deadline_time TEXT NOT NULL," +
                    "place, place_latitude, place_longitude, participants, note" +
                    ");";
    public EventDatabase(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        System.out.println("In constructor");

    }

    /* (non-Javadoc)
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        try{
            //Create Database
            db.execSQL(FLEX_SCHEDULER_TABLE_CREATE);

        }catch(Exception e){
            e.printStackTrace();
        }

        db.close();
    }

    /* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
    @Override
    public void onUpgrade(SQLiteDatabase arg0, int oldVersion, int newVersion) {


    }

}
