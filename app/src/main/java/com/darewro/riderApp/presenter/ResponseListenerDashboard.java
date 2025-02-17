package com.darewro.riderApp.presenter;

import com.darewro.riderApp.data.api.models.Dashboard;

public interface ResponseListenerDashboard extends BaseResponseListener {

    void onSuccess(String calledApi, Dashboard dashboard);
}
