package cse.osu.edu.flexscheduler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

/**
 * An alarm receiver that offers a notification of event
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // strings to display the information of event on notification area
        String title = intent.getStringExtra("title");
        String durationHour = intent.getStringExtra("duration_hour");
        String durationMinute = intent.getStringExtra("duration_minute");
        String deadlineDate = intent.getStringExtra("deadline_date");
        String deadlineTime = intent.getStringExtra("deadline_time");

        // intent when notification is clicked
        Intent i = new Intent(context, LoginActivity.class);
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
