package com.darewro.riderApp.view.activities;

import android.os.Bundle;

import com.darewro.riderApp.data.api.ApiCalls;
import com.darewro.riderApp.view.listeners.AlertDialogResponseListener;
import com.darewro.riderApp.view.utils.AlertDialogUtils;

public class FireBaseViewNotificationActivity extends BaseLocationSearchFullScreenActivity implements AlertDialogResponseListener {
    String title,message,payload=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null) {
            title = getIntent().getStringExtra("title");
            message = getIntent().getStringExtra("message");
            if(getIntent().hasExtra("payload"))
                payload = getIntent().getStringExtra("payload");
        }

        if(payload!=null && !payload.equals("")){
            AlertDialogUtils.showNotificationDialog(this, 0, title,message, FireBaseViewNotificationActivity.this, ApiCalls.BASE_URL_IMAGE_NOTIFICATION+payload);

        }else{
            AlertDialogUtils.showConfirmationAlertDialog(this, 0, title,message, FireBaseViewNotificationActivity.this);
        }
    }

    @Override
    public void OnSuccess() {

    }

    @Override
    public void OnCancel() {

    }

    @Override
    public void OnSuccess(Object object) {

    }

    @Override
    public void OnCancel(Object object) {

    }

    @Override
    public void OnSuccess(int alertId) {
        finish();
    }

    @Override
    public void OnCancel(int alertId) {
        finish();
    }



    @Override
    public void OnSuccess(int alertId, Object object) {

    }

    @Override
    public void OnCancel(int alertId, Object object) {

    }

    @Override
    public void OnSuccess(int alertId, Object object, Object object2) {

    }

    @Override
    public void OnCancel(int alertId, Object object, Object object2) {

    }

    @Override
    public void initializeViews() {

    }

    @Override
    public void setListeners() {

    }

    @Override
    public void handleIntent() {

    }

}
