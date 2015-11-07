package cse.osu.edu.flexscheduler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import cse.osu.edu.flexscheduler.database.SchedCursorWrapper;
import cse.osu.edu.flexscheduler.database.SchedDbSchema.SchedTable;
import cse.osu.edu.flexscheduler.database.SchedBaseHelper;

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


    public void addSched(Sched s) {
        ContentValues values = getContentValues(s);

        mDatabase.insert(SchedTable.NAME, null, values);
    }

    public List<Sched> getScheds() {
        List<Sched> scheds = new ArrayList<>();

        SchedCursorWrapper cursor = queryScheds(null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            scheds.add(cursor.getSched());
            cursor.moveToNext();
        }
        cursor.close();

        return scheds;
    }

    public Sched getSched(UUID id) {
        SchedCursorWrapper cursor = queryScheds(
                SchedTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
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

    public File getPhotoFile(Sched sched) {
        File externalFilesDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, sched.getPhotoFilename());
    }

    public void updateSched(Sched sched) {
        String uuidString = sched.getId().toString();
        ContentValues values = getContentValues(sched);

        mDatabase.update(SchedTable.NAME, values,
                SchedTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private static ContentValues getContentValues(Sched sched) {
        ContentValues values = new ContentValues();
        values.put(SchedTable.Cols.UUID, sched.getId().toString());
        values.put(SchedTable.Cols.TITLE, sched.getTitle());
        values.put(SchedTable.Cols.DATE, sched.getDate().getTime());
        values.put(SchedTable.Cols.SOLVED, sched.isSolved() ? 1 : 0);
        values.put(SchedTable.Cols.SUSPECT, sched.getSuspect());

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
