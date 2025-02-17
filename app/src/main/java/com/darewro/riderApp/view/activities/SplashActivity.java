package com.darewro.riderApp.view.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.darewro.riderApp.R;
import com.darewro.riderApp.data.api.ApiCalls;
import com.darewro.riderApp.view.listeners.AlertDialogResponseListener;
import com.darewro.riderApp.view.listeners.NGROKAlertDialogResponseListener;
import com.darewro.riderApp.view.locationService.LocationService;
import com.darewro.riderApp.view.utils.AlertDialogUtils;
import com.darewro.riderApp.view.utils.AppUtils;
import com.darewro.riderApp.view.utils.SharedPreferenceHelper;


public class SplashActivity extends BaseFullScreenActivity implements AlertDialogResponseListener, NGROKAlertDialogResponseListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (ApiCalls.getBaseUrl().contains("ngrok") && SharedPreferenceHelper.has("NGROK", SplashActivity.this) && !TextUtils.isEmpty(SharedPreferenceHelper.getString("NGROK", SplashActivity.this)) && SharedPreferenceHelper.getString("NGROK", SplashActivity.this) != null) {
            String url = SharedPreferenceHelper.getString("NGROK", SplashActivity.this);

            ApiCalls.BASE_URL = url + "api/";
            ApiCalls.BASE_URL_IMAGE = url;
        }

        SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.FORCE_STOP, false, SplashActivity.this);


        if ((SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, SplashActivity.this)) || (!SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, SplashActivity.this).equals(""))) {
            SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.FORCE_STOP, false, SplashActivity.this);
            Intent intent = new Intent(SplashActivity.this, LocationService.class);
            //if(LocationService.isRunning(InvoiceActivity.this))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }
        initializeViews();
        getDeviceStats();

    }

    private void getDeviceStats() {

        try {
            SharedPreferenceHelper.saveString(SharedPreferenceHelper.DEVICE_BRAND, android.os.Build.MANUFACTURER, SplashActivity.this);
            SharedPreferenceHelper.saveString(SharedPreferenceHelper.DEVICE_MODEL, android.os.Build.MODEL, SplashActivity.this);
            SharedPreferenceHelper.saveString(SharedPreferenceHelper.DEVICE_OS_VERSION, android.os.Build.VERSION.RELEASE, SplashActivity.this);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void initializeViews() {

        if (ApiCalls.getBaseUrl().contains("ngrok")) {
            String url = ApiCalls.getBaseUrl().replaceAll("http://", "");
            url = url.split(".ngrok")[0];
            AlertDialogUtils.makeAlertDialogNGROK(getResources().getString(R.string.ngrok_url), url, SplashActivity.this, SplashActivity.this);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_LOGGED_IN, SplashActivity.this)) {
                        Bundle bundle = getIntent().getExtras();
                        AppUtils.switchActivity(SplashActivity.this, MainActivity.class, bundle);
                        SplashActivity.this.finish();
                    } else {
                        AppUtils.switchActivity(SplashActivity.this, LoginActivity.class, null);
                        SplashActivity.this.finish();
                    }

                }
            }, 3000);
        }
    }

    @Override
    public void setListeners() {

    }

    @Override
    public void handleIntent() {

    }

    @Override
    public void OnSuccess() {

        if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_LOGGED_IN, SplashActivity.this)) {
            Bundle bundle = getIntent().getExtras();
            AppUtils.switchActivity(SplashActivity.this, MainActivity.class, bundle);
        } else {
            AppUtils.switchActivity(SplashActivity.this, LoginActivity.class, null);
        }
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

    }

    @Override
    public void OnCancel(int alertId) {

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
    public void OnNGROKSuccess(String url) {

        SharedPreferenceHelper.saveString("NGROK", url, SplashActivity.this);

        ApiCalls.BASE_URL = url + "api/";
        ApiCalls.BASE_URL_IMAGE = url;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_LOGGED_IN, SplashActivity.this)) {
                    Bundle bundle = getIntent().getExtras();
                    AppUtils.switchActivity(SplashActivity.this, MainActivity.class, bundle);
                    SplashActivity.this.finish();
                } else {
                    AppUtils.switchActivity(SplashActivity.this, LoginActivity.class, null);
                    SplashActivity.this.finish();
                }

            }
        }, 3000);
    }
}
