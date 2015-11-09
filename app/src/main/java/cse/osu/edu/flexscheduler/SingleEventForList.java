package cse.osu.edu.flexscheduler;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Jihoon Yun on 11/8/2015.
 */
class SingleEventForList {
    String mAccountID;
    String mEventID;
    String mEventTitle;
    String mEventStartDate;
    String mEventStartTime;
    String mEventDeadlineDate;
    String mEventDeadlineTime;

    SingleEventForList(String accountID, String eventID, String eventTitle, String eventStartDate, String eventStartTime, String eventDeadlineDate, String eventDeadlineTime) {
        mAccountID = accountID;
        mEventID = eventID;
        mEventTitle = eventTitle;
        mEventStartDate = eventStartDate;
        mEventStartTime = eventStartTime;
        mEventDeadlineDate = eventDeadlineDate;
        mEventDeadlineTime = eventDeadlineTime;
    }

}
