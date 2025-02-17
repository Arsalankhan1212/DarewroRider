package com.darewro.riderApp.presenter;

public interface ResponseListenerLogin extends BaseResponseListener {
    public void onSuccess(String calledApi,String id,String code,String token);
    public void onError(String calledApi,String message);
    public void onError(String calledApi,String message, int errorCode);


}
