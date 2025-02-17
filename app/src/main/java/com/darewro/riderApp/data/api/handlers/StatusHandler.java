package com.darewro.riderApp.data.api.handlers;

import android.app.Activity;
import android.content.Context;

import com.darewro.riderApp.presenter.BaseResponseListener;
import com.darewro.riderApp.presenter.ResponseListenerStatus;

import org.json.JSONArray;
import org.json.JSONObject;

public class StatusHandler extends ApiBaseResponseHandler {

    ResponseListenerStatus responseListenerStatus;

    public StatusHandler(Activity activity, String calledApi, BaseResponseListener baseResponseListener) {
        super(activity, calledApi, baseResponseListener);
        this.responseListenerStatus = (ResponseListenerStatus) baseResponseListener;

    }

    public StatusHandler(Context context, String calledApi, BaseResponseListener baseResponseListener) {
        super(context, calledApi, baseResponseListener);
    }


    public StatusHandler(Context context, String calledApi) {
        super(context, calledApi);
    }


    @Override
    public void setApiResponse(JSONObject jsonObject, String calledApi) {
        if (responseListenerStatus != null)
            responseListenerStatus.onSuccess(calledApi, jsonObject.optString("message"));

    }

    @Override
    public void setApiResponse(JSONArray jsonArray, String calledApi) {
        if (responseListenerStatus != null)
            responseListenerStatus.onSuccess(calledApi);

    }

    @Override
    public void setApiResponse(String jsonAsString, String calledApi) {
        if (responseListenerStatus != null)
            responseListenerStatus.onSuccess(calledApi);

    }

    @Override
    public void setApiError(String calledApi) {
        if (responseListenerStatus != null)
            responseListenerStatus.onError(calledApi);

    }

    @Override
    public void setApiError(String errorMessage, String calledApi) {
        if (responseListenerStatus != null)
            responseListenerStatus.onError(calledApi, errorMessage);

    }

    @Override
    public void setApiError(String errorMessage, String calledApi, int errorCode) {
        if (responseListenerStatus != null)
            responseListenerStatus.onError(calledApi, errorMessage, errorCode);

    }


}
