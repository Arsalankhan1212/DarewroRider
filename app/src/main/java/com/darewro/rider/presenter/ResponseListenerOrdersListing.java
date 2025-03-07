package com.darewro.rider.presenter;

import com.darewro.rider.view.models.Order;

import java.util.List;

public interface ResponseListenerOrdersListing extends BaseResponseListener {
    public void onSuccess(String calledApi, String json);

    public void onSuccess(String calledApi, List<Order> orders);
    public void onSuccess(String calledApi, List<Order> orders, List<Order> completedCrders);

    public void onError(String calledApi, String errorMessage);
    public void onError(String calledApi, String errorMessage, int errorCode);
}
