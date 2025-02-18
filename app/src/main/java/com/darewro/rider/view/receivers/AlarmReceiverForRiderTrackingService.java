package com.darewro.rider.view.receivers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import androidx.legacy.content.WakefulBroadcastReceiver;

import com.darewro.rider.view.services.WakefulServiceTracking;
import com.darewro.rider.view.utils.AppUtils;
import com.darewro.rider.view.utils.TimeUtil;

import java.util.Calendar;

public class AlarmReceiverForRiderTrackingService extends WakefulBroadcastReceiver {

    private static final String TAG = AlarmReceiverForRiderTrackingService.class.getName();
    Context context = null;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        String currentTime = TimeUtil.toString(Calendar.getInstance().getTime(),
                TimeUtil.DATE_TIME_FORMAT_13);

        //AppUtils.showNotification(context,currentTime);

        Intent service = new Intent(context, WakefulServiceTracking.class);
        startWakefulService(context, service);

    }


}