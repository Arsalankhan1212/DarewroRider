package com.darewro.rider.presenter;

import com.darewro.rider.data.api.models.assignOrder.AssignOrderResponseData;

public interface ResponseListenerAssignOrderToRider extends BaseResponseListener {
    public void onSuccess(AssignOrderResponseData response, String calledApi);
    public void onError(String calledApi, String errorMessage);
}
