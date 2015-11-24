package cse.osu.edu.flexscheduler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DB schema for all the event in the program
 */
public class EventDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "flextest4.db";
    private static final int DATABASE_VERSION = 1;
    public static final String FLEX_SCHEDULER_TABLE_NAME = "test4";

    // Attributes: Event id, User id, Event title, Start date+time, Duration (hr(s)/min(s))
    //            Deadline date+time, Place and its GPS coordinate, Participants info, and
    //            Comment info
    private static final String FLEX_SCHEDULER_TABLE_CREATE =
            "CREATE TABLE " + FLEX_SCHEDULER_TABLE_NAME + "(" +
                    "event_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "account_id TEXT NOT NULL," +
                    "title TEXT NOT NULL, start_date_time TEXT NOT NULL," +
                    "duration TEXT NOT NULL," +
                    "deadline_date_time TEXT NOT NULL, " +
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

        // Create Database: Running once at the new user registration
        try{
            //Create Database
            db.execSQL(FLEX_SCHEDULER_TABLE_CREATE);

        }catch(Exception e){
            e.printStackTrace();
        }

      //  db.close();
    }

    /* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
    @Override
    public void onUpgrade(SQLiteDatabase arg0, int oldVersion, int newVersion) {


    }

}
