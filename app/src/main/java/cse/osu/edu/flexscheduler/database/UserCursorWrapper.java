package cse.osu.edu.flexscheduler.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import cse.osu.edu.flexscheduler.User;
import cse.osu.edu.flexscheduler.database.SchedDbSchema.UserTable;

public class UserCursorWrapper extends CursorWrapper {
    public UserCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public User getUser() {
        String pid = getString(getColumnIndex(UserTable.UserCols.PID));
        String email = getString(getColumnIndex(UserTable.UserCols.EMAIL));
        String password = getString(getColumnIndex(UserTable.UserCols.PASSWORD));
        String curr_loc = getString(getColumnIndex(UserTable.UserCols.CURR_LOC));

        User user = new User(UUID.fromString(pid));
        user.setEmail(email);
        user.setPassword(password);
        user.setCurrLoc(curr_loc);

        return user;
    }
}
