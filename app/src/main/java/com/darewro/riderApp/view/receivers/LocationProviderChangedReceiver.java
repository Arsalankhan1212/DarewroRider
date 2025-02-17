package com.darewro.riderApp.view.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.darewro.riderApp.view.activities.WarningActivity;
import com.darewro.riderApp.view.utils.AppUtils;
import com.darewro.riderApp.view.utils.SharedPreferenceHelper;


public class LocationProviderChangedReceiver extends BroadcastReceiver {

    private final static String TAG = "LocationProviderChanged";

    boolean isGpsEnabled;
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED"))
        {
            Log.i(TAG, "Location Providers changed");

            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (!isGpsEnabled) {
                    if(!SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_GPS_DIALOG_VISIBLE,context)) {
                        SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.IS_GPS_DIALOG_VISIBLE,true,context);
                        Intent intnt = new Intent(context,WarningActivity.class);
                        intnt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intnt);
                    }
                }

            System.out.println("connection : -------Location Provider Changed Reciever");

            AppUtils.pushRiderStatsIfInternetWorking(context);
        }
    }
}
