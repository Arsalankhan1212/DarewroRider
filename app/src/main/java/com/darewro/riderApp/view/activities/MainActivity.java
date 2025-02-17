package com.darewro.riderApp.view.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.darewro.riderApp.BuildConfig;
import com.darewro.riderApp.data.api.handlers.AllOrdersListHandler;
import com.darewro.riderApp.data.api.models.allOrders.AllOrder;
import com.darewro.riderApp.presenter.ResponseListenerAllOrdersListing;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.darewro.riderApp.R;
import com.darewro.riderApp.data.api.ApiCalls;
import com.darewro.riderApp.data.api.Interface.InitApi;
import com.darewro.riderApp.data.api.handlers.NearestOrdersHandler;
import com.darewro.riderApp.data.api.handlers.OrdersDetailsHandler;
import com.darewro.riderApp.data.api.handlers.OrdersListingHandler;
import com.darewro.riderApp.data.api.handlers.StatusHandler;
import com.darewro.riderApp.data.api.requests.JsonObjectRequestCall;
import com.darewro.riderApp.presenter.ResponseListenerNearestOrdersListing;
import com.darewro.riderApp.presenter.ResponseListenerOrdersDetails;
import com.darewro.riderApp.presenter.ResponseListenerOrdersListing;
import com.darewro.riderApp.presenter.ResponseListenerStatus;
import com.darewro.riderApp.view.adapters.OrderFragmentViewPagerAdapter;
import com.darewro.riderApp.view.customViews.RoundedEdgesImageView;
import com.darewro.riderApp.view.listeners.AlertDialogResponseListener;
import com.darewro.riderApp.view.locationService.LocationService;
import com.darewro.riderApp.view.models.NearestOrder;
import com.darewro.riderApp.view.models.Order;
import com.darewro.riderApp.view.receivers.AlarmReceiver;
import com.darewro.riderApp.view.receivers.AlarmReceiverForRiderTrackingService;
import com.darewro.riderApp.view.utils.AlertDialogUtils;
import com.darewro.riderApp.view.utils.AppUtils;
import com.darewro.riderApp.view.utils.DbUtils;
import com.darewro.riderApp.view.utils.PermissionsRequest;
import com.darewro.riderApp.view.utils.SharedPreferenceHelper;
import com.darewro.riderApp.view.xmpp.LocalBinder;
import com.darewro.riderApp.view.xmpp.XmppClient;
import com.darewro.riderApp.view.xmpp.XmppService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, ResponseListenerOrdersListing, ResponseListenerNearestOrdersListing, ResponseListenerOrdersDetails, ResponseListenerAllOrdersListing, AlertDialogResponseListener, ResponseListenerStatus, CompoundButton.OnCheckedChangeListener {

    private static final int requestStorageId = 112;
    private static final String TAG = MainActivity.class.getName();

    TextView userName, rider_id;

  public static XmppService xmppService;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //do something based on the intent's action
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {

                if (xmppService != null && xmppService.getXmpp() != null){
                    if(!xmppService.getXmpp().isConnected())
                    xmppService.getXmpp().connect("On Network State Changed");
                    else if(!xmppService.getXmpp().isAuthenticated())
                        xmppService.getXmpp().login();
                    //else
                      //  xmppService.getXmpp().connect("On Network State Changed");

                }
//                else{
//                    mBounded = false;
//                    doBindService();
//                    //Intent serviceIntent = new Intent(MainActivity.this, XmppService.class);
//                    //bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
//                    //startService(serviceIntent);
//                }
            }
            //should check null because in airplane mode it will be null
        }
    };

    BroadcastReceiver mBroadcastReceiverToast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case XmppClient.TOAST: {

                    String body = intent.getStringExtra(XmppClient.TOAST_Message);
                    AppUtils.makeNotification(body, MainActivity.this);


                    return;
                }
            }

        }
    };
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DrawerLayout drawer;
    private boolean isDoublePress;
    private long ALARM_PERIODIC_TIME = /*15*/2 * 1000;
    private SwitchCompat statusSwitch;
    private boolean isLogout = false;
    private RoundedEdgesImageView profilePic;
    private OrderFragmentViewPagerAdapter adapter;
    private int currentTab = 0;
    private boolean mBounded = false;
    private final ServiceConnection mConnection = new ServiceConnection() {

        @SuppressWarnings("unchecked")
        @Override
        public void onServiceConnected(final ComponentName name, final IBinder service) {
            xmppService = ((LocalBinder<XmppService>) service).getService();
            mBounded = true;
            Log.d("XMPP1", "onServiceConnected");
            //AppUtils.showNotification(MainActivity.this, "Service Created()", "10");
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
//            xmppService = null;
//            XmppClient.clearInstance();
            mBounded = false;
            Log.d("XMPP1", "onServiceDisconnected");
            //AppUtils.showNotification(MainActivity.this, "Service Destroyed()", "10");

        }
    };
    private com.google.firebase.remoteconfig.FirebaseRemoteConfig firebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.IS_LOGGED_IN, true, MainActivity.this);

        //checking permission
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            requestPermissions(PermissionsRequest.writeExternalStorage, requestStorageId);
        }

        handleIntent();
        initializeViews();
        setListeners();
        setRemoteConfig();
        //checkXmppConnection();
    }

    private void setRemoteConfig() {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        // Create a Remote Config Setting to enable developer mode,

        FirebaseRemoteConfigSettings.Builder configBuilder = new FirebaseRemoteConfigSettings.Builder();
        // Sets the minimum interval between successive fetch calls.

        if (BuildConfig.DEBUG) {
            long cacheInterval = 0;
            configBuilder.setMinimumFetchIntervalInSeconds(5);
        }
        // finally build config settings and sets to Remote Config
        firebaseRemoteConfig.setConfigSettingsAsync(configBuilder.build());

        //set default values
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        fetchRemoteConfig();
        getDeviceToken();
    }
