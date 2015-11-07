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

public class CrimeLab {
    private static CrimeLab sCrimeLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new SchedBaseHelper(mContext)
                .getWritableDatabase();
    }


    public void addCrime(Crime c) {
        ContentValues values = getContentValues(c);

        mDatabase.insert(SchedTable.NAME, null, values);
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();

        SchedCursorWrapper cursor = queryCrimes(null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            crimes.add(cursor.getCrime());
            cursor.moveToNext();
        }
        cursor.close();

        return crimes;
    }

    public Crime getCrime(UUID id) {
        SchedCursorWrapper cursor = queryCrimes(
                SchedTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Crime crime) {
        File externalFilesDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, crime.getPhotoFilename());
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(SchedTable.NAME, values,
                SchedTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(SchedTable.Cols.UUID, crime.getId().toString());
        values.put(SchedTable.Cols.TITLE, crime.getTitle());
        values.put(SchedTable.Cols.DATE, crime.getDate().getTime());
        values.put(SchedTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(SchedTable.Cols.SUSPECT, crime.getSuspect());

        return values;
    }

    private SchedCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
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
