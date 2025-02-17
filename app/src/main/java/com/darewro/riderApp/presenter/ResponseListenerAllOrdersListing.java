package com.darewro.riderApp.presenter;

import com.darewro.riderApp.data.api.models.allOrders.AllOrder;

import java.util.List;

public interface ResponseListenerAllOrdersListing extends BaseResponseListener {
    public void onSuccess(List<AllOrder> allOrders, String calledApi);
    public void onError(String calledApi, String errorMessage);
}