/*    private void checkXmppConnection() {
        if (xmppService != null && xmppService.getXmpp() != null){
            if(!xmppService.getXmpp().isConnected())
                xmppService.getXmpp().connect("On Network State Changed");
            else if(!xmppService.getXmpp().isAuthenticated())
                xmppService.getXmpp().login();
            else
                xmppService.getXmpp().connect("On Network State Changed");

        }
        else{
            mBounded = false;
           // doBindService();
        }
    }*/


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

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        unregisterReceiver(mBroadcastReceiverToast);
        super.onDestroy();
    }

    @Override
    public void initializeViews() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");


        IntentFilter filter2 = new IntentFilter(XmppClient.TOAST);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(receiver, filter,RECEIVER_EXPORTED);
            registerReceiver(mBroadcastReceiverToast, filter2,RECEIVER_EXPORTED);

        }else{
            registerReceiver(receiver, filter);
            registerReceiver(mBroadcastReceiverToast, filter2);
        }


        toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setPadding(0, 0, 0, 0);//for tab otherwise give space in tab Placed
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(ContextCompat.getColor(this,R.color.white));
        DrawerArrowDrawable aero = new DrawerArrowDrawable(this);
        aero.setBarLength(60);
        aero.setColor(getResources().getColor(R.color.white));
        aero.setBarThickness(4);
        aero.setGapSize(12);
        toggle.setDrawerArrowDrawable(aero);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        final NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);


        // get menu from navigationView
        Menu menu = navigationView.getMenu();

        // find MenuItem you want to change
        MenuItem dashboard = menu.findItem(R.id.dashboard);
        MenuItem logout = menu.findItem(R.id.logout);
        MenuItem aboutUs = menu.findItem(R.id.aboutUs);
        MenuItem notifications = menu.findItem(R.id.notifications);

        AppUtils.setMontserratBold(userName);
        AppUtils.setMontserratBold(rider_id);
        AppUtils.setMontserrat(dashboard.getActionView());
        AppUtils.setMontserrat(logout.getActionView());
        AppUtils.setMontserrat(aboutUs.getActionView());
        AppUtils.setMontserrat(notifications.getActionView());


        String title = getResources().getString(R.string.version) + " - " + BuildConfig.VERSION_NAME;

        TextView toolbarText = findViewById(R.id.toolbarTextId);
        toolbarText.setText(title);
        AppUtils.setMontserrat(toolbarText);

        TextView appVersionDrawer = findViewById(R.id.footer);
        appVersionDrawer.setText(title);
        AppUtils.setMontserrat(appVersionDrawer);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);


        //tabLayout.addTab(tabLayout.newTab().setText(Order.NEAREST_ORDERS));
        tabLayout.addTab(tabLayout.newTab().setText(Order.All_ORDERS));
        tabLayout.addTab(tabLayout.newTab().setText(Order.NEW_TEXT));
        tabLayout.addTab(tabLayout.newTab().setText(Order.COMPLETED_TEXT));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        adapter = new OrderFragmentViewPagerAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount() - 1);

        highLightCurrentTab(currentTab);

        userName = headerView.findViewById(R.id.name);
        rider_id = headerView.findViewById(R.id.rider_id);
        profilePic = headerView.findViewById(R.id.profile_pic);


        statusSwitch = findViewById(R.id.switch_status);
        AppUtils.setMontserrat(statusSwitch);

        if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, MainActivity.this)) {
            statusSwitch.setChecked(true);
            statusSwitch.setText("Available");
            bindLocationService(true);
            changeAvailabilityStatus(true);

        } else {
            if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.RIDER_STATUS, MainActivity.this)) {
                statusSwitch.setChecked(true);
                statusSwitch.setText("Available");
                SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.IS_AVAILABLE, true, MainActivity.this);
                bindLocationService(true);
                changeAvailabilityStatus(true);
            } else {
                statusSwitch.setChecked(false);
                statusSwitch.setText("Unavailable");
            }
        }

        statusSwitch.setOnCheckedChangeListener(this);


        userName.setText(SharedPreferenceHelper.getString(SharedPreferenceHelper.USERNAME, MainActivity.this));
        rider_id.setText(SharedPreferenceHelper.getString(SharedPreferenceHelper.ID, MainActivity.this));


        Log.d("TAG111","USER ID= "+SharedPreferenceHelper.getString(SharedPreferenceHelper.ID, MainActivity.this));
        Log.d("TAG111","USER Contact Number= "+SharedPreferenceHelper.getString(SharedPreferenceHelper.MSISDN, MainActivity.this));


        if (SharedPreferenceHelper.getString(SharedPreferenceHelper.PICTUREPATH, MainActivity.this).equals("")) {

        } else {
            AppUtils.loadPicture(MainActivity.this, profilePic, ApiCalls.getImageUrl(SharedPreferenceHelper.getString(SharedPreferenceHelper.PICTUREPATH, MainActivity.this)));
        }

    }

//    public void doBindService() {
//        if (!mBounded) {
//            Intent serviceIntent = new Intent(this, XmppService.class);
//            bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
//            startService(serviceIntent);
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                startForegroundService(serviceIntent);
////            } else {
////                startService(serviceIntent);
////            }
//        }
//    }

  /*  public void doBindService(Intent intent) {
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }*/

//    void doUnbindService() {
//        if (mBounded) {
//            if (mConnection != null) {
//                unbindService(mConnection);
//                AppUtils.stopXMPPService(MainActivity.this);
//            }
//            mBounded = false;
//        }
//    }

    private void highLightCurrentTab(int position) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            assert tab != null;
            tab.setCustomView(null);
            tab.setCustomView(adapter.getTabView(i));
        }

        TabLayout.Tab tab = tabLayout.getTabAt(position);
        assert tab != null;
        tab.setCustomView(null);
        tab.setCustomView(adapter.getSelectedTabView(position));
    }

//    private void setOrderCount(int newOrders, int completedOrders) {
//
//        TabLayout.Tab tab = tabLayout.getTabAt(0);
//        assert tab != null;
//        tab.setCustomView(null);
//        tab.setCustomView(adapter.setOrderCount(0, Order.NEW_TEXT+" - "+newOrders));
//
//        TabLayout.Tab tab1 = tabLayout.getTabAt(1);
//        assert tab1 != null;
//        tab1.setCustomView(null);
//        tab1.setCustomView(adapter.setOrderCount(1, Order.COMPLETED_TEXT+" - "+completedOrders));
//
//    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void onResume() {
        super.onResume();

        //TODO: Moved this code to onCreate Method, b/c this was refreshing the activity infinitely
       /* if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            requestPermissions(PermissionsRequest.writeExternalStorage, requestStorageId);
        }*/

        getOrders(0, 0, "", false);
        getNearestOrders();

        getAllOrders();

        highLightCurrentTab(currentTab);
        AppUtils.isDateTimeAuto(this);
        isLogout = false;

//        AppUtils.makeNotification(AppUtils.getUTCdatetimeAsString(),MainActivity.this);




        checkIfAlarmSet();
        checkIfAlarmSetForTracking();

