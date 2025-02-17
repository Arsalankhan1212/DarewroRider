package com.darewro.riderApp.data.api.handlers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.darewro.riderApp.presenter.BaseResponseListener;
import com.darewro.riderApp.presenter.ResponseListenerGeneric;
import com.darewro.riderApp.presenter.ResponseListenerStatus;

import org.json.JSONArray;
import org.json.JSONObject;

public class GenericHandler extends ApiBaseResponseHandler {

    ResponseListenerGeneric responseListenerGeneric;

    public GenericHandler(Activity activity, String calledApi, BaseResponseListener baseResponseListener) {
        super(activity, calledApi, baseResponseListener);
        this.responseListenerGeneric = (ResponseListenerGeneric) baseResponseListener;

    }

    public GenericHandler(Context activity, String calledApi, BaseResponseListener baseResponseListener) {
        super(activity, calledApi, baseResponseListener);
        this.responseListenerGeneric = (ResponseListenerGeneric) baseResponseListener;

    }

    public GenericHandler(Context activity, String calledApi) {
        super(activity, calledApi);

    }




    @Override
    public void setApiResponse(JSONObject jsonObject, String calledApi) {
        Log.i("response",jsonObject.toString());

        if(responseListenerGeneric!=null) {
            if(jsonObject.has("filePath") && !jsonObject.optString("filePath").equals(""))
                responseListenerGeneric.onSuccess(calledApi,jsonObject.optString("filePath"));
            if(jsonObject.optString("message")!=null)
                responseListenerGeneric.onSuccess(calledApi, jsonObject.optString("message"));
            else
                responseListenerGeneric.onSuccess(calledApi, jsonObject.optJSONArray("response").toString());

        }
    }

    @Override
    public void setApiResponse(JSONArray jsonArray, String calledApi) {
        if(responseListenerGeneric!=null)
            responseListenerGeneric.onSuccess(calledApi, jsonArray.toString());

    }

    @Override
    public void setApiResponse(String jsonAsString, String calledApi) {
        if(responseListenerGeneric!=null)
            responseListenerGeneric.onSuccess(calledApi);

    }

    @Override
    public void setApiError(String calledApi) {
        if(responseListenerGeneric!=null)
            responseListenerGeneric.onError(calledApi);

    }

    @Override
    public void setApiError(String errorMessage, String calledApi) {
        if(responseListenerGeneric!=null)
            responseListenerGeneric.onError(calledApi, errorMessage);

    }

    @Override
    public void setApiError(String errorMessage, String calledApi, int errorCode) {
        if(responseListenerGeneric!=null)
            responseListenerGeneric.onError(calledApi, errorMessage, errorCode);

    }


}
