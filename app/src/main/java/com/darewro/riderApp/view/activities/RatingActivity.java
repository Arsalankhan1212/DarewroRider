package com.darewro.riderApp.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.darewro.riderApp.R;
import com.darewro.riderApp.data.api.ApiCalls;
import com.darewro.riderApp.data.api.Interface.InitApi;
import com.darewro.riderApp.data.api.handlers.GenericHandler;
import com.darewro.riderApp.data.api.requests.JsonObjectRequestCall;
import com.darewro.riderApp.presenter.ResponseListenerGeneric;
import com.darewro.riderApp.view.models.Order;
import com.darewro.riderApp.view.utils.AlertDialogUtils;
import com.darewro.riderApp.view.utils.AppUtils;
import com.darewro.riderApp.view.utils.SharedPreferenceHelper;
import com.darewro.riderApp.view.utils.StringUtility;

import java.util.HashMap;


public class RatingActivity extends Activity implements ResponseListenerGeneric {

    Order order;
    private TextView name, date, time;
    EditText comments;
    private RatingBar customRatingBar;
    private Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        handleIntent();
        initializeViews();
        setListeners();
    }

    public void initializeViews() {
        name = findViewById(R.id.name);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        submit = findViewById(R.id.submit);
        comments = findViewById(R.id.comments);

        AppUtils.setMontserratBold(name);
        AppUtils.setMontserrat(date);
        AppUtils.setMontserrat(time);
        AppUtils.setMontserratBold((TextView)findViewById(R.id.description));
        AppUtils.setMontserrat(comments);
        AppUtils.setMontserrat(submit);

        customRatingBar = findViewById(R.id.rating);
        populateOrder();
        setListeners();
    }

    private void populateOrder() {

        name.setText(order.getOrderDetails().getCustomer().getUserName());
        date.setText(order.getDate());
        time.setText(order.getTime());
    }

    public void setListeners() {
        customRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateCustomer();
            }
        });
    }

    public void handleIntent() {
        if(getIntent().hasExtra("order"))
            order = getIntent().getExtras().getParcelable("order");
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
            AlertDialogUtils.showAlertDialog(RatingActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), getString(R.string.app_update), getString(R.string.ok), null, true);

    }

    @Override
    public void onSuccess(String calledApi, String response) {
        AppUtils.makeNotification(response,RatingActivity.this);
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @Override
    public void onError(String calledApi, String errorMessage) {

    }

    @Override
    public void onError(String calledApi, String errorMessage, int errorCode) {
        if(errorCode == 403)
            AlertDialogUtils.showAlertDialog(RatingActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), errorMessage, getString(R.string.ok), null, true);

    }

    public void rateCustomer() {

        InitApi initApi = new InitApi() {
            @Override
            public HashMap<String, String> getBody() {

                HashMap<String, String> body = new HashMap<>();
                body.put("EntityId", order.getOrderDetails().getCustomerID());
                body.put("EntityType", StringUtility.Customer);
                body.put("RatedById", SharedPreferenceHelper.getString(SharedPreferenceHelper.UID,RatingActivity.this));
                body.put("Rating", String.valueOf(customRatingBar.getRating()));
                body.put("OrderId",String.valueOf(order.getOrderDetails().getId()));
                body.put("Comments",comments.getText().toString());
                return body;
            }

            @Override
            public HashMap<String, Object> getObjBody() {
                return null;
            }

            @Override
            public HashMap<String, String> getHeader() {
                return AppUtils.getStandardHeaders(RatingActivity.this);
            }
        };

        String rateApi = ApiCalls.rateApi();
        GenericHandler ordersListingHandler = new GenericHandler(RatingActivity.this, rateApi, this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), rateApi, Request.Method.POST, RatingActivity.this, ordersListingHandler);
        jsonObjectRequestCall.sendData();
    }


}
