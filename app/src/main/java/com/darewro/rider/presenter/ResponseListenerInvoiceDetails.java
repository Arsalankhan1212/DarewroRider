package com.darewro.rider.presenter;

import com.darewro.rider.view.models.Invoice;
import com.darewro.rider.view.models.Order;

public interface ResponseListenerInvoiceDetails extends BaseResponseListener {
    public void onSuccess(String calledApi, String json);

    public void onSuccess(String calledApi, Invoice invoice);

    public void onError(String calledApi);
}
