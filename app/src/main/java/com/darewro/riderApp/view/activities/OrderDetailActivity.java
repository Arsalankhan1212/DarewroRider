package com.darewro.riderApp.view.activities;

import static com.darewro.riderApp.view.utils.LocationUtils.TYPE_DELIVERY;
import static com.darewro.riderApp.view.utils.LocationUtils.TYPE_DELIVERY_STRING;
import static com.darewro.riderApp.view.utils.LocationUtils.TYPE_PAYMENT;
import static com.darewro.riderApp.view.utils.LocationUtils.TYPE_PAYMENT_STRING;
import static com.darewro.riderApp.view.utils.LocationUtils.TYPE_PICKUP;
import static com.darewro.riderApp.view.utils.LocationUtils.TYPE_PICKUP_STRING;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.darewro.riderApp.R;
import com.darewro.riderApp.data.api.ApiCalls;
import com.darewro.riderApp.data.api.Interface.InitApi;
import com.darewro.riderApp.data.api.handlers.AllOrdersListHandler;
import com.darewro.riderApp.data.api.handlers.AssignOrderToRiderHandler;
import com.darewro.riderApp.data.api.handlers.GenericHandler;
import com.darewro.riderApp.data.api.handlers.InvoiceDetailsHandler;
import com.darewro.riderApp.data.api.handlers.OrdersDetailsHandler;
import com.darewro.riderApp.data.api.models.OrderComponent;
import com.darewro.riderApp.data.api.models.OrderLocation;
import com.darewro.riderApp.data.api.models.assignOrder.AssignOrderResponseData;
import com.darewro.riderApp.data.api.requests.JsonObjectRequestCall;
import com.darewro.riderApp.data.db.model.OrderPath;
import com.darewro.riderApp.presenter.RefreshListener;
import com.darewro.riderApp.presenter.ResponseListenerAssignOrderToRider;
import com.darewro.riderApp.presenter.ResponseListenerGeneric;
import com.darewro.riderApp.presenter.ResponseListenerInvoiceDetails;
import com.darewro.riderApp.presenter.ResponseListenerOrdersDetails;
import com.darewro.riderApp.view.adapters.RecyclerAdapterOrderDetailGeneral;
import com.darewro.riderApp.view.adapters.RecyclerAdapterOrderDetailPartner;
import com.darewro.riderApp.view.customViews.ItemOffsetDecoration;
import com.darewro.riderApp.view.listeners.AlertDialogDeliverOrderResponseListener;
import com.darewro.riderApp.view.listeners.AlertDialogResponseListener;
import com.darewro.riderApp.view.locationService.LocationService;
import com.darewro.riderApp.view.models.Invoice;
import com.darewro.riderApp.view.models.LatLong;
import com.darewro.riderApp.view.models.Order;
import com.darewro.riderApp.view.models.OrderPartners;
import com.darewro.riderApp.view.utils.AlertDialogUtils;
import com.darewro.riderApp.view.utils.AppUtils;
import com.darewro.riderApp.view.utils.DbUtils;
import com.darewro.riderApp.view.utils.LocationUtils;
import com.darewro.riderApp.view.utils.Logger;
import com.darewro.riderApp.view.utils.PermissionsRequest;
import com.darewro.riderApp.view.utils.SharedPreferenceHelper;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderDetailActivity extends BaseActivity implements ResponseListenerOrdersDetails, ResponseListenerGeneric, ResponseListenerAssignOrderToRider, ResponseListenerInvoiceDetails, RefreshListener, AlertDialogResponseListener, AlertDialogDeliverOrderResponseListener, Runnable//, OnMapReadyCallback
{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final float ZOOM_LEVEL = 16f;
    private static final int REQUEST_CODE_CAMERA = 2;
    static String voiceNote = null;
    private static Order order = null;
    private static boolean isAccepted = false;
    private static boolean isPaymentReceived = false;
    private static SeekBar seekBar;
    private static MediaPlayer mediaPlayer = new MediaPlayer();
    private static boolean wasPlaying = false;

    private static boolean isFromAllOrderListScreen = false;
    LinearLayout bottomLayoutHideable, orderAdditionalLocationLayout, layoutPenalty, layoutBonus;
    ImageButton collapseButton;
    Button btnAccept, btnReject, btnDelivered, btnCompleted, btnInvoice;
    LinearLayout root_pending, root_delivered, root_completed, root_invoice;
    ImageView btnCall, btnText, btnDirections, btnVoiceMsg;
    TextView txtBonus, txtPenalty, txtLabelBonus, txtLabelPenalty;
    String realDistance = "0";
    String relativeDistance = "0";
    TextView is_corporate, two_way, isFrequent, isDuplicate, isOnlinePayment, isReserved, more_than_3000, is_edited, is_fragile, multi_picked, multi_delivered, is_surprise, created_by;
    //private GoogleMap mMap;
    //private SupportMapFragment mapFragment;
    private final String TAG = "Firebase Tracking";
    private String orderId = null;
    private String eventType = null;
    private LatLng riderLatLng;
    private List<LatLong> latLngList;
    private LinkedHashMap<String, LatLong> locations;
    private TextView name, detail, location, locationContact, refId, orderAdditionalLocationDetail, estimatedDeliveryFee;
    private Marker riderMarker;
    private LinearLayout addOnsLayout;
    private TextView addOns, addOnsTitle, unread_badge;
    private RecyclerView recyclerView;
    private AsyncTask<Void, Void, Boolean> asyncTask;
    private long mLastClickTime = 0;
    private MapView osm;
    private MapController mc;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        handleIntent();
        initializeViews();
        setListeners();

    }

    @Override
    public void initializeViews() {
//        mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        refId = findViewById(R.id.refId);
        name = findViewById(R.id.name);
        detail = findViewById(R.id.detail);
        location = findViewById(R.id.location);
        locationContact = findViewById(R.id.location_contact);
        collapseButton = findViewById(R.id.collapse_button);
        bottomLayoutHideable = findViewById(R.id.bottom_layout_hideable);
        btnAccept = findViewById(R.id.accept);
        btnReject = findViewById(R.id.reject);
        btnDelivered = findViewById(R.id.delivered);
        btnCompleted = findViewById(R.id.completed);
        btnInvoice = findViewById(R.id.invoice);
        root_pending = findViewById(R.id.root_pending);
        txtBonus = findViewById(R.id.bonus);
        txtPenalty = findViewById(R.id.deduction);
        txtLabelBonus = findViewById(R.id.label_bonus);
        txtLabelPenalty = findViewById(R.id.label_deduction);
        layoutBonus = findViewById(R.id.layout_bonus);
        layoutPenalty = findViewById(R.id.layout_deduction);

        root_completed = findViewById(R.id.root_completed);
        root_delivered = findViewById(R.id.root_delivered);
        root_invoice = findViewById(R.id.root_invoice);
        btnText = findViewById(R.id.btnText);
        btnCall = findViewById(R.id.btnCall);
        btnVoiceMsg = findViewById(R.id.btnVoiceMsg);
        estimatedDeliveryFee = findViewById(R.id.estimated_delivery_fee);
        btnDirections = findViewById(R.id.btnDirection);
        orderAdditionalLocationDetail = findViewById(R.id.location_additional);
        orderAdditionalLocationLayout = findViewById(R.id.location_additional_layout);
        unread_badge = findViewById(R.id.unread_badge);

        addOnsTitle = findViewById(R.id.add_ons_title);
        addOnsLayout = findViewById(R.id.add_ons_layout);
        addOns = findViewById(R.id.add_ons);
        addOns.setMovementMethod(new ScrollingMovementMethod());

        is_corporate = findViewById(R.id.is_corporate);
        two_way = findViewById(R.id.two_way);
        isFrequent = findViewById(R.id.isFrequent);
        isDuplicate = findViewById(R.id.isDuplicate);
        isOnlinePayment = findViewById(R.id.isOnlinePayment);
        isReserved = findViewById(R.id.isReserved);
        more_than_3000 = findViewById(R.id.more_than_3000);
        is_edited = findViewById(R.id.is_edited);
        is_fragile = findViewById(R.id.is_fragile);
        multi_picked = findViewById(R.id.multi_picked);
        multi_delivered = findViewById(R.id.multi_delivered);
        is_surprise = findViewById(R.id.is_surprise);
        created_by = findViewById(R.id.created_by);

        AppUtils.setMontserratBold(refId);
        AppUtils.setMontserratBold(name);
        AppUtils.setMontserrat(detail);
        AppUtils.setMontserrat(location);
        AppUtils.setMontserrat(btnAccept);
        AppUtils.setMontserrat(btnReject);
        AppUtils.setMontserrat(btnDelivered);
        AppUtils.setMontserrat(btnCompleted);
        AppUtils.setMontserrat(btnInvoice);
        AppUtils.setMontserrat(estimatedDeliveryFee);
        AppUtils.setMontserrat(orderAdditionalLocationDetail);
        AppUtils.setMontserratBold(addOnsTitle);
        AppUtils.setMontserrat(addOns);
        AppUtils.setMontserrat(txtBonus);
        AppUtils.setMontserrat(txtPenalty);
        AppUtils.setMontserratBold(findViewById(R.id.estimated_delivery_fee_title));
        AppUtils.setMontserratBold(findViewById(R.id.location_label_additional));
        AppUtils.setMontserratBold(findViewById(R.id.location_label));


        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(OrderDetailActivity.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        recyclerView.addItemDecoration(new ItemOffsetDecoration(5));

        populateOrder();

        Context ctx = this;
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        osm = (MapView) findViewById(R.id.mapview);
        osm.setTileSource(TileSourceFactory.MAPNIK);
        osm.setBuiltInZoomControls(false);
        osm.setMultiTouchControls(true);

        mc = (MapController) osm.getController();
        mc.setZoom(15);


    }

    @Override
    public void setListeners() {

        collapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomLayoutHideable.getVisibility() == View.VISIBLE) {
                    bottomLayoutHideable.setVisibility(View.GONE);
                    collapseButton.setImageResource(R.drawable.ic_arrow_up);
                } else {
                    bottomLayoutHideable.setVisibility(View.VISIBLE);
                    collapseButton.setImageResource(R.drawable.ic_arrow_down);
                }
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, OrderDetailActivity.this)) {

                    //if user is from AllOrdersFragment, then first assign order to rider and then change order status to accepted
                    if (isFromAllOrderListScreen){
                        if (orderId != null)
                            assignOrderToRider(orderId);
                        else
                            AppUtils.makeNotificationToast("Make sure order id is not null",OrderDetailActivity.this);
                    }
                    else{
                        isAccepted = true;
                        if (order != null)
                            changeOrderStatus(isAccepted);
//                    }
                    }
                } else
                    AlertDialogUtils.showAlertDialog(OrderDetailActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.warning), getString(R.string.warning_status_a), getString(R.string.ok), OrderDetailActivity.this);

            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, OrderDetailActivity.this)) {
                    isAccepted = false;
                    if (order != null)
                        changeOrderStatus(isAccepted);
                } else
                    AlertDialogUtils.showAlertDialog(OrderDetailActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.warning), getString(R.string.warning_status_a), getString(R.string.ok), OrderDetailActivity.this);

            }
        });

        btnDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (order.getOrderDetails().isCorporateCustomers()) {
                    Log.d("TAG111","SHOWING PAYMENT DIALOG");
                    AlertDialogUtils.showPaymentReceivedDialog(OrderDetailActivity.this, AlertDialogUtils.ALERT_DIALOG_PAYMENT_RECEIVED, getString(R.string.confirmation), getString(R.string.payment_conifrmation), getString(R.string.yes), getString(R.string.no), OrderDetailActivity.this);
                } else {
                    Log.d("TAG111","Async Task");
                    AlertDialogUtils.getInstance().showLoading(OrderDetailActivity.this);
                    if (asyncTask != null && (asyncTask.getStatus() == AsyncTask.Status.PENDING || asyncTask.getStatus() == AsyncTask.Status.RUNNING)) {
                        asyncTask.cancel(true);
                    }
                    initAsyncTask(orderId);
                }
            }
        });

        btnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, OrderDetailActivity.this))
                    completedOrder(order.getOrderDetails().getId());
                else
                    AlertDialogUtils.showAlertDialog(OrderDetailActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.warning), getString(R.string.warning_status_a), getString(R.string.ok), OrderDetailActivity.this);
            }
        });

        btnInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                completedOrder(order.getOrderDetails().getId());
            }
        });

        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (ContextCompat.checkSelfPermission(OrderDetailActivity.this, Manifest.permission.READ_PHONE_STATE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(OrderDetailActivity.this, new String[]{Manifest.permission.SEND_SMS}, PermissionsRequest.SMS_REQUEST_CODE);
//                } else {
//                    Uri uri = Uri.parse("smsto:" + order.getOrderDetails().getCustomer().getMsisdn());
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                    intent.putExtra("sms_body", "");
//                    startActivity(intent);
//                }


                Bundle bundle = new Bundle();
                bundle.putString("EXTRA_CONTACT_JID", order.getOrderDetails().getCustomer().getId());
                bundle.putString("EXTRA_CONTACT_NAME", order.getOrderDetails().getCustomer().getUserName());
                bundle.putString("EXTRA_ORDER_ID", order.getOrderDetails().getId());
                AppUtils.switchActivity(OrderDetailActivity.this, ChatActivity.class, bundle);

            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("tel:" + order.getOrderDetails().getCustomer().getMsisdn()));
                startActivity(intent);

            }
        });

        btnVoiceMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getVoiceNote(order.getOrderDetails().getId());


            }
        });

        btnDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = generatePath();
                Uri gmmIntentUri = Uri.parse(url);

                Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                intent.setPackage("com.google.android.apps.maps");
                try {
                    startActivityForResult(intent, 101);
                } catch (ActivityNotFoundException ex) {
                    try {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        startActivityForResult(unrestrictedIntent, 101);
                    } catch (ActivityNotFoundException innerEx) {
                        AppUtils.makeNotification("Please install a maps application", OrderDetailActivity.this);
                    }
                }
            }
        });

        locationContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.openDialer(locationContact.getText().toString(), OrderDetailActivity.this);
            }
        });
    }

    private void initAsyncTask(final String orderId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {

                List<OrderPath> paths = DbUtils.getAllOrderPathForOrder(order.getOrderDetails().getId());
                Log.i("paths size", paths.size() + "");
                Log.d("TAG111","Path size= "+paths.size());
                try {
                    double rDistance = 0;
                    if (paths.size() > 1) {
                        rDistance = AppUtils.calculateDistance(OrderDetailActivity.this, paths);
                    }

                    realDistance = rDistance + "";//dist;

//                        for(OrderPath path : paths){
//                            if(path.isRepeated())
//                                paths.remove(path);
//                        }
                    Iterator<OrderPath> i = paths.iterator();
                    while (i.hasNext()) {
                        OrderPath path = i.next(); // must be called before you can call i.remove()
                        // Do something
                        if (path.isRepeated())
                            i.remove();
                    }

                    Log.i("paths size", paths.size() + "");

                    double relDistance = 0;
                    if (paths.size() > 1) {
                        relDistance = AppUtils.calculateDistance(OrderDetailActivity.this, paths);
                    }

                    relativeDistance = relDistance + "";//dist;


                } catch (Exception e) {
                    Log.d("TAG111","Exception in calculating path= "+e.getMessage());
                    Log.i("Exception", e.getMessage());
                    AlertDialogUtils.getInstance().hideLoading();
                    e.printStackTrace();
                }


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialogUtils.getInstance().hideLoading();
                        if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, OrderDetailActivity.this)) {

                            AlertDialogUtils.deliverOrder(OrderDetailActivity.this, getResources().getString(R.string.deliver) + " " + getResources().getString(R.string.order) + " ", "Distance Covered\n" + realDistance + " / " + relativeDistance, OrderDetailActivity.this);
                        } else {
                            AlertDialogUtils.showAlertDialog(OrderDetailActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.warning), getString(R.string.warning_status_a), getString(R.string.ok), OrderDetailActivity.this);
                        }

                    }
                });
            }
        });
        /*if (asyncTask == null) {
            asyncTask = new AsyncTask<Void, Void, Boolean>() {
                //String finalDistance = null;
                @Override
                protected Boolean doInBackground(Void... voids) {
                    Log.d("TAG111","DO in background");
                    if (isCancelled()) {
                        AlertDialogUtils.getInstance().hideLoading();
                        return false;
                    }
                    List<OrderPath> paths = DbUtils.getAllOrderPathForOrder(order.getOrderDetails().getId());
                    Log.i("paths size", paths.size() + "");
                    Log.d("TAG111","Path size= "+paths.size());
                    try {
                        double rDistance = 0;
                        if (paths.size() > 1) {
                            rDistance = AppUtils.calculateDistance(OrderDetailActivity.this, paths);
                        }

                        realDistance = rDistance + "";//dist;

//                        for(OrderPath path : paths){
//                            if(path.isRepeated())
//                                paths.remove(path);
//                        }
                        Iterator<OrderPath> i = paths.iterator();
                        while (i.hasNext()) {
                            OrderPath path = i.next(); // must be called before you can call i.remove()
                            // Do something
                            if (path.isRepeated())
                                i.remove();
                        }

                        Log.i("paths size", paths.size() + "");

                        double relDistance = 0;
                        if (paths.size() > 1) {
                            relDistance = AppUtils.calculateDistance(OrderDetailActivity.this, paths);
                        }

                        relativeDistance = relDistance + "";//dist;


                    } catch (Exception e) {
                        Log.d("TAG111","Exception in calculating path= "+e.getMessage());
                        Log.i("Exception", e.getMessage());
                        AlertDialogUtils.getInstance().hideLoading();
                        e.printStackTrace();
                    }

                    return true;
                }

                @Override
                protected void onCancelled(Boolean aBoolean) {
                    super.onCancelled(aBoolean);
                    Log.d("TAG111","onCancelled= "+aBoolean);
                    AlertDialogUtils.getInstance().hideLoading();
                    Log.i("onCancelled", "onCancelled");
                    asyncTask = null;
                }

                @Override
                protected void onCancelled() {
                    super.onCancelled();
                    Log.d("TAG111","onCancelled= ");
                    AlertDialogUtils.getInstance().hideLoading();
                    Log.i("onCancelled", "onCancelled");
                    asyncTask = null;
                }

                @Override
                protected void onPostExecute(Boolean aVoid) {
                    super.onPostExecute(aVoid);
                    Log.i("onPostExecute", "onPostExecute");
                    Log.d("TAG111","onPostExecute");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialogUtils.getInstance().hideLoading();
                            if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, OrderDetailActivity.this)) {

                                AlertDialogUtils.deliverOrder(OrderDetailActivity.this, getResources().getString(R.string.deliver) + " " + getResources().getString(R.string.order) + " ", "Distance Covered\n" + realDistance + " / " + relativeDistance, OrderDetailActivity.this);
                            } else {
                                AlertDialogUtils.showAlertDialog(OrderDetailActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.warning), getString(R.string.warning_status_a), getString(R.string.ok), OrderDetailActivity.this);
                            }

                        }
                    });
                    //AlertDialogUtils.getInstance().hideLoading();
                    asyncTask = null;
                }
            };
            asyncTask.execute();
        }else{
            Log.d("TAG111","Async task is null");
        }*/

    }

    @Override
    public void handleIntent() {
        voiceNote = null;
        if (getIntent().hasExtra("order")) {
            order = getIntent().getExtras().getParcelable("order");
            orderId = null;
        }
        if (getIntent().hasExtra("orderId")) {
            orderId = getIntent().getExtras().getString("orderId");
            Log.d("TAG111","ORDER id= "+orderId);
            order = null;
        }
        if (getIntent().hasExtra("eventType")) {
            orderId = getIntent().getExtras().getString("orderId");
            order = null;
            eventType = getIntent().getExtras().getString("eventType");
        }

        if (getIntent().hasExtra("fromAllOrderList")){
            isFromAllOrderListScreen = getIntent().getBooleanExtra("fromAllOrderList",false);
        }

    }

