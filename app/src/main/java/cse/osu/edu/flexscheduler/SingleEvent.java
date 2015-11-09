package cse.osu.edu.flexscheduler;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Jihoon Yun on 11/8/2015.
 */
 class SingleEvent {
    long mAccountID;
    long mEventID;
    String mEventTitle;
    String mEventStartDate;
    String mEventStartTime;
    String mEventDuration;
    String mEventDeadlineDate;
    String mEventDeadlineTime;
    String mEventPlace;
    String mEventPlaceLatitude;
    String mEventPlaceLongitude;
    String mEventParticipants;
    String mEventNote;

    SingleEvent(long accountID, long eventID, String eventTitle, String eventStartDate, String eventStartTime,
            String eventDuration, String eventDeadlineDate, String eventDeadlineTime, String eventPlace, String eventPlaceLatitude,
            String eventPlaceLongitude, String eventParticipants, String eventNote) {
        mAccountID = accountID;
        mEventID = eventID;
        mEventTitle = eventTitle;
        mEventStartDate = eventStartDate;
        mEventStartTime = eventStartTime;
        mEventDuration = eventDuration;
        mEventDeadlineDate = eventDeadlineDate;
        mEventDeadlineTime = eventDeadlineTime;
        mEventPlace = eventPlace;
        mEventPlaceLatitude = eventPlaceLatitude;
        mEventPlaceLongitude = eventPlaceLongitude;
        mEventParticipants = eventParticipants;
        mEventNote = eventNote;
    }

}
