package com.darewro.riderApp.view.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import com.darewro.riderApp.view.locationService.LocationService;
import com.darewro.riderApp.view.utils.SharedPreferenceHelper;

public class RestartServiceReceiver extends BroadcastReceiver {
    SharedPreferences sP;
    @Override
    public void onReceive(Context context, Intent intent) {

        if(SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE,context)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context.getApplicationContext(), LocationService.class));
            } else {
                context.startService(new Intent(context.getApplicationContext(), LocationService.class));
            }
        }
    }

}
