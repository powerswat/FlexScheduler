package cse.osu.edu.flexscheduler.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import cse.osu.edu.flexscheduler.Sched;
import cse.osu.edu.flexscheduler.database.SchedDbSchema.SchedTable;

import java.util.Date;
import java.util.UUID;

public class SchedCursorWrapper extends CursorWrapper {
    public SchedCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Sched getSched() {
        String uuidString = getString(getColumnIndex(SchedTable.Cols.UUID));
        String title = getString(getColumnIndex(SchedTable.Cols.TITLE));
        long date = getLong(getColumnIndex(SchedTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(SchedTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(SchedTable.Cols.SUSPECT));

        Sched Sched = new Sched(UUID.fromString(uuidString));
        Sched.setTitle(title);
        Sched.setDate(new Date(date));
        Sched.setSolved(isSolved != 0);
        Sched.setSuspect(suspect);

        return Sched;
    }
}
