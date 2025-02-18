package com.darewro.rider.view.locationService;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;
import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.android.volley.Request;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.instacart.library.truetime.TrueTimeRx;
import com.darewro.rider.BuildConfig;
import com.darewro.rider.R;
import com.darewro.rider.data.api.ApiCalls;
import com.darewro.rider.data.api.Interface.InitApi;
import com.darewro.rider.data.api.handlers.GenericHandler;
import com.darewro.rider.data.api.requests.JsonObjectRequestCall;
import com.darewro.rider.presenter.ResponseListenerGeneric;
import com.darewro.rider.view.activities.InvoiceActivity;
import com.darewro.rider.view.activities.RatingActivity;
import com.darewro.rider.view.receivers.AlarmReceiverForRiderTrackingService;
import com.darewro.rider.view.utils.AlertDialogUtils;
import com.darewro.rider.view.utils.AppUtils;
import com.darewro.rider.view.utils.SharedPreferenceHelper;
import com.darewro.rider.view.utils.TimeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Listens for geofence transition changes.
 */

public class LocationService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResponseListenerGeneric {
    private static final String TAG = "LocationService";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private static final int DISPLACEMENT = 5;
    LocationRequest locationRequest;
    GoogleApiClient googleApiClient;
    Location loc;
    private AlarmReceiverForRiderTrackingService alarmReceiverForRiderTrackingService = null;

    public static boolean isRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (int i = 0; i < services.size(); i++) {
            ComponentName componentName = services.get(i).service;
            String serviceName = componentName.getClassName();
            if (serviceName.equals(LocationService.class.getName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        createLocationRequest();



        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(1, AppUtils.createNotification(getApplicationContext(), null));
        } else {
            startForeground(1, AppUtils.createNotification(getApplicationContext(),null),FOREGROUND_SERVICE_TYPE_LOCATION);
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerBroadcastReceiver();
    }

    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setSmallestDisplacement(DISPLACEMENT);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    @Override

    public void onDestroy() {
        super.onDestroy();

        unRegisterReceiver();
        if (!SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.FORCE_STOP, getApplicationContext()))
            sendBroadcast(new Intent("restartLocationService"));
        else {

            AppUtils.stopXMPPService(LocationService.this);

            if (googleApiClient != null && googleApiClient.isConnected()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    } else {
                        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                    }
                } else {
                    LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                }
            }

        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }
        } else {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (googleApiClient != null && googleApiClient.isConnected())
            startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        startForeground(1, AppUtils.createNotification(getApplicationContext(), location.getLatitude() + "," + location.getLongitude()));


        SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCATION_PROVIDER, String.valueOf(location.getProvider()), getApplicationContext());
        SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCATION_TIME, String.valueOf(location.getTime()), getApplicationContext());
        SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCATION_ELAPSED_REAL_TIME_NANOS, String.valueOf(location.getElapsedRealtimeNanos()), getApplicationContext());
        SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCATION_LAT, String.valueOf(location.getLatitude()), getApplicationContext());
        SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCATION_LNG, String.valueOf(location.getLongitude()), getApplicationContext());

        if (location.hasAltitude())
            SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCATION_ALTITUDE, String.valueOf(location.getAltitude()), getApplicationContext());
        else
            SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCATION_ALTITUDE, "null", getApplicationContext());

        if (location.hasSpeed())
            SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCATION_SPEED, String.valueOf(location.getSpeed()), getApplicationContext());
        else
            SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCATION_SPEED, "null", getApplicationContext());
        if (location.hasBearing())
            SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCATION_BEARING, String.valueOf(location.getBearing()), getApplicationContext());
        else
            SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCATION_BEARING, "null", getApplicationContext());
        if (location.hasAccuracy())
            SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCATION_HORIZONTAL_ACCURACY_METERS, String.valueOf(location.getAccuracy()), getApplicationContext());
        else
            SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCATION_HORIZONTAL_ACCURACY_METERS, "null", getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (location.hasVerticalAccuracy())
                SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCATION_VERTICAL_ACCURACY_METERS, String.valueOf(location.getVerticalAccuracyMeters()), getApplicationContext());
            else
                SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCATION_VERTICAL_ACCURACY_METERS, "null", getApplicationContext());
            if (location.hasSpeedAccuracy())
                SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCATION_SPEED_ACCURACY_METERS_PER_SECONDS, String.valueOf(location.getSpeedAccuracyMetersPerSecond()), getApplicationContext());
            else
                SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCATION_SPEED_ACCURACY_METERS_PER_SECONDS, "null", getApplicationContext());
            if (location.hasBearingAccuracy())
                SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCATION_BEARING_ACCURACY_DEGREES, String.valueOf(location.getBearingAccuracyDegrees()), getApplicationContext());
            else
                SharedPreferenceHelper.saveString(SharedPreferenceHelper.LOCATION_BEARING_ACCURACY_DEGREES, "null", getApplicationContext());
        }
        SharedPreferenceHelper.saveString(SharedPreferenceHelper.TIMESTAMP, String.valueOf(System.currentTimeMillis()), getApplicationContext());
        Log.i("is between bounds",((location.getLatitude()>33.73&&location.getLatitude()<34.16)&&(location.getLongitude()>71.32&&location.getLongitude()<72.23))+"");
//        if((location.getLatitude()>33.73&&location.getLatitude()<34.16)&&(location.getLongitude()>71.32&&location.getLongitude()<72.23))
        pushLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));




    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    private void pushLocation(String lat, String lng) {

        if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, getApplicationContext())) {

            //System.out.println("connection : ------PushLocation when lat long changed");

            AppUtils.pushRiderStatsIfInternetWorking(getApplicationContext());

//            String time = SharedPreferenceHelper.getString(SharedPreferenceHelper.UPDATED_TIME, getApplicationContext());
//            String currentTime = TimeUtil.toString(Calendar.getInstance().getTime(),
//                    TimeUtil.DATE_TIME_FORMAT_12);

//            if (riderPushTime != null && !riderPushTime.equals("")) {
//                long current = TimeUtil.toDate(currentTime, TimeUtil.DATE_TIME_FORMAT_13).getTime();
//                long lastUpdated = TimeUtil.toDate(riderPushTime, TimeUtil.DATE_TIME_FORMAT_13).getTime();
//
//
//                if ((int) ((current / 1000) - (lastUpdated / 1000)) > 5) {
//                    SharedPreferenceHelper.saveString(SharedPreferenceHelper.RIDER_UPDATED_TIME, currentTime, getApplicationContext());
//                    sendRiderLocationNewApi();
//                }
//            } else {
//                SharedPreferenceHelper.saveString(SharedPreferenceHelper.RIDER_UPDATED_TIME, currentTime, getApplicationContext());
//                sendRiderLocationNewApi();
//            }

//            if (time != null && !time.equals("")) {
//                long current = TimeUtil.toDate(currentTime, TimeUtil.DATE_TIME_FORMAT_12).getTime();
//                long lastUpdated = TimeUtil.toDate(time, TimeUtil.DATE_TIME_FORMAT_12).getTime();
//
//                if ((int) ((current / 60000) - (lastUpdated / 60000)) > 0) {
//                    SharedPreferenceHelper.saveString(SharedPreferenceHelper.UPDATED_TIME, currentTime, getApplicationContext());
//                    sendLocation(lat, lng);
//                }
//            } else {
//                SharedPreferenceHelper.saveString(SharedPreferenceHelper.UPDATED_TIME, currentTime, getApplicationContext());
//                sendLocation(lat, lng);
//            }
        }

    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (!SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.FORCE_STOP, getApplicationContext())) {
            Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
            restartServiceIntent.setPackage(getPackageName());

            PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmService.set(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 1000,
                    restartServicePendingIntent);

            sendBroadcast(new Intent("restartLocationService"));
        }
        super.onTaskRemoved(rootIntent);

    }

    public void sendRiderLocationNewApi() {

        InitApi initApi = new InitApi() {
            @Override
            public HashMap<String, String> getBody() {

                return null;
            }

            @Override
            public HashMap<String, Object> getObjBody() {

                String orderId = SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, getApplicationContext());

                Log.i("orderIds**",orderId);

                HashMap<String, Object> userMap = AppUtils.getBatteryStats(getApplicationContext());//new HashMap<>();

                userMap.put("riderId", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, getApplicationContext()));
                userMap.put("accuracy", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_HORIZONTAL_ACCURACY_METERS, getApplicationContext()));
                userMap.put("appVersion", BuildConfig.VERSION_NAME);
                userMap.put("deviceBrand", SharedPreferenceHelper.getString(SharedPreferenceHelper.DEVICE_BRAND, getApplicationContext()));
                userMap.put("deviceModel", SharedPreferenceHelper.getString(SharedPreferenceHelper.DEVICE_MODEL, getApplicationContext()));
                userMap.put("deviceOSVersion", SharedPreferenceHelper.getString(SharedPreferenceHelper.DEVICE_OS_VERSION, getApplicationContext()));
                userMap.put("gpsStatus", AppUtils.checkGPSStatus(getApplicationContext()));
                userMap.put("isAvailable", SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, getApplicationContext()));
                if (TrueTimeRx.isInitialized())
                    userMap.put("lastStatsSynced", String.valueOf(TrueTimeRx.now().getTime()));
                else
                    userMap.put("lastStatsSynced", String.valueOf(System.currentTimeMillis()));
                userMap.put("lat", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LAT, getApplicationContext()));
                userMap.put("long", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LNG, getApplicationContext()));
                userMap.put("locationAltitude", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_ALTITUDE, getApplicationContext()));
                userMap.put("locationBearing", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_BEARING, getApplicationContext()));
                userMap.put("locationBearingAccuracyDegrees", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_BEARING_ACCURACY_DEGREES, getApplicationContext()));
                userMap.put("locationElapsedRealTImeNanos", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_ELAPSED_REAL_TIME_NANOS, getApplicationContext()));
                userMap.put("locationProvider", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_PROVIDER, getApplicationContext()));
                userMap.put("locationSpeed", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_SPEED, getApplicationContext()));
                userMap.put("locationSpeedAccuracyMeterPerSeconds", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_SPEED_ACCURACY_METERS_PER_SECONDS, getApplicationContext()));
                userMap.put("locationTime", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_TIME, getApplicationContext()));
                userMap.put("locationVerticalAccuracyMeters", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_VERTICAL_ACCURACY_METERS, getApplicationContext()));
                if (!orderId.equals("")) {
                    userMap.put("order", true);
                    userMap.put("orderCount", orderId.split(",").length);
                }
                else{
                    userMap.put("order", false);
                    userMap.put("orderCount", 0);
                }
                userMap.put("orderId", orderId);
                if (TrueTimeRx.isInitialized())
                    userMap.put("timestamp", String.valueOf(TrueTimeRx.now().getTime()));
                else
                    userMap.put("timestamp", SharedPreferenceHelper.getString(SharedPreferenceHelper.TIMESTAMP, getApplicationContext()));

                userMap.put("signalStrength", AppUtils.getSignalStrength(getApplicationContext()));

                return userMap;
            }

            @Override
            public HashMap<String, String> getHeader() {
                return AppUtils.getStandardHeaders(getApplicationContext());
            }
        };

        String saveRiderLocation = ApiCalls.saveRiderLocation();
        GenericHandler ordersListingHandler = new GenericHandler(getApplicationContext(), saveRiderLocation, this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), new JSONObject(initApi.getObjBody()), saveRiderLocation, Request.Method.POST,  getApplicationContext(),true, ordersListingHandler);
        jsonObjectRequestCall.sendData();
    }

    public void sendLocation(final String lat, final String lng) {

        InitApi initApi = new InitApi() {
            @Override
            public HashMap<String, String> getBody() {

                String orderId = SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, getApplicationContext());

                HashMap<String, String> body = new HashMap<>();
                body.put("RiderId", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, getApplicationContext()));
                body.put("Latitude", lat);
                body.put("Longitude", lng);
                if (TrueTimeRx.isInitialized())
                    body.put("timestamp", String.valueOf(TrueTimeRx.now().getTime()));
                else
                    body.put("timestamp", String.valueOf(System.currentTimeMillis()));
                if (!orderId.equals(""))
                    body.put("order", String.valueOf(true));
                else
                    body.put("order", String.valueOf(false));
                body.put("deviceBrand", SharedPreferenceHelper.getString(SharedPreferenceHelper.DEVICE_BRAND, getApplicationContext()));
                body.put("deviceModel", SharedPreferenceHelper.getString(SharedPreferenceHelper.DEVICE_MODEL, getApplicationContext()));
                body.put("deviceOSVersion", SharedPreferenceHelper.getString(SharedPreferenceHelper.DEVICE_OS_VERSION, getApplicationContext()));
                return body;
            }

            @Override
            public HashMap<String, Object> getObjBody() {
                return null;
            }

            @Override
            public HashMap<String, String> getHeader() {
                return AppUtils.getStandardHeaders(getApplicationContext());
            }
        };

        String saveOrderLocation = ApiCalls.saveOrderLocation();
        GenericHandler ordersListingHandler = new GenericHandler(getApplicationContext(), saveOrderLocation, this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), saveOrderLocation, Request.Method.POST, getApplicationContext(), ordersListingHandler);
        jsonObjectRequestCall.sendData();
    }

    @Override
    public void onSuccess(String calledApi) {

    }

    @Override
    public void onError(String calledApi) {

    }

    @Override
    public void onError(String calledApi, int errorCode) {
        if(errorCode == 403)
            AlertDialogUtils.showAlertDialog(getApplicationContext(), AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), getString(R.string.app_update), getString(R.string.ok), null, true);

    }

    @Override
    public void onSuccess(String calledApi, String response) {

    }

    @Override
    public void onError(String calledApi, String errorMessage) {

    }

    @Override
    public void onError(String calledApi, String errorMessage, int errorCode) {
        if(errorCode == 403)
            AlertDialogUtils.showAlertDialog(getApplicationContext(), AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), errorMessage, getString(R.string.ok), null, true);

    }

    public void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();

        // Add network connectivity change action.
        intentFilter.addAction("com.darewro.rider.view.receivers.alarmReceiverForRiderTrackingService");
//        intentFilter.addAction("android.intent.action.SCREEN_OFF");

        // Set broadcast receiver priority.
        intentFilter.setPriority(100);

        // Create a network change broadcast receiver.
        alarmReceiverForRiderTrackingService = new AlarmReceiverForRiderTrackingService();

        // Register the broadcast receiver with the intent filter object.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(alarmReceiverForRiderTrackingService, intentFilter, RECEIVER_EXPORTED);
            }else{
                registerReceiver(alarmReceiverForRiderTrackingService, intentFilter);
            }
        }
        else{
            registerReceiver(alarmReceiverForRiderTrackingService, intentFilter);
        }
    }

    public void unRegisterReceiver() {
        if (alarmReceiverForRiderTrackingService != null)
            unregisterReceiver(alarmReceiverForRiderTrackingService);
    }

//    public static void deleteAtPath(final String orderId, final Context ctxt)  {
//
//        InitApi initApi = new InitApi() {
//            @Override
//            public HashMap<String, String> getBody() {
//                return null;
//            }
//
//            @Override
//            public HashMap<String, Object> getObjBody() {
//                try {
//                    JSONObject obj = new JSONObject();
//                    obj.put("orderId", orderId);
//                    HashMap<String, Object> body = new HashMap<>();
//                    body.put("data", obj);
//
//                    return body;
//                }catch (JSONException ex){
//                    return null;
//                }
//            }
//
//            @Override
//            public HashMap<String, String> getHeader() {
//                return AppUtils.getGenericHeaders(ctxt);
//            }
//        };
//
//
//        JSONObject obj = new JSONObject();
//        try {
//            obj.put("data",new JSONObject().put("orderId", orderId));
//
//        }catch (JSONException ex){
//        }
//        String deleteOrder = ApiCalls.DELETE_ORDER;
//        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), obj, deleteOrder, Request.Method.POST, ctxt,true, null);
//        jsonObjectRequestCall.sendData();
//    }
}