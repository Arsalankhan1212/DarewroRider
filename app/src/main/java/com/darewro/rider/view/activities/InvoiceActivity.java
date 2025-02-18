package com.darewro.rider.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.darewro.rider.R;
import com.darewro.rider.data.api.ApiCalls;
import com.darewro.rider.data.api.Interface.InitApi;
import com.darewro.rider.data.api.handlers.GenericHandler;
import com.darewro.rider.data.api.requests.JsonObjectRequestCall;
import com.darewro.rider.presenter.ResponseListenerGeneric;
import com.darewro.rider.view.adapters.RecyclerAdapterInvoiceDetailGeneral;
import com.darewro.rider.view.adapters.RecyclerAdapterInvoiceDetailPartner;
import com.darewro.rider.view.customViews.ItemOffsetDecoration;
import com.darewro.rider.view.listeners.AlertDialogResponseListener;
import com.darewro.rider.view.locationService.LocationService;
import com.darewro.rider.view.models.Invoice;
import com.darewro.rider.view.models.Order;
import com.darewro.rider.view.utils.AlertDialogUtils;
import com.darewro.rider.view.utils.AppUtils;
import com.darewro.rider.view.utils.PermissionsRequest;
import com.darewro.rider.view.utils.SharedPreferenceHelper;

import java.util.HashMap;

public class InvoiceActivity extends Activity implements ResponseListenerGeneric, AlertDialogResponseListener {
    private Invoice invoice = null;
    private TextView sub_total, tax, discount, grand_total, delivery_charges, payment;
    private LinearLayout l_sub_total, l_tax, l_discount, l_grand_total;
    private RecyclerView recyclerView;
    Button btnCompleted;
    private Order order;

    //EditText cash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invoice);

        handleIntent();

        initializeViews();
        setListeners();
    }

    public void initializeViews() {

        l_sub_total = findViewById(R.id.l_sub_total);
        l_tax = findViewById(R.id.l_tax);
        l_discount = findViewById(R.id.l_discount);
        l_grand_total = findViewById(R.id.l_total);
        sub_total = findViewById(R.id.sub_total);
        tax = findViewById(R.id.tax);
        discount = findViewById(R.id.discount);
        grand_total = findViewById(R.id.total);
        delivery_charges = findViewById(R.id.delivery_charges);
        payment = findViewById(R.id.payment);
        btnCompleted = findViewById(R.id.completed);
        // cash = findViewById(R.id.cash);

        AppUtils.setMontserratBold((TextView) findViewById(R.id.title));
        AppUtils.setMontserrat((TextView) findViewById(R.id.label_subtotal));
        AppUtils.setMontserrat((TextView) findViewById(R.id.label_tax));
        AppUtils.setMontserrat((TextView) findViewById(R.id.label_discount));
        AppUtils.setMontserrat((TextView) findViewById(R.id.label_total));
        AppUtils.setMontserrat((TextView) findViewById(R.id.label_delivery_charges));
        AppUtils.setMontserratBold((TextView) findViewById(R.id.label_payment));

        AppUtils.setMontserrat(sub_total);
        AppUtils.setMontserrat(tax);
        AppUtils.setMontserrat(discount);
        AppUtils.setMontserrat(grand_total);
        AppUtils.setMontserrat(delivery_charges);
        AppUtils.setMontserratBold(payment);
        AppUtils.setMontserrat(btnCompleted);


        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(InvoiceActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        recyclerView.addItemDecoration(new ItemOffsetDecoration(5));

        if (invoice != null) {
            if (invoice.getDiscount() != null) {
                discount.setText("Rs. " + invoice.getDiscount());
            }
            if (invoice.getTax() != null) {
                tax.setText("Rs. " + invoice.getTax());
            }
            if (invoice.getSubamount() != null) {
                sub_total.setText("Rs. " + invoice.getSubamount());
            }
            if (invoice.getTotal() != null) {
                grand_total.setText("Rs. " + invoice.getTotal());
            }
            if (invoice.getDeliveryCharges() != null)
                delivery_charges.setText("Rs. " + invoice.getDeliveryCharges());
            else delivery_charges.setText("Rs. 0");
            if (invoice.getDeliveryCharges() != null && invoice.getTotal() != null) {
                Double paymentAmount = Double.parseDouble(invoice.getTotal()) + Double.parseDouble(invoice.getDeliveryCharges());
                payment.setText("Rs. " + String.valueOf(paymentAmount));
            } else {
                if (invoice.getDeliveryCharges() == null) {
                    Double paymentAmount = Double.parseDouble(invoice.getTotal());
                    payment.setText("Rs. " + String.valueOf(paymentAmount));
                }
            }

            if (order.getOrderDetails().getOrderType().equals(Order.ORDER_TYPE_PARTNER)) {
                RecyclerAdapterInvoiceDetailPartner adapter = new RecyclerAdapterInvoiceDetailPartner(InvoiceActivity.this, invoice.getInvoicePartners());
                recyclerView.setAdapter(adapter);
                l_sub_total.setVisibility(View.VISIBLE);
                l_tax.setVisibility(View.VISIBLE);
                l_discount.setVisibility(View.VISIBLE);
                l_grand_total.setVisibility(View.VISIBLE);
                // cash.setVisibility(View.GONE);
            } else {
                RecyclerAdapterInvoiceDetailGeneral adapter = new RecyclerAdapterInvoiceDetailGeneral(InvoiceActivity.this, order.getOrderDetails().getOrderComponents());
                recyclerView.setAdapter(adapter);
                l_sub_total.setVisibility(View.GONE);
                l_tax.setVisibility(View.GONE);
                l_discount.setVisibility(View.GONE);
                l_grand_total.setVisibility(View.GONE);
                // cash.setVisibility(View.GONE);
            }

        }

        if (order.getOrderDetails().getStatus().equals(Order.PAYMENT_RECEIVED))
            btnCompleted.setText(getResources().getString(R.string.close));
        else btnCompleted.setText(getResources().getString(R.string.collect));

    }

    public void setListeners() {

        btnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btnCompleted.getText().toString().equals(getResources().getString(R.string.close))) {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                } else {
                    if (SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, InvoiceActivity.this))
                        completedOrder();
                    else
                        AlertDialogUtils.showAlertDialog(InvoiceActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.warning), getString(R.string.warning_status_a), getString(R.string.ok), InvoiceActivity.this);

                }
