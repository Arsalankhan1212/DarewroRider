package com.darewro.rider.presenter;

import com.darewro.rider.data.api.models.Dashboard;

public interface ResponseListenerDashboard extends BaseResponseListener {

    void onSuccess(String calledApi, Dashboard dashboard);
}
