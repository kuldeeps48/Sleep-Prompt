package com.kuldeep.aadarsha.theeralabs.sleepnotifier.sleepnotify;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

public class SleepService extends Service {

    private int hour;
    private int min;
    private PendingIntent promptIntent;
    private AlarmManager alarmManager;

    public SleepService() {
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        readFromSharedPreferences(getApplicationContext());
        //Just to format time text
        String toShowHour = "";
        String toShowMin = "";
        String suffix = "";
        if (android.text.format.DateFormat.is24HourFormat(this)) {
            if (hour < 10)
                toShowHour = "0" + hour;
            else
                toShowHour = "" + hour;
            if (min < 10)
                toShowMin = "0" + min;
            else
                toShowMin = "" + min;
            suffix = "Hours";
        } else {
            //12 hour format
            if (hour > 12) {
                if ((hour - 12) < 10)
                    toShowHour = "0" + (hour - 12);
                else
                    toShowHour = "" + (hour - 12);
            }
            if (hour <= 12 && hour >= 10)
                toShowHour = "" + hour;
            if (hour < 10)
                toShowHour = "0" + hour;

            if (min < 10)
                toShowMin = "0" + min;
            else
                toShowMin = "" + min;

            if (hour >= 12 && min >= 0)
                suffix = "PM";
            else
                suffix = "AM";
        }
        //Create Notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(SleepService.this);
        mBuilder.setContentTitle("Sleep Prompt set for " + toShowHour + ":" + toShowMin + " " + suffix);
        mBuilder.setSmallIcon(R.drawable.tired);
        mBuilder.setOngoing(true);
        mBuilder.setVisibility(Notification.VISIBILITY_SECRET);
        mBuilder.setShowWhen(false);
        //On Clicking Notification Visit result page
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        mBuilder.setContentIntent(pi);
        //Create notification and show
        Notification notification = mBuilder.build();
        startForeground(7, notification);

        //Intent to open Prompt
        Intent intent1 = new Intent(getApplicationContext(), SleepTriggerReceiver.class);
        promptIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent1, 0);

        //Set Alarm time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        //To handle 00:00 time, since alarm manager considers it to be in past
        Calendar now = Calendar.getInstance();
        if (calendar.before(now)) {
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), promptIntent);

        return START_STICKY;
    }

    private void readFromSharedPreferences(Context context) {
        SharedPreferences hourSettings = context.getSharedPreferences("HourPrefFile", 0);
        hour = hourSettings.getInt("hour", 0);

        SharedPreferences  minSettings = context.getSharedPreferences("MinPrefFile", 0);
        min = minSettings.getInt("min", 0);
    }


    @Override
    public void onDestroy() {
        alarmManager.cancel(promptIntent);
        stopForeground(true);
        super.onDestroy();
    }

}