package cse.osu.edu.flexscheduler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import cse.osu.edu.flexscheduler.database.SchedBaseHelper;
import cse.osu.edu.flexscheduler.database.SchedCursorWrapper;
import cse.osu.edu.flexscheduler.database.SchedDbSchema.SchedTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SchedLab {
    private static SchedLab sSchedLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static SchedLab get(Context context) {
        if (sSchedLab == null) {
            sSchedLab = new SchedLab(context);
        }
        return sSchedLab;
    }

    private SchedLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new SchedBaseHelper(mContext)
                .getWritableDatabase();
    }


    public void addSched(Sched c) {
        ContentValues values = getContentValues(c);

        mDatabase.insert(SchedTable.NAME, null, values);
    }

    public List<Sched> getScheds() {
        List<Sched> Scheds = new ArrayList<>();

        SchedCursorWrapper cursor = queryScheds(null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Scheds.add(cursor.getSched());
            cursor.moveToNext();
        }
        cursor.close();

        return Scheds;
    }

    public Sched getSched(UUID id) {
        SchedCursorWrapper cursor = queryScheds(
                SchedTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getSched();
        } finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Sched Sched) {
        File externalFilesDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, Sched.getPhotoFilename());
    }

    public void updateSched(Sched Sched) {
        String uuidString = Sched.getId().toString();
        ContentValues values = getContentValues(Sched);

        mDatabase.update(SchedTable.NAME, values,
                SchedTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private static ContentValues getContentValues(Sched Sched) {
        ContentValues values = new ContentValues();
        values.put(SchedTable.Cols.UUID, Sched.getId().toString());
        values.put(SchedTable.Cols.TITLE, Sched.getTitle());
        values.put(SchedTable.Cols.DATE, Sched.getDate().getTime());
        values.put(SchedTable.Cols.SOLVED, Sched.isSolved() ? 1 : 0);
        values.put(SchedTable.Cols.SUSPECT, Sched.getSuspect());

        return values;
    }

    private SchedCursorWrapper queryScheds(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                SchedTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new SchedCursorWrapper(cursor);
    }
}
