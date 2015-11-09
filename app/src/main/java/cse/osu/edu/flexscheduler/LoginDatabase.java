package cse.osu.edu.flexscheduler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class LoginDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "flexScheduler.db";
    private static final int DATABASE_VERSION = 1;
    public static final String FLEX_SCHEDULER_TABLE_NAME = "login";
    private static final String FLEX_SCHEDULER_TABLE_CREATE =
            "CREATE TABLE " + FLEX_SCHEDULER_TABLE_NAME + "(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "email TEXT NOT NULL, password TEXT NOT NULL);";

    public LoginDatabase(Context context) {

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
    }

    /* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
    @Override
    public void onUpgrade(SQLiteDatabase arg0, int oldVersion, int newVersion) {


    }

}
