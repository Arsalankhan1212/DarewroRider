package com.darewro.riderApp.data.api;


import android.app.Activity;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.darewro.riderApp.data.api.handlers.ApiBaseResponseHandler;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Ahmad Abbas Zaidi on 3/21/2017.
 */

public abstract class BaseResponseHandler {

    protected static RequestQueue requestQueue;
    protected Context activity;
    protected JSONObject responseJson;
    protected String responseString;
    protected String calledApi;
    protected Map<String, String> body;
    protected JSONObject alternateBody;
    protected Map<String, String> headers;
    protected ApiBaseResponseHandler apiBaseResponseHandler;

    public BaseResponseHandler(Activity activity, String calledApi, Map<String, String> body, Map<String, String> headers, ApiBaseResponseHandler apiBaseResponseHandler) {
        this.activity = activity;
        this.calledApi = calledApi;
        this.body = body;
        this.headers = headers;
        this.apiBaseResponseHandler = apiBaseResponseHandler;
    }

    public BaseResponseHandler(Context activity, String calledApi, Map<String, String> body, Map<String, String> headers, ApiBaseResponseHandler apiBaseResponseHandler) {
        this.activity = activity;
        this.calledApi = calledApi;
        this.body = body;
        this.headers = headers;
        this.apiBaseResponseHandler = apiBaseResponseHandler;
    }

    public BaseResponseHandler(Activity activity, String calledApi, JSONObject alternateBody, Map<String, String> headers, ApiBaseResponseHandler apiBaseResponseHandler) {
        this.activity = activity;
        this.calledApi = calledApi;
        this.alternateBody = alternateBody;
        this.headers = headers;
        this.apiBaseResponseHandler = apiBaseResponseHandler;
    }

    public BaseResponseHandler(Context activity, String calledApi, JSONObject alternateBody, Map<String, String> headers, ApiBaseResponseHandler apiBaseResponseHandler) {
        this.activity = activity;
        this.calledApi = calledApi;
        this.alternateBody = alternateBody;
        this.headers = headers;
        this.apiBaseResponseHandler = apiBaseResponseHandler;
    }


    public abstract void setResponse(JSONObject responseJson, String responseString);

    public abstract void handleResponse();
    public abstract void errorResponse(String error);
    public abstract void errorResponse(String error, int Code);
    public abstract void errorResponse();
}
