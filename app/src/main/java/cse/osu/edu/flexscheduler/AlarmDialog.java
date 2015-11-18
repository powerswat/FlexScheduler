package cse.osu.edu.flexscheduler;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Jihoon Yun on 11/18/2015.
 */
public class AlarmDialog extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