//        enableAutoStart();

        AppUtils.allowInBatteryEfficientMode(this);
        AppUtils.checkBatteryOptimization(this);
        AppUtils.checkGPS(MainActivity.this);
        String packageName = "com.darewro.darian";
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Log.d("isBackgroundRestricted", "onCreate: activityManager.isBackgroundRestricted() = " + activityManager.isBackgroundRestricted());
        }
    }

    @Override
    public void setListeners() {
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());


                currentTab = tab.getPosition();
                highLightCurrentTab(currentTab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void handleIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("EventType")) {
                String eventType = bundle.getString("EventType");
                if (eventType.equals("ForceLogout")) {
                    AppUtils.forceLogout(MainActivity.this);
                }
                if (eventType.equals("Chat")) {
                    String orderId = bundle.getString("orderId");
                    Intent intent = new Intent(MainActivity.this, OrderDetailActivity.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("eventType", eventType);
                    startActivity(intent);
                } else if (eventType.equals("RiderNotification")) {
                    //String message = remoteMessage.getNotification().getBody();
                    //String title = remoteMessage.getNotification().getTitle();
                    String title = bundle.getString("Title");
                    String message = bundle.getString("Body");
                    String payload = bundle.getString("Payload");

                    Intent i = new Intent();
                    i.setClass(getApplicationContext(), FireBaseViewNotificationActivity.class);
                    i.putExtra("title", title);
                    i.putExtra("message", message);
                    if(payload!=null&&!payload.equals(""))
                        i.putExtra("payload",payload);

                    startActivity(i);
                }else if (eventType.equals("OrderReAssignedToRiderEvent")) {
                    String payload = bundle.getString("Payload");
                    DbUtils.deleteOrderData(payload);

                }else if (eventType.equals("OrderCancelledtoRiderEvent")) {
                    String payload = bundle.getString("Payload");
                    DbUtils.deleteOrderData(payload);
                }
//                else if(eventType.equalsIgnoreCase("AssignedOrderRejectionPushEvent")) {
//                    //AlertDialogUtils.showAlertDialog(MainActivity.this, AlertDialogUtils.ALERT_DIALOG_ACCEPT_REJECT, title, message, getString(R.string.details), DialogActivity.this);
//                    try {
//                        JSONObject payload = new JSONObject(bundle.getString("Payload"));
//                        Bundle bundle1 = new Bundle();
//                        bundle1.putString("orderId", payload.getString("orderID"));
//                        AppUtils.switchActivity(MainActivity.this, OrderDetailActivity.class, bundle1);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
                else if(eventType.equalsIgnoreCase("AssignedOrderAcceptanceBenefitPushEvent")){
//                    AlertDialogUtils.showAlertDialog(MainActivity.this, AlertDialogUtils.ALERT_DIALOG_ACCEPT_REJECT, title, message, getString(R.string.details), DialogActivity.this);
                    try {
                        JSONObject payload = new JSONObject(bundle.getString("Payload"));

                        String acceptMessage = payload.getString("pushNotiAcceptMessage").equals("")?"":payload.getString("pushNotiAcceptMessage").substring(payload.getString("pushNotiAcceptMessage").indexOf("###")+3).replace("###","\n");
                        String rejectMessage = payload.getString("pushNotiRejectMessage").equals("")?"":payload.getString("pushNotiRejectMessage").substring(payload.getString("pushNotiRejectMessage").indexOf("###")+3).replace("###","\n");

                        SharedPreferenceHelper.saveString(payload.getString("orderID")+"_acceptance",acceptMessage,getApplicationContext());
                        SharedPreferenceHelper.saveString(payload.getString("orderID")+"_rejection",rejectMessage,getApplicationContext());

                        Bundle bundle1 = new Bundle();
                        bundle1.putString("orderId", payload.getString("orderID"));
                        AppUtils.switchActivity(MainActivity.this, OrderDetailActivity.class, bundle1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    if (bundle.containsKey("Payload")) {
                        String title = bundle.getString("Title");
                        String message = bundle.getString("Body");
                        String orderId = bundle.getString("Payload");

                        Intent intent = new Intent(MainActivity.this, OrderDetailActivity.class);
                        intent.putExtra("orderId", orderId);
                        startActivity(intent);
                    }
                }
            } else {
                if (bundle.containsKey("Payload")) {

                    String title = bundle.getString("Title");
                    String message = bundle.getString("Body");
                    String orderId = bundle.getString("Payload");

                    Intent intent = new Intent(MainActivity.this, OrderDetailActivity.class);
                    intent.putExtra("orderId", orderId);
                    startActivity(intent);
                }
            }
        } /*else {
            if (bundle.containsKey("title") && bundle.containsKey("message") && bundle.containsKey("orderId")) {
                String title = getIntent().getStringExtra("title");
                String message = getIntent().getStringExtra("message");
                String orderId = getIntent().getStringExtra("orderId");

                Bundle bun = new Bundle();
                bun.putString("EXTRA_CONTACT_JID", title);
                bun.putString("orderId", orderId);
                AppUtils.switchActivity(MainActivity.this, OrderDetailActivity.class, bun);

            }
        }*/
    }


    public void getOrders(int pageIndex, int limit, String search, boolean IsCustomer) {

        //AlertDialogUtils.getInstance().showLoading(MainActivity.this);
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
                return AppUtils.getStandardHeaders(MainActivity.this);
            }
        };

        //String getPartners = ApiCalls.getNewOrderListing() + "?UserID=99388" + "&IsCustomer=" + IsCustomer;
        String getPartners = ApiCalls.getNewOrderListing() + "?UserID=" + SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, MainActivity.this) + "&IsCustomer=" + IsCustomer;
        OrdersListingHandler ordersListingHandler = new OrdersListingHandler(MainActivity.this, getPartners, this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), getPartners, Request.Method.GET, MainActivity.this, ordersListingHandler);
        jsonObjectRequestCall.sendData(false);
    }

    public void getAllOrders() {

        //AlertDialogUtils.getInstance().showLoading(MainActivity.this);
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
                return AppUtils.getStandardHeaders(MainActivity.this);
            }
        };

        //String getPartners = ApiCalls.getNewOrderListing() + "?UserID=99388" + "&IsCustomer=" + IsCustomer;
        String getPartners = ApiCalls.getAllOrderListing() +"?Limit=2500"+"&StartingIndex=0"+"&SearchString="+"&SearchBy=9"+"&UserID=" + SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, MainActivity.this)
                + "&OrderType=3" +"&PartnerType=4" +"&_=1739360982676";

        AllOrdersListHandler ordersListingHandler = new AllOrdersListHandler(MainActivity.this, getPartners, this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), getPartners, Request.Method.GET, MainActivity.this, ordersListingHandler);
        jsonObjectRequestCall.sendData(false);

    }
    public void getNearestOrders() {

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
                return AppUtils.getStandardHeaders(MainActivity.this);
            }
        };

        String getNearestOrders = ApiCalls.getNearestOrders();
        NearestOrdersHandler ordersListingHandler = new NearestOrdersHandler(MainActivity.this, getNearestOrders, this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), getNearestOrders, Request.Method.GET, MainActivity.this, ordersListingHandler);
        jsonObjectRequestCall.sendData(false);
    }


    public void getOrderDetails(String orderId) {

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
                return AppUtils.getStandardHeaders(MainActivity.this);
            }
        };

        String getOrderDetails = ApiCalls.getOrderDetails() + "?OrderID=" + orderId;
        OrdersDetailsHandler ordersListingHandler = new OrdersDetailsHandler(MainActivity.this, getOrderDetails, this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), getOrderDetails, Request.Method.GET, MainActivity.this, ordersListingHandler);
        jsonObjectRequestCall.sendData();
    }

    @Override
    public void onSuccess(String calledApi, String json) {
        Log.d("TAG111","CalledAPI ="+calledApi+", json ="+json);
        //status change callback
        AppUtils.makeNotification(json, MainActivity.this);

        if (isLogout) {
            stopAlarm();
            stopAlarmForTracking();
            AppUtils.pushRiderStats(MainActivity.this, false, false);
            String token = SharedPreferenceHelper.getString(SharedPreferenceHelper.FIRE_BASE_TOKEN, MainActivity.this);
            SharedPreferenceHelper.clearPrefrences(MainActivity.this);
            SharedPreferenceHelper.saveString(SharedPreferenceHelper.FIRE_BASE_TOKEN, token, MainActivity.this);
            SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.FORCE_STOP, true, MainActivity.this);
            AppUtils.switchActivity(MainActivity.this, LoginActivity.class, null);
            bindLocationService(false);
            DbUtils.deleteAllData();
            MainActivity.this.finish();
        } else {
            if (statusSwitch.isChecked()) {
                checkIfAlarmSet();
                checkIfAlarmSetForTracking();
                SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.IS_AVAILABLE, true, MainActivity.this);
                bindLocationService(true);
            } else {
                stopAlarm();
                stopAlarmForTracking();
                SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.FORCE_STOP, true, MainActivity.this);
                SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.IS_AVAILABLE, false, MainActivity.this);
                bindLocationService(false);
                AppUtils.pushRiderStats(MainActivity.this, false, false);
            }
        }
    }


    @Override
    public void onSuccess(String calledApi, Order order) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("order", order);

    }

