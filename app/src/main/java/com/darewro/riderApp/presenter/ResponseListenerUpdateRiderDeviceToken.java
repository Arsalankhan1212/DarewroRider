package com.darewro.riderApp.presenter;


import com.darewro.riderApp.data.api.models.User;

public interface ResponseListenerUpdateRiderDeviceToken extends BaseResponseListener{
    public void onSuccess(String calledApi, boolean isAlreadyExists, User user);
    public void onError(String calledApi, String message);
}
