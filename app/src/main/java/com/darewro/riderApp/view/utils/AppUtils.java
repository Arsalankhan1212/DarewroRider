package com.darewro.riderApp.view.utils;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.media.RingtoneManager.TYPE_NOTIFICATION;
import static android.media.RingtoneManager.getDefaultUri;
import static com.darewro.riderApp.view.utils.AlertDialogUtils.makeAlertDialog;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.darewro.riderApp.App;
import com.darewro.riderApp.BuildConfig;
import com.darewro.riderApp.R;
import com.darewro.riderApp.data.api.ApiCalls;
import com.darewro.riderApp.data.api.Interface.InitApi;
import com.darewro.riderApp.data.api.handlers.GenericHandler;
import com.darewro.riderApp.data.api.requests.JsonObjectRequestCall;
import com.darewro.riderApp.data.db.model.ChatMessage;
import com.darewro.riderApp.data.db.model.OrderPath;
import com.darewro.riderApp.view.activities.LoginActivity;
import com.darewro.riderApp.view.customViews.ProgressBarDotted;
import com.darewro.riderApp.view.fragments.ToastFragmentManager;
import com.darewro.riderApp.view.listeners.AlertDialogResponseListener;
import com.darewro.riderApp.view.locationService.LocationService;
import com.darewro.riderApp.view.receivers.AlarmReceiver;
import com.darewro.riderApp.view.receivers.AlarmReceiverForRiderTrackingService;
import com.darewro.riderApp.view.xmpp.XmppClient;
import com.darewro.riderApp.view.xmpp.XmppService;
import com.instacart.library.truetime.TrueTimeRx;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;


public class AppUtils {


    public static final String M_CONNECTION_CHANNEL_ID = "9";
    public static final String M_SERVICE_CHANNEL_ID = "2";
    public static final String TEMP_FORMAT = "yyyyMMdd_HHmmss";
    public static final String LOCAL_TIME_FORMAT = "EE MMM dd hh:mm:ss ZZZZ yyyy";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String APP_NAME = "Darewro";
    public static final String HOUR = "HH";
    public static final String MIN = "mm";
    public static final String DAY = "dd";
    public static final String MONTH = "MM";
    public static final String YEAR = "yyyy";
    public static final String FORMAT1 = "yyyy-MM-dd HH:mm:ss.S";
    public static final String FORMAT2 = "yyyy-MM-dd HH:mm";
    public static final String FORMAT3 = "yyyy-MM-dd";
    public static final String FORMAT4 = "EEE MMM dd HH:mm:ss zzz yyyy";
    public static final String FORMAT5 = "hh:mm a";
    public static final String FORMAT6 = "HH:mm";
    public static final String FORMAT7 = "HH";
    public static final String FORMAT8 = "EEE";
    public static final String FORMAT9 = "dd, MMMM yyyy";
    public static final String FORMAT10 = "MMM dd, yyyy"; //   Nov 12, 2016
    public static final String FORMAT11 = "MM/dd/yyyy hh:mm a"; //    08/20/2018 6:13 PM
    public static final String FORMAT12 = "dd/MM/yyyy hh:mm a"; //    20/08/2018 6:13 PM
    public static final String FORMAT13 = "dd/MM/yyyy hh:mm:ss a"; //    20/08/2018 6:13 PM
    public static final String FORMAT14 = "yyyy-MM-dd HH:mm:ss"; //    20/08/2018 6:13
    public static final String FORMAT15 = "dd-MM-yyyy"; //    20/08/2018
    public static final String FORMAT16 = "dd-MM-yyyy hh:mm a"; //    20/08/2018 6:13 PM
    public static final String FORMAT17 = "hh:mm:ss a"; //    20/08/2018 6:13 PM
    private static final String CHANNEL_ID = "XMPP";
    //public static final String USERID = "6";
    private static String TAG = "AppUtils";
    private static Toast toast;
    private final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;

    public static Thread thread = null;

    public static int IMAGE = 1;
    public static int AUDIO = 0;

    public static File getDirectory() {
        String pathname = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + App.getInstance().getResources().getString(R.string.app_name);
        File sd = new File(pathname);

        if (!sd.exists()) // if there was no folder at this path , then create it .
        {
            sd.mkdirs();
        }
        return sd;
    }

    public static String getFileName(String filepath) {

        String fileName = filepath.replace(getDirectory().getAbsolutePath(), "");
        fileName = fileName.split(".")[0];

        return fileName;
    }

    public static String getFileExtension(String filepath) {

        String fileType = filepath.replace(getDirectory().getAbsolutePath(), "");
        fileType = fileType.split(".")[1];

        return "." + fileType;
    }

    public static String getFileName(int type) {
        String filepath = null;
        if (type == IMAGE) {
            String pathname = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + App.getInstance().getResources().getString(R.string.app_name);
            File sd = new File(pathname);

            if (!sd.exists()) // if there was no folder at this path , then create it .
            {
                sd.mkdirs();
            }

            String fileName = "image_" + getCurrentTimeStampInMilliSeconds() + ".jpg";
            filepath = pathname + "/" + fileName;
        } else if (type == AUDIO) {
            String audioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + App.getInstance().getResources().getString(R.string.app_name);
            File sd = new File(audioSavePathInDevice);

            if (!sd.exists()) // if there was no folder at this path , then create it .
            {
                sd.mkdirs();
            }

            String fileName = "audio_" + getCurrentTimeStampInMilliSeconds() + ".aac";
            filepath = audioSavePathInDevice + "/" + fileName;

        }
        return filepath;
    }


    public static String getUTCdatetimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }


    public static String generateNewMessageId() {
        return String.format("%05d-%04d-%04d-%04d%04d%04d", new Random().nextInt(100000), new Random().nextInt(10000), new Random().nextInt(10000), new Random().nextInt(10000), new Random().nextInt(10000), new Random().nextInt(10000));
    }

    public static void playsound(Context context) {
        Uri alarmSound = getDefaultUri(TYPE_NOTIFICATION);
        MediaPlayer mp = MediaPlayer.create(context, alarmSound);
        mp.start();
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
            ComponentName componentName = service.service;
            String serviceName = componentName.getClassName();
            if (serviceName.equals(serviceClass.getName())) {
                return true;
            }
        }
        return false;
    }

    public static String getDay(String date) {
        Calendar calendar = Calendar.getInstance();

        String[] datetime = date.split(" ");
        String[] ymd = datetime[0].split("-");
        Date currentdate = calendar.getTime();
        int year = Integer.parseInt(ymd[0]) - 1900;
        int month = Integer.parseInt(ymd[1]) - 1;
        int day = Integer.parseInt(ymd[2]);
        if (currentdate.getYear() == year && currentdate.getMonth() == month && currentdate.getDate() == day) {
            return "TODAY";
        }

        Date newdate = new Date();
        newdate.setYear(year);
        newdate.setMonth(month);
        newdate.setDate(day);
        Log.i("date1 ", String.valueOf(newdate.getTime()));
        calendar.setTime(newdate);
        Log.i("date2 ", String.valueOf(calendar.getTime()));

        String[] days = new String[]{"", "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};

        String d = days[calendar.get(Calendar.DAY_OF_WEEK)];
        return d;
    }

    /*public static String getDeviceId(Context context) {

        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();

        return deviceId;
    }*/


    public static void setMontserrat(View view) {

        Typeface montserrat = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            montserrat = ResourcesCompat.getFont(App.getInstance(), R.font.montserrat);
        } else {
            // do something for phones running an SDK before lollipop
            montserrat = Typeface.createFromAsset(App.getInstance().getAssets(), "fonts/Montserrat_Regular.ttf");
        }

        if (montserrat == null) {
            return;
        }

        if (view instanceof TextView)
            ((TextView) view).setTypeface(montserrat);
        else if (view instanceof AppCompatEditText)
            ((AppCompatEditText) view).setTypeface(montserrat);
        else if (view instanceof EditText)
            ((EditText) view).setTypeface(montserrat);
        else if (view instanceof Button)
            ((Button) view).setTypeface(montserrat);
        else if (view instanceof AutoCompleteTextView)
            ((AutoCompleteTextView) view).setTypeface(montserrat);
        else if (view instanceof RadioButton)
            ((RadioButton) view).setTypeface(montserrat);
        else if (view instanceof CheckBox)
            ((CheckBox) view).setTypeface(montserrat);
        else if (view instanceof androidx.appcompat.widget.SwitchCompat)
            ((androidx.appcompat.widget.SwitchCompat) view).setTypeface(montserrat);
        else if (view instanceof Switch)
            ((Switch) view).setTypeface(montserrat);


    }


    public static void setMontserratBold(View view) {
        Typeface montserratBold = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // API 26+
            montserratBold = ResourcesCompat.getFont(App.getInstance(), R.font.montserrat_bold);
        } else {
            montserratBold = Typeface.createFromAsset(App.getInstance().getAssets(), "fonts/Montserrat_Bold.ttf");
        }

        if (montserratBold == null) {
            return;
        }


        if (view instanceof TextView)
            ((TextView) view).setTypeface(montserratBold);
        else if (view instanceof AppCompatEditText)
            ((AppCompatEditText) view).setTypeface(montserratBold);
        else if (view instanceof EditText)
            ((EditText) view).setTypeface(montserratBold);
        else if (view instanceof Button)
            ((Button) view).setTypeface(montserratBold);
        else if (view instanceof AutoCompleteTextView)
            ((AutoCompleteTextView) view).setTypeface(montserratBold);
        else if (view instanceof RadioButton)
            ((RadioButton) view).setTypeface(montserratBold);
        else if (view instanceof CheckBox)
            ((CheckBox) view).setTypeface(montserratBold);
        else if (view instanceof androidx.appcompat.widget.SwitchCompat)
            ((androidx.appcompat.widget.SwitchCompat) view).setTypeface(montserratBold);
        else if (view instanceof Switch)
            ((Switch) view).setTypeface(montserratBold);

    }


    public static void checkBatteryOptimization(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (pm != null && !pm.isIgnoringBatteryOptimizations(App.getInstance().getPackageName())) {
                askIgnoreOptimization(context);
            } else {

            }
        } else {
        }
    }

    public static void askIgnoreOptimization(Context context) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + App.getInstance().getPackageName()));
            context.startActivity(intent);
        } else {
        }
    }

    public static void allowInBatteryEfficientMode(final Context context) {


        if (SharedPreferenceHelper.getString(SharedPreferenceHelper.DEVICE_BRAND, context).toLowerCase().contains("oppo")) {
        } else
            PowerSaverUtils.startPowerSaverIntent2(context);

      /*  try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent();
                String packageName = context.getPackageName();
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

                if (pm.isIgnoringBatteryOptimizations(packageName))
                    intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                else {
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                }
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if(SharedPreferenceHelper.getString(SharedPreferenceHelper.DEVICE_BRAND,context).toLowerCase().contains("huawei")){
                PowerSaverUtils.startPowerSaverIntent(context);
            }else{
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            Intent intent = new Intent();
            if (!pm.isIgnoringBatteryOptimizations(App.getInstance().getPackageName())) {
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                context.startActivity(intent);
                }
            }
        } else {
         */  // PowerSaverUtils.startPowerSaverIntent(context);
        // }

        /*
        if (!SharedPreferenceHelper.getBoolean("protected", context)) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent();
                if (pm.isIgnoringBatteryOptimizations(App.getInstance().getPackageName()))
                    intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                else {
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + App.getInstance().getPackageName()));
                }
                context.startActivity(intent);
            } else {
                if ("huawei".equalsIgnoreCase(android.os.Build.MANUFACTURER) && !SharedPreferenceHelper.getBoolean("protected", context)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.huawei_headline).setMessage(R.string.huawei_text)
                            .setPositiveButton(R.string.go_to_protected, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent();
                                    intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                                    context.startActivity(intent);
                                    SharedPreferenceHelper.saveBoolean("protected", true, context);
                                }
                            }).create().show();
                }
            }
        }
*/
    }

    public static void hideKeyboard(Activity activity) {
        try {
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            Window window = activity.getWindow();
            if (window != null) {
                window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static int getScreenWidth(Activity activity) {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int widthPixels = displaymetrics.widthPixels;
        return widthPixels;
    }

    public static String getCurrentTimeStamp() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
            return dateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static Date getDateStamp(String date) {
        DateFormat format = new SimpleDateFormat(FORMAT9, Locale.ENGLISH);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long getCurrentTimeStampInMilliSeconds() {
        return System.currentTimeMillis();
    }

    public static void deleteFolder(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles())
                deleteFolder(child);
        }
        fileOrDirectory.delete();
//        UpdateAndroidGallery();
    }

    public static String getDefaultTimeZoneDateTime(String datetime) {

        String scheduleTime = datetime;

        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();

        SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        readDate.setTimeZone(TimeZone.getTimeZone("GMT")); // missing line
        Date date2 = null;
        try {
            date2 = readDate.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat writeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        writeDate.setTimeZone(new SimpleTimeZone(mGMTOffset, mTimeZone.getID()));
        scheduleTime = writeDate.format(date2);

        Log.i("Date2 = ", date2.toString());
        Log.i("scheduledTime = ", scheduleTime);

        return scheduleTime;
    }

    public static String getDateWithMonthInInteger(String date) {

        if (date == null || date.isEmpty()) {
            return "";
        }
        Log.e("date = ", date + ">>");
        date = date.replace(",", "");
        date = date.replace(".", "");
        date = date.replace(" ", "-");
        String[] datearray = date.split("-");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat("MMM").parse(datearray[1]));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int monthInt = cal.get(Calendar.MONTH) + 1;
        String day = Integer.parseInt(datearray[0]) < 10 ? "0" + datearray[0] : datearray[0];
        String mon = monthInt < 10 ? "0" + monthInt : String.valueOf(monthInt);
//        String newdate = day + "-" + mon + "-" + datearray[2];
        String newdate = datearray[2] + "-" + mon + "-" + day;

        return newdate;
    }

    public static String getConvertedDateFromOneFormatToOther(String date, String currentFormat, String requiredFormat) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat(currentFormat);
            SimpleDateFormat sdf2 = new SimpleDateFormat(requiredFormat);
            Date c = null;
            c = sdf.parse(date);
            String date1 = sdf2.format(c);
//            System.out.println(date1);
            return date1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static Date getDateFromString(String date, String currentFormat) {
        if (date.contains("T")) {
            date = date.replace('T', ' ');
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(currentFormat);
            Date c = null;
            c = sdf.parse(date);
            return c;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static Date getDateFromString2(String date) {
        if (date.contains("T")) {
            date = date.replace('T', ' ');
        }
        try {

            SimpleDateFormat sdf = new SimpleDateFormat();
            if (DATE_FORMAT.contains(".")) {
                sdf = new SimpleDateFormat(FORMAT1);
            } else {
                sdf = new SimpleDateFormat(FORMAT14);
            }
            Date c = null;
            c = sdf.parse(date);
            return c;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String getStringFromDate(Date date, String currentFormat) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(currentFormat);
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    public static String getCurrentTimeStampNoSeconds() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return dateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static String getCurrentTimeOfDay() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            return dateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static String getCurrentTimeOnlyWithSeconds() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            return dateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static String getCurrentDate() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static String getCurrentDate_dd_mm_yyyy() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            return dateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static String getCurrentTimeStamp(String outputFormat) {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat(outputFormat);

            return dateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static String getSha1Hex(String clearString) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(clearString.getBytes());
            byte[] bytes = messageDigest.digest();
            StringBuilder buffer = new StringBuilder();
            for (byte b : bytes) {
                buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return buffer.toString();
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return null;
        }
    }

    // Coverts a bitmap into a base64 image.
    public static String getBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static String getDeviceDetails() {
        return (Build.MANUFACTURER + " -> " + Build.MODEL + " -> "
                + Build.VERSION.RELEASE);
    }

    public static boolean checkNetworkState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void makeNotificationToast(String notification, Activity activity) {
        toast = new Toast(activity);
        toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);

        View myToast = activity.getLayoutInflater().inflate(R.layout.toast_layout,
                (ViewGroup) activity.findViewById(R.id.toastParent_new), true);
        TextView textView = myToast.findViewById(R.id.toast_text);
        textView.setText(notification);
        toast.setView(myToast);
        toast.show();

    }

    public static void hideNotificationToast() {
        if (toast != null)
            toast.cancel();
    }

    public static void makeNotification(String notification, Activity activity) {
        ToastFragmentManager manager = ToastFragmentManager.getInstance(activity);
        manager.showToast(notification);
    }



   /* public static void makeNotification(String notification, Context activity) {
        ToastFragmentManager manager = ToastFragmentManager.getInstance(activity);
        manager.showToast(notification);
    }*/

    public static void makeNotification(String notification, Activity activity, int icon) {
        try {
            ToastFragmentManager manager = ToastFragmentManager.getInstance(activity);
            manager.showToast(notification);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void apiSpecificTasks(Activity activity) {
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {

            activity.getWindow().addFlags(Window.FEATURE_NO_TITLE);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        } else {

        }
    }

    public static int getStatusBarHeight(Activity activity) {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean checkEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean checkPassword(String password) {
        if (password.length() >= 8) {
            for (int x = 0; x < password.length(); x++) {
                if (Character.isDigit(password.charAt(x))) {
                    return true;
                }

            }
            return false;
        } else {
            return false;
        }
    }


    public static void justify(final TextView textView) {

        final AtomicBoolean isJustify = new AtomicBoolean(false);

        final String textString = textView.getText().toString();

        final TextPaint textPaint = textView.getPaint();

        final SpannableStringBuilder builder = new SpannableStringBuilder();

        textView.post(new Runnable() {
            @Override
            public void run() {

                if (!isJustify.get()) {

                    final int lineCount = textView.getLineCount();
                    final int textViewWidth = textView.getWidth();

                    for (int i = 0; i < lineCount; i++) {

                        int lineStart = textView.getLayout().getLineStart(i);
                        int lineEnd = textView.getLayout().getLineEnd(i);

                        String lineString = textString.substring(lineStart, lineEnd);

                        if (i == lineCount - 1) {
                            builder.append(new SpannableString(lineString));
                            break;
                        }

                        String trimSpaceText = lineString.trim();
                        String removeSpaceText = lineString.replaceAll(" ", "");

                        float removeSpaceWidth = textPaint.measureText(removeSpaceText);
                        float spaceCount = trimSpaceText.length() - removeSpaceText.length();

                        float eachSpaceWidth = (textViewWidth - removeSpaceWidth) / spaceCount;

                        SpannableString spannableString = new SpannableString(lineString);
                        for (int j = 0; j < trimSpaceText.length(); j++) {
                            char c = trimSpaceText.charAt(j);
                            if (c == ' ') {
                                Drawable drawable = new ColorDrawable(0x00ffffff);
                                drawable.setBounds(0, 0, (int) eachSpaceWidth, 0);
                                ImageSpan span = new ImageSpan(drawable);
                                spannableString.setSpan(span, j, j + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }

                        builder.append(spannableString);
                    }

                    textView.setText(builder);
                    isJustify.set(true);
                }
            }
        });
    }


    public static void backButtonPress(AppCompatActivity activity) {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(startMain);
        activity.overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    public static void setRippleAnimation(Context context, View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StateListAnimator stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.lift_on_touch);
            view.setStateListAnimator(stateListAnimator);
        }
    }

    /*public static void refreshCartView(AppCompatActivity appCompatActivity) {
//        CartStickyLayout fragment = new CartStickyLayout();*//* CartStickyLayout.getINSTANCE();*//*

        Log.i("refreshCartView", appCompatActivity.getClass().getName());
        CartStickyLayout fragment = (CartStickyLayout) appCompatActivity.getSupportFragmentManager().findFragmentByTag(appCompatActivity.getClass().getName());

        if (DBUtils.getCartItemSize() > 0) {
            if (fragment != null) {
                fragment.updateValues();
            } else {
                fragment = new CartStickyLayout();
                fragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog_NoTitle_Cart);
                fragment.show(appCompatActivity.getSupportFragmentManager(), appCompatActivity.getClass().getName());
                setMargins(((ViewGroup) appCompatActivity.getWindow().getDecorView().findViewById(android.R.id.content)), 0, 0, 0, (int) Math.ceil(dp2px(appCompatActivity.getResources(), 48f)));
            }
        } else {
            if (fragment != null) {
                appCompatActivity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                setMargins(((ViewGroup) appCompatActivity.getWindow().getDecorView().getRootView().findViewById(android.R.id.content)), 0, 0, 0, 0);
            }

        }
    }*/

   /* public static void checkIfInCart(List<PartnerCatalog> partnerCatalogs) {

        for (PartnerCatalog partnerCatalog : partnerCatalogs) {
            for (Product product : partnerCatalog.getProducts()) {
                CartItems cartItems = DBUtils.getCartItem(product.getId());
                if (cartItems != null) {
                    product.setInCart(true);
                    product.setCartQuantity(Integer.parseInt(cartItems.getRequiredQuantity()));
                } else {
                    product.setInCart(false);
                    product.setCartQuantity(0);
                }
            }
            checkIfInCart(partnerCatalog.getChildMenus());
        }
    }*/

    public static void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public static void setPersistentLocale(Locale l, Context context) {
        SharedPreferences languagePrefs = context.getSharedPreferences(context.getString(R.string.localename), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = languagePrefs.edit();
        editor.clear();
        editor.putString("locale", l.getLanguage());
        editor.apply();
    }

    public static Locale getPersistentLocale(Context context) {
        SharedPreferences languagePrefs = context.getSharedPreferences(context.getString(R.string.localename), Context.MODE_PRIVATE);
        if (languagePrefs.contains("locale")) {
            String locale = languagePrefs.getString("locale", "");
            Locale l = new Locale(locale);
            AppWideVariables.locale = l;
            Locale.setDefault(l);
            return l;
        } else return null;

    }

    public static void setText(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setText("---");
        } else {
            textView.setText(text);
        }
    }

    public static HashMap<String, String> getStandardHeaders(Context context) {
        HashMap<String, String> map = new HashMap<String, String>();
        String token = SharedPreferenceHelper.getString(SharedPreferenceHelper.API_TOKEN, context);
        map.put("Authorization", "Bearer " + token);//"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1bmlxdWVfbmFtZSI6Im1hc2doYXJAbG1rdC5jb20iLCJuYmYiOjE1NDI4NjUzMjAsImV4cCI6MTU0Mjg2NjUyMCwiaWF0IjoxNTQyODY1MzIwLCJpc3MiOiJMTUtULkFQSS5JZGVudGl0eSIsImF1ZCI6IkRhcmV3cm9Vc2VycyJ9.ksYpm343vsCxyhNSGW8Rk9_lJjarf5fcbaCF4-2jxhk");
        map.put("Content-Type", "application/json");
//        map.put("Content-Type", "application/x-www-form-urlencoded");
        Log.d("TAG111","token api= "+token);
        return map;
    }

    public static HashMap<String, String> getFileStandardHeaders(Context context) {
        HashMap<String, String> map = new HashMap<String, String>();
        String token = SharedPreferenceHelper.getString(SharedPreferenceHelper.API_TOKEN, context);
        map.put("Authorization", "Bearer " + token);//"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1bmlxdWVfbmFtZSI6Im1hc2doYXJAbG1rdC5jb20iLCJuYmYiOjE1NDI4NjUzMjAsImV4cCI6MTU0Mjg2NjUyMCwiaWF0IjoxNTQyODY1MzIwLCJpc3MiOiJMTUtULkFQSS5JZGVudGl0eSIsImF1ZCI6IkRhcmV3cm9Vc2VycyJ9.ksYpm343vsCxyhNSGW8Rk9_lJjarf5fcbaCF4-2jxhk");
        //map.put("Content-Type", "application/json");
//        map.put("Content-Type", "application/x-www-form-urlencoded");
        return map;
    }

    public static HashMap<String, String> getGenericHeaders(Context context) {
        HashMap<String, String> map = new HashMap<String, String>();
        String token = SharedPreferenceHelper.getString(SharedPreferenceHelper.API_TOKEN, context);
//        map.put("Authorization", "Bearer " + token);//"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1bmlxdWVfbmFtZSI6Im1hc2doYXJAbG1rdC5jb20iLCJuYmYiOjE1NDI4NjUzMjAsImV4cCI6MTU0Mjg2NjUyMCwiaWF0IjoxNTQyODY1MzIwLCJpc3MiOiJMTUtULkFQSS5JZGVudGl0eSIsImF1ZCI6IkRhcmV3cm9Vc2VycyJ9.ksYpm343vsCxyhNSGW8Rk9_lJjarf5fcbaCF4-2jxhk");
        map.put("Content-Type", "application/json");
//        map.put("Content-Type", "application/x-www-form-urlencoded");
        return map;
    }

    public static String readStream(InputStream stream) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(stream));

            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            return stringBuilder.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public static void writeFile(byte[] data, File file) throws IOException {

        BufferedOutputStream bos = null;

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(data);
        } finally {
            if (bos != null) {
                try {
                    bos.flush();
                    bos.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static byte[] getFileContents(File file) {

        byte[] data = null;
        try {
            RandomAccessFile f = new RandomAccessFile(file.getPath(), "r");
            data = new byte[(int) f.length()];
            f.read(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String getFilePathFromUri(Activity activity, Uri uri) {
        String path = "";
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return path;
    }

    public static void viewImage(Context context, String imagePath) {
        try {
            File file = new File(imagePath);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "image/*");
            context.startActivity(intent);
        } catch (Throwable t) {

        }
    }

    public static boolean checkExternalStorageState() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static void loadImage(final Context context, final ImageView imageView, final ProgressBarDotted progresBar, String url) {

        Log.i("imvWidth = ", imageView.getMeasuredWidth() + " ........");
        Log.i("imvHeight = ", imageView.getMeasuredHeight() + " .......");

//        ImageUtils imageUtils = getResizedImageUtils(context,imageView.getMeasuredWidth(),imageView.getMeasuredHeight());

        if (!TextUtils.isEmpty(url) && url != null) {
            progresBar.setVisibility(View.VISIBLE);
            progresBar.startProgress();
            Glide.with((AppCompatActivity) context)
                    .load(url)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            imageView.setBackground(context.getResources().getDrawable(R.drawable.no_image));
                            progresBar.stopProgress();
                            progresBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progresBar.stopProgress();
                            progresBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .apply(new RequestOptions().timeout(30000)/*.override(imageUtils.getWidth(),imageUtils.getHeight())*/)
                    .into(imageView);
        }
    }

    public static void loadPicture(final Context context, final ImageView imageView, final ProgressBarDotted progresBar, String url) {

        Log.i("imvWidth = ", imageView.getMeasuredWidth() + " ........");
        Log.i("imvHeight = ", imageView.getMeasuredHeight() + " .......");

//        ImageUtils imageUtils = getResizedImageUtilsPortrait(context,imageView.getMeasuredWidth(),0);

       /* if(url.endsWith("null"))
        {
            imageView.setBackground(context.getResources().getDrawable(R.drawable.no_image));
            progresBar.stopProgress();
            progresBar.setVisibility(View.GONE);
            return;
        }*/

        if (!TextUtils.isEmpty(url) && url != null) {
            progresBar.setVisibility(View.VISIBLE);
            progresBar.startProgress();
            Glide.with((AppCompatActivity) context)
                    .load(url)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            imageView.setBackground(context.getResources().getDrawable(R.drawable.no_image));
                            if (progresBar != null) {
                                progresBar.stopProgress();
                                progresBar.setVisibility(View.GONE);
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (progresBar != null) {
                                progresBar.stopProgress();
                                progresBar.setVisibility(View.GONE);
                            }
                            return false;
                        }
                    })
                    .apply(new RequestOptions().timeout(30000).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)/*.override(imageUtils.getWidth(),imageUtils.getHeight())*/)
                    .into(imageView);
        }
    }


    public static void loadPicture(final Context context, final ImageView imageView, String picture) {
        try {
            if (!TextUtils.isEmpty(picture)) {
                RequestOptions ro = new RequestOptions();
                //ro.placeholder(R.drawable.no_image);
                ro.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                Glide.with(context).applyDefaultRequestOptions(ro).asBitmap().load(picture).into(imageView);
            } else {
                // log("AppUtils", "picture is null or empty!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File getImageStoringFile() {
        String timestamp = System.currentTimeMillis() / 1000 + ".jpeg";

        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), timestamp);
    }

    public static int getColor(int resId) {
        int color = 0;
        try {
            color = Resources.getSystem().getColor(resId);
        } catch (Exception e) {
        }

        return color;
    }

    public static String getString(int resId) {
        String s = "";
        try {
            s = App.getInstance().getString(resId);
        } catch (Exception e) {

        }

        return s;
    }

    public static List<String> getCameraImages(Context context) {

        String sort = MediaStore.Images.ImageColumns.DATE_MODIFIED/*_TAKEN*//*ADDED*/ + " DESC";
        final String[] projection = {MediaStore.Images.Media.DATA};
        final Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, sort);
        ArrayList<String> result = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            do {
                final String data = cursor.getString(dataColumn);
                result.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public static void UpdateAndroidGallery(Context context) {
        File file = new File(getAppDirectory(context).getPath());
        file.delete();
        if (file.exists()) {
            try {
                file.getCanonicalFile().delete();
                context.getApplicationContext().deleteFile(file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getCurrentUtcTime() {

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    public static Date getCurrentDateTime() {

        String currentUtcTime = getCurrentUtcTime();
        DateFormat inputFormatter1 = new SimpleDateFormat(DATE_FORMAT);
        Date date1 = null;
        try {
            date1 = inputFormatter1.parse(currentUtcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }

    public static File getAppDirectory(Context context) {

        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, /*"Pictures/"+*/APP_NAME);
        if (!folder.exists()) {
            Log.d("Directory", "mkdirs():" + folder.mkdirs());
        }
        return folder;
    }

    public static byte[] getImageBytes(String path) {
        File fileName = new File(path);
        byte[] bytes;
        byte[] buffer = new byte[512];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            InputStream inputStream = new FileInputStream(fileName);
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bytes = output.toByteArray();
        return bytes;
    }

    public static byte[] getJpegConvertedImageBytes(String path, String type) {
        File imagefile = new File(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes;
        byte[] buffer = new byte[512];
        int bytesRead;
        if (type.equalsIgnoreCase("img")) {
            Log.i("image", "image");
            FileInputStream fis = null;

            try {
                fis = new FileInputStream(imagefile);
                Bitmap bm = BitmapFactory.decodeStream(fis);
                bm.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (type.equalsIgnoreCase("vid")) {
            Log.i("video", "video");
            try {
                InputStream inputStream = new FileInputStream(imagefile);
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bytes = baos.toByteArray();
//        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return bytes;
    }

    public static String getFormattedLocalTimeFromUtc(String utcTimeStamp, String outputFormat) {

        String formattedTime = null;
        if (!TextUtils.isEmpty(utcTimeStamp)) {

            String localTime = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date localDateTime = null;
            try {
                localDateTime = sdf.parse(utcTimeStamp);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            DateFormat outputDateFormat1 = new SimpleDateFormat(outputFormat);
            localTime = outputDateFormat1.format(localDateTime);

            DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            inputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = null;
            try {
                date = inputDateFormat.parse(localTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            DateFormat outputDateFormat = new SimpleDateFormat(outputFormat);
            formattedTime = outputDateFormat.format(date);
            Log.d("TIME", formattedTime);
        }
        return formattedTime;
    }

    @Nullable
    public static String formatDateTime(String dateTime) {
        try {
            if (!TextUtils.isEmpty(dateTime) && dateTime.contains(" ")) {
                String date = dateTime.split(" ")[0];
                String time = dateTime.split(" ")[1];

                if (!TextUtils.isEmpty(date) && !TextUtils.isEmpty(time)) {
                    if (time.contains(":")) {
                        try {
                            int hr = Integer.parseInt(time.split(":")[0].trim());
                            int min = Integer.parseInt(time.split(":")[1].trim());
                            DecimalFormat df = new DecimalFormat("00");
                            time = String.format("%s:%s", df.format(hr), df.format(min));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                dateTime = String.format("%s %s", date, time);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return dateTime;
    }

    public static float dp2px(Resources resources, float dp) {

        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float dp2px(Resources resources, int dimenId) {

        final float scale = resources.getDisplayMetrics().density;
        return resources.getDimension(dimenId) * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp) {

        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public static float pixelsToSp(Context context, float px) {

        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    public static void log(Class a, String msg) {
        Log.e(" >>> " + a.getName(), msg);
    }

    public static void log(String tag, String msg) {
        Log.e(" >>> " + tag, msg);
    }

    public static void loadWithGlide(Context c, String imgPath, ImageView iv) {
        if (TextUtils.isEmpty(imgPath)) {
            log(TAG, "loadWithGlide: imgPath is null!");
            return;
        }
        try {
            RequestOptions ro = new RequestOptions();
            ro.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
            Glide.with(c).applyDefaultRequestOptions(ro).load(imgPath).into(iv);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static Bitmap getBlurredScreenShot(Activity activity) {
        final View content = activity.findViewById(R.id.layout).getRootView();
        if (content != null) {
            final Bitmap[] image = new Bitmap[1];
            if (content.getWidth() > 0) {
                image[0] = BlurBuilder.blur(content);
//            window.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), image));
            } else {
                content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        image[0] = BlurBuilder.blur(content);
//                    window.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), image));
                    }
                });
            }
//            changeBitmapColor(activity,image[0]);
            return image[0];
        }
        return null;
    }

    public static boolean compareDates(String oldDate, String compareDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date Date1 = null;
        try {
            Date1 = sdf.parse(oldDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date Date2 = null;
        try {
            Date2 = sdf.parse(compareDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Date2.after(Date1);
    }

    public static int sortDates(String oldDate, String compareDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date Date1 = null;
        try {
            Date1 = sdf.parse(oldDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date Date2 = null;
        try {
            Date2 = sdf.parse(compareDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (Date2.after(Date1)) {
            return 1;
        } else if (Date1.after(Date2)) {
            return -1;
        }
        return 0;
    }

    public static String getUTF8EncodedString(String text) {
        try {
            String str = Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
            Log.i("text = ", text);
            Log.i("encoded text = ", str);
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    public static void switchActivity(Context context, Class<?> activityClass, Bundle bundle) {

        Intent intent = new Intent(context, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (bundle != null) {
            intent.putExtras(bundle);
        }

        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

    }

    public static void switchActivityforResult(AppCompatActivity context, Class<?> activityClass, Bundle bundle, int requestCode) {

        Intent intent = new Intent(context, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (bundle != null) {
            intent.putExtras(bundle);
        }
        hideNotification(context);

        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

    }

    public static void hideNotification(Activity activity) {
        ToastFragmentManager manager = ToastFragmentManager.getInstance(activity);
        manager.hideToast();
    }

    public static String getUTF8DecodedString(String text) {
        try {
            String str = new String(Base64.decode(text.getBytes(), Base64.DEFAULT));
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    public static void setUnderligned(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public static void setUnderligned(EditText editText) {
        editText.setPaintFlags(editText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public static void setUnderligned(Button button) {
        button.setPaintFlags(button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public static void showView(TextView view) {
        view.setVisibility(View.VISIBLE);
    }

    public static void hideView(TextView view) {
        view.setVisibility(View.GONE);
    }

    public static String getDate(String dateTimePlacement) {
        if (dateTimePlacement.contains("T")) {
            dateTimePlacement = dateTimePlacement.replace('T', ' ');
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT1);
            SimpleDateFormat sdf2 = new SimpleDateFormat(FORMAT9);
            Date c = null;
            c = dateFormat.parse(dateTimePlacement);
            String date1 = sdf2.format(c);
//            System.out.println(date1);
            return date1;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static String getTime(String dateTimePlacement) {
        if (dateTimePlacement.contains("T")) {
            dateTimePlacement = dateTimePlacement.replace('T', ' ');
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT1);
            SimpleDateFormat sdf2 = new SimpleDateFormat(FORMAT5);
            Date c = null;
            c = dateFormat.parse(dateTimePlacement);
            String date1 = sdf2.format(c);
//            System.out.println(date1);
            return date1;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static void showNoLayout(String noLayoutText, TextView noLayout, View dataLayout) {
        dataLayout.setVisibility(View.INVISIBLE);
        noLayout.setText(noLayoutText);
        noLayout.setVisibility(View.VISIBLE);
    }

    public static void hideNoLayout(TextView noLayout, View dataLayout) {
        dataLayout.setVisibility(View.VISIBLE);
        noLayout.setVisibility(View.INVISIBLE);
    }

    public static String getFormattedPhoneNumber(String msisdn) {
        String phone = msisdn;

        if (!TextUtils.isEmpty(msisdn)) {
            if (msisdn.length() == 13 && msisdn.startsWith("+92")) {
                phone = msisdn;
            } else if (msisdn.length() == 14 && msisdn.startsWith("0092")) {
                phone = msisdn.replaceFirst("0092", "+92");
            } else if (msisdn.length() == 11 && msisdn.startsWith("03")) {
                phone = msisdn.replaceFirst("0", "+92");
            }
        }
        return phone;
    }

    public static void promptGpsAlert(final Context context) {
        android.app.AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle("GPS Disabled");
        alert.setMessage("Please Enable GPS/Location Services from phone settings");
        alert.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ((Activity) context).finish();
            }
        });
        alert.setCancelable(false);
        Dialog d = alert.create();

        if (!d.isShowing()) {
            d.show();
        }
    }

    public static void checkGPS(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //Start your Activity if location was enabled:

        if (!isGpsEnabled) {
            if (!SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_GPS_DIALOG_VISIBLE, context)) {
                SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.IS_GPS_DIALOG_VISIBLE, true, context);
                AlertDialogUtils.showConfirmationAlertDialog(context, AlertDialogUtils.ALERT_DIALOG_GPS, context.getResources().getString(R.string.gps), context.getResources().getString(R.string.gps_warning), context.getResources().getString(R.string.proceed), context.getResources().getString(R.string.cancel), (AlertDialogResponseListener) context);
            }
        }
    }

    public static boolean checkGPSStatus(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return isGpsEnabled;
    }

//    public static void pushRiderStats(final Context context) {
//
//
//        final String riderId = SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, context);
//        final String orderId = SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, context);
//
//        Map<String, Object> userMap = getBatteryStats(context);//new HashMap<>();
//        userMap.put("locationProvider", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_PROVIDER, context));
//        userMap.put("locationTime", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_TIME, context));
//        userMap.put("locationElapsedRealTImeNanos", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_ELAPSED_REAL_TIME_NANOS, context));
//        userMap.put("locationAltitude", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_ALTITUDE, context));
//        userMap.put("locationSpeed", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_SPEED, context));
//        userMap.put("locationBearing", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_BEARING, context));
//        userMap.put("locationVerticalAccuracyMeters", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_VERTICAL_ACCURACY_METERS, context));
//        userMap.put("locationSpeedAccuracyMeterPerSeconds", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_SPEED_ACCURACY_METERS_PER_SECONDS, context));
//        userMap.put("locationBearingAccuracyDegrees", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_BEARING_ACCURACY_DEGREES, context));
//        userMap.put("lat", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LAT, context));
//        userMap.put("long", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LNG, context));
//        userMap.put("isAvailable", SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, context));
//        userMap.put("accuracy", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_HORIZONTAL_ACCURACY_METERS, context));
//
//
//        if (!orderId.equals(""))
//            userMap.put("order", true);
//        else
//            userMap.put("order", false);
//
//
//        userMap.put("orderId", orderId);
//
//
//        if (TrueTimeRx.isInitialized())
//            userMap.put("timestamp", String.valueOf(TrueTimeRx.now().getTime()));
//        else
//            userMap.put("timestamp", SharedPreferenceHelper.getString(SharedPreferenceHelper.TIMESTAMP, context));
//
//        if (TrueTimeRx.isInitialized())
//            userMap.put("lastStatsSynced", String.valueOf(TrueTimeRx.now().getTime()));
//        else
//            userMap.put("lastStatsSynced", String.valueOf(System.currentTimeMillis()));
//        userMap.put("deviceBrand", SharedPreferenceHelper.getString(SharedPreferenceHelper.DEVICE_BRAND, context));
//        userMap.put("deviceModel", SharedPreferenceHelper.getString(SharedPreferenceHelper.DEVICE_MODEL, context));
//        userMap.put("deviceOSVersion", SharedPreferenceHelper.getString(SharedPreferenceHelper.DEVICE_OS_VERSION, context));
//        userMap.put("signalStrength", getSignalStrength(context));
//        userMap.put("gpsStatus", checkGPSStatus(context));
//        userMap.put("appVersion", BuildConfig.VERSION_NAME);
//
//        Log.i("appVersion", BuildConfig.VERSION_NAME);
//
//        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//        firebaseFirestore.clearPersistence();
//
//        if (!riderId.equals("")) {
//            firebaseFirestore.collection(ApiCalls.FIREBASE_KEY_RIDERS).document(riderId).set(userMap);
//        }
//
//    }

    public static void stopService(Context context) {
        SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.FORCE_STOP, true, context);
        Intent intent = new Intent(context, LocationService.class);
        if (LocationService.isRunning(context)) {
            context.stopService(intent);
        }
    }

    public static HashMap<String, Object> getBatteryStats(Context context) {
        HashMap<String, Object> map = new HashMap();

        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        int percent = (level * 100) / scale;

        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
            map.put("chargingState", true);
        } else {
            map.put("chargingState", false);
        }

        map.put("batteryLevel", percent + "%");

        return map;
    }

    public static HashMap<String, Object> getBatteryStatsChanged(Context context) {
        HashMap<String, Object> map = new HashMap();

        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        int percent = (level * 100) / scale;

        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
            map.put("chargingState", String.valueOf(true));
        } else {
            map.put("chargingState", false);
        }

        map.put("batteryLevel", percent + "%");

        return map;
    }

    public static String getSignalStrength(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String strength = "";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();   //This will give info of all sims present inside your mobile
        if (cellInfos != null) {
            for (int i = 0; i < cellInfos.size(); i++) {
                if (cellInfos.get(i).isRegistered()) {

                    if (cellInfos.get(i) instanceof CellInfoWcdma) {
                        CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) telephonyManager.getAllCellInfo().get(i);
                        CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                        strength = String.valueOf(cellSignalStrengthWcdma.getLevel());//.getDbm());
                    } else if (cellInfos.get(i) instanceof CellInfoGsm) {
                        CellInfoGsm cellInfogsm = (CellInfoGsm) telephonyManager.getAllCellInfo().get(i);
                        CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();
                        strength = String.valueOf(cellSignalStrengthGsm.getLevel());
                    } else if (cellInfos.get(i) instanceof CellInfoLte) {
                        CellInfoLte cellInfoLte = (CellInfoLte) telephonyManager.getAllCellInfo().get(i);
                        CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                        strength = String.valueOf(cellSignalStrengthLte.getLevel());
                    }
                }
            }
        }
        return strength;
    }

    public static void addLatLngInDB(String orderId, String lat, String lng, boolean isRepeated) {
        final OrderPath orderPath = new OrderPath();
        orderPath.setTimeStamp(getCurrentTimeStampInMilliSeconds());
        orderPath.setOrderID(orderId);
        orderPath.setLat(lat);
        orderPath.setLng(lng);
        orderPath.setRepeated(isRepeated);
        orderPath.save();
    }

    public static void pushOrderStats(Context context, String lat, String lng) {
        final String orderId = SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, context);
        final String orderUserId = SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_USER_ID, context);
        Log.i("orderIds**", orderId);


        if (!orderId.equals("")) {
            if (orderId.contains(",")) {
                final String[] orderIds = orderId.split(",");
                final String[] orderUsersIds = orderUserId.split(",");

                //WriteBatch batch = firebaseFirestore.batch();

                for (int i = 0; i < orderIds.length; i++) {

//                    long timesetmap1 = System.currentTimeMillis();
//
//                    final int finalI = i;
//

                    if (i == 0) {
                        Log.i("orderIds", orderIds[i] + false);
                        if (!lat.isEmpty() && !lng.isEmpty() && !lat.equals("") && !lng.equals(""))
                            addLatLngInDB(orderIds[i], lat, lng, false);
                    } else {
                        Log.i("orderIds****", orderIds[i] + true);
                        if (!lat.isEmpty() && !lng.isEmpty() && !lat.equals("") && !lng.equals(""))
                            addLatLngInDB(orderIds[i], lat, lng, true);
                    }
//                    XmppClient.getInstance().sendTrackingMessage(lat+":"+lng, orderUsersIds[i].split(";")[1], lat+":"+lng, orderId, ChatMessage.MediaType.TEXT);

                    if (XmppClient.getInstance() != null)
                        XmppClient.getInstance().sendTrackingMessage(lat + ":" + lng, orderUsersIds[i].split(":")[1], lat + ":" + lng, orderIds[i], ChatMessage.MediaType.TEXT);

                    //      firebaseFirestore.collection(ApiCalls.FIREBASE_KEY_ORDERS).document(orderIds[i]).collection("LocationHistory").document(String.valueOf(timesetmap1)).set(userMap);
                }

            } else {


                if (!lat.isEmpty() && !lng.isEmpty() && !lat.equals("") && !lng.equals(""))
                    addLatLngInDB(orderId, lat, lng, false);
//                XmppClient.getInstance().sendTrackingMessage(lat+":"+lng, orderUserId.split(":")[1], lat+":"+lng, orderId, ChatMessage.MediaType.TEXT);
                if (XmppClient.getInstance() != null)
                    XmppClient.getInstance().sendTrackingMessage(lat + ":" + lng, orderUserId.split(":")[1], lat + ":" + lng, orderId, ChatMessage.MediaType.TEXT);

//                firebaseFirestore.collection(ApiCalls.FIREBASE_KEY_ORDERS).document(orderId).collection("LocationHistory").document(String.valueOf(timesetmap)).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//
//                        if (task.isSuccessful()) {
//
//                        } else {
//                            String error = task.getException().getMessage();
//                        }
//
//                    }
//                });
            }

        }


    }

//    public static void startXMPPService(Context context) {
//        if (!isServiceRunning(context, XmppService.class)) {
//            //Start the service
//            Intent intent = new Intent(context, XmppService.class);
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                context.startForegroundService(intent);
////            } else {
////                context.startService(intent);
////            }
//            context.startService(intent);
//        }
//    }

//    public static void stopXMPPService(Context context) {
//        if (isServiceRunning(context, XmppService.class)) {
//            //Start the service
//
//            Intent intent = new Intent(context, XmppService.class);
//            context.stopService(intent);
//
//        }
//    }


   /* public static void startXMPPService(Context context) {

        if(SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.SHOULD_CONNECT_XMPP,context)){
        Intent intent = new Intent(context, XmppService.class);

        if (!isServiceRunning(context, XmppService.class)) {
            context.startService(intent);
        } else {
            if (XmppClient.getInstance() != null) {
                if (!XmppClient.getInstance().isConnected())
                    XmppClient.getInstance().connect("test");
                else if (!XmppClient.getInstance().isAuthenticated())
                    XmppClient.getInstance().login();
            } else {
                context.startService(intent);
            }
        }
        }else{
            stopXMPPService(context);
        }
    }*/

    public static void startXMPPService(Context context) {
        if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.SHOULD_CONNECT_XMPP, context)) {
            Intent intent = new Intent(context, XmppService.class);

            if (!isServiceRunning(context, XmppService.class)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent); // Required for Android 8+
                } else {
                    context.startService(intent);
                }
            } else {
                if (XmppClient.getInstance() != null) {
                    if (!XmppClient.getInstance().isConnected()) {
                        XmppClient.getInstance().connect("test");
                    } else if (!XmppClient.getInstance().isAuthenticated()) {
                        XmppClient.getInstance().login();
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent);
                    } else {
                        context.startService(intent);
                    }
                }
            }
        } else {
            stopXMPPService(context);
        }
    }


    public static void stopXMPPService(Context context) {
        if (isServiceRunning(context, XmppService.class)) {
            Intent intent = new Intent(context, XmppService.class);
            context.stopService(intent);
        }
    }

    public static Notification createNotification(Context context, String str, String channelId) {

        Notification notification = null;
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelId, "XMPP Service", NotificationManager.IMPORTANCE_LOW);
            channel.setBypassDnd(true);
            channel.setLockscreenVisibility(0);
            channel.enableVibration(true);
            channel.setVibrationPattern(null);

            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            String content = "";
            if (str != null)
                content = str;
            notification = new NotificationCompat.Builder(context, channelId)
                    .setContentTitle("XMPP Service")
                    .setOngoing(true)
                    .setContentText(content)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                    .setVibrate(new long[]{0L})
                    .setSound(null)
                    .build();

        } else {

            String content = "Xmpp";
            if (str != null)
                content = str;
            notification = new NotificationCompat.Builder(context)
                    .setContentTitle("XMPP")
                    .setOngoing(true)
                    .setContentText(content)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setVibrate(null)
                    .setPriority(NotificationManager.IMPORTANCE_LOW)
                    .build();

            notification.sound = null;
            notification.vibrate = null;
            notification.defaults &= ~Notification.DEFAULT_SOUND;
            notification.defaults &= ~Notification.DEFAULT_VIBRATE;

        }

//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        notificationManager.notify(0, notification);

        return notification;
    }

    public static Notification createNotification(Context context, String str) {

        Notification notification = null;
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(ApiCalls.CHANNEL_ID,
                    "Darewro",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setBypassDnd(true);
            channel.setLockscreenVisibility(0);
            channel.enableVibration(false);
            channel.setVibrationPattern(null);

            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            String content = "Darewro";
            if (str != null)
                content = str;
            notification = new NotificationCompat.Builder(context, ApiCalls.CHANNEL_ID)
                    .setContentTitle("Darewro")
                    .setContentText(content)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                    .setVibrate(new long[]{0L})
                    .setSound(null)
                    .build();


        } else {

            String content = "Darewro";
            if (str != null)
                content = str;
            notification = new NotificationCompat.Builder(context)
                    .setContentTitle("Darewro")
                    .setContentText(content)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setVibrate(null)
                    .setPriority(NotificationManager.IMPORTANCE_LOW)
                    .build();

            notification.sound = null;
            notification.vibrate = null;
            notification.defaults &= ~Notification.DEFAULT_SOUND;
            notification.defaults &= ~Notification.DEFAULT_VIBRATE;

        }

        return notification;
    }

    public static void sendOrderNotification(Context context, String title, String message, Intent intent, String orderId, Context ctxt) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/raw/instrumental_ring");

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "CH_ID_DARIAN")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title + " test")
                .setContentText(message)
                .setAutoCancel(true)
                .setVibrate(new long[]{100, 200, 300})
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // ✅ Fix: Ensure NotificationManager is not null
        if (mNotificationManager == null) {
            Log.e("Notification", "NotificationManager is null!");
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // ✅ Fix: Ensure valid NotificationChannel ID
            String CHANNEL_ID = "CH_ID_DARIAN";

            // ✅ Fix: Set valid audio attributes
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, title, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(soundUri, audioAttributes);
            notificationChannel.setBypassDnd(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300});

            // ✅ Fix: Ensure channel creation only happens on API 26+
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        mNotificationManager.notify(0, notificationBuilder.build());
    }

    public static void sendCustomNotification(Context context, String title, String message, Intent intent, String url, Context ctxt) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/raw/instrumental_ring");

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "CH_ID1")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title + " test111")
                .setContentText(message)
                .setAutoCancel(true)
                .setVibrate(new long[]{100, 200, 300})
                .setSound(soundUri)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setContentIntent(pendingIntent);

        final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // ✅ Fix: Ensure NotificationManager is not null
        if (mNotificationManager == null) {
            Log.e("Notification", "NotificationManager is null!");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "CH_ID1";

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, title, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(soundUri, audioAttributes);
            notificationChannel.setBypassDnd(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300});

            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        try {
            Bitmap resource = GlideApp.with(context)
                    .asBitmap()
                    .load(ApiCalls.BASE_URL_IMAGE_NOTIFICATION + url)
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();

            notificationBuilder.setLargeIcon(resource);
            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(resource));
        } catch (InterruptedException | ExecutionException e) {
            Log.e("Notification", "Error loading image: " + e.getMessage());
        }

        mNotificationManager.notify(0, notificationBuilder.build());
    }


    @SuppressLint("MissingPermission")
    public static Notification showChatNotification(Context context, String str, String userid, String channelId, Intent intent) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Uri soundUri = getDefaultUri(TYPE_NOTIFICATION);

        Notification notification = null;
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelId,
                    userid,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setBypassDnd(true);
            channel.setLockscreenVisibility(0);
            channel.enableVibration(true);

            channel.setVibrationPattern(null);


            if (soundUri != null) {

                AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
                channel.setSound(soundUri, audioAttributes);
            }


            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            String content = userid;
            if (str != null)
                content = str;
            notification = new NotificationCompat.Builder(context, channelId)
                    .setContentTitle(userid)
                    .setOngoing(false)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setVibrate(new long[]{0L})
                    .setSound(soundUri)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .build();


        } else {

            String content = userid;
            if (str != null)
                content = str;
            notification = new NotificationCompat.Builder(context)
                    .setContentTitle(userid)
                    .setOngoing(false)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setVibrate(new long[]{0L})
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setSound(soundUri)
                    .build();

        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(Integer.parseInt(channelId), notification);

        return notification;
    }

    @SuppressLint("MissingPermission")
    public static Notification showChatNotification(Context context, String str, String userid, String channelId) {

        Notification notification = null;
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelId,
                    userid,
                    NotificationManager.IMPORTANCE_LOW);
            channel.setBypassDnd(true);
            channel.setLockscreenVisibility(0);
            channel.enableVibration(true);
            channel.setVibrationPattern(null);

            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            String content = userid;
            if (str != null)
                content = str;
            notification = new NotificationCompat.Builder(context, channelId)
                    .setContentTitle(userid)
                    .setOngoing(false)
                    .setContentText(content)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                    .setVibrate(new long[]{0L})
                    .setSound(null)
                    .build();

        } else {

            String content = userid;
            if (str != null)
                content = str;
            notification = new NotificationCompat.Builder(context)
                    .setContentTitle(userid)
                    .setOngoing(false)
                    .setContentText(content)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setVibrate(null)
                    .setPriority(NotificationManager.IMPORTANCE_LOW)
                    .build();

            notification.sound = null;
            notification.vibrate = null;
            notification.defaults &= ~Notification.DEFAULT_SOUND;
            notification.defaults &= ~Notification.DEFAULT_VIBRATE;

        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(Integer.parseInt(channelId), notification);

        return notification;
    }

    public static boolean oldTimeStampIsLessThanCurrantTimeStampInMillis(Context context, String oldTimeStamp, long difInMilliSec) {

        long diff = Math.abs(getCurrentTimeStampInMilliSeconds()) - Long.valueOf(oldTimeStamp);
        if (diff <= difInMilliSec) {
            System.out.println("connection : Time Difference = " + diff + " milliSeconds");
            return true;
        } else {
            return false;
        }

    }

    public static void pushRiderStatsIfInternetWorking(final Context context) {

        if (SharedPreferenceHelper.getString(SharedPreferenceHelper.LAST_LOCATION_PUSHED, context) != null && !TextUtils.isEmpty(SharedPreferenceHelper.getString(SharedPreferenceHelper.LAST_LOCATION_PUSHED, context))) {
            if (oldTimeStampIsLessThanCurrantTimeStampInMillis(context, SharedPreferenceHelper.getString(SharedPreferenceHelper.LAST_LOCATION_PUSHED, context), 5000)) {
                return;
            } else {
                SharedPreferenceHelper.saveString(SharedPreferenceHelper.LAST_LOCATION_PUSHED, String.valueOf(getCurrentTimeStampInMilliSeconds()), context);
            }
        } else {
            SharedPreferenceHelper.saveString(SharedPreferenceHelper.LAST_LOCATION_PUSHED, String.valueOf(getCurrentTimeStampInMilliSeconds()), context);
        }

        double lat = SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LAT, context).equals("") ? 0 : Double.parseDouble(SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LAT, context));
        double lng = SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LNG, context).equals("") ? 0 : Double.parseDouble(SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LNG, context));

//       if((lat>33.73&&lat<34.16)&&(lng>71.32&&lng<72.23))
//        {
        thread = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    //writeLog(context, "\n\n" + getCurrentTimeStamp() + " DATA ENABLED = " + checkIfDataEnabled(context));
                    //writeLog(context, getCurrentTimeStamp() + " GPS ENABLED = " + checkGPSStatus(context));
                    try (Socket socket = new Socket()) {
                        socket.connect(new InetSocketAddress("www.google.com", 80), 2000);
                        System.out.println("connection : internet connected");

                        sendRiderLocationNewApi(context);
                        if (!SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, context).equals("")) {
                           // startXMPPService(context);
                            pushOrderStats(context, SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LAT, context), SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LNG, context));
                        } else {
                            //addLatLngInDB(null, lat, lng);
                          //  stopXMPPService(context);
                        }
//                        writeLog(context, getCurrentTimeStamp() + " Internet is Connected");
//                        DocumentReference docRef = FirebaseFirestore.getInstance().collection("CacheResolve").document("getConnection");
//                        docRef.get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                writeLog(context, getCurrentTimeStamp() + " Firebase is Connected");
//                                if (task.isSuccessful()) {
//
//                                    pushRiderStats(context);
//                                } else {
//                                    Log.d(TAG, "get failed with ", task.getException());
//                                }
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                writeLog(context, getCurrentTimeStamp() + " Firebase Not Connected");
//                            }
//                        });

                    } catch (IOException e) {
                        System.out.println(e);
                        System.out.println("connection : internet not connected");
                        //writeLog(context, getCurrentTimeStamp() + " Internet is not Connected");

                    }
                }
            });
        }

        thread.start();
//        }

    }

    @SuppressLint("MissingPermission")
    private static boolean checkIfDataEnabled(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return tm.isDataEnabled();

        } else {
            return tm.getSimState() == TelephonyManager.SIM_STATE_READY && tm.getDataState() != TelephonyManager.DATA_DISCONNECTED;
        }
    }

    public static boolean isDateTimeAuto(Context context) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        int timeSetting;
        int timeZoneSetting;
        if (currentapiVersion < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            timeSetting = android.provider.Settings.System.getInt(context.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0);
            timeZoneSetting = android.provider.Settings.System.getInt(context.getContentResolver(), Settings.System.AUTO_TIME_ZONE, 0);
        } else {
            timeSetting = android.provider.Settings.System.getInt(context.getContentResolver(), android.provider.Settings.Global.AUTO_TIME, 0);
            timeZoneSetting = android.provider.Settings.System.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0);
        }
        if (timeSetting == 0) {
            makeAlertDialog("Info", "Set your Date & time to Automatic in your mobile settings", context);
            return false;
        }
        if (timeZoneSetting == 0) {
            makeAlertDialog("Info", "Set your time zone to Automatic in your mobile settings", context);
            return false;
        }
        return true;
    }

    public static void writeLog(Context context, String txt) {

        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                return;
            }
        }

        File backupPath = Environment.getExternalStorageDirectory();

        backupPath = new File(backupPath.getPath() + "/DarewroRider");

        if (!backupPath.exists()) {
            backupPath.mkdirs();
        }

        FileOutputStream fos;
        try {
            String date = "/" + getCurrentDate() + ".txt";
            fos = new FileOutputStream(backupPath.getPath() + date, true);//"/logs.txt"
            fos.write(txt.getBytes());
            fos.write("\n".getBytes());
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    public static void openDialer(String number, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("tel:" + number));
        context.startActivity(intent);
    }

    public static void sendRiderLocationNewApi(final Context context) {

        InitApi initApi = new InitApi() {
            @Override
            public HashMap<String, String> getBody() {

                return null;
            }

            @Override
            public HashMap<String, Object> getObjBody() {

                String orderId = SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, context);

                HashMap<String, Object> userMap = getBatteryStats(context);//new HashMap<>();

                userMap.put("riderId", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, context));
                userMap.put("accuracy", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_HORIZONTAL_ACCURACY_METERS, context));
                userMap.put("appVersion", BuildConfig.VERSION_NAME);
                userMap.put("deviceBrand", SharedPreferenceHelper.getString(SharedPreferenceHelper.DEVICE_BRAND, context));
                userMap.put("deviceModel", SharedPreferenceHelper.getString(SharedPreferenceHelper.DEVICE_MODEL, context));
                userMap.put("deviceOSVersion", SharedPreferenceHelper.getString(SharedPreferenceHelper.DEVICE_OS_VERSION, context));
                userMap.put("gpsStatus", checkGPSStatus(context));
                userMap.put("isAvailable", SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, context));
                if (TrueTimeRx.isInitialized())
                    userMap.put("lastStatsSynced", String.valueOf(TrueTimeRx.now().getTime()));
                else
                    userMap.put("lastStatsSynced", String.valueOf(System.currentTimeMillis()));
                userMap.put("lat", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LAT, context));
                userMap.put("long", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LNG, context));
                userMap.put("locationAltitude", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_ALTITUDE, context));
                userMap.put("locationBearing", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_BEARING, context));
                userMap.put("locationBearingAccuracyDegrees", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_BEARING_ACCURACY_DEGREES, context));
                userMap.put("locationElapsedRealTImeNanos", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_ELAPSED_REAL_TIME_NANOS, context));
                userMap.put("locationProvider", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_PROVIDER, context));
                userMap.put("locationSpeed", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_SPEED, context));
                userMap.put("locationSpeedAccuracyMeterPerSeconds", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_SPEED_ACCURACY_METERS_PER_SECONDS, context));
                userMap.put("locationTime", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_TIME, context));
                userMap.put("locationVerticalAccuracyMeters", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_VERTICAL_ACCURACY_METERS, context));
                if (!orderId.equals("")) {
                    userMap.put("order", true);
                    if (orderId.contains(",")) {
                        userMap.put("orderCount", orderId.split(",").length);
                        userMap.put("activeOrderId", orderId.split(",")[0]);
                    } else {
                        userMap.put("orderCount", 1);
                        userMap.put("activeOrderId", orderId);
                    }
                } else {
                    userMap.put("order", false);
                    userMap.put("orderCount", 0);
                }
                userMap.put("orderId", orderId);
                if (TrueTimeRx.isInitialized())
                    userMap.put("timestamp", String.valueOf(TrueTimeRx.now().getTime()));
                else
                    userMap.put("timestamp", SharedPreferenceHelper.getString(SharedPreferenceHelper.TIMESTAMP, context));

                userMap.put("signalStrength", getSignalStrength(context));

                return userMap;
            }

            @Override
            public HashMap<String, String> getHeader() {
                return getStandardHeaders(context);
            }
        };

        String saveRiderLocation = ApiCalls.saveRiderLocation();
        GenericHandler ordersListingHandler = new GenericHandler(context, saveRiderLocation);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), new JSONObject(initApi.getObjBody()), saveRiderLocation, Request.Method.POST, context, true, ordersListingHandler);
        jsonObjectRequestCall.sendData();

    }

    public static void sendRiderLocationNewApi(final Context context, final boolean isAvailable, final boolean logout) {

        InitApi initApi = new InitApi() {
            @Override
            public HashMap<String, String> getBody() {

                return null;
            }

            @Override
            public HashMap<String, Object> getObjBody() {

                String orderId = SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, context);

                HashMap<String, Object> userMap = getBatteryStats(context);//new HashMap<>();

                userMap.put("riderId", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, context));
                userMap.put("accuracy", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_HORIZONTAL_ACCURACY_METERS, context));
                userMap.put("appVersion", BuildConfig.VERSION_NAME);
                userMap.put("deviceBrand", SharedPreferenceHelper.getString(SharedPreferenceHelper.DEVICE_BRAND, context));
                userMap.put("deviceModel", SharedPreferenceHelper.getString(SharedPreferenceHelper.DEVICE_MODEL, context));
                userMap.put("deviceOSVersion", SharedPreferenceHelper.getString(SharedPreferenceHelper.DEVICE_OS_VERSION, context));
                userMap.put("gpsStatus", checkGPSStatus(context));
                userMap.put("isAvailable", isAvailable);
                if (TrueTimeRx.isInitialized())
                    userMap.put("lastStatsSynced", String.valueOf(TrueTimeRx.now().getTime()));
                else
                    userMap.put("lastStatsSynced", String.valueOf(System.currentTimeMillis()));
                userMap.put("lat", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LAT, context));
                userMap.put("long", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LNG, context));
                userMap.put("locationAltitude", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_ALTITUDE, context));
                userMap.put("locationBearing", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_BEARING, context));
                userMap.put("locationBearingAccuracyDegrees", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_BEARING_ACCURACY_DEGREES, context));
                userMap.put("locationElapsedRealTImeNanos", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_ELAPSED_REAL_TIME_NANOS, context));
                userMap.put("locationProvider", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_PROVIDER, context));
                userMap.put("locationSpeed", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_SPEED, context));
                userMap.put("locationSpeedAccuracyMeterPerSeconds", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_SPEED_ACCURACY_METERS_PER_SECONDS, context));
                userMap.put("locationTime", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_TIME, context));
                userMap.put("locationVerticalAccuracyMeters", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_VERTICAL_ACCURACY_METERS, context));
                if (!orderId.equals("")) {
                    userMap.put("order", true);
                    userMap.put("orderCount", orderId.split(",").length);
                } else {
                    userMap.put("order", false);
                    userMap.put("orderCount", 0);
                }
                userMap.put("orderId", orderId);
                if (TrueTimeRx.isInitialized())
                    userMap.put("timestamp", String.valueOf(TrueTimeRx.now().getTime()));
                else
                    userMap.put("timestamp", SharedPreferenceHelper.getString(SharedPreferenceHelper.TIMESTAMP, context));

                userMap.put("signalStrength", getSignalStrength(context));

                return userMap;
            }

            @Override
            public HashMap<String, String> getHeader() {
                return getStandardHeaders(context);
            }
        };

        String saveRiderLocation = ApiCalls.saveRiderLocation();
        GenericHandler ordersListingHandler = new GenericHandler(context, saveRiderLocation);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), new JSONObject(initApi.getObjBody()), saveRiderLocation, Request.Method.POST, context, true, ordersListingHandler);
        jsonObjectRequestCall.sendData();

        if (logout) {
            String token = SharedPreferenceHelper.getString(SharedPreferenceHelper.FIRE_BASE_TOKEN, context);
            SharedPreferenceHelper.clearPrefrences(context);
            SharedPreferenceHelper.saveString(SharedPreferenceHelper.FIRE_BASE_TOKEN, token, context);
            stopService(context);

            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
    }


    public static void pushRiderStats(final Context context, boolean isAvailable, final boolean logout) {

        double lat = SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LAT, context).equals("") ? 0 : Double.parseDouble(SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LAT, context));
        double lng = SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LNG, context).equals("") ? 0 : Double.parseDouble(SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LNG, context));

