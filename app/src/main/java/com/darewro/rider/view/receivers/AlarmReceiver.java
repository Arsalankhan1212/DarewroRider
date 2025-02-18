package com.darewro.rider.view.receivers;

import android.content.Context;
import android.content.Intent;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.android.volley.Request;
import com.darewro.rider.R;
import com.darewro.rider.data.api.ApiCalls;
import com.darewro.rider.data.api.Interface.InitApi;
import com.darewro.rider.data.api.handlers.StatusHandler;
import com.darewro.rider.data.api.requests.JsonObjectRequestCall;
import com.darewro.rider.presenter.ResponseListenerStatus;
import com.darewro.rider.view.activities.MainActivity;
import com.darewro.rider.view.listeners.AlertDialogResponseListener;
import com.darewro.rider.view.services.WakefulService;
import com.darewro.rider.view.utils.AlertDialogUtils;
import com.darewro.rider.view.utils.AppUtils;
import com.darewro.rider.view.utils.SharedPreferenceHelper;

import java.util.HashMap;

public class AlarmReceiver extends WakefulBroadcastReceiver/*BroadcastReceiver*/ implements ResponseListenerStatus {

    Context context = null;
    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        Intent service = new Intent(context, WakefulService.class);

        startWakefulService(context, service);



    }

//    private void changeStatus(final boolean status, final Context context) {
//
//        InitApi initApi = new InitApi() {
//            @Override
//            public HashMap<String, String> getBody() {
//
//                HashMap<String, String> params = new HashMap<String, String>();
//                params.put("riderId", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID,context)/*"6"*/);
//                params.put("userId", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID,context)/*"6"*/);
//                params.put("isAvailable", String.valueOf(status));
//                return params;
//            }
//
//            @Override
//            public HashMap<String, Object> getObjBody() {
//                return null;
//            }
//
//            @Override
//            public HashMap<String, String> getHeader() {
//                return AppUtils.getStandardHeaders(context);
//            }
//        };
//
//        StatusHandler partnersHandler = new StatusHandler(context, ApiCalls.changeStatus(), this);
//        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), ApiCalls.changeStatus(), Request.Method.POST, context, partnersHandler);
//        jsonObjectRequestCall.sendData();
//
//    }


    @Override
    public void onSuccess(String calledApi, String response) {


    }

    @Override
    public void onError(String calledApi, String errorMessage) {

    }

    @Override
    public void onError(String calledApi, String errorMessage, int errorCode) {
        if(errorCode == 403)
            AlertDialogUtils.showAlertDialog(context, AlertDialogUtils.ALERT_DIALOG_WARNING, context.getString(R.string.alert), errorMessage, context.getString(R.string.ok), null, true);
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

}