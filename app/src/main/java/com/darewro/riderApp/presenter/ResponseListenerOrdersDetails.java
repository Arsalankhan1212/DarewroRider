package com.darewro.riderApp.presenter;

import com.darewro.riderApp.data.api.models.OrderDetail;
import com.darewro.riderApp.view.models.Order;

public interface ResponseListenerOrdersDetails extends BaseResponseListener {
    public void onSuccess(String calledApi, String json);

    public void onSuccess(String calledApi, Order order);

    public void onError(String calledApi);
}
