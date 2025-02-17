package com.darewro.riderApp.presenter;

import com.darewro.riderApp.data.api.models.assignOrder.AssignOrderResponseData;

public interface ResponseListenerAssignOrderToRider extends BaseResponseListener {
    public void onSuccess(AssignOrderResponseData response, String calledApi);
    public void onError(String calledApi, String errorMessage);
}