//        if((lat>33.73&&lat<34.16)&&(lng>71.32&&lng<72.23))
        sendRiderLocationNewApi(context, isAvailable, logout);
//        String riderId = SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, context);
//        String orderId = SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, context);
//
//        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//
//        Map<String, Object> userMap = getBatteryStats(context);
//
//        userMap.put("locationProvider", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_PROVIDER, context));
//        userMap.put("locationTime", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_TIME, context));
//        userMap.put("locationElapsedRealTImeNanos", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_ELAPSED_REAL_TIME_NANOS, context));
//        userMap.put("locationAltitude", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_ALTITUDE, context));
//        userMap.put("locationSpeed", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_SPEED, context));
//        userMap.put("locationBearing", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_BEARING, context));
//        userMap.put("locationVerticalAccuracyMeters", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_VERTICAL_ACCURACY_METERS, context));
//        userMap.put("locationSpeedAccuracyMeterPerSeconds", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_SPEED_ACCURACY_METERS_PER_SECONDS, context));
//        userMap.put("locationBearingAccuracyDegrees", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_BEARING_ACCURACY_DEGREES, context));
//
//
//        userMap.put("lat", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LAT, context));
//        userMap.put("long", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LNG, context));
//        userMap.put("isAvailable", isAvailable);
//        userMap.put("accuracy", SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_HORIZONTAL_ACCURACY_METERS, context));
//
//        if (!orderId.equals(""))
//            userMap.put("order", true);
//        else
//            userMap.put("order", false);
//
//
//        userMap.put("orderId", orderId);
//
//
//        if (TrueTimeRx.isInitialized())
//            userMap.put("timestamp", String.valueOf(TrueTimeRx.now().getTime()));
//        else
//            userMap.put("timestamp", SharedPreferenceHelper.getString(SharedPreferenceHelper.TIMESTAMP, context));
//
//        if (TrueTimeRx.isInitialized())
//            userMap.put("lastStatsSynced", String.valueOf(TrueTimeRx.now().getTime()));
//        else
//            userMap.put("lastStatsSynced", String.valueOf(System.currentTimeMillis()));
//        userMap.put("deviceBrand", SharedPreferenceHelper.getString(SharedPreferenceHelper.DEVICE_BRAND, context));
//        userMap.put("deviceModel", SharedPreferenceHelper.getString(SharedPreferenceHelper.DEVICE_MODEL, context));
//        userMap.put("deviceOSVersion", SharedPreferenceHelper.getString(SharedPreferenceHelper.DEVICE_OS_VERSION, context));
//        userMap.put("signalStrength", getSignalStrength(context));
//        userMap.put("gpsStatus", checkGPSStatus(context));
//        userMap.put("appVersion", BuildConfig.VERSION_NAME);
//
//        Log.i("appVersion", BuildConfig.VERSION_NAME);
//
//        if (!riderId.equals("")) {
//            firebaseFirestore.collection(ApiCalls.FIREBASE_KEY_RIDERS).document(riderId).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//
//                    if (task.isSuccessful()) {
//
//                        if (logout) {
//                            String token = SharedPreferenceHelper.getString(SharedPreferenceHelper.FIRE_BASE_TOKEN, context);
//                            SharedPreferenceHelper.clearPrefrences(context);
//                            SharedPreferenceHelper.saveString(SharedPreferenceHelper.FIRE_BASE_TOKEN, token, context);
//                            stopService(context);
//
//                            Intent intent = new Intent(context, LoginActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            context.startActivity(intent);
//                        }
//                    } else {
//
//                        String error = task.getException().getMessage();
//                        //       Toast.makeText(getApplicationContext(), "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();
//
//                    }
//
//                }
//
//            });
//        }
    }

    public static boolean isNetworkConnected(Context ctxt) {
        ConnectivityManager cm = (ConnectivityManager) ctxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static void forceLogout(Context context) {
        stopAlarm(context);
        stopAlarmForTracking(context);
        pushRiderStats(context, false, false);
        String token = SharedPreferenceHelper.getString(SharedPreferenceHelper.FIRE_BASE_TOKEN, context);
        SharedPreferenceHelper.clearPrefrences(context);
        SharedPreferenceHelper.saveString(SharedPreferenceHelper.FIRE_BASE_TOKEN, token, context);
        SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.FORCE_STOP, true, context);
//        switchActivity(context, LoginActivity.class, null);
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        forceStopService(context);
        ((Activity) context).finish();
    }

    private static void stopAlarm(Context context) {

        Intent intent = new Intent(
                context,
                AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(
                        context,
                        1234567, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        boolean alarmUp = (PendingIntent.getBroadcast(context, 1234567,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE) != null);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

        if (alarmUp) {
            alarmManager.cancel(pendingIntent);
        } else {

        }

    }

    private static void stopAlarmForTracking(Context context) {

        Intent intent = new Intent(
                context,
                AlarmReceiverForRiderTrackingService.class);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(
                        context,
                        12345678, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        boolean alarmUp = (PendingIntent.getBroadcast(context, 12345678,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE) != null);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

        if (alarmUp) {
            alarmManager.cancel(pendingIntent);
        } else {

        }

    }


    public static void forceStopService(Context context) {
        if ((!SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, context)) && SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, context).equals("")) {
            SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.FORCE_STOP, true, context);
            Intent intent = new Intent(context, LocationService.class);
            if (LocationService.isRunning(context)) {
                context.stopService(intent);
            }
            //doUnbindService();
        }
    }

    public static boolean isAppInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static double calculateDistanceInKilometer(double userLat, double userLng,
                                                      double venueLat, double venueLng) {

        int R = 6371; // Radius of the earth in km
        double dLat = ((venueLat - userLat) * Math.PI / 180.0);//deg2rad(lat2-lat1);  // deg2rad below
        double dLon = ((venueLng - userLng) * Math.PI / 180.0);
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(((userLat) * Math.PI / 180.0)) * Math.cos(((venueLat) * Math.PI / 180.0)) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        double miles = d / 1.609344;

        Log.i("distance in km", String.valueOf(d));

        return d;
    }


    public static double calculateDistance(Activity activity, List<OrderPath> orderPaths) {

        double distance = 0;

        for (int i = 0; i < orderPaths.size() - 1; i++) {

            Double locALat = Double.parseDouble(orderPaths.get(i).getLat());
            Double locALng = Double.parseDouble(orderPaths.get(i).getLng());
            Double locBLat = Double.parseDouble(orderPaths.get(i + 1).getLat());
            Double locBLng = Double.parseDouble(orderPaths.get(i + 1).getLng());

            double dist = calculateDistanceInKilometer(locALat, locALng,
                    locBLat, locBLng);
            distance = distance + dist;
        }

        Log.i("Distance = ", distance + "");

        return round(distance, 2);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
//    public static double getDistace2D(double distance) {
//        return (distance / 180 * Math.PI * 6371);
//    }

    public static byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Turn drawable into byte array.
     *
     * @param drawable data
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

}


