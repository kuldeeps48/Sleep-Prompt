package com.kuldeep.aadarsha.theeralabs.sleepnotifier.sleepnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class SleepTriggerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AlarmReceiverPartial");
        wl.acquire();

        Intent intent1 = new Intent(context, PromptActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);

        Intent serviceIntent = new Intent(context, SleepService.class);
        context.startService(serviceIntent);

        wl.release();
    }
}