//    private void populateData() {
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                List<OrdersTable> ordersTables = DbUtils.getAllOrders();
//
//                List<Order> orders = new ArrayList<>();
//                for (OrdersTable ordersTable : ordersTables) {
//                    orders.add(ordersTable.getOrders());
//                }
//
//                initAdapter(orders);
//            }
//        });
//    }

    private void initAdapter(List<Order> orders) {
        Collections.sort(orders, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return Integer.valueOf(o1.getStatusType()).compareTo(Integer.valueOf(o2.getStatusType()));
            }
        });

        final List<Order> newTabList = new ArrayList<>();
        final List<Order> completedTabList = new ArrayList<>();

        for (int i = 0; i < orders.size(); i++) {
            if (Integer.parseInt(orders.get(i).getStatusType()) < Integer.parseInt(Order.PAYMENT_RECEIVED)) {
                newTabList.add(orders.get(i));
            } else {
                completedTabList.add(orders.get(i));
            }
        }


        runOnUiThread(() -> {
           // AppUtils.makeNotification("Completed Orders"+completedTabList.toString(), MainActivity.this);
            adapter.getItem(0).setData(newTabList);
            adapter.getItem(1).setData(completedTabList);
            //

        });

        syncOrderList(orders);

        tabLayout.getTabAt(0).setText(Order.COMPLETED_TEXT+"-"+newTabList.size());
        tabLayout.getTabAt(1).setText(Order.COMPLETED_TEXT+"-"+completedTabList.size());
    }

    @Override
    public void onSuccess(String calledApi, final List<Order> orders) {
//        populateData();

        initAdapter(orders);

    }

    @Override
    public void onSuccess(List<AllOrder> allOrders, String calledApi) {
        adapter.getItem(2).setData(true,allOrders);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tabLayout.getTabAt(2).setText(Order.All_ORDERS+"-"+allOrders.size());
                //adapter.getItem(2).setData(allOrders);
            }
        });
    }


    @Override
    public void onSuccess(String calledApi, final List<Order> orders, final List<Order> completedOrders) {

//        Collections.sort(orders, new Comparator<Order>() {
//            @Override
//            public int compare(Order o1, Order o2) {
//                return Integer.valueOf(o1.getStatusType()).compareTo(Integer.valueOf(o2.getStatusType()));
//            }
//        });

//        final List<Order> newTabList = new ArrayList<>();
//        final List<Order> completedTabList = new ArrayList<>();
//
//        for (int i = 0; i < orders.size(); i++) {
//            if (Integer.parseInt(orders.get(i).getStatusType()) < Integer.parseInt(Order.PAYMENT_RECEIVED)) {
//                newTabList.add(orders.get(i));
//            } else {
//                completedTabList.add(orders.get(i));
//            }
//        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.getItem(0).setData(orders);
                adapter.getItem(1).setData(completedOrders);

                // adapter.getItem(2).setData(completedTabList);

            }
        });

//        Log.i("comorders**",completedOrders.get(0).getOrderDetails().getAcceptanceDateTime());

//        Collections.sort(completedOrders, new Comparator<Order>() {
//            DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
//            @Override
//            public int compare(Order order1, Order order2) {
//                try {
//                    if(null == order1.getOrderDetails().getAcceptanceDateTime()) {
//                        return null == order2.getOrderDetails().getAcceptanceDateTime() ? 0 : 1;
//                    }
//                    else if(null == order2.getOrderDetails().getAcceptanceDateTime()) {
//                        return -1;
//                    }
////                    return str1.compareTo(str2);
//                    return f.parse(order1.getOrderDetails().getAcceptanceDateTime()).compareTo(f.parse(order2.getOrderDetails().getAcceptanceDateTime()));
//                } catch (ParseException e) {
//                    throw new IllegalArgumentException(e);
//                }
//            }
//        });
//        Log.i("ordered comorders**",completedOrders.get(0).getOrderDetails().getAcceptanceDateTime());

        syncOrderList(orders);
