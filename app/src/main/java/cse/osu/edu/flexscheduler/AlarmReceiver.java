package cse.osu.edu.flexscheduler;

/**
 * Created by Jihoon Yun on 11/15/2015.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String accountID = intent.getStringExtra("accountID");
        String eventID = intent.getStringExtra("eventID");
        String title = intent.getStringExtra("title");
        String durationHour = intent.getStringExtra("duration_hour");
        String durationMinute = intent.getStringExtra("duration_minute");
        String deadlineDate = intent.getStringExtra("deadline_date");
        String deadlineTime = intent.getStringExtra("deadline_time");

        Intent i = new Intent(context, LoginActivity.class);
    /*    i.putExtra("accountID", accountID);
        i.putExtra("eventID", eventID);
        i.putExtra("detailListMode",String.valueOf(2));*/
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(context);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("FlexScheduler")
                .setContentTitle(title)
                .setContentText("Duration : " + durationHour + ":" + durationMinute + " Deadline : " + deadlineDate + " " + deadlineTime)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());
    }
}
