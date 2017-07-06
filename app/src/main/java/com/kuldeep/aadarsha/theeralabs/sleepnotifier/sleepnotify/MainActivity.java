package com.kuldeep.aadarsha.theeralabs.sleepnotifier.sleepnotify;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private int hour;
    private int min;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get time
        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(android.text.format.DateFormat.is24HourFormat(getApplicationContext()));
        if (Build.VERSION.SDK_INT >= 23) {
            hour = timePicker.getHour();
            min = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            min = timePicker.getCurrentMinute();
        }
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                hour = i;
                min = i1;
            }
        });

        final Button button = (Button) findViewById(R.id.button);
        //Check if service is running
        if (isMyServiceRunning(SleepService.class)) {
            button.setText(R.string.stop);
        } else
            button.setText(R.string.begin);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //if service not running
                    if (!isMyServiceRunning(SleepService.class)) {
                        button.setText(R.string.stop);

                        writeToSharedPreferences(hour, min);

                        SharedPreferences isCancelled = getSharedPreferences("cancelledFile", 0);
                        SharedPreferences.Editor editor = isCancelled.edit();
                        editor.putBoolean("cancelled", false);
                        editor.commit();

                        serviceIntent = new Intent(MainActivity.this, SleepService.class);
                        startService(serviceIntent);
                        Toast.makeText(getApplicationContext(), "Great! That's it", Toast.LENGTH_LONG).show();
                        finish();

                    } else {
                        button.setText(R.string.begin);
                        stopService(new Intent(MainActivity.this, SleepService.class));

                        //Save that we have stopped
                        SharedPreferences isCancelled = getSharedPreferences("cancelledFile", 0);
                        SharedPreferences.Editor editor = isCancelled.edit();
                        editor.putBoolean("cancelled", true);
                        editor.commit();
                    }
                } catch (java.lang.NullPointerException e) {
                    Log.e("SleepNotify: MainActy: ", e.toString());
                }
            }
        });

        Button rateButton = (Button) findViewById(R.id.rateRewviewButton);
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlayStorePage();
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void writeToSharedPreferences(int hour, int min) {
        SharedPreferences hourSettings = getSharedPreferences("HourPrefFile", 0);
        SharedPreferences.Editor editor = hourSettings.edit();
        editor.putInt("hour", hour);
        editor.commit();

        SharedPreferences  minSettings = getSharedPreferences("MinPrefFile", 0);
        SharedPreferences.Editor editor1 = minSettings.edit();
        editor1.putInt("min", min);
        editor1.commit();
    }

    private void openPlayStorePage() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }
}
