package com.darewro.rider.presenter;

import com.darewro.rider.view.models.NearestOrder;
import com.darewro.rider.view.models.Order;

import java.util.List;

public interface ResponseListenerNearestOrdersListing extends BaseResponseListener {
    public void onSuccess(String calledApi, String json);

    public void onSuccess(String calledApi, List<NearestOrder> orders, boolean nearest);

    public void onError(String calledApi, String errorMessage);
    public void onError(String calledApi, String errorMessage, int errorCode);
}