//                InvoiceActivity.this.finish();
            }
        });

    }

    public void handleIntent() {
        if (getIntent().hasExtra("invoice"))
            invoice = getIntent().getExtras().getParcelable("invoice");
        if (getIntent().hasExtra("order")) order = getIntent().getExtras().getParcelable("order");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionsRequest.LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //             mapFragment.getMapAsync(OrderDetailActivity.this);
                //           setupMap();
            } else {
                // Permission was denied or request was cancelled
            }

        }
    }

    public void completedOrder() {

        InitApi initApi = new InitApi() {
            @Override
            public HashMap<String, String> getBody() {

                HashMap<String, String> body = new HashMap<>();
                body.put("invoiceID", invoice.getId());
                body.put("orderID", invoice.getOrderId());
                body.put("paymentAmount", invoice.getTotal());
                body.put("paymentMethodID", "1");
                body.put("userId", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, InvoiceActivity.this)/*"6"*/);
                return body;
            }

            @Override
            public HashMap<String, Object> getObjBody() {
                return null;
            }

            @Override
            public HashMap<String, String> getHeader() {
                return AppUtils.getStandardHeaders(InvoiceActivity.this);
            }
        };

        String invoicePayment = ApiCalls.invoicePayment();
        GenericHandler ordersListingHandler = new GenericHandler(InvoiceActivity.this, invoicePayment, this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), invoicePayment, Request.Method.POST, InvoiceActivity.this, ordersListingHandler);
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
        if (errorCode == 403)
            AlertDialogUtils.showAlertDialog(InvoiceActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), getString(R.string.app_update), getString(R.string.ok), null, true);

    }

    @Override
    public void onSuccess(String calledApi, String response) {
        AppUtils.makeNotification(response, InvoiceActivity.this);

        String orderId = SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, getApplicationContext());
        String orderUserId = SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_USER_ID, getApplicationContext());
        //LocationService.deleteAtPath(order.getOrderDetails().getId(), InvoiceActivity.this);

        if (orderId.contains(",")) {

            String updatedOrderId = "";
            String updatedOrderUserId = "";
            String orderIds[] = orderId.split(",");
            String orderUserIds[] = orderUserId.split(",");

            for (int i = 0; i < orderIds.length; i++) {
                if (order.getOrderDetails().getId().replaceAll("\\D", "").equals(orderIds[i])) {

                } else {
                    if (updatedOrderId.equals("")) {
                        updatedOrderId += String.valueOf(orderIds[i]);
                        updatedOrderUserId += String.valueOf(orderUserIds[i]);

                    } else {
                        updatedOrderId += "," + String.valueOf(orderIds[i]);
                        updatedOrderUserId += "," + String.valueOf(orderUserIds[i]);
                    }
                }
            }

            SharedPreferenceHelper.saveString(SharedPreferenceHelper.ORDER_ID, updatedOrderId, getApplicationContext());
            SharedPreferenceHelper.saveString(SharedPreferenceHelper.ORDER_USER_ID, updatedOrderUserId, getApplicationContext());

        } else {
            if (orderId.contains(order.getOrderDetails().getId().replaceAll("\\D", ""))) {
                SharedPreferenceHelper.saveString(SharedPreferenceHelper.ORDER_ID, "", getApplicationContext());
                SharedPreferenceHelper.saveString(SharedPreferenceHelper.ORDER_USER_ID, "", getApplicationContext());

            }
        }

        AppUtils.makeNotification(SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, InvoiceActivity.this), InvoiceActivity.this);
        if ((!SharedPreferenceHelper.getBoolean(SharedPreferenceHelper.IS_AVAILABLE, InvoiceActivity.this)) && SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, InvoiceActivity.this).equals("")) {
            SharedPreferenceHelper.saveBoolean(SharedPreferenceHelper.FORCE_STOP, true, InvoiceActivity.this);
            Intent intent = new Intent(InvoiceActivity.this, LocationService.class);
            if (LocationService.isRunning(InvoiceActivity.this)) stopService(intent);
        }

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
//        deleteAtPath(order.getOrderDetails().getId());
    }

    @Override
    public void onError(String calledApi, String errorMessage) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
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
    public void onError(String calledApi, String errorMessage, int errorCode) {
        if (errorCode == 403)
            AlertDialogUtils.showAlertDialog(InvoiceActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), errorMessage, getString(R.string.ok), null, true);
    }

}
