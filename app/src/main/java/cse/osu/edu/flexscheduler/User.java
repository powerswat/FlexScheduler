package cse.osu.edu.flexscheduler;

import java.util.Date;
import java.util.UUID;

public class User {

    private UUID mPid;
    private String mEmail;
    private String mPassword;
    private String mCurrLoc;

    public User() {
        this(UUID.randomUUID());
    }

    public User(UUID id) {
        mPid = id;
    }

    public UUID getId() {
        return mPid;
    }

    public void setId(UUID id) {
        mPid = id;
    }

    public UUID getPid() {
        return mPid;
    }

    public void setPid(UUID pid) {
        mPid = pid;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getCurrLoc() {
        return mCurrLoc;
    }

    public void setCurrLoc(String currLoc) {
        mCurrLoc = currLoc;
    }
}
