package cse.osu.edu.flexscheduler;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import java.lang.Exception;
import java.lang.Override;

import cse.osu.edu.flexscheduler.LoginActivity;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    LoginActivity mLoginActivity;
    String[] email = {"aaa@aa.aaa", "bbb@bb.bbb"};
    String[] password = {"11111", "22222"};
    boolean[] answer = {true, true};

    public LoginActivityTest(){
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mLoginActivity = getActivity();
    }

    public void testPreconditions(){
        assertNotNull(mLoginActivity);
    }

    public void testValidateLogin(){
        Context context = mLoginActivity;
        for (int i = 0; i < email.length; i++){
            boolean is_valid = mLoginActivity.validateLogin(email[i], password[i], context);
            assertEquals(answer[i], is_valid);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        mLoginActivity.finish();
    }
}