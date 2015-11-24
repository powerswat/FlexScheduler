package cse.osu.edu.flexscheduler;

import java.util.Date;
import java.util.UUID;

/**
 * Data type for an individual event item on RecyclerView
 */
class SingleEventForList {
    String mAccountID;              // User id
    String mEventID;                // Event id
    String mEventTitle;             // Event title
    String mEventStartDate;         // Event start date
    String mEventStartTime;         // Event start time
    String mEventDeadlineDate;      // Event deadline date
    String mEventDeadlineTime;      // Event deadline time

    // Constructor: All the necessary information will be provided at a time
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
