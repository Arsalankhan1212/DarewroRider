package com.darewro.rider.data.api.handlers;

import android.app.Activity;
import android.content.Context;

import com.darewro.rider.presenter.BaseResponseListener;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class ApiBaseResponseHandler {

    Context activity;
    String calledApi;
    BaseResponseListener baseResponseListener;

    public ApiBaseResponseHandler(Activity activity, String calledApi, BaseResponseListener baseResponseListener) {
        this.activity = activity;
        this.calledApi = calledApi;
        this.baseResponseListener = baseResponseListener;
    }

    public ApiBaseResponseHandler(Context activity, String calledApi, BaseResponseListener baseResponseListener) {
        this.activity = activity;
        this.calledApi = calledApi;
        this.baseResponseListener = baseResponseListener;
    }

    public ApiBaseResponseHandler(Context activity, String calledApi) {
        this.activity = activity;
        this.calledApi = calledApi;
    }

    public abstract void setApiResponse(JSONObject jsonObject, String calledApi);

    public abstract void setApiResponse(JSONArray jsonArray, String calledApi);

    public abstract void setApiResponse(String jsonAsString, String calledApi);

    public abstract void setApiError(String calledApi);

    public abstract void setApiError(String errorMessage, String calledApi);
    public abstract void setApiError(String errorMessage, String calledApi, int errorCode);

}
