package com.kuldeep.aadarsha.theeralabs.sleepnotifier.sleepnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;

public class BootCompletedRestartService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences isCancelled = context.getSharedPreferences("cancelledFile", 0);
        boolean cancelled = isCancelled.getBoolean("cancelled", true);

        if (!cancelled) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BootCompleteSleepNotify");
            wl.acquire();

            Intent serviceIntent = new Intent(context, SleepService.class);
            context.startService(serviceIntent);


            wl.release();

        }
    }
}
