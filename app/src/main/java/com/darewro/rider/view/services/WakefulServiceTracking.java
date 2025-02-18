package com.darewro.rider.view.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.darewro.rider.view.locationService.LocationService;
import com.darewro.rider.view.receivers.AlarmReceiverForRiderTrackingService;
import com.darewro.rider.view.utils.AppUtils;
import com.darewro.rider.view.utils.SharedPreferenceHelper;

import java.util.Calendar;


public class WakefulServiceTracking extends IntentService {

    String TAG = "WAKE_FULL_SERVICE  ";
    Intent myIntent = null;

    private long alarmPeriodicTime = 15 * 1000;

    public WakefulServiceTracking() {
        super("WakefulService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //TODO:Service code
        myIntent = intent;

        if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, getApplicationContext())) {
//            if (!LocationService.isRunning(getApplicationContext())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(getApplicationContext(), LocationService.class));
                } else {
                    startService(new Intent(getApplicationContext(), LocationService.class));
                }
//            }
            setAlarm();

            System.out.println("connection : ------Wakeful Service Tracking");


            AppUtils.pushRiderStatsIfInternetWorking(getApplicationContext());

        }
    }

    private void setAlarm() {


        if (Build.VERSION.SDK_INT <= 23) {
            return;
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiverForRiderTrackingService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 12345678, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        //long Current_time = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= 23) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + 15);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d(TAG, "ALARM TRIGGERED");
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmPeriodicTime, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmPeriodicTime, pendingIntent);
        }

    }


}
