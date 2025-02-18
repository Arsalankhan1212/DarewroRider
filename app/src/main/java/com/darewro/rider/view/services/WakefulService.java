package com.darewro.rider.view.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.android.volley.Request;
import com.darewro.rider.R;
import com.darewro.rider.data.api.ApiCalls;
import com.darewro.rider.data.api.Interface.InitApi;
import com.darewro.rider.data.api.handlers.StatusHandler;
import com.darewro.rider.data.api.requests.JsonObjectRequestCall;
import com.darewro.rider.presenter.ResponseListenerStatus;
import com.darewro.rider.view.activities.LoginActivity;
import com.darewro.rider.view.activities.MainActivity;
import com.darewro.rider.view.listeners.AlertDialogResponseListener;
import com.darewro.rider.view.locationService.LocationService;
import com.darewro.rider.view.receivers.AlarmReceiver;
import com.darewro.rider.view.utils.AlertDialogUtils;
import com.darewro.rider.view.utils.AppUtils;
import com.darewro.rider.view.utils.SharedPreferenceHelper;

import java.util.HashMap;

public class WakefulService extends IntentService implements ResponseListenerStatus, AlertDialogResponseListener {

    Context context;
    private long alarmPeriodicTime = 60 * 1000;

    public WakefulService() {
        super("WakefulService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //TODO:Service code
        this.context = getApplicationContext();
        if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, context)) {
            //changeStatus(false, context);
//            AppUtils.pushRiderStats(context,true, true);
            String token = SharedPreferenceHelper.getString(SharedPreferenceHelper.FIRE_BASE_TOKEN, context);
            SharedPreferenceHelper.clearPrefrences(context);
            SharedPreferenceHelper.saveString(SharedPreferenceHelper.FIRE_BASE_TOKEN, token, context);
            SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.FORCE_STOP, true, context);
            Intent intnt = new Intent(context, LocationService.class);
            if (LocationService.isRunning(context)) {
                context.stopService(intnt);
            }
            Intent intent1 = new Intent(context, LoginActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent1);
        } else {
            String token = SharedPreferenceHelper.getString(SharedPreferenceHelper.FIRE_BASE_TOKEN, context);
            SharedPreferenceHelper.clearPrefrences(context);
            SharedPreferenceHelper.saveString(SharedPreferenceHelper.FIRE_BASE_TOKEN, token, context);
            Intent intnt = new Intent(context, LoginActivity.class);
            intnt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intnt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intnt);

        }

        AlarmReceiver.completeWakefulIntent(intent);

    }

    private void changeStatus(final boolean status, final Context context) {

        InitApi initApi = new InitApi() {
            @Override
            public HashMap<String, String> getBody() {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("riderId", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, context)/*"6"*/);
                params.put("userId", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, context)/*"6"*/);
                params.put("isAvailable", String.valueOf(status));
                return params;
            }

            @Override
            public HashMap<String, Object> getObjBody() {
                return null;
            }

            @Override
            public HashMap<String, String> getHeader() {
                return AppUtils.getStandardHeaders(context);
            }
        };

        StatusHandler partnersHandler = new StatusHandler(context, ApiCalls.changeStatus(), this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), ApiCalls.changeStatus(), Request.Method.POST, context, partnersHandler);
        jsonObjectRequestCall.sendData();

    }

    @Override
    public void onSuccess(String calledApi, String response) {

    }

    @Override
    public void onError(String calledApi, String errorMessage) {

    }

    @Override
    public void onError(String calledApi, String errorMessage, int errorCode) {
        if(errorCode == 403)
            AlertDialogUtils.showAlertDialog(getApplicationContext(), AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), errorMessage, getString(R.string.ok), this, true);
    }

    @Override
    public void onSuccess(String calledApi) {

    }

    @Override
    public void onError(String calledApi) {

    }

    @Override
    public void onError(String calledApi, int errorCode) {

    }

    @Override
    public void OnSuccess() {

    }

    @Override
    public void OnCancel() {

    }

    @Override
    public void OnSuccess(Object object) {

    }

    @Override
    public void OnCancel(Object object) {

    }

    @Override
    public void OnSuccess(int alertId) {

    }

    @Override
    public void OnCancel(int alertId) {

    }

    @Override
    public void OnSuccess(int alertId, Object object) {

    }

    @Override
    public void OnCancel(int alertId, Object object) {

    }

    @Override
    public void OnSuccess(int alertId, Object object, Object object2) {

    }

    @Override
    public void OnCancel(int alertId, Object object, Object object2) {

    }
}
