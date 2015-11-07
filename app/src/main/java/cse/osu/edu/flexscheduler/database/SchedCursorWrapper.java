package cse.osu.edu.flexscheduler.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import cse.osu.edu.flexscheduler.Crime;
import cse.osu.edu.flexscheduler.database.SchedDbSchema.SchedTable;

import java.util.Date;
import java.util.UUID;

public class SchedCursorWrapper extends CursorWrapper {
    public SchedCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(SchedTable.Cols.UUID));
        String title = getString(getColumnIndex(SchedTable.Cols.TITLE));
        long date = getLong(getColumnIndex(SchedTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(SchedTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(SchedTable.Cols.SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);

        return crime;
    }
}
