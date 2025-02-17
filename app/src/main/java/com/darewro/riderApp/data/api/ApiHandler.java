package com.darewro.riderApp.data.api;


import android.app.Activity;
import android.content.Context;

import com.darewro.riderApp.data.api.handlers.ApiBaseResponseHandler;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by AhmedAbbas on 11/24/2017.
 */

public class ApiHandler {
    protected JSONObject alternatebody;
    protected JSONObject responseJson;
    protected String calledApi;
    protected int method;
    protected Map<String, String> body;
    protected Map<String, String> header;
    protected Context activity;
    protected BaseResponseHandler responseHandler;
    protected boolean isAlternate = false;
    protected ApiBaseResponseHandler apiBaseResponseHandler;

    public ApiHandler(Map<String, String> header, Map<String, String> body, String calledApi, int method, Activity activity, ApiBaseResponseHandler apiBaseResponseHandler) {
        this.header = header;
        this.body = body;
        this.calledApi = calledApi;
        this.method = method;
        this.activity = activity;
        this.apiBaseResponseHandler = apiBaseResponseHandler;
    }

    public ApiHandler(Map<String, String> header, Map<String, String> body, String calledApi, int method, Context activity, ApiBaseResponseHandler apiBaseResponseHandler) {
        this.header = header;
        this.body = body;
        this.calledApi = calledApi;
        this.method = method;
        this.activity = activity;
        this.apiBaseResponseHandler = apiBaseResponseHandler;
    }

    public ApiHandler(Map<String, String> header, JSONObject body, String calledApi, int method, Context activity, boolean isAlternate, ApiBaseResponseHandler apiBaseResponseHandler) {
        this.header = header;
        this.alternatebody = body;
        this.calledApi = calledApi;
        this.method = method;
        this.activity = activity;
        this.isAlternate = isAlternate;
        this.apiBaseResponseHandler = apiBaseResponseHandler;
    }

}
