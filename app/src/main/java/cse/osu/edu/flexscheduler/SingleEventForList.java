package cse.osu.edu.flexscheduler;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Jihoon Yun on 11/8/2015.
 */
class SingleEventForList {
    String mEventTitle;
    String mEventStartDate;
    String mEventStartTime;
    String mEventDeadlineDate;
    String mEventDeadlineTime;

    SingleEventForList(String eventTitle, String eventStartDate, String eventStartTime, String eventDeadlineDate, String eventDeadlineTime) {
        mEventTitle = eventTitle;
        mEventStartDate = eventStartDate;
        mEventStartTime = eventStartTime;
        mEventDeadlineDate = eventDeadlineDate;
        mEventDeadlineTime = eventDeadlineTime;
    }

}