//    private void setupMap() {
//        mMap.getUiSettings().setZoomGesturesEnabled(true);
//        mMap.getUiSettings().setRotateGesturesEnabled(false);
//        mMap.getUiSettings().setZoomControlsEnabled(false);
//        mMap.getUiSettings().setCompassEnabled(true);
//        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
//        mMap.setBuildingsEnabled(true);
//        mMap.setIndoorEnabled(true);
//
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(this, PermissionsRequest.LOCATION_PERMISSIONS, PermissionsRequest.LOCATION_REQUEST_CODE);
//        } else {
//            mMap.setMyLocationEnabled(true);
//        }
//
//    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth() * 2, vectorDrawable.getIntrinsicHeight() * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                dispatchTakePictureIntent();
            } else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        }

        if (requestCode == PermissionsRequest.LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //mapFragment.getMapAsync(OrderDetailActivity.this);
                //setupMap();
            } else {
                // Permission was denied or request was cancelled
            }
        }

        if (requestCode == PermissionsRequest.CALL_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + order.getOrderDetails().getCustomer().getMsisdn()));
                startActivity(intent);
            } else {

            }
        }

        if (requestCode == PermissionsRequest.SMS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Uri uri = Uri.parse("smsto:" + order.getOrderDetails().getCustomer().getMsisdn());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.putExtra("sms_body", "");
                startActivity(intent);

            } else {

            }
        }


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        setListeners();
//
//
//        setupMap();
//        if (order != null && order.getOrderDetails() != null) {
//            setupPins();
//        }
//    }
    private void setupPins() {

        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        LatLong obj = null;
        locations = new LinkedHashMap<>();
        latLngList = new ArrayList<>();
        try {
            for (OrderComponent orderComponent : order.getOrderDetails().getOrderComponents()) {
                for (OrderLocation orderLocation : orderComponent.getOrderLocations()) {

                    LatLng latLng = new LatLng(Double.parseDouble(orderLocation.getLatitude()), Double.parseDouble(orderLocation.getLongitude()));
                    String name = "";
                    if (orderLocation.getName() != null && !TextUtils.isEmpty(orderLocation.getName())) {
                        name = orderLocation.getName();
                    } else {
                        switch (orderLocation.getLocationType()) {
                            case TYPE_PICKUP:
                                name = TYPE_PICKUP_STRING;
                                break;
                            case TYPE_DELIVERY:
                                name = TYPE_DELIVERY_STRING;
                                break;
                            case TYPE_PAYMENT:
                                name = TYPE_PAYMENT_STRING;
                                break;
                        }
                    }

                    if (locations.containsKey(orderLocation.getLatitude() + "," + orderLocation.getLongitude())) {
                    } else {
                        locations.put((orderLocation.getLatitude() + "," + orderLocation.getLongitude()), new LatLong(latLng, name));
                    }
                    if (order.getOrderDetails().getStatus().equals(Order.RIDER_ACCEPTED) || order.getOrderDetails().getStatus().equals(Order.DELIVERED) || order.getOrderDetails().getStatus().equals(Order.PAYMENT_RECEIVED)) {
                        LatLong latLong = new LatLong(latLng, name);
                        latLngList.add(latLong);
                        if (obj == null)
                            obj = latLong;
                        else
                            addMarker(new GeoPoint(Double.parseDouble(orderLocation.getLatitude()), Double.parseDouble(orderLocation.getLongitude())), R.drawable.delivery, name);
//                        showPinOnMap(latLong, BitmapDescriptorFactory.fromResource(R.drawable.delivery));
                    } else {
                        if (orderLocation.getLocationType().equals(TYPE_PICKUP)) {
                            LatLong latLong = new LatLong(latLng, name);
                            latLngList.add(latLong);
                            if (obj == null)
                                obj = latLong;
                            else
                                addMarker(new GeoPoint(Double.parseDouble(orderLocation.getLatitude()), Double.parseDouble(orderLocation.getLongitude())), R.drawable.delivery, name);
//                              showPinOnMap(latLong, BitmapDescriptorFactory.fromResource(R.drawable.delivery));
                        }
                    }
                }
            }

            if (order.getOrderDetails().getPaymentLocation() != null) {
                LatLng latLng = new LatLng(Double.parseDouble(order.getOrderDetails().getPaymentLocation().getLatitude()), Double.parseDouble(order.getOrderDetails().getPaymentLocation().getLongitude()));
                String name = "";
                if (order.getOrderDetails().getPaymentLocation().getName() != null && !TextUtils.isEmpty(order.getOrderDetails().getPaymentLocation().getName())) {
                    name = order.getOrderDetails().getPaymentLocation().getName();
                } else {
                    switch (order.getOrderDetails().getPaymentLocation().getLocationType()) {
                        case TYPE_PICKUP:
                            name = TYPE_PICKUP_STRING;
                            break;
                        case TYPE_DELIVERY:
                            name = TYPE_DELIVERY_STRING;
                            break;
                        case TYPE_PAYMENT:
                            name = TYPE_PAYMENT_STRING;
                            break;
                    }

                }
                if (locations.containsKey(order.getOrderDetails().getPaymentLocation().getLatitude() + "," + order.getOrderDetails().getPaymentLocation().getLongitude())) {
                } else {
                    locations.put((order.getOrderDetails().getPaymentLocation().getLatitude() + "," + order.getOrderDetails().getPaymentLocation().getLongitude()), new LatLong(latLng, name));
                }
                if (order.getOrderDetails().getStatus().equals(Order.RIDER_ACCEPTED) || order.getOrderDetails().getStatus().equals(Order.DELIVERED) || order.getOrderDetails().getStatus().equals(Order.PAYMENT_RECEIVED)) {
                    LatLong latLong = new LatLong(latLng, name);
                    latLngList.add(latLong);
                    addMarker(new GeoPoint(Double.parseDouble(order.getOrderDetails().getPaymentLocation().getLatitude()), Double.parseDouble(order.getOrderDetails().getPaymentLocation().getLongitude())), R.drawable.payment, name);
//                    showPinOnMap(latLong, BitmapDescriptorFactory.fromResource(R.drawable.payment));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (obj != null)
            addMarker(new GeoPoint(obj.getLatLng().latitude, obj.getLatLng().longitude), R.drawable.payment, obj.getName());

//        showPinOnMap(obj, BitmapDescriptorFactory.fromResource(R.drawable.delivery));

    }


//    private void registerFirestore() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection("Orders").document(order.getOrderDetails().getId()).collection("LocationHistory").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w(TAG, "listen:error", e);
//                    return;
//                }
//                for (DocumentChange dc : snapshots.getDocumentChanges()) {
//                    switch (dc.getType()) {
//                        case ADDED:
//                            Log.d(TAG, "New Loc: " + dc.getDocument().getData());
//                            riderLatLng = new LatLng(Double.valueOf(dc.getDocument().get("lat").toString()), Double.valueOf(dc.getDocument().get("long").toString()));
//
//                            break;
//                        case MODIFIED:
//                            Log.d(TAG, "Modified Loc: " + dc.getDocument().getData());
//                            break;
//                        case REMOVED:
//                            Log.d(TAG, "Removed Loc: " + dc.getDocument().getData());
//                            break;
//                    }
//
//                }
//                updateLocation(riderLatLng);
//            }
//        });
//    }

//    private void updateLocation(LatLng location) {
//
//        animatePinOnMap(location);
//
//    }

//    private void showPinOnMap(LatLong latLong, BitmapDescriptor bitmapDescriptor) {
//        mMap.addMarker(new MarkerOptions().icon(bitmapDescriptor).position(latLong.getLatLng()).title(latLong.getName()));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLong.getLatLng().latitude, latLong.getLatLng().longitude), 15));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
//    }

    public void addMarker(GeoPoint center, int pin, String name) {
        org.osmdroid.views.overlay.Marker marker = new org.osmdroid.views.overlay.Marker(osm);
        marker.setPosition(center);
        marker.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM);
        marker.setIcon(ContextCompat.getDrawable(this,pin));
//        osm.getOverlays().clear();
        osm.getOverlays().add(marker);
        osm.invalidate();
        marker.setTitle(name);
        mc.animateTo(center);

    }

//    private void animatePinOnMap(LatLng latLng) {
//        if (latLng == null)
//            return;
//        if (riderMarker == null) {
//            riderMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.cycling)).position(riderLatLng));
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL), 2000, null);
//        } else {
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(riderLatLng));
//            MarkerAnimation.animateMarkerToGB(riderMarker, riderLatLng, new LatLngInterpolator.Spherical());
//        }
//    }

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
                return AppUtils.getStandardHeaders(OrderDetailActivity.this);
            }
        };

        String getOrderDetails = ApiCalls.getOrderDetails() + "?OrderID=" + orderId + "&AppCall=" + true;
        OrdersDetailsHandler ordersListingHandler = new OrdersDetailsHandler(OrderDetailActivity.this, getOrderDetails, this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), getOrderDetails, Request.Method.GET, OrderDetailActivity.this, ordersListingHandler);
        jsonObjectRequestCall.sendData();

    }

    public void changeOrderStatus(final boolean status) {

        InitApi initApi = new InitApi() {
            @Override
            public HashMap<String, String> getBody() {

                HashMap<String, String> body = new HashMap<>();
                body.put("OrderID", order.getOrderDetails().getId());
                body.put("RiderId", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, OrderDetailActivity.this)/*"6"*/);
                body.put("IsAccepted", String.valueOf(status));
                body.put("UserId", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, OrderDetailActivity.this)/*"6"*/);
                body.put("Comment", "");
                return body;
            }

            @Override
            public HashMap<String, Object> getObjBody() {
                return null;
            }

            @Override
            public HashMap<String, String> getHeader() {
                return AppUtils.getStandardHeaders(OrderDetailActivity.this);
            }
        };

        String changeStatus = ApiCalls.changeOrderStatus();
        GenericHandler ordersListingHandler = new GenericHandler(OrderDetailActivity.this, changeStatus, this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), changeStatus, Request.Method.POST, OrderDetailActivity.this, ordersListingHandler);
        jsonObjectRequestCall.sendData();
    }

    private void assignOrderToRider(String orderId){
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
                return AppUtils.getStandardHeaders(OrderDetailActivity.this);
            }
        };

        String riderId = SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, OrderDetailActivity.this);
        String riderContact = SharedPreferenceHelper.getString(SharedPreferenceHelper.MSISDN, OrderDetailActivity.this);

        String getPartners = ApiCalls.assignOrderToRider() + "?orderID=" + orderId + "&riderId=" + riderId + "&UserID=" + riderId + "&ContactNumber=" + riderContact;

        AssignOrderToRiderHandler assignOrderToRiderHandler = new AssignOrderToRiderHandler(OrderDetailActivity.this, getPartners, this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), getPartners, Request.Method.PUT, OrderDetailActivity.this, assignOrderToRiderHandler);
        jsonObjectRequestCall.sendData(false);

    }

    public void deliverOrder(final String base64Image) {

        InitApi initApi = new InitApi() {
            @Override
            public HashMap<String, String> getBody() {
//                HashMap<String, String> body = new HashMap<>();
//                body.put("orderID", order.getOrderDetails().getId());
//                body.put("distanceTraveled",realDistance);
//                body.put("userId", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, OrderDetailActivity.this)/*"6"*/);
//                if (base64Image != null && !TextUtils.isEmpty(base64Image)) {
//                    body.put("image", base64Image);
//                }
//                if(order.getOrderDetails().isCorporateCustomers()){
//                    body.put("IsPaymentReceived",isPaymentReceived);
//                }
                return null;
            }

            @Override
            public HashMap<String, Object> getObjBody() {
                HashMap<String, Object> body = new HashMap<>();
                body.put("orderID", order.getOrderDetails().getId());
                //body.put("distanceTraveled",realDistance);
                //body.put("relativeDistance",relativeDistance);
                //body.put("distanceTraveled",realDistance);
                body.put("distanceTraveled", realDistance);
                body.put("clubDistanceTraveled", relativeDistance);
                body.put("userId", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, OrderDetailActivity.this)/*"6"*/);
                if (base64Image != null && !TextUtils.isEmpty(base64Image)) {
                    body.put("image", base64Image);
                }
                if (order.getOrderDetails().isCorporateCustomers()) {
                    body.put("IsPaymentReceived", isPaymentReceived);
                }
                return body;
            }

            @Override
            public HashMap<String, String> getHeader() {
                return AppUtils.getStandardHeaders(OrderDetailActivity.this);
            }
        };

        String deliverOrder = ApiCalls.deliverOrder();
        InvoiceDetailsHandler ordersListingHandler = new InvoiceDetailsHandler(OrderDetailActivity.this, deliverOrder, this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), new JSONObject(initApi.getObjBody()), deliverOrder, Request.Method.POST, OrderDetailActivity.this, true, ordersListingHandler);
        jsonObjectRequestCall.sendData();
        Log.d("TAG111","Deliver api is called");

    }

    public void completedOrder(String orderId) {

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
                return AppUtils.getStandardHeaders(OrderDetailActivity.this);
            }
        };

        String getInvoice = ApiCalls.getInvoice() + "?orderID=" + orderId;
        InvoiceDetailsHandler ordersListingHandler = new InvoiceDetailsHandler(OrderDetailActivity.this, getInvoice, this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), getInvoice, Request.Method.GET, OrderDetailActivity.this, ordersListingHandler);

        jsonObjectRequestCall.sendData();
    }

    public void getVoiceNote(String orderId) {

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
                return AppUtils.getStandardHeaders(OrderDetailActivity.this);
            }
        };

        String getVoiceNote = ApiCalls.getVoiceNote() + "?OrderId=" + orderId;
        GenericHandler voiceNoteHandler = new GenericHandler(OrderDetailActivity.this, getVoiceNote, this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), getVoiceNote, Request.Method.GET, OrderDetailActivity.this, voiceNoteHandler);

        jsonObjectRequestCall.sendData();
    }

    @Override
    public void onSuccess(String calledApi) {
        Log.d("TAG111","success calledApi= "+calledApi);
    }

    @Override
    public void onSuccess(String calledApi, String json) {
        Log.d("TAG111","success JSON= "+json);
        if (calledApi.contains(ApiCalls.getVoiceNote())) {
            Log.i("response", json);

            if (json != null && !json.equals(""))
                voiceNote = json;


            if (voiceNote != null && !voiceNote.equals("")) {
                Log.i("responsesuccess", voiceNote);
                showAlertDialog(OrderDetailActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, ApiCalls.BASE_URL_VOICE_NOTE_AZURE + voiceNote, getString(R.string.close), OrderDetailActivity.this);

//                if(order.getOrderDetails().getIsAccepted() != null && (order.getOrderDetails().getStatus().equals(Order.DELIVERED)||order.getOrderDetails().getStatus().equals(Order.RIDER_ACCEPTED)))
//                btnVoiceMsg.setVisibility(View.VISIBLE);
            }
//            else{
//                btnVoiceMsg.setVisibility(View.GONE);
//            }
        } else {
            AppUtils.makeNotification(json, OrderDetailActivity.this);
            if (isAccepted) {

                String id = SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, OrderDetailActivity.this);
                String orderUserIds = SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_USER_ID, OrderDetailActivity.this);
                if (id.equals("")) {
                    id += String.valueOf(order.getOrderDetails().getId());
                    orderUserIds += (order.getOrderDetails().getId()) + ":" + order.getOrderDetails().getCustomer().getId();

                } else {
                    id += "," + order.getOrderDetails().getId();
                    orderUserIds += "," + (order.getOrderDetails().getId()) + ":" + order.getOrderDetails().getCustomer().getId();

                }
                SharedPreferenceHelper.saveString(SharedPreferenceHelper.ORDER_ID, id, OrderDetailActivity.this);
                SharedPreferenceHelper.saveString(SharedPreferenceHelper.ORDER_USER_ID, orderUserIds, OrderDetailActivity.this);

                if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, getApplicationContext())) {

                    Intent intent = new Intent(OrderDetailActivity.this, LocationService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    } else {
                        startService(intent);
                    }
                }

                if (SharedPreferenceHelper.has(order.getOrderDetails().getId() + "_acceptance", OrderDetailActivity.this))
                    SharedPreferenceHelper.removeKey(order.getOrderDetails().getId() + "_acceptance", OrderDetailActivity.this);

                if (SharedPreferenceHelper.has(order.getOrderDetails().getId() + "_rejection", OrderDetailActivity.this))
                    SharedPreferenceHelper.removeKey(order.getOrderDetails().getId() + "_rejection", OrderDetailActivity.this);

                getOrderDetails(order.getOrderDetails().getId());


            } else
                OrderDetailActivity.this.finish();
        }
    }

    @Override
    public void onSuccess(String calledApi, Invoice invoice) {
        Log.d("TAG111","Invoice success= "+invoice.getStatus());
        DbUtils.deleteOrderData(order.getOrderDetails().getId());
        Bundle bundle = new Bundle();
        bundle.putParcelable("invoice", invoice);
        bundle.putParcelable("order", order);
        Intent intnt = new Intent(OrderDetailActivity.this, InvoiceActivity.class);
        intnt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (bundle != null) {
            intnt.putExtras(bundle);
        }

        startActivityForResult(intnt, 100);
    }

    @Override
    public void onSuccess(AssignOrderResponseData response, String calledApi) {
        Log.d("TAG111","Change order status, calledApi= "+calledApi);

        isAccepted = true;
        changeOrderStatus(isAccepted);
    }

    @Override
    public void onError(String calledApi, String errorMessage) {
        Log.d("TAG111","api error= "+errorMessage);
    }

    @Override
    public void onError(String calledApi, String errorMessage, int errorCode) {
        Log.d("TAG111","api error= "+errorMessage +", errorCode= "+errorCode);
        if (errorCode == 403)
            AlertDialogUtils.showAlertDialog(OrderDetailActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), errorMessage, getString(R.string.ok), null, true);

    }

    @Override
    public void onSuccess(String calledApi, Order order) {
        Log.d("TAG111","ORDER SUCCESS");
        OrderDetailActivity.order = order;
        setupPins();
        populateOrder();

    }

    @Override
    public void onError(String calledApi) {
        Log.d("TAG111","Error CalledApi= "+calledApi);
    }

    @Override
    public void onError(String calledApi, int errorCode) {
        Log.d("TAG111","Error CalledApi= "+calledApi +", errorCode= "+errorCode);
        if (errorCode == 403)
            AlertDialogUtils.showAlertDialog(OrderDetailActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), getString(R.string.app_update), getString(R.string.ok), null, true);

    }

    void populateOrder() {

        if (order != null) {

            if (order.getOrderDetails().getEstimatedDeliveryFee() != null) {
                estimatedDeliveryFee.setText(order.getOrderDetails().getEstimatedDeliveryFee() + " Rs");
            }

            if (order.getOrderDetails().getCustomer() != null) {
                name.setText(order.getOrderDetails().getCustomer().getUserName());
                detail.setText(order.getOrderDetails().getCustomer().getMsisdn());
            }
            if (order.getOrderDetails().getReferenceNumber() != null) {
                refId.setText("Ref # " + order.getOrderDetails().getReferenceNumber());
            }

            if (order.getOrderDetails().getOrderAdditionalComment() != null && !TextUtils.isEmpty(order.getOrderDetails().getOrderAdditionalComment())) {
                addOnsLayout.setVisibility(View.VISIBLE);
                addOns.setText(order.getOrderDetails().getOrderAdditionalComment());

//                addOns.setOnClickListener(new View.OnClickListener() {
//
//                    public void onClick(View arg0) {
//
//                        AlertDialogUtils.showAlert(order.getOrderDetails().getOrderAdditionalComment(), OrderDetailActivity.this);
//
//                    }
//                });
            } else {
                addOnsLayout.setVisibility(View.GONE);
            }

            if (order.getOrderDetails().getPaymentLocation() != null) {
                if (order.getOrderDetails().getPaymentLocation().getName() != null) {
                    location.setText(order.getOrderDetails().getPaymentLocation().getName() == null ? LocationUtils.TYPE_PAYMENT_STRING : order.getOrderDetails().getPaymentLocation().getName());

                    if (order.getOrderDetails().isSurpriseOrder())
                        locationContact.setText(order.getOrderDetails().getCustomer().getMsisdn());
                    else {
                        if (order.getOrderDetails().getPaymentLocation().getLocationContacts() != null && order.getOrderDetails().getPaymentLocation().getLocationContacts().size() > 0 && order.getOrderDetails().getPaymentLocation().getLocationContacts().get(0).getContactNumber() != null)
                            locationContact.setText(order.getOrderDetails().getPaymentLocation().getLocationContacts().get(0).getContactNumber());
                    }
                } else {
                    LatLng latlng = new LatLng(Double.parseDouble(order.getOrderDetails().getPaymentLocation().getLatitude()), Double.parseDouble(order.getOrderDetails().getPaymentLocation().getLongitude()));
                    String locationName = LocationUtils.getAddress(OrderDetailActivity.this, latlng);
                    location.setText(locationName == null ? LocationUtils.TYPE_PAYMENT_STRING : locationName);
                    if (order.getOrderDetails().isSurpriseOrder())
                        locationContact.setText(order.getOrderDetails().getCustomer().getMsisdn());
                    else {
                        if (order.getOrderDetails().getPaymentLocation().getLocationContacts() != null && order.getOrderDetails().getPaymentLocation().getLocationContacts().size() > 0 && order.getOrderDetails().getPaymentLocation().getLocationContacts().get(0).getContactNumber() != null)
                            locationContact.setText(order.getOrderDetails().getPaymentLocation().getLocationContacts().get(0).getContactNumber());
                    }
                }
            }

            if (order.getOrderDetails().getOrderComponents().get(0).getOrderLocations().get(0).getManualLocation() != null && !TextUtils.isEmpty(order.getOrderDetails().getOrderComponents().get(0).getOrderLocations().get(0).getManualLocation())) {
                orderAdditionalLocationDetail.setText(order.getOrderDetails().getOrderComponents().get(0).getOrderLocations().get(0).getManualLocation());
                orderAdditionalLocationLayout.setVisibility(View.VISIBLE);
            } else {
                orderAdditionalLocationLayout.setVisibility(View.GONE);
            }

            if (order.getOrderDetails().getIsAccepted() == null) {
                if (order.getOrderDetails().getStatus().equals(Order.RIDER_ASSIGNED) || order.getOrderDetails().getStatus().equals(Order.NEW)) {
                    root_pending.setVisibility(View.VISIBLE);
                    root_delivered.setVisibility(View.GONE);
                    root_completed.setVisibility(View.GONE);
                    root_invoice.setVisibility(View.GONE);
                    detail.setVisibility(View.GONE);
                    btnDirections.setVisibility(View.INVISIBLE);
                    btnCall.setVisibility(View.INVISIBLE);
                    btnText.setVisibility(View.INVISIBLE);
                    btnVoiceMsg.setVisibility(View.INVISIBLE);

                    if (SharedPreferenceHelper.has(order.getOrderDetails().getId() + "_acceptance", OrderDetailActivity.this) ||
                            SharedPreferenceHelper.has(order.getOrderDetails().getId() + "_rejection", OrderDetailActivity.this)
                    ) {
                        //                    Log.d("prefbonus",SharedPreferenceHelper.has(order.getOrderDetails().getId()+"_acceptance",OrderDetailActivity.this)+
//                            SharedPreferenceHelper.getString(order.getOrderDetails().getId()+"_acceptance",OrderDetailActivity.this)+"");
                        if (SharedPreferenceHelper.has(order.getOrderDetails().getId() + "_acceptance", OrderDetailActivity.this) &&
                                (!SharedPreferenceHelper.getString(order.getOrderDetails().getId() + "_acceptance", OrderDetailActivity.this).equals(""))) {
                            layoutBonus.setVisibility(View.VISIBLE);
                            txtBonus.setVisibility(View.VISIBLE);
                            txtLabelBonus.setVisibility(View.VISIBLE);
                            txtBonus.setText(SharedPreferenceHelper.getString(order.getOrderDetails().getId() + "_acceptance", OrderDetailActivity.this));
                        } else {
//                        Log.d("pref","in bonus");
                            layoutBonus.setVisibility(View.INVISIBLE);
                            txtBonus.setVisibility(View.INVISIBLE);
                            txtLabelBonus.setVisibility(View.INVISIBLE);
                        }
//                    Log.d("prefpenalty",SharedPreferenceHelper.has(order.getOrderDetails().getId()+"_rejection",OrderDetailActivity.this)+
//                            SharedPreferenceHelper.getString(order.getOrderDetails().getId()+"_rejection",OrderDetailActivity.this)+"");
                        if (SharedPreferenceHelper.has(order.getOrderDetails().getId() + "_rejection", OrderDetailActivity.this) &&
                                (!SharedPreferenceHelper.getString(order.getOrderDetails().getId() + "_rejection", OrderDetailActivity.this).equals(""))) {
                            txtPenalty.setText(SharedPreferenceHelper.getString(order.getOrderDetails().getId() + "_rejection", OrderDetailActivity.this));
                            layoutPenalty.setVisibility(View.VISIBLE);
                            txtPenalty.setVisibility(View.VISIBLE);
                            txtLabelPenalty.setVisibility(View.VISIBLE);
                        } else {
//                        Log.d("pref","in penalty");
                            layoutPenalty.setVisibility(View.INVISIBLE);
                            txtPenalty.setVisibility(View.INVISIBLE);
                            txtLabelPenalty.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        layoutPenalty.setVisibility(View.GONE);
                        txtPenalty.setVisibility(View.GONE);
                        txtLabelPenalty.setVisibility(View.GONE);
                        layoutBonus.setVisibility(View.GONE);
                        txtBonus.setVisibility(View.GONE);
                        txtLabelBonus.setVisibility(View.GONE);
                    }

                } else {
                    root_pending.setVisibility(View.GONE);
                    layoutPenalty.setVisibility(View.GONE);
                    layoutBonus.setVisibility(View.GONE);
                    root_delivered.setVisibility(View.GONE);
                    root_completed.setVisibility(View.GONE);
                    root_invoice.setVisibility(View.GONE);
                    detail.setVisibility(View.GONE);
                    btnCall.setVisibility(View.INVISIBLE);
                    btnText.setVisibility(View.INVISIBLE);
                    btnDirections.setVisibility(View.INVISIBLE);
                    btnVoiceMsg.setVisibility(View.INVISIBLE);
                }
            } else {
                if (order.getOrderDetails().getIsAccepted() != null && order.getOrderDetails().getStatus().equals(Order.DELIVERED)) {
                    root_pending.setVisibility(View.GONE);
                    layoutPenalty.setVisibility(View.GONE);
                    layoutBonus.setVisibility(View.GONE);
                    root_completed.setVisibility(View.VISIBLE);
                    root_invoice.setVisibility(View.GONE);
                    root_delivered.setVisibility(View.GONE);
                    detail.setVisibility(View.VISIBLE);
                    btnCall.setVisibility(View.VISIBLE);
                    btnText.setVisibility(View.VISIBLE);
                    btnVoiceMsg.setVisibility(View.VISIBLE);
                    //btnText.setVisibility(View.INVISIBLE);
                    btnDirections.setVisibility(View.VISIBLE);
                } else if (order.getOrderDetails().getIsAccepted() != null && order.getOrderDetails().getStatus().equals(Order.RIDER_ACCEPTED)) {
                    root_pending.setVisibility(View.GONE);
                    layoutPenalty.setVisibility(View.GONE);
                    layoutBonus.setVisibility(View.GONE);
                    root_completed.setVisibility(View.GONE);
                    root_delivered.setVisibility(View.VISIBLE);
                    root_invoice.setVisibility(View.GONE);
                    detail.setVisibility(View.VISIBLE);
                    btnCall.setVisibility(View.VISIBLE);
//                    btnText.setVisibility(View.INVISIBLE);
                    btnText.setVisibility(View.VISIBLE);
                    btnVoiceMsg.setVisibility(View.VISIBLE);
                    btnDirections.setVisibility(View.VISIBLE);
                } else if (order.getOrderDetails().getIsAccepted() != null && order.getOrderDetails().getStatus().equals(Order.PAYMENT_RECEIVED)) {
                    root_pending.setVisibility(View.GONE);
                    layoutPenalty.setVisibility(View.GONE);
                    layoutBonus.setVisibility(View.GONE);
                    root_completed.setVisibility(View.GONE);
                    root_invoice.setVisibility(View.VISIBLE);
                    root_delivered.setVisibility(View.GONE);
                    detail.setVisibility(View.GONE);
                    btnCall.setVisibility(View.INVISIBLE);
                    btnText.setVisibility(View.INVISIBLE);
                    btnDirections.setVisibility(View.INVISIBLE);
                    btnVoiceMsg.setVisibility(View.INVISIBLE);
                    collapseButton.performClick();
                } else {
                    root_pending.setVisibility(View.GONE);
                    layoutPenalty.setVisibility(View.GONE);
                    layoutBonus.setVisibility(View.GONE);
                    root_completed.setVisibility(View.GONE);
                    root_delivered.setVisibility(View.GONE);
                    root_invoice.setVisibility(View.GONE);
                    detail.setVisibility(View.GONE);
                    btnCall.setVisibility(View.INVISIBLE);
                    btnText.setVisibility(View.INVISIBLE);
                    btnDirections.setVisibility(View.INVISIBLE);
                    btnVoiceMsg.setVisibility(View.INVISIBLE);
                }
            }

            if (order.getOrderDetails().getOrderType().equals(Order.ORDER_TYPE_PARTNER)) {
                RecyclerAdapterOrderDetailPartner adapter = new RecyclerAdapterOrderDetailPartner(OrderDetailActivity.this, order, order.getOrderPartners());
                recyclerView.setAdapter(adapter);
            } else {
                RecyclerAdapterOrderDetailGeneral adapter = new RecyclerAdapterOrderDetailGeneral(OrderDetailActivity.this, order, order.getOrderDetails().getOrderComponents());
                recyclerView.setAdapter(adapter);

            }

            if (order.getOrderDetails().isCorporateCustomers())
                is_corporate.setVisibility(View.VISIBLE);
            else
                is_corporate.setVisibility(View.GONE);

            if (order.getOrderDetails().isTwoWayOrder())
                two_way.setVisibility(View.VISIBLE);
            else
                two_way.setVisibility(View.GONE);

            if (order.getOrderDetails().isFrequentCustomer())
                isFrequent.setVisibility(View.VISIBLE);
            else
                isFrequent.setVisibility(View.GONE);

            if (order.getOrderDetails().isDuplicateOrder())
                isDuplicate.setVisibility(View.VISIBLE);
            else
                isDuplicate.setVisibility(View.GONE);

            if (order.getOrderDetails().isOnlinePayment())
                isOnlinePayment.setVisibility(View.VISIBLE);
            else
                isOnlinePayment.setVisibility(View.GONE);

            if (order.getOrderDetails().isReserved())
                isReserved.setVisibility(View.VISIBLE);
            else
                isReserved.setVisibility(View.GONE);

            if (order.getOrderDetails().isMoreThan3000())
                more_than_3000.setVisibility(View.VISIBLE);
            else
                more_than_3000.setVisibility(View.GONE);

            if (order.getOrderDetails().isEdited())
                is_edited.setVisibility(View.VISIBLE);
            else
                is_edited.setVisibility(View.GONE);

            if (order.getOrderDetails().isFragile())
                is_fragile.setVisibility(View.VISIBLE);
            else
                is_fragile.setVisibility(View.GONE);

            if (order.getOrderDetails().isMultiplePickup())
                multi_picked.setVisibility(View.VISIBLE);
            else
                multi_picked.setVisibility(View.GONE);

            if (order.getOrderDetails().isMultipleDelivery())
                multi_delivered.setVisibility(View.VISIBLE);
            else
                multi_delivered.setVisibility(View.GONE);

            if (order.getOrderDetails().isSurpriseOrder())
                is_surprise.setVisibility(View.VISIBLE);
            else
                is_surprise.setVisibility(View.GONE);

            created_by.setText(order.getOrderDetails().getCreatedByName());

            int unread_count = DbUtils.getUnreadMessageCount(order.getOrderDetails().getId());

            if (eventType != null && eventType.equalsIgnoreCase("Chat")) {
                if (unread_count > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString("EXTRA_CONTACT_JID", order.getOrderDetails().getCustomer().getId());
                    bundle.putString("EXTRA_CONTACT_NAME", order.getOrderDetails().getCustomer().getUserName());
                    bundle.putString("EXTRA_ORDER_ID", order.getOrderDetails().getId());
                    AppUtils.switchActivity(OrderDetailActivity.this, ChatActivity.class, bundle);
                }
            }

            if (unread_count > 0) {
                unread_badge.setVisibility(View.VISIBLE);
                unread_badge.setText(DbUtils.getUnreadMessageCount(order.getOrderDetails().getId()) + "");
            } else
                unread_badge.setVisibility(View.GONE);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            String base64Image = AppUtils.getBase64(imageBitmap);
            Logger.d("IMAGE", base64Image, Logger.DEBUG);
            deliverOrder(base64Image);
        }
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {

                Bundle bundle = new Bundle();
                bundle.putParcelable("order", order);
                Intent intnt = new Intent(OrderDetailActivity.this, RatingActivity.class);
                intnt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                if (bundle != null) {
                    intnt.putExtras(bundle);
                }

                startActivityForResult(intnt, 101);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {


                OrderDetailActivity.this.finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }


    }


    @Override
    public void onRefresh() {
        getOrderDetails(order.getOrderDetails().getId());
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (orderId != null)
            getOrderDetails(orderId);
        else if (order != null) {
            getOrderDetails(order.getOrderDetails().getId());

//            if (DbUtils.getUnreadMessageCount(order.getOrderDetails().getId()) > 0) {
//                unread_badge.setVisibility(View.VISIBLE);
//                unread_badge.setText(DbUtils.getUnreadMessageCount(order.getOrderDetails().getId()) + "");
//            } else
//                unread_badge.setVisibility(View.GONE);
        }
    }

    private String generatePath() {

        String src = "";
        String dest = "";
        String wayPoints = "";
        LatLong obj = null;
        String url = "";
        if ((!SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LAT, OrderDetailActivity.this).equals("")) && (!SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LNG, OrderDetailActivity.this).equals(""))) {

            Double lat = Double.parseDouble(SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LAT, OrderDetailActivity.this));
            Double lng = Double.parseDouble(SharedPreferenceHelper.getString(SharedPreferenceHelper.LOCATION_LNG, OrderDetailActivity.this));
            src = lat + "," + lng;
        }

        try {
            for (OrderComponent orderComponent : order.getOrderDetails().getOrderComponents()) {
                for (OrderLocation orderLocation : orderComponent.getOrderLocations()) {

                    LatLng latLng = new LatLng(Double.parseDouble(orderLocation.getLatitude()), Double.parseDouble(orderLocation.getLongitude()));
                    String name = "";
                    if (orderLocation.getName() != null && !TextUtils.isEmpty(orderLocation.getName())) {
                        name = orderLocation.getName();
                    } else {
                        switch (orderLocation.getLocationType()) {
                            case TYPE_PICKUP:
                                name = TYPE_PICKUP_STRING;
                                break;
                            case TYPE_DELIVERY:
                                name = TYPE_DELIVERY_STRING;
                                break;
                            case TYPE_PAYMENT:
                                name = TYPE_PAYMENT_STRING;
                                break;
                        }
                    }

                    if (orderComponent.getStatus().equals(OrderPartners.PICKED)) {

                    } else {
                        if (src.equals("") && order.getOrderDetails().getPaymentLocation() != null)
                            src = order.getOrderDetails().getPaymentLocation().getLatitude() + "," + order.getOrderDetails().getPaymentLocation().getLongitude();
                        else {
                            if (orderLocation.getLocationType().equals(TYPE_PICKUP)) {
                                if (wayPoints.equals(""))
                                    wayPoints += orderLocation.getLatitude() + "," + orderLocation.getLongitude();
                                else
                                    wayPoints += "|" + orderLocation.getLatitude() + "," + orderLocation.getLongitude();
                            }
                        }
                    }

                }
            }

            if (order.getOrderDetails().getPaymentLocation() != null) {
                LatLng latLng = new LatLng(Double.parseDouble(order.getOrderDetails().getPaymentLocation().getLatitude()), Double.parseDouble(order.getOrderDetails().getPaymentLocation().getLongitude()));
                String name = "";
                if (order.getOrderDetails().getPaymentLocation().getName() != null && !TextUtils.isEmpty(order.getOrderDetails().getPaymentLocation().getName())) {
                    name = order.getOrderDetails().getPaymentLocation().getName();
                } else {
                    switch (order.getOrderDetails().getPaymentLocation().getLocationType()) {
                        case TYPE_PICKUP:
                            name = TYPE_PICKUP_STRING;
                            break;
                        case TYPE_DELIVERY:
                            name = TYPE_DELIVERY_STRING;
                            break;
                        case TYPE_PAYMENT:
                            name = TYPE_PAYMENT_STRING;
                            break;
                    }

                }
                dest = order.getOrderDetails().getPaymentLocation().getLatitude() + "," + order.getOrderDetails().getPaymentLocation().getLongitude();

            }

            url = "https://www.google.com/maps/dir/?api=1&origin=" + src + "&destination=" + dest;

            if (!wayPoints.equals(""))
                url += "&waypoints=" + wayPoints + "&travelMode=driving&optimizeWaypoints=true";
            else
                url += "&travelmode=driving&optimizeWaypoints=true";
        } catch (Exception e) {
            e.printStackTrace();
        }


        return url;
    }

    @Override
    public void OnSuccess() {

    }

    @Override
    public void OnDeliverClick() {
        deliverOrder(null);
    }

    @Override
    public void OnDeliverWithPictureClick() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
            } else {
                dispatchTakePictureIntent();
            }
        } else {
            dispatchTakePictureIntent();
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

        if (alertId == AlertDialogUtils.ALERT_DIALOG_PAYMENT_RECEIVED) {
            isPaymentReceived = true;
            AlertDialogUtils.getInstance().showLoading(OrderDetailActivity.this);
            if (asyncTask != null && (asyncTask.getStatus() == AsyncTask.Status.PENDING || asyncTask.getStatus() == AsyncTask.Status.RUNNING)) {
                asyncTask.cancel(true);
            }
            initAsyncTask(orderId);
        }
    }

    @Override
    public void OnCancel(int alertId) {
        if (alertId == AlertDialogUtils.ALERT_DIALOG_PAYMENT_RECEIVED) {
            isPaymentReceived = false;
            AlertDialogUtils.getInstance().showLoading(OrderDetailActivity.this);
            if (asyncTask != null && (asyncTask.getStatus() == AsyncTask.Status.PENDING || asyncTask.getStatus() == AsyncTask.Status.RUNNING)) {
                asyncTask.cancel(true);
            }
            initAsyncTask(orderId);
        }
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
    public void run() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int total = mediaPlayer.getDuration();

        while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
            try {
                Thread.sleep(1000);
                currentPosition = mediaPlayer.getCurrentPosition();
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }

            seekBar.setProgress(currentPosition);
        }
    }

    public void showAlertDialog(final Context context, final int alertId, final String voiceNote, String close, final AlertDialogResponseListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View alertView = inflater.inflate(R.layout.layout_alert_dialog_voice_note, null);
        ((Button) alertView.findViewById(R.id.button)).setText("Play");

        ((Button) alertView.findViewById(R.id.close)).setText(close);

        alertView.findViewById(R.id.close).setVisibility(View.VISIBLE);

        final TextView seekBarHint = alertView.findViewById(R.id.textView);

        seekBar = alertView.findViewById(R.id.seekbar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                seekBarHint.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                seekBarHint.setVisibility(View.VISIBLE);
                int x = (int) Math.ceil(progress / 1000f);

                if (x < 10)
                    seekBarHint.setText("0:0" + x);
                else
                    seekBarHint.setText("0:" + x);

                double percent = progress / (double) seekBar.getMax();
                int offset = seekBar.getThumbOffset();
                int seekWidth = seekBar.getWidth();
                int val = (int) Math.round(percent * (seekWidth - 2 * offset));
                int labelWidth = seekBarHint.getWidth();
                seekBarHint.setX(offset + seekBar.getX() + val
                        - Math.round(percent * offset)
                        - Math.round(percent * labelWidth / 2));

                if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    clearMediaPlayer();
                    ((Button) alertView.findViewById(R.id.button)).setText("Play");
                    seekBar.setProgress(0);
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });

        builder.setView(alertView);


        AppUtils.setMontserrat(alertView.findViewById(R.id.button));
        AppUtils.setMontserrat(alertView.findViewById(R.id.close));

        builder.setCancelable(false);
        final AlertDialog myDialog = builder.create();

        final SeekBar finalSeekBar = seekBar;
        alertView.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //listener.OnSuccess(alertId);
                try {


                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        clearMediaPlayer();
                        finalSeekBar.setProgress(0);
                        wasPlaying = true;
                        ((Button) alertView.findViewById(R.id.button)).setText("Play");

                    }


                    if (!wasPlaying) {

                        if (mediaPlayer == null) {
                            mediaPlayer = new MediaPlayer();
                        }

                        ((Button) alertView.findViewById(R.id.button)).setText("Stop");

//                        AssetFileDescriptor descriptor = getAssets().openFd("suits.mp3");
//                        mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
//                        descriptor.close();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                        // below line is use to set our
                        // url to our media player.
                        try {
                            Log.i("response complete url", voiceNote);
                            mediaPlayer.setDataSource(voiceNote);
                            // below line is use to prepare
                            // and start our media player.
                            // mediaPlayer.prepare();
                            // mediaPlayer.start();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.prepare();
//                        mediaPlayer.setVolume(0.5f, 0.5f);
                        mediaPlayer.setLooping(false);
                        finalSeekBar.setMax(mediaPlayer.getDuration());
                        Log.i("mediaPlayer", mediaPlayer.getDuration() + "");
                        mediaPlayer.start();
                        new Thread(OrderDetailActivity.this).start();

                    }

                    wasPlaying = false;
                } catch (Exception e) {
                    e.printStackTrace();

                }
                //myDialog.dismiss();
            }
        });

        alertView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnCancel(alertId);
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    clearMediaPlayer();
                    finalSeekBar.setProgress(0);
                    wasPlaying = false;
                    ((Button) alertView.findViewById(R.id.button)).setText("Play");

                }

                myDialog.cancel();
            }
        });

        myDialog.show();
    }

    private void clearMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            clearMediaPlayer();
        }
    }
}
