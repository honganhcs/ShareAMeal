package com.example.shareameal.schedulecleanup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = BootReceiver.class.getSimpleName();
    private static final long DEFAULT_INTERVAL = 1000 * 60;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            PendingIntent cleanUp = PendingIntent.getService(context, -1, new Intent(context, CleanUpService.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Log.d(TAG, "On receive");
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, System.currentTimeMillis(), DEFAULT_INTERVAL, cleanUp);
        }
    }
}
