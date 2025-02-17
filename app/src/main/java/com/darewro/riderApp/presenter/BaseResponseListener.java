package com.darewro.riderApp.presenter;


public interface BaseResponseListener {
    public void onSuccess(String calledApi);
    public void onError(String calledApi);
    public void onError(String calledApi, int errorCode);
    public void onError(String calledApi,String error, int errorCode);

}
