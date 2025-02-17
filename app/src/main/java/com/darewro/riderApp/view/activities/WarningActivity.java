package com.darewro.riderApp.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.darewro.riderApp.R;
import com.darewro.riderApp.view.listeners.AlertDialogResponseListener;
import com.darewro.riderApp.view.utils.AlertDialogUtils;
import com.darewro.riderApp.view.utils.SharedPreferenceHelper;

public class WarningActivity extends Activity implements AlertDialogResponseListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


            AlertDialogUtils.showConfirmationAlertDialog(WarningActivity.this, AlertDialogUtils.ALERT_DIALOG_GPS, getResources().getString(R.string.gps), getResources().getString(R.string.gps_warning), getResources().getString(R.string.proceed), getResources().getString(R.string.cancel), this);

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

        launchGPSSettings();
    }

    @Override
    public void OnCancel(int alertId) {

        WarningActivity.this.finish();
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

    private void launchGPSSettings() {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        finish();

    }


}
