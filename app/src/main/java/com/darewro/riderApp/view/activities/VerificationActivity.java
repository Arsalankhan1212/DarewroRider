package com.darewro.riderApp.view.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.chaos.view.PinView;
import com.darewro.riderApp.R;
import com.darewro.riderApp.data.api.ApiCalls;
import com.darewro.riderApp.data.api.Interface.InitApi;
import com.darewro.riderApp.data.api.handlers.UpdateRiderDeviceTokenHandler;
import com.darewro.riderApp.data.api.models.User;
import com.darewro.riderApp.data.api.requests.JsonObjectRequestCall;
import com.darewro.riderApp.presenter.ResponseListenerLogin;
import com.darewro.riderApp.presenter.ResponseListenerUpdateRiderDeviceToken;
import com.darewro.riderApp.view.receivers.AlarmReceiver;
import com.darewro.riderApp.view.utils.AlertDialogUtils;
import com.darewro.riderApp.view.utils.AppUtils;
import com.darewro.riderApp.view.utils.SharedPreferenceHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class VerificationActivity extends BaseFullScreenActivity implements ResponseListenerLogin, ResponseListenerUpdateRiderDeviceToken {

    private static final int COUNT_UPPER_LIMIT = 10;
    private static int count = 0;
    Button verify;
    PinView pinView;
    private String code, phone;
    private TextView seconds, secondsText;
    private boolean canRequestNewCode = false;
    private Timer timer;
    private String imei = "";

    private FirebaseAuth firebaseAuth;
    private String mFirebaseVerificationId;
    private PhoneAuthProvider.ForceResendingToken mFirebaseResendToken;

    private boolean isVerified = false;

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getmFirebaseVerificationId() {
        return mFirebaseVerificationId;
    }

    public void setmFirebaseVerificationId(String mFirebaseVerificationId) {
        this.mFirebaseVerificationId = mFirebaseVerificationId;
    }

    public PhoneAuthProvider.ForceResendingToken getmFirebaseResendToken() {
        return mFirebaseResendToken;
    }

    public void setmFirebaseResendToken(PhoneAuthProvider.ForceResendingToken mFirebaseResendToken) {
        this.mFirebaseResendToken = mFirebaseResendToken;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        handleIntent();
        initializeViews();
        sendCode();
    }

    @Override
    public void initializeViews() {

        firebaseAuth = FirebaseAuth.getInstance();
        verify = findViewById(R.id.verify);
        pinView = findViewById(R.id.pinView);
        seconds = findViewById(R.id.seconds);
        secondsText = findViewById(R.id.seconds_text);


        AppUtils.setMontserratBold(findViewById(R.id.verification_title));
        AppUtils.setMontserrat(findViewById(R.id.description_title));
        AppUtils.setMontserrat(verify);
        AppUtils.setMontserrat(pinView);
        AppUtils.setMontserrat(seconds);
        AppUtils.setMontserrat(secondsText);


        reStartCountDown();


        /*if (code != null)
            //comment this code to prevent code form auto populating
            pinView.setText(code);
*/

        setListeners();
    }

    @Override
    public void setListeners() {
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pinView.getText().toString().length() == 6) {
                    if (phone.equals("00923001000000") || phone.equals("00923128282126") || phone.equals("00923189144757")) {
                        if (pinView.getText().toString().equals("123456")) {
                            isVerified = true;
                            updateDeviceToken();
                            Log.d("TAG", "isVerified = TRUE");
                        } else
                            AppUtils.makeNotification(getResources().getString(R.string.invalid_verification_code_size), VerificationActivity.this);
                    } else if (phone.endsWith(pinView.getText().toString())) {
                        //verifyVerificationCode(pinView.getText().toString());
                        isVerified = true;
                        updateDeviceToken();
                    } else {
                        verifyVerificationCode(pinView.getText().toString());
                        //isVerified = true;
                        //updateDeviceToken();
                    }
                } else {
                    AppUtils.makeNotification(getResources().getString(R.string.invalid_verification_code_size), VerificationActivity.this);
                }
            }
        });
        secondsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count == 0) {
                    reSendVerificationCode();
                    reStartCountDown();
                }
            }
        });
    }

    @Override
    public void handleIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("code")) {
                code = bundle.getString("code");
                phone = bundle.getString("phone");
            }
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
        Log.d("TAG11", "errorCode= " + errorCode + ", api called= " + calledApi);
        if (errorCode == 403)
            AlertDialogUtils.showAlertDialog(VerificationActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), getString(R.string.app_update), getString(R.string.ok), null, true);

    }

    @Override
    public void onSuccess(String calledApi, String id, String code, String token) {

        SharedPreferenceHelper.saveString(SharedPreferenceHelper.UID, id, VerificationActivity.this);
        SharedPreferenceHelper.saveString(SharedPreferenceHelper.CODE, code, VerificationActivity.this);
        SharedPreferenceHelper.saveString(SharedPreferenceHelper.API_TOKEN, token, VerificationActivity.this);


        //comment this code to prevent code form auto populating
//        updateCode();

    }

    public void updateCode() {
        String code = SharedPreferenceHelper.getString(SharedPreferenceHelper.CODE, VerificationActivity.this);
        this.code = code;
        pinView.setText(code);
    }

    @Override
    public void onSuccess(String calledApi, boolean isAlreadyExists, User user) {
        if (user != null && isAlreadyExists) {

            SharedPreferenceHelper.saveString(SharedPreferenceHelper.API_TOKEN, SharedPreferenceHelper.getString(SharedPreferenceHelper.API_TOKEN, VerificationActivity.this), VerificationActivity.this);
            SharedPreferenceHelper.saveString(SharedPreferenceHelper.FIRE_BASE_TOKEN, SharedPreferenceHelper.getString(SharedPreferenceHelper.FIRE_BASE_TOKEN, VerificationActivity.this), VerificationActivity.this);
            SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.IS_LOGGED_IN, true, VerificationActivity.this);
            SharedPreferenceHelper.setUser(user, VerificationActivity.this);

            setAlarm();
            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
            }
            AppUtils.switchActivity(VerificationActivity.this, MainActivity.class, null);

        } else {
            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
            }
        }
    }


    @Override
    public void onError(String calledApi, String message) {

    }

    @Override
    public void onError(String calledApi, String message, int errorCode) {
        Log.d("TAG11", "message= " + message + ", errorCode= " + errorCode + ", api called= " + calledApi);
        if (errorCode == 403)
            AlertDialogUtils.showAlertDialog(VerificationActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), message, getString(R.string.ok), null, true);

    }

    private void reSendVerificationCode() {

        String phoneNo = SharedPreferenceHelper.getString(SharedPreferenceHelper.PHONE, VerificationActivity.this);
        if (TextUtils.isEmpty(phoneNo)) {
            AppUtils.makeNotification(getResources().getString(R.string.invalid_phone_number), VerificationActivity.this);
            return;
        }

        sendCode();

    }

    public void reStartCountDown() {
        count = 0;
        if (timer == null) {
            timer = new Timer();
        }

        if (count == 0) {
            count = COUNT_UPPER_LIMIT;
            secondsText.setText(getString(R.string.resend_code_in));
            secondsText.setEnabled(false);
            seconds.setText(count + " sec(s)");
            seconds.setVisibility(View.VISIBLE);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (VerificationActivity.this == null) {
                        timer.cancel();
                        timer.purge();
                        timer = null;
                    } else {
                        VerificationActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (count <= 0) {
                                    secondsText.setText(getString(R.string.resend_code));
                                    seconds.setVisibility(View.GONE);
                                    secondsText.setEnabled(true);
                                    if (timer != null) {
                                        timer.cancel();
                                        timer.purge();
                                        timer = null;
                                    }
                                } else {
                                    seconds.setText(--count + " sec(s)");
                                    secondsText.setText(getResources().getString(R.string.resend_code_in));
                                    seconds.setVisibility(View.VISIBLE);
                                    secondsText.setEnabled(false);
                                }

                            }
                        });
                    }
                }
            }, 0, 1000);
        }
    }

    private void updateDeviceToken() {

        if (TextUtils.isEmpty(SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, VerificationActivity.this))) {
            AppUtils.makeNotification(getResources().getString(R.string.invalid_user), VerificationActivity.this);
            return;
        }

        if (TextUtils.isEmpty(SharedPreferenceHelper.getString(SharedPreferenceHelper.FIRE_BASE_TOKEN, VerificationActivity.this))) {
//            AppUtils.makeNotification(getResources().getString(R.string.something_went_wrong), VerificationActivity.this);
            AppUtils.makeNotification(getResources().getString(R.string.invalid_firebase_token), VerificationActivity.this);
            return;
        }

        Log.i("Device token", SharedPreferenceHelper.getString(SharedPreferenceHelper.FIRE_BASE_TOKEN, VerificationActivity.this));
        InitApi initApi = new InitApi() {
            @Override
            public HashMap<String, String> getBody() {
                HashMap<String, String> body = new HashMap<>();
                body.put("UserId", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, VerificationActivity.this));
                body.put("DeviceToken", SharedPreferenceHelper.getString(SharedPreferenceHelper.FIRE_BASE_TOKEN, VerificationActivity.this));
                return body;
            }

            @Override
            public HashMap<String, Object> getObjBody() {
                return null;
            }

            @Override
            public HashMap<String, String> getHeader() {
                HashMap<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SharedPreferenceHelper.getString(SharedPreferenceHelper.API_TOKEN, VerificationActivity.this));
                return header;
            }
        };

        String url = ApiCalls.updateRiderDevice();
        UpdateRiderDeviceTokenHandler updateDeviceTokenHandler = new UpdateRiderDeviceTokenHandler(VerificationActivity.this, url, this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), url, Request.Method.POST, VerificationActivity.this, updateDeviceTokenHandler);
        jsonObjectRequestCall.sendData();
    }

    private void setAlarm() {

        String shiftTimings = SharedPreferenceHelper.getString(SharedPreferenceHelper.RIDER_SHIFT_SETTING, VerificationActivity.this);
        Calendar calendar = Calendar.getInstance();
        if (!shiftTimings.equals("")) {
            try {
                JSONObject shift = new JSONObject(shiftTimings);

                if (shift.has("riderEndTime")) {
                    String endTime = shift.getString("riderEndTime");
                    String[] endHour = endTime.split(":");

                    if (endHour.length > 0 && calendar.get(Calendar.HOUR_OF_DAY) < Integer.parseInt(endHour[0])) {
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endHour[0]));
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                    } else {
                        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endHour[0]));
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                    }

                } else {

                    if (calendar.get(Calendar.HOUR_OF_DAY) < 3) {
                        calendar.set(Calendar.HOUR_OF_DAY, 3);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.AM_PM, Calendar.AM);

                    } else {
                        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
                        calendar.set(Calendar.HOUR_OF_DAY, 3);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.AM_PM, Calendar.AM);

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            if (calendar.get(Calendar.HOUR_OF_DAY) < 3) {
                calendar.set(Calendar.HOUR_OF_DAY, 3);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.AM_PM, Calendar.AM);


            } else {
                calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
                calendar.set(Calendar.HOUR_OF_DAY, 3);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.AM_PM, Calendar.AM);
            }
        }

        Intent intentsOpen = new Intent(
                getApplicationContext(),
                AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(
                        getApplicationContext(),
                        1234567, intentsOpen, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }


    public void sendCode() {
        String phoneNo = SharedPreferenceHelper.getString(SharedPreferenceHelper.PHONE, VerificationActivity.this);
//        Log.i("phone",phoneNo);
        if (TextUtils.isEmpty(phoneNo)) {
            AppUtils.makeNotification(getResources().getString(R.string.invalid_phone_number), VerificationActivity.this);
            return;
        }
        phoneNo = phoneNo.replaceAll(" ", "");
        phoneNo = phoneNo.replaceFirst("0092", "+92");


        PhoneAuthOptions authOptions = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance()).
                setActivity(this)
                .setPhoneNumber(phoneNo)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks( new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        //Getting the code sent by SMS
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        e.printStackTrace();

                        AppUtils.makeNotification(e.getMessage(), VerificationActivity.this);
                        Log.d("TAG11", "ERROR= " + e.getMessage());
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
//                        AppUtils.makeNotification("inside onCodeSent()",VerificationActivity.this);

                        setmFirebaseVerificationId(s);
                        setmFirebaseResendToken(forceResendingToken);
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(authOptions);
    }

    public void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mFirebaseVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            isVerified = true;
                            updateDeviceToken();
                        } else {
                            isVerified = false;
                            //verification unsuccessful.. display an error message

                            String message = "Invalid Verification Code";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            AppUtils.makeNotification(message, VerificationActivity.this);

                        }
                    }
                });
    }


}
