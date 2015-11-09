package cse.osu.edu.flexscheduler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Adopted from: https://github.com/SaketSrivastav/Simple-Login-Page
 */

public class DatabaseActivity extends Activity implements OnClickListener {
    
	Button mLogin;
	Button mNewUser;
	Button mShowAll;
	EditText mUsername;
	EditText mPassword;
	DbHelper mydb = null;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mNewUser = (Button)findViewById(R.id.buttonNewUser);
        mNewUser.setOnClickListener(this);
        
        mLogin = (Button)findViewById(R.id.buttonLogin);
        mLogin.setOnClickListener(this);
        
        mShowAll = (Button)findViewById(R.id.buttonShowAll);
        mShowAll.setOnClickListener(this);
    }
    
	public void onClick(View v) {
	
		switch(v.getId()){
		
		case R.id.buttonLogin:
			mUsername = (EditText)findViewById(R.id.editUsername);
			mPassword = (EditText)findViewById(R.id.editPassword);
			
			String uname = mUsername.getText().toString();
			String pass = mPassword.getText().toString();
			
			if(uname.equals("") || uname == null){
				Toast.makeText(getApplicationContext(), "Username Empty", Toast.LENGTH_SHORT).show();
			}else if(pass.equals("") || pass == null){
				Toast.makeText(getApplicationContext(), "Password Empty", Toast.LENGTH_SHORT).show();
			}else{
				boolean validLogin = validateLogin(uname, pass, DatabaseActivity.this);
				if(validLogin){
					System.out.println("In Valid");
					Intent i = new Intent(DatabaseActivity.this, SchedListActivity.class);
                    i.putExtra("UID", uname);
					startActivity(i);
					finish();
				}
			}
			break;
			
		case R.id.buttonNewUser:
			Intent i = new Intent(DatabaseActivity.this, NewUserActivity.class);
			startActivity(i);
			finish();
			break;
			
		case R.id.buttonShowAll:
			Intent i_admin = new Intent(DatabaseActivity.this, AdminPage.class);
			startActivity(i_admin);
			finish();
			break;
		}
	}

	public boolean validateLogin(String uname, String pass, Context context) {
		
		mydb = new DbHelper(context);

        // 필요한 DB를 가져오는 부분
		SQLiteDatabase db = mydb.getReadableDatabase();
		//SELECT
		String[] columns = {"_id"};
		
		//WHERE clause
		String selection = "username=? AND password=?";
		
		//WHERE clause arguments
		String[] selectionArgs = {uname,pass};
		
		Cursor cursor = null;
		try{
		//SELECT _id FROM login WHERE username=uname AND password=pass
        // 실제로 DB에 query를 해서 data를 가져오는 부분
		cursor = db.query(DbHelper.SAKET_TABLE_NAME, columns, selection, selectionArgs, null, null, null);
		
		startManagingCursor(cursor);
		}catch(Exception e){
			e.printStackTrace();
		}
		int numberOfRows = cursor.getCount();
		
		if(numberOfRows <= 0){
		
			Toast.makeText(getApplicationContext(), "Login Failed..\nTry Again", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		
		return true;
	}
	
	public void onDestroy(){
		super.onDestroy();
	//	mydb.close();
	}
}