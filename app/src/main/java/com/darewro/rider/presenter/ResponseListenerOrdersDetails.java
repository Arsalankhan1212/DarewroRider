package com.darewro.rider.presenter;

import com.darewro.rider.data.api.models.OrderDetail;
import com.darewro.rider.view.models.Order;

public interface ResponseListenerOrdersDetails extends BaseResponseListener {
    public void onSuccess(String calledApi, String json);

    public void onSuccess(String calledApi, Order order);

    public void onError(String calledApi);
}
