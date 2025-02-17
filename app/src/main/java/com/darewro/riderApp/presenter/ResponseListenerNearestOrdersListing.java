package com.darewro.riderApp.presenter;

import com.darewro.riderApp.view.models.NearestOrder;
import com.darewro.riderApp.view.models.Order;

import java.util.List;

public interface ResponseListenerNearestOrdersListing extends BaseResponseListener {
    public void onSuccess(String calledApi, String json);

    public void onSuccess(String calledApi, List<NearestOrder> orders, boolean nearest);

    public void onError(String calledApi, String errorMessage);
    public void onError(String calledApi, String errorMessage, int errorCode);
}
