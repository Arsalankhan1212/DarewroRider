package com.darewro.riderApp.data.api.handlers;

import android.app.Activity;
import android.content.Context;

import com.darewro.riderApp.presenter.BaseResponseListener;
import com.darewro.riderApp.presenter.ResponseListenerFile;

import org.json.JSONArray;
import org.json.JSONObject;

public class FileHandler extends ApiBaseResponseHandler {

    ResponseListenerFile responseListenerFile;

    public FileHandler(Activity activity, String calledApi, BaseResponseListener baseResponseListener) {
        super(activity, calledApi, baseResponseListener);
        this.responseListenerFile = (ResponseListenerFile) baseResponseListener;
    }

    public FileHandler(Context context, String calledApi, BaseResponseListener baseResponseListener) {
        super(context, calledApi, baseResponseListener);
        this.responseListenerFile = (ResponseListenerFile) baseResponseListener;
    }

    @Override
    public void setApiResponse(JSONObject jsonObject, String calledApi) {
        responseListenerFile.onSuccess(calledApi, String.valueOf(jsonObject));

//        {"id":null,"pathUrl":"http:\/\/localhost:19005\/Upload\/32edffcf-0e04-45b0-b305-a8d7bd5cf4cd_image15931720245511221120851.jpg"}
    }

    @Override
    public void setApiResponse(JSONArray jsonArray, String calledApi) {
        responseListenerFile.onSuccess(calledApi);
    }

    @Override
    public void setApiResponse(String jsonAsString, String calledApi) {
        responseListenerFile.onSuccess(calledApi);
    }

    @Override
    public void setApiError(String calledApi) {
        responseListenerFile.onError(calledApi);
    }

    @Override
    public void setApiError(String errorMessage, String calledApi) {
        responseListenerFile.onError(calledApi, errorMessage);
    }

    @Override
    public void setApiError(String errorMessage, String calledApi, int errorCode) {
        responseListenerFile.onError(calledApi, errorMessage,errorCode);

    }


}