//        Log.i("OrderSizenew",orders.size()+"");
//        Log.i("OrderSizecomplete",completedOrders.size()+"");
//        TabLayout.Tab newTab = tabLayout.getTabAt(0);
//        newTab.setText(Order.NEW_TEXT+"-"+orders.size());
//        TabLayout.Tab completedTab = tabLayout.getTabAt(1);
//        completedTab.setText(Order.COMPLETED_TEXT+"-"+completedOrders.size());
            highLightCurrentTab(tabLayout.getSelectedTabPosition());
//        setOrderCount(orders.size(),completedOrders.size());
    }



    @Override
    public void onSuccess(String calledApi, final List<NearestOrder> orders, boolean nearest) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.getItem(2).setData(orders, true);
            }
        });
    }

    private void syncOrderList(List<Order> orders) {

        String id = "";
        String orderUserIds = "";
        boolean hasPending = false;

        Log.i("orders**",orders.toString());

        Collections.sort(orders, new Comparator<Order>() {
            DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            @Override
            public int compare(Order order1, Order order2) {
                try {
                    if(null == order1.getOrderDetails().getAcceptanceDateTime()) {
                        return null == order2.getOrderDetails().getAcceptanceDateTime() ? 1 : 0;
                    }
                    else if(null == order2.getOrderDetails().getAcceptanceDateTime()) {
                        return 1;
                    }
//                    return str1.compareTo(str2);
                    return f.parse(order1.getOrderDetails().getAcceptanceDateTime()).compareTo(f.parse(order2.getOrderDetails().getAcceptanceDateTime()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
        Log.i("ordered orders**",orders.toString());

        for (Order order : orders) {

            if (order.getOrderDetails().getStatus().equals(Order.RIDER_ACCEPTED) || order.getOrderDetails().getStatus().equals(Order.DELIVERED)) {
                if (id.equals("")) {
                    id += String.valueOf(order.getOrderDetails().getId());
                    orderUserIds += String.valueOf(order.getOrderDetails().getId() + ":" + order.getOrderDetails().getCustomer().getId());
                } else {
                    if (id.contains(",")) {
                        boolean ifExists = false;
                        String[] orderIds = id.split(",");
                        for (int i = 0; i < orderIds.length; i++) {
                            if (order.getOrderDetails().getId().equals(orderIds[i])) {
                                ifExists = true;
                                break;
                            }
                        }
                        if (ifExists) {
                        } else {
                            id += "," + order.getOrderDetails().getId();
                            orderUserIds += "," + String.valueOf(order.getOrderDetails().getId() + ":" + order.getOrderDetails().getCustomer().getId());
                        }
                    } else {
                        if (id.equals(order.getOrderDetails().getId())) {

                        } else {
                            id += "," + order.getOrderDetails().getId();
                            orderUserIds += "," + String.valueOf(order.getOrderDetails().getId() + ":" + order.getOrderDetails().getCustomer().getId());

                        }
                    }
                }
            }
            if (order.getOrderDetails().getIsAccepted() == null && order.getOrderDetails().getStatus().equals(Order.RIDER_ASSIGNED)) {
                hasPending = true;
            }
        }

        SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.HAS_PENDING_ORDERS, hasPending, MainActivity.this);

        SharedPreferenceHelper.saveString(SharedPreferenceHelper.ORDER_ID, id, MainActivity.this);
        SharedPreferenceHelper.saveString(SharedPreferenceHelper.ORDER_USER_ID, orderUserIds, MainActivity.this);

       /* if (!SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, MainActivity.this).equals("")) {
            AppUtils.startXMPPService(MainActivity.this);
        }else{
            AppUtils.stopXMPPService(MainActivity.this);
        }*/
    }


    @Override
    public void onSuccess(String calledApi) {

    }

    @Override
    public void onError(String calledApi) {
        adapter.getItem(0).setError();
        adapter.getItem(1).setError();
        adapter.getItem(2).setError();

    }

    @Override
    public void onError(String calledApi, int errorCode) {
        if (errorCode == 403)
            AlertDialogUtils.showAlertDialog(MainActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), getString(R.string.app_update), getString(R.string.ok), null, true);
    }


    @Override
    public void onError(String calledApi, String errorMessage) {
        adapter.getItem(0).setError();
        adapter.getItem(1).setError();

        AppUtils.makeNotification(errorMessage, MainActivity.this);
        if (calledApi.equalsIgnoreCase(ApiCalls.changeStatus())) {
            statusSwitch.setOnCheckedChangeListener(null);
            statusSwitch.setChecked(!statusSwitch.isChecked());
            if (statusSwitch.isChecked()) {
                statusSwitch.setText("Available");
            } else {
                statusSwitch.setText("Unavailable");
            }
            statusSwitch.setOnCheckedChangeListener(this);

        }
    }

    @Override
    public void onError(String calledApi, String errorMessage, int errorCode) {
        if (errorCode == 403)
            AlertDialogUtils.showAlertDialog(MainActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), errorMessage, getString(R.string.ok), null, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        handleBackKeyEvent();
    }

    public void handleBackKeyEvent() {

        if (isDoublePress) {
            super.onBackPressed();
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isDoublePress = false;
            }
        }, 300);
        isDoublePress = true;

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            /*case R.id.profile: {
                AppUtils.switchActivity(MainActivity.this, ProfileActivity.class, null);
            }
            break;
            */
            case R.id.logout: {

                String orderId = SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, getApplicationContext());

                if (orderId.equals("")) {

                    if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.HAS_PENDING_ORDERS, MainActivity.this)) {
                        AlertDialogUtils.showAlertDialog(MainActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.warning), getString(R.string.warning_logout_p), getString(R.string.ok), MainActivity.this);

                    } else
                        AlertDialogUtils.showConfirmationAlertDialog(MainActivity.this, AlertDialogUtils.ALERT_DIALOG_LOGOUT, getString(R.string.confirmation), getString(R.string.logout), MainActivity.this, null);

                } else {
                    AlertDialogUtils.showAlertDialog(MainActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.warning), getString(R.string.warning_logout), getString(R.string.ok), MainActivity.this);
                }


            }
            break;
            case R.id.aboutUs: {
                AlertDialogUtils.showAlertDialog(MainActivity.this, AlertDialogUtils.ALERT_DIALOG_ABOUT, getString(R.string.about_us), getString(R.string.version), getString(R.string.ok), MainActivity.this);
            }
            break;
            case R.id.notifications: {
                AppUtils.switchActivity(MainActivity.this, NotificationsActivity.class, null);
            }
            break;
            case R.id.dashboard: {
                AppUtils.switchActivity(MainActivity.this, RiderDashboardActivity.class, null);
//                AlertDialogUtils.showAlertDialog(MainActivity.this, AlertDialogUtils.ALERT_DIALOG_ABOUT, getString(R.string.about_us), getString(R.string.version), getString(R.string.ok), MainActivity.this);
            }
            break;
        }
        return false;
    }

    private void changeStatus(final boolean status) {
        Log.d("TAG11","IsAvailable= "+String.valueOf(status));
        InitApi initApi = new InitApi() {
            @Override
            public HashMap<String, String> getBody() {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("riderId", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, MainActivity.this)/*"6"*/);
                params.put("userId", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, MainActivity.this)/*"6"*/);
                params.put("isAvailable", String.valueOf(status));
                return params;
            }

            @Override
            public HashMap<String, Object> getObjBody() {
                return null;
            }

            @Override
            public HashMap<String, String> getHeader() {
                return AppUtils.getStandardHeaders(MainActivity.this);
            }
        };

        StatusHandler partnersHandler = new StatusHandler(MainActivity.this, ApiCalls.changeStatus(), this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), ApiCalls.changeStatus(), Request.Method.POST, MainActivity.this, partnersHandler);
        jsonObjectRequestCall.sendData();

    }

    private void changeAvailabilityStatus(final boolean status) {


        InitApi initApi = new InitApi() {
            @Override
            public HashMap<String, String> getBody() {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("riderId", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, MainActivity.this)/*"6"*/);
                params.put("userId", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, MainActivity.this)/*"6"*/);
                params.put("isAvailable", String.valueOf(status));
                return params;
            }

            @Override
            public HashMap<String, Object> getObjBody() {
                return null;
            }

            @Override
            public HashMap<String, String> getHeader() {
                return AppUtils.getStandardHeaders(MainActivity.this);
            }
        };

        StatusHandler partnersHandler = new StatusHandler(MainActivity.this, ApiCalls.changeStatus());
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), ApiCalls.changeStatus(), Request.Method.POST, MainActivity.this, partnersHandler);
        jsonObjectRequestCall.sendData();


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
        if (alertId == AlertDialogUtils.ALERT_DIALOG_GPS) {
            launchGPSSettings();
        }
        if (alertId == AlertDialogUtils.ALERT_DIALOG_DISCLAIMER) {
            requestPermissions(MainActivity.this);
        }

    }

    @Override
    public void OnCancel(int alertId) {
        if (alertId == AlertDialogUtils.ALERT_DIALOG_CHANGE_STATUS) {
            statusSwitch.setOnCheckedChangeListener(null);
            statusSwitch.setChecked(!statusSwitch.isChecked());
            if (statusSwitch.isChecked()) {
                statusSwitch.setText("Available");
            } else {
                statusSwitch.setText("Unavailable");
            }
            statusSwitch.setOnCheckedChangeListener(this);
        }
        if (alertId == AlertDialogUtils.ALERT_DIALOG_LOGOUT) {

        }
        if (alertId == AlertDialogUtils.ALERT_DIALOG_DISCLAIMER) {

        }
    }

    @Override
    public void OnSuccess(int alertId, Object object) {
        if (alertId == AlertDialogUtils.ALERT_DIALOG_CHANGE_STATUS) {
            changeStatus((boolean) object);
            SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.IS_AVAILABLE, (boolean) object,MainActivity.this);
        }
        if (alertId == AlertDialogUtils.ALERT_DIALOG_LOGOUT) {
            if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, MainActivity.this)) {
                isLogout = true;
                changeStatus(false);
            } else {
                String token = SharedPreferenceHelper.getString(SharedPreferenceHelper.FIRE_BASE_TOKEN, MainActivity.this);
                SharedPreferenceHelper.clearPrefrences(MainActivity.this);
                SharedPreferenceHelper.saveString(SharedPreferenceHelper.FIRE_BASE_TOKEN, token, MainActivity.this);
                AppUtils.switchActivity(MainActivity.this, LoginActivity.class, null);
                MainActivity.this.finish();
            }
        }
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
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        String orderId = SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, getApplicationContext());

        if (isChecked) {
            AlertDialogUtils.showConfirmationAlertDialog(MainActivity.this, AlertDialogUtils.ALERT_DIALOG_CHANGE_STATUS, getString(R.string.confirmation), getString(R.string.status_change), MainActivity.this, isChecked);
            if (isChecked) {
                statusSwitch.setText("Available");

            } else {
                statusSwitch.setText("Unavailable");
            }
        } else {
            if (orderId.equals("")) {
                if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.HAS_PENDING_ORDERS, MainActivity.this)) {
                    statusSwitch.setOnCheckedChangeListener(null);
                    statusSwitch.setChecked(true);
                    statusSwitch.setText("Available");
                    statusSwitch.setOnCheckedChangeListener(this);
                    AlertDialogUtils.showAlertDialog(MainActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.warning), getString(R.string.warning_status_p), getString(R.string.ok), MainActivity.this);

                } else {
                    AlertDialogUtils.showConfirmationAlertDialog(MainActivity.this, AlertDialogUtils.ALERT_DIALOG_CHANGE_STATUS, getString(R.string.confirmation), getString(R.string.status_change), MainActivity.this, isChecked);

                    if (isChecked) {
                        statusSwitch.setText("Available");

                    } else {
                        statusSwitch.setText("Unavailable");
                    }
                }

            } else {
                statusSwitch.setOnCheckedChangeListener(null);
                statusSwitch.setChecked(true);
                statusSwitch.setText("Available");
                statusSwitch.setOnCheckedChangeListener(this);
                AlertDialogUtils.showAlertDialog(MainActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.warning), getString(R.string.warning_status), getString(R.string.ok), MainActivity.this);

            }
        }


    }

    private void bindLocationService(boolean isStart) {
        if (isStart) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        ) {
                    AlertDialogUtils.showAlertDialog(MainActivity.this, AlertDialogUtils.ALERT_DIALOG_DISCLAIMER, getString(R.string.disclaimer), getString(R.string.disclaimer_text), getString(R.string.accept), MainActivity.this);

                } else {
                    initService();
                }
            } else
                initService();
        } else
            stopService();
    }

    public void initService() {
        SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.FORCE_STOP, false, MainActivity.this);
        if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, getApplicationContext())) {

            Intent intent = new Intent(MainActivity.this, LocationService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }

    }

    public void stopService() {
        if ((!SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, MainActivity.this)) && SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, MainActivity.this).equals("")) {
            SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.FORCE_STOP, true, MainActivity.this);
            Intent intent = new Intent(MainActivity.this, LocationService.class);
            if (LocationService.isRunning(MainActivity.this)) {
                stopService(intent);
            }
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == PermissionsRequest.LOCATION_REQUEST_CODE) {
//            if (grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                initService();
//            }
//            if (requestCode == PermissionsRequest.FOREGROUND_SERVICE) {
//                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, getApplicationContext())) {
//
//                        Intent intent = new Intent(MainActivity.this, LocationService.class);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            startForegroundService(new Intent(getApplicationContext(), LocationService.class));
//                        } else {
//                            startService(new Intent(getApplicationContext(), LocationService.class));
//                        }
//                    }
//                } else
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.FOREGROUND_SERVICE}, PermissionsRequest.FOREGROUND_SERVICE);
//            }
//
//        }
//        if (requestCode == requestStorageId) {
//
//          /*  if ((grantResults.length > 0) &&(grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
//            } else {
//                AppUtils.makeNotification("Please choose Allow to proceed!", MainActivity.this);
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    requestPermissions(PermissionsRequest.writeExternalStorage, requestStorageId);
//                }
//            }
//*/
//        }
//    }

    private void setAlarm() {

        String shiftTimings = SharedPreferenceHelper.getString(SharedPreferenceHelper.RIDER_SHIFT_SETTING, MainActivity.this);
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

                    if (calendar.get(Calendar.HOUR_OF_DAY) < 2) {
                        calendar.set(Calendar.HOUR_OF_DAY, 2);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.AM_PM, Calendar.AM);

                    } else {
                        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
                        calendar.set(Calendar.HOUR_OF_DAY, 2);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.AM_PM, Calendar.AM);

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            if (calendar.get(Calendar.HOUR_OF_DAY) < 2) {
                calendar.set(Calendar.HOUR_OF_DAY, 2);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.AM_PM, Calendar.AM);


            } else {
                calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
                calendar.set(Calendar.HOUR_OF_DAY, 2);
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
                        1234567, intentsOpen, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private void checkIfAlarmSet() {
        Intent intent = new Intent(
                getApplicationContext(),
                AlarmReceiver.class);

        boolean alarmUp = (PendingIntent.getBroadcast(MainActivity.this, 1234567,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE) != null);

        if (alarmUp) {
        } else {
            setAlarm();
        }

    }

    private void checkIfAlarmSetForTracking() {
        /*if (Build.VERSION.SDK_INT <= 23) {
            return;
        }*/
        Intent intent = new Intent(
                getApplicationContext(),
                AlarmReceiverForRiderTrackingService.class);

        boolean alarmUp = (PendingIntent.getBroadcast(MainActivity.this, 12345678,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE) != null);

        //  if (alarmUp) {
        //   } else {
        setAlarmForTracking();
        // }

    }

    private void stopAlarm() {

        Intent intent = new Intent(
                getApplicationContext(),
                AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(
                        getApplicationContext(),
                        1234567, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        boolean alarmUp = (PendingIntent.getBroadcast(MainActivity.this, 1234567,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE) != null);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmUp) {
            alarmManager.cancel(pendingIntent);
        } else {

        }

    }

    private void stopAlarmForTracking() {
      /*  if (Build.VERSION.SDK_INT >= 23) {
            return;
        }*/

        Intent intent = new Intent(
                getApplicationContext(),
                AlarmReceiverForRiderTrackingService.class);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(
                        getApplicationContext(),
                        12345678, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        boolean alarmUp = (PendingIntent.getBroadcast(MainActivity.this, 12345678,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE) != null);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmUp) {
            alarmManager.cancel(pendingIntent);
        } else {

        }

    }

    private void launchGPSSettings() {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);

    }

    private void setAlarmForTracking() {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, AlarmReceiverForRiderTrackingService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 12345678, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (Build.VERSION.SDK_INT >= 23) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + 15);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d(TAG, " --------  ALARM TRIGGERED");

        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, ALARM_PERIODIC_TIME, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, ALARM_PERIODIC_TIME, pendingIntent);
        }
    }

    public void enableAutoStart() {

        if (Build.BRAND.equalsIgnoreCase("xiaomi")) {
            new AlertDialog.Builder(MainActivity.this).setTitle("Enable AutoStart")
                    .setMessage("Please allow AppName to always run in the background,else our services can't be accessed.")
                    .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            try {
                                Intent intent = new Intent();
                                intent.setComponent(new ComponentName("com.miui.securitycenter",
                                        "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .show();
        } else if (Build.BRAND.equalsIgnoreCase("Letv")) {

            new AlertDialog.Builder(MainActivity.this).setTitle("Enable AutoStart")
                    .setMessage("Please allow AppName to always run in the background,else our services can't be accessed.")
                    .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            try {
                                Intent intent = new Intent();
                                intent.setComponent(new ComponentName("com.letv.android.letvsafe",
                                        "com.letv.android.letvsafe.AutobootManageActivity"));
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .show();


        } else if (Build.BRAND.equalsIgnoreCase("Honor")) {

            new AlertDialog.Builder(MainActivity.this).setTitle("Enable AutoStart")
                    .setMessage("Please allow AppName to always run in the background,else our services can't be accessed.")
                    .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            try {
                                Intent intent = new Intent();

                                intent.setComponent(new ComponentName("com.huawei.systemmanager",
                                        "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .show();


        } else if (Build.MANUFACTURER.equalsIgnoreCase("oppo")) {


            new AlertDialog.Builder(MainActivity.this).setTitle("Enable AutoStart")
                    .setMessage("Please allow AppName to always run in the background,else our services can't be accessed.")
                    .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Intent intent = new Intent();
                                intent.setClassName("com.coloros.safecenter",
                                        "com.coloros.safecenter.permission.startup.StartupAppListActivity");
                                startActivity(intent);
                            } catch (Exception e) {
                                try {
                                    Intent intent = new Intent();
                                    intent.setClassName("com.oppo.safe",
                                            "com.oppo.safe.permission.startup.StartupAppListActivity");
                                    startActivity(intent);
                                } catch (Exception ex) {
                                    try {
                                        Intent intent = new Intent();
                                        intent.setClassName("com.coloros.safecenter",
                                                "com.coloros.safecenter.startupapp.StartupAppListActivity");
                                        startActivity(intent);
                                    } catch (Exception exx) {

                                    }
                                }
                            }
                        }
                    }).show();


        } else if (Build.MANUFACTURER.contains("vivo")) {

            new AlertDialog.Builder(MainActivity.this).setTitle("Enable AutoStart")
                    .setMessage("Please allow AppName to always run in the background,else our services can't be accessed.")
                    .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Intent intent = new Intent();
                                intent.setComponent(new ComponentName("com.iqoo.secure",
                                        "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"));
                                startActivity(intent);
                            } catch (Exception e) {
                                try {
                                    Intent intent = new Intent();
                                    intent.setComponent(new ComponentName("com.vivo.permissionmanager",
                                            "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                                    startActivity(intent);
                                } catch (Exception ex) {
                                    try {
                                        Intent intent = new Intent();
                                        intent.setClassName("com.iqoo.secure",
                                                "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager");
                                        startActivity(intent);
                                    } catch (Exception exx) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    }).show();
        }
    }

    static boolean secondPassR = false;
    private static final int REQUEST_CODE_MULTIPLE_PERMISSIONS = 57;
    private boolean requestPermissions(Activity activity)
    {
        Log.v(TAG, "requestPermissions() called");
        String[] permissions;

      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            permissions = new String[]{ Manifest.permission.ACCESS_BACKGROUND_LOCATION,Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.POST_NOTIFICATIONS};
        }else*/
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.POST_NOTIFICATIONS};
        }else {
            permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        }

        Permissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                initService();
            }
        });

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            final List<String> permissionsList = new ArrayList<>();
            final List<String> reasonList = new ArrayList<>();

            if(!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION, activity))
            {
                reasonList.add("LOCATION PERMISSION is required to capture Real Time Location for accurate order assignment!\n\n");
            }
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) && (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) || secondPassR)
            {
                if (!addPermission(permissionsList, Manifest.permission.ACCESS_BACKGROUND_LOCATION, activity))
                {
                    reasonList.add("BACKGROUND PERMISSION is required to capture Real Time Location for accurate order assignment in the background.\n\n");
                }
            }
            if (permissionsList.size() > 0)
            {
                if (reasonList.size() > 0)
                {
                    // Need Rationale
                    StringBuilder message = new StringBuilder(reasonList.get(0));
                    for (int i = 1; i < reasonList.size(); i++)
                    {
                        message.append(" ").append(reasonList.get(i));
                    }
                    final androidx.appcompat.app.AlertDialog.Builder builder =
                            new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.Theme_AppCompat_Light));
                    builder.setTitle("Requesting Permission.");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    {
                        builder.setMessage(Html.fromHtml(message.toString(), Html.FROM_HTML_MODE_LEGACY));
                    }
                    else
                    {
                        builder.setMessage(Html.fromHtml(message.toString()));
                    }
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(dialog -> {
                        Log.v(TAG, "Requesting permissions");
                        activity.requestPermissions(permissionsList.toArray(new String[0]),  // newer Java recommended
                                REQUEST_CODE_MULTIPLE_PERMISSIONS);
                    });
                    builder.show();
                    return false;
                }
                activity.requestPermissions(permissionsList.toArray(new String[0]),   // newer Java recommended
                        REQUEST_CODE_MULTIPLE_PERMISSIONS);
            }
            else
            {
                return true;
            }
        }
        else
        {
            initService();
            return true;
        }*/
        return false;
    }

    @TargetApi(23)
    private boolean addPermission(List<String> permissionsList, String permission, Activity activity)
    {
        Log.v(TAG,
                "addPermission() called with: " + "permissionsList = " +
                        "[" + permissionsList + "], permission = [" + permission + "]");
        if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED)
        {
            permissionsList.add(permission);
            // Check for Rationale Option
            return activity.shouldShowRequestPermissionRationale(permission);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String permission = "";
        Log.v(TAG,
                "onRequestPermissionsResult() called with: " + "requestCode = [" + requestCode +
                        "], permissions = [" + Arrays.toString(permissions) + "]," +
                        " grantResults = [" + Arrays.toString(grantResults) + "]");
        if (requestCode == REQUEST_CODE_MULTIPLE_PERMISSIONS)
        {
            for (int i = 0; i < permissions.length; i++)
            {
                switch (permissions[i])
                {
                    case Manifest.permission.ACCESS_FINE_LOCATION:
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        {
                            Log.d(TAG, "H@H: onRequestPermissionsResult: FINE LOCATION PERMISSION");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                            {
                                Log.d(TAG, "H@H: Now requesting BACKGROUND PERMISSION for version 11+");
                                secondPassR = true;
                                requestPermissions(MainActivity.this);
                                return;
                            }
                        }
                        break;
                    case Manifest.permission.ACCESS_BACKGROUND_LOCATION:
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        {
                            Log.d(TAG, "H@H: onRequestPermissionsResult: BACKGROUND PERMISSION");
                            initService();

                            if (requestCode == PermissionsRequest.FOREGROUND_SERVICE) {
                                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                                    if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, getApplicationContext())) {

                                        Intent intent = new Intent(MainActivity.this, LocationService.class);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            startForegroundService(new Intent(getApplicationContext(), LocationService.class));
                                        } else {
                                            startService(new Intent(getApplicationContext(), LocationService.class));
                                        }
                                    }
                                } else
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.FOREGROUND_SERVICE}, PermissionsRequest.FOREGROUND_SERVICE);
                            }
                        }
                        break;
                }
            }
        }
        Log.d(TAG, "Starting primary activity");
        secondPassR = false;
        if (requestCode == PermissionsRequest.FOREGROUND_SERVICE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, getApplicationContext())) {

                    Intent intent = new Intent(MainActivity.this, LocationService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(new Intent(getApplicationContext(), LocationService.class));
                    } else {
                        startService(new Intent(getApplicationContext(), LocationService.class));
                    }
                }
            } else
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.FOREGROUND_SERVICE}, PermissionsRequest.FOREGROUND_SERVICE);
        }
//        startActivityForResult(new Intent(context, PchaDemoPhg_Activity.class), EXIT_QUIT);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener)
    {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", okListener)
                .create()
                .show();
    }

    private void fetchRemoteConfig() {
        // set text from remote
        // [START fetch_config_with_callback]
        // override default value from Remote Config

        firebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            Log.d(TAG, "should_connect_new_xmpp: " + firebaseRemoteConfig.getBoolean("should_connect_new_xmpp"));
                            SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.SHOULD_CONNECT_XMPP,firebaseRemoteConfig.getBoolean("should_connect_new_xmpp"),MainActivity.this);
                        } else {
                        }
                    }
                });
        // [END fetch_config_with_callback]
    }

}
