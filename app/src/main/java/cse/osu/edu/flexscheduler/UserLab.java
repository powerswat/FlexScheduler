package cse.osu.edu.flexscheduler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import cse.osu.edu.flexscheduler.database.SchedCursorWrapper;
import cse.osu.edu.flexscheduler.database.SchedDbSchema.UserTable;
import cse.osu.edu.flexscheduler.database.SchedBaseHelper;
import cse.osu.edu.flexscheduler.database.UserCursorWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserLab {
    private static UserLab sUserLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static UserLab get(Context context) {
        if (sUserLab == null) {
            sUserLab = new UserLab(context);
        }
        return sUserLab;
    }

    private UserLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new SchedBaseHelper(mContext)
                .getWritableDatabase();
    }


    public void addUser(User u) {
        ContentValues values = getContentValues(u);

        mDatabase.insert(UserTable.NAME, null, values);
    }

    public List<User> getUser() {
        List<User> users = new ArrayList<>();

        UserCursorWrapper cursor = queryUsers(null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            users.add(cursor.getUser());
            cursor.moveToNext();
        }
        cursor.close();

        return users;
    }

    public User getUser(UUID id) {
        UserCursorWrapper cursor = queryUsers(
                UserTable.UserCols.EID + " = ?",
                new String[]{id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getUser();
        } finally {
            cursor.close();
        }
    }

    public void updateUser(User user) {
        String uuidString = user.getId().toString();
        ContentValues values = getContentValues(user);

        mDatabase.update(UserTable.NAME, values,
                UserTable.UserCols.EID + " = ?",
                new String[] { uuidString });
    }

    private static ContentValues getContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(UserTable.UserCols.EID, user.getId().toString());
        values.put(UserTable.UserCols.PID, user.getPid().toString());
        values.put(UserTable.UserCols.EMAIL, user.getEmail());
        values.put(UserTable.UserCols.PASSWORD, user.getPassword());
        values.put(UserTable.UserCols.CURR_LOC, user.getCurrLoc());

        return values;
    }

    private UserCursorWrapper queryUsers(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                UserTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new UserCursorWrapper(cursor);
    }
}
