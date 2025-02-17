package com.darewro.riderApp.presenter;

public interface ResponseListenerGeneric extends BaseResponseListener {
    public void onSuccess(String calledApi, String response);
    public void onError(String calledApi, String errorMessage);
    public void onError(String calledApi, String errorMessage, int errorCode);


}
