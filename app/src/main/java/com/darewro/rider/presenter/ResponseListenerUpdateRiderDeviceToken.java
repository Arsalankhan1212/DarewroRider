package com.darewro.rider.presenter;


import com.darewro.rider.data.api.models.User;

public interface ResponseListenerUpdateRiderDeviceToken extends BaseResponseListener{
    public void onSuccess(String calledApi, boolean isAlreadyExists, User user);
    public void onError(String calledApi, String message);
}
