package com.darewro.riderApp.view.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaDrm;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.darewro.riderApp.R;
import com.darewro.riderApp.data.api.ApiCalls;
import com.darewro.riderApp.data.api.Interface.InitApi;
import com.darewro.riderApp.data.api.handlers.LoginHandler;
import com.darewro.riderApp.data.api.requests.JsonObjectRequestCall;
import com.darewro.riderApp.data.db.model.OrderPath;
import com.darewro.riderApp.presenter.ResponseListenerLogin;
import com.darewro.riderApp.view.listeners.AlertDialogResponseListener;
import com.darewro.riderApp.view.utils.AlertDialogUtils;
import com.darewro.riderApp.view.utils.AppUtils;
import com.darewro.riderApp.view.utils.DbUtils;
import com.darewro.riderApp.view.utils.MaskingUtils;
import com.darewro.riderApp.view.utils.PermissionsRequest;
import com.darewro.riderApp.view.utils.SharedPreferenceHelper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.redmadrobot.inputmask.MaskedTextChangedListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LoginActivity extends BaseFullScreenActivity implements AlertDialogResponseListener, ResponseListenerLogin {

    private static final int IGNORE_BATTERY_OPTIMIZATION_REQUEST = 1002;
    EditText phone;
    Button sendCode;
    private String phoneNo = "";
    String imei = "0";
    String android_id = "";
    String udid = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeViews();


    }

    @Override
    public void initializeViews() {
        phone = findViewById(R.id.phone);
        sendCode = findViewById(R.id.send_code);
        AppUtils.setMontserratBold((TextView) findViewById(R.id.login_title));
        AppUtils.setMontserrat((TextView) findViewById(R.id.description_title));
        AppUtils.setMontserrat((TextView) findViewById(R.id.andID));
        AppUtils.setMontserrat((TextView) findViewById(R.id.andIMEI));
        AppUtils.setMontserrat(phone);
        AppUtils.setMontserrat(sendCode);
        AppUtils.setMontserrat((TextView) findViewById(R.id.note_title));
        setListeners();
        getAndroidDeviceIdOrIMEI();

        getDeviceToken();
    }


    private void getDeviceToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("TAG11", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get the new FCM registration token
                    String token = task.getResult();
                   sendRegistrationToServer(token);
                   Log.d("TAG111","token= "+token);

                    // You can send this token to your server if needed
                });
    }

    private void sendRegistrationToServer(String token) {
        SharedPreferenceHelper.saveString(SharedPreferenceHelper.FIRE_BASE_TOKEN, token, getApplicationContext());
    }
    private void getAndroidDeviceIdOrIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (telephonyManager != null) {
                    try {
                        imei = telephonyManager.getImei();
                    } catch (Exception e) {
                        e.printStackTrace();
                        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                    }
                }
            } else {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PermissionsRequest.READ_PHONE_STATE);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (telephonyManager != null) {
                    imei = telephonyManager.getDeviceId();
                }
            } else {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PermissionsRequest.READ_PHONE_STATE);
            }
        }
        try {
            udid = getUniqueID();
        } catch (Exception ex) {
            udid = "";
        }
        if (imei == null) {
            imei = "0";
        }

        if (android_id == null) {
            android_id = "";
        }

        ((TextView) findViewById(R.id.andID)).setText(getResources().getString(R.string.android_id) + " : " + android_id);
        ((TextView) findViewById(R.id.andIMEI)).setText(getResources().getString(R.string.android_imei) + " : " + imei);
        ((TextView) findViewById(R.id.andTestUUID)).setText(getResources().getString(R.string.android_uuid) + " : " + udid);

    }


    @Override
    public void setListeners() {
        MaskedTextChangedListener listenerPhone = new MaskedTextChangedListener(
                MaskingUtils.MASK_PHONE,
                phone,
                new MaskedTextChangedListener.ValueListener() {
                    @Override
                    public void onTextChanged(boolean b, @NonNull String extractedValue, @NonNull String s1, @NonNull String s2) {
                        if (extractedValue.length() < 5) {
                            phone.setText(MaskingUtils.DEFAULT_CODE);
                            phone.setSelection(extractedValue.length() + 1);
                        }//                        Log.i("Phone = ", extractedValue);
                        Log.i("Phone = ", MaskingUtils.getMaskedText(MaskingUtils.MASK_PHONE, extractedValue));
                    }
                }
        );

        phone.addTextChangedListener(listenerPhone);
        phone.setOnFocusChangeListener(listenerPhone);
        phone.setHint(listenerPhone.placeholder());

        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                OrderPath path = new OrderPath();
//                List<OrderPath> paths = new ArrayList<>();
//                path = new OrderPath();
//                path.setLat("33.9862394");
//                path.setLng("71.4093647");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9862387");
//                path.setLng("71.4093626");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9862388");
//                path.setLng("71.4093628");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9862386");
//                path.setLng("71.409363");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9862402");
//                path.setLng("71.4093621");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9862402");
//                path.setLng("71.4093621");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9862465");
//                path.setLng("71.409361");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9862606");
//                path.setLng("71.4093636");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9862606");
//                path.setLng("71.4093636");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9862603");
//                path.setLng("71.4093636");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9862526");
//                path.setLng("71.4093659");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9862318");
//                path.setLng("71.4093568");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9862436");
//                path.setLng("71.4093578");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9862494");
//                path.setLng("71.4093687");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9862494");
//                path.setLng("71.4093687");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9862494");
//                path.setLng("71.4093687");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9862494");
//                path.setLng("71.4093687");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9862494");
//                path.setLng("71.4093687");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9862421");
//                path.setLng("71.4093698");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("34.024054");
//                path.setLng("71.4855976");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("34.0239564");
//                path.setLng("71.4859737");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("34.007474");
//                path.setLng("71.5244977");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9994756");
//                path.setLng("71.4951433");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("34.0020681");
//                path.setLng("71.500002");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("34.0021785");
//                path.setLng("71.500323");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("34.0018288");
//                path.setLng("71.5002682");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("34.0005647");
//                path.setLng("71.5002916");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9992969");
//                path.setLng("71.5002984");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9976113");
//                path.setLng("71.5004853");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.99749");
//                path.setLng("71.500945");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9967781");
//                path.setLng("71.5009084");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9959835");
//                path.setLng("71.5008583");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9955406");
//                path.setLng("71.5017061");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9954684");
//                path.setLng("71.5021849");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.995424");
//                path.setLng("71.5027498");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9948318");
//                path.setLng("71.5039235");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9945318");
//                path.setLng("71.5039201");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9944919");
//                path.setLng("71.5038895");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9944919");
//                path.setLng("71.5038893");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9944908");
//                path.setLng("71.5038897");
//                paths.add(path);
//                path = new OrderPath();
//                path.setLat("33.9945897");
//                path.setLng("71.5039245");
//                paths.add(path);
//                try {
//                    double distance = 0;
//                    //if(paths.size()>1) {
//                    distance = AppUtils.calculateDistance(LoginActivity.this, paths);
//                    //}
//
//                    Log.i("distance", String.valueOf(distance) + " km");//dist;
//                } catch (Exception e) {
//                    Log.i("Exception", e.getMessage());
//                    e.printStackTrace();
//                }
                if (MaskingUtils.getMaskedText(MaskingUtils.MASK_PHONE, phone.getText().toString()).length() < 13) {
                    phone.setError(getString(R.string.provide_valid_field));
                    return;
                }
                if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PermissionsRequest.READ_PHONE_STATE);
                } else {
                    AlertDialogUtils.showConfirmationAlertDialog(LoginActivity.this, AlertDialogUtils.ALERT_DIALOG_SEND_V_CODE, getString(R.string.confirm_your_number), MaskingUtils.getMaskedText(MaskingUtils.MASK_PHONE, phone.getText().toString()) + " " + getString(R.string.correct_number), getString(R.string.yes), getString(R.string.edit), LoginActivity.this);
                }
            }
        });
    }


    @Override
    public void handleIntent() {

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
        if (alertId == AlertDialogUtils.ALERT_DIALOG_SEND_V_CODE) {
            login();
        }
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

    private void login() {

        if (TextUtils.isEmpty(phone.getText().toString())) {
            AppUtils.makeNotification(getResources().getString(R.string.please_enter_phone_num), LoginActivity.this);
            return;
        }
        if (phone.getText().toString().length() <= 12 && phone.getText().toString().length() > 0) {
            AppUtils.makeNotification(getResources().getString(R.string.invalid_phone_number), LoginActivity.this);
            return;
        }

        InitApi initApi = new InitApi() {
            @Override
            public HashMap<String, String> getBody() {
                return null;
            }

            @Override
            public HashMap<String, Object> getObjBody() {
                return null;
            }

            @Override
            public HashMap<String, String> getHeader() {
                HashMap<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");
                return header;//AppUtils.getStandardHeaders(getActivity());
            }
        };

        phoneNo = phone.getText().toString().replaceAll(" ", "");
        phoneNo = phoneNo.replace("+", "00");


        if (phoneNo.equals("00923001000000")) {
            //imei="100000000000000";
            //android_id="0";
            udid = "GGdD2GNaI833BDNJBXB+AWPY611vkMI8alpyRfDBG9g=";
        }
        if (phoneNo.equals("00923189144757")){
            udid = "iwdbj1uCrRqVod9KnRJQ0nTsW6gGL35qxtxyfYwuhao=";
        }
        if (phoneNo.equals("00923128282126")) {
            //imei="100000000000000";
            //android_id="0";
            udid = "sF/fYh6Ag3p3oFNhTkl6r04UGNZmwCNNInx7UmeSVF4=";
        }

//        if(phoneNo.equals("00923334437044")){
//            //imei="100000000000000";
//            //android_id="0";
//            udid = "U0VDUEhPTkVfMDAwMDAwMDAwMDAwMDAwMDAzMDczNDI=";
//        }
//        if(phoneNo.equals("00923433207882")){
////            imei="100000000000000";
////            android_id="0";
//            udid = "mooPc5dy965lXh9N8yl2Ahy7UwcU4TPbioneAb/Yc64=";
//        }
       /* TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            imei = telephonyManager.getDeviceId();
        }*/
        //03307139037
        //imei="0";
        //android_id="6a1981f6a532e6b4";
        //imei="355091084400811";
        //android_id="0";

        //mine
        //android_id = "6a1981f6a532e6b4";
        //imei = "0";
        //android_id = "6c89ca0d904d269b";
        //imei = "0";//"356630090464037";
        // MSISDN: 03307139080

        //NAME.ATIF IQBAL
        //MSISDN. 03307139031
        //IMEI. 359388080460203
        //android_id="0";
        //imei = "359388080460203";
//        MSISDN.  03307139032
        //android_id = "0";
        //imei =  "868112043201015";


        //android_id = "f4e5691357b7756c";
        //imei ="0";

        try {
//            Log.i("udid",udid.trim()+"end");

            //          String url = ApiCalls.riderLogin() + "?mobileNumber=" + phoneNo + "&imei=" + imei + "&androidId=" + android_id;
            String query = URLEncoder.encode("" + udid.trim(), "utf-8");
            String url = ApiCalls.riderLogin() + "?mobileNumber=" + phoneNo + "&Devicekey=" + query;
            LoginHandler signupHandler = new LoginHandler(LoginActivity.this, url, this);
            JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), url, Request.Method.GET, LoginActivity.this, signupHandler);
            jsonObjectRequestCall.sendData();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("TAG11","API Error= "+ex.getMessage());
        }

    }


    @Override
    public void onSuccess(String calledApi) {

    }

    @Override
    public void onError(String calledApi) {

    }

    @Override
    public void onError(String calledApi, int errorCode) {
        Log.d("TAG11","Error: "+calledApi +" errorcode= "+errorCode);
        if (errorCode == 403)
            AlertDialogUtils.showAlertDialog(LoginActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), getString(R.string.app_update), getString(R.string.ok), null, true);

    }


    @Override
    public void onSuccess(String calledApi, String id, String code, String token) {

//        phoneNo = "00923448560083";
        SharedPreferenceHelper.saveString(SharedPreferenceHelper.UID, id, LoginActivity.this);
        SharedPreferenceHelper.saveString(SharedPreferenceHelper.PHONE, phoneNo.trim(), LoginActivity.this);
        SharedPreferenceHelper.saveString(SharedPreferenceHelper.CODE, code, LoginActivity.this);
        SharedPreferenceHelper.saveString(SharedPreferenceHelper.API_TOKEN, token, LoginActivity.this);

        Bundle bundle = new Bundle();
        bundle.putString("code", code);
        bundle.putString("phone", phoneNo);
        AppUtils.switchActivity(LoginActivity.this, VerificationActivity.class, bundle);


      //  AppUtils.switchActivity(LoginActivity.this, MainActivity.class, null);


        Log.d("TAG11","Login SUCCESS");
    }

    @Override
    public void onError(String calledApi, String message) {
        Log.d("TAG11","ERROR="+message);
    }

    @Override
    public void onError(String calledApi, String message, int errorCode) {
        if (errorCode == 403)
            AlertDialogUtils.showAlertDialog(LoginActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), message, getString(R.string.ok), null, true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionsRequest.READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getAndroidDeviceIdOrIMEI();
                    if (MaskingUtils.getMaskedText(MaskingUtils.MASK_PHONE, phone.getText().toString()).length() == 13) {
                        AlertDialogUtils.showConfirmationAlertDialog(LoginActivity.this, AlertDialogUtils.ALERT_DIALOG_SEND_V_CODE, getString(R.string.confirm_your_number), MaskingUtils.getMaskedText(MaskingUtils.MASK_PHONE, phone.getText().toString()) + " " + getString(R.string.correct_number), getString(R.string.yes), getString(R.string.edit), LoginActivity.this);
                    }
                } else
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PermissionsRequest.READ_PHONE_STATE);

                break;

            default:
                break;
        }
    }

    @Nullable
    String getUniqueID() {
        UUID wideVineUuid = new UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L);
        try {
            MediaDrm wvDrm = new MediaDrm(wideVineUuid);
            byte[] wideVineId = wvDrm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID);
            return Base64.encodeToString(wideVineId, Base64.DEFAULT);// Arrays.toString(wideVineId);
        } catch (Exception e) {
            return null;
        }

    }
}
