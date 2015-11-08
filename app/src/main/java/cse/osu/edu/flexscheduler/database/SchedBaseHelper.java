package cse.osu.edu.flexscheduler.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cse.osu.edu.flexscheduler.database.SchedDbSchema.SchedTable;
import cse.osu.edu.flexscheduler.database.SchedDbSchema.UserTable;

public class SchedBaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "SchedBaseHelper";
    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "schedBase5.db";

    public SchedBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + SchedTable.NAME + "(" +
                        " _id integer primary key autoincrement, " +
                        SchedTable.Cols.UUID + ", " +
                        SchedTable.Cols.TITLE + ", " +
                        SchedTable.Cols.DATE + ", " +
                        SchedTable.Cols.SOLVED + ", " +
                        SchedTable.Cols.SUSPECT +
                        ")"
        );

        db.execSQL("create table " + UserTable.NAME + "(" +
                        " _id integer primary key autoincrement, " +
                        UserTable.UserCols.PID + ", " +
                        UserTable.UserCols.EMAIL + ", " +
                        UserTable.UserCols.PASSWORD + ", " +
                        UserTable.UserCols.CURR_LOC +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
