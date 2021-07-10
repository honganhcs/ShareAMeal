package com.example.shareameal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    private ReceiverForScheduleCleanUp receiver = new ReceiverForScheduleCleanUp();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            receiver.setAlarm(context);
        }
    }
}
