/**
 * 
 */
package cse.osu.edu.flexscheduler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author saket
 * Adopted from: https://github.com/SaketSrivastav/Simple-Login-Page
 */
public class DbHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "saket.db";

	// Version이 변경되면 DB를 다시 update 함. 이 신호를 알려주는 용도로 사용
	private static final int DATABASE_VERSION = 1;
    public static final String SAKET_TABLE_NAME = "login";
	private static final String SAKET_TABLE_CREATE =
	                "CREATE TABLE " + SAKET_TABLE_NAME + "(" +
	                "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
	                "username TEXT NOT NULL, password TEXT NOT NULL, email TEXT NOT NULL);";
	private static final String SAKET_DB_ADMIN = "INSERT INTO "+SAKET_TABLE_NAME+"values(1, admin, password, admin@gmail.com);";
	

	public DbHelper(Context context) {
		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		System.out.println("In constructor");
		
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		try{
			// USER DB를 만드는 부분
			db.execSQL(SAKET_TABLE_CREATE);
			
			//create admin account
			db.execSQL(SAKET_DB_ADMIN);
			System.out.println("In onCreate");
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