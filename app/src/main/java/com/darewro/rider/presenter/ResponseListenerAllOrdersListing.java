package com.darewro.rider.presenter;

import com.darewro.rider.data.api.models.allOrders.AllOrder;

import java.util.List;

public interface ResponseListenerAllOrdersListing extends BaseResponseListener {
    public void onSuccess(List<AllOrder> allOrders, String calledApi);
    public void onError(String calledApi, String errorMessage);
}
