package com.darewro.riderApp.presenter;

import com.darewro.riderApp.view.models.Invoice;
import com.darewro.riderApp.view.models.Order;

public interface ResponseListenerInvoiceDetails extends BaseResponseListener {
    public void onSuccess(String calledApi, String json);

    public void onSuccess(String calledApi, Invoice invoice);

    public void onError(String calledApi);
}
