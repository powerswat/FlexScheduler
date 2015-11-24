package cse.osu.edu.flexscheduler;

import android.test.ActivityInstrumentationTestCase2;

import java.lang.Exception;
import java.lang.Override;

import cse.osu.edu.flexscheduler.LoginActivity;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    LoginActivity mLoginActivity;
    String email = "aaa@aa.aaa";
    boolean is_valid;

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

    @Override
    protected void tearDown() throws Exception {
        mLoginActivity.finish();
    }
}