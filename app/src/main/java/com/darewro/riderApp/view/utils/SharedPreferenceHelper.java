package com.darewro.riderApp.view.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.darewro.riderApp.data.api.models.User;
import com.darewro.riderApp.view.xmpp.XMPPUser;


/**
 * Created by Tsaeed on 3/30/2017.
 */

public class SharedPreferenceHelper {

    // public static String Token = "token";
    public static String FIRE_BASE_TOKEN = "firebaseToken";
    public static String API_TOKEN = "apiToken";

    public static String ID = "id";
    public static String USERNAME = "userName";
    public static String EMAIL = "email";
    public static String MSISDN = "msisdn";
    public static String PICTUREPATH = "picturePath";
    public static String DEVICETOKEN = "deviceToken";
    public static String UPDATED_TIME = "updatedTime";

    public static String LAST_LOCATION_PUSHED = "";
    //    public static String RIDER_ID = "riderId";
    public static String ORDER_ID = "orderId";
    public static String ORDER_USER_ID = "orderUserId";
    public static String FORCE_STOP = "forceStop";
    public static String PHONE = "phone";
    public static String CODE = "code";
    public static String UID = "uId";
    public static String HAS_PENDING_ORDERS = "hasPendingOrders";
    public static String IS_AVAILABLE = "isAvailable";
    public static String IS_LOGGED_IN = "isLoggedIn";
    public static String IS_ON_BOARDED = "isOnBoarded";
    public static String IS_LOCALE_SELECTED = "isLocaleSelected";
    public static String LOCALE_SELECTED = "localeSelected";
    public static String TIMESTAMP = "timestamp";
    public static String RIDER_STATUS = "rider_status";
    public static String RIDER_SHIFT_SETTING = "rider_shift_setting";
    public static String SHOULD_CONNECT_XMPP = "should_connect_new_xmpp";


    //Location Detail
    public static String LOCATION_PROVIDER = "lProvider";
    public static String LOCATION_TIME = "lTime";
    public static String LOCATION_ELAPSED_REAL_TIME_NANOS = "lElapsedRealtimeNanos";
    public static String LOCATION_LAT = "location_lat";
    public static String LOCATION_LNG = "location_lng";
    public static String LOCATION_ALTITUDE = "lAltitude";
    public static String LOCATION_SPEED = "lSpeed";
    public static String LOCATION_BEARING = "lBearing";
    public static String LOCATION_HORIZONTAL_ACCURACY_METERS = "lHorizontalAccuracyMeters";
    public static String LOCATION_VERTICAL_ACCURACY_METERS = "lVerticalAccuracyMeters";
    public static String LOCATION_SPEED_ACCURACY_METERS_PER_SECONDS = "lSpeedAccuracyMetersPerSecond";
    public static String LOCATION_BEARING_ACCURACY_DEGREES = "lBearingAccuracyDegrees";


    public static String DEVICE_BRAND = "device_brand";
    public static String DEVICE_MODEL = "device_model";
    public static String DEVICE_OS_VERSION = "device_os_version";
    public static String PREFRENCESNAME = "Darewro";
    private static String BIKENUMBER = "bikeNumber";
    private static String RATING = "rating";
    public static String IS_GPS_DIALOG_VISIBLE = "isGPSDialogVisible";

    public static void saveFloat(String key, float value, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFRENCESNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static float getFloat(String key, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFRENCESNAME, Context.MODE_PRIVATE);
//        return preferences.getString(key, "NaN");
        return preferences.getFloat(key, 0);
    }

    public static void saveString(String key, String value, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFRENCESNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(String key, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFRENCESNAME, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public static void saveBoolean(String key, boolean value, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFRENCESNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBoolean(String key, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFRENCESNAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public static void saveBooleanFile(String key, boolean value, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFRENCESNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void saveSyncBoolean(String key, boolean value, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFRENCESNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getSyncBoolean(String key, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFRENCESNAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, true);
    }

    public static void clearPrefrences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFRENCESNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().commit();
        editor.commit();
    }

    public static void setUser(User user, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFRENCESNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ID, user.getId());
        editor.putString(USERNAME, user.getUserName());
        editor.putString(EMAIL, user.getEmail());
        editor.putString(MSISDN, user.getMsisdn());
        editor.putString(PICTUREPATH, user.getPicturePath());
        editor.putString(DEVICETOKEN, user.getDeviceToken());
        editor.putString(BIKENUMBER, user.getBikeNumber());
        editor.putString(RATING, user.getRating());
        editor.putBoolean(RIDER_STATUS, user.isAvailable());
        editor.putString(RIDER_SHIFT_SETTING, user.getRiderShiftSettings());

//        String userID = user.getMsisdn().replace("+92","0");
//        String password = user.getMsisdn().substring(user.getMsisdn().length() - 4);
        SharedPreferenceHelper.saveString(XMPPUser.XMPP_ID, user.getId(), context);
        SharedPreferenceHelper.saveString(XMPPUser.XMPP_PASSWORD, user.getId(), context);

        Log.d("XMPP_ID",user.getId());
        editor.commit();
    }

    public static void removeKey(String key, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFRENCESNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.commit();
    }


    public static boolean has(String key, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFRENCESNAME, Context.MODE_PRIVATE);
        return preferences.contains(key);
    }

    public static User getUser(Context context) {
        User user = new User();
        user.setId(getString(ID, context));
        user.setUserName(getString(USERNAME, context));
        user.setEmail(getString(EMAIL, context));
        user.setMsisdn(getString(MSISDN, context));
        user.setPicturePath(getString(PICTUREPATH, context));
        user.setDeviceToken(getString(DEVICETOKEN, context));
        user.setBikeNumber(getString(BIKENUMBER, context));
        user.setRating(getString(RATING, context));
        user.setAvailable(getBoolean(RIDER_STATUS, context));
        user.setRiderShiftSettings(getString(RIDER_SHIFT_SETTING, context));

        return user;
    }
}