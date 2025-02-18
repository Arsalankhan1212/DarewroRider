package com.darewro.rider.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.JsonArray;
import com.darewro.rider.R;
import com.darewro.rider.data.api.ApiCalls;
import com.darewro.rider.data.api.Interface.InitApi;
import com.darewro.rider.data.api.handlers.GenericHandler;
import com.darewro.rider.data.api.handlers.OrdersListingHandler;
import com.darewro.rider.data.api.requests.JsonObjectRequestCall;
import com.darewro.rider.presenter.ResponseListenerGeneric;
import com.darewro.rider.view.adapters.RecyclerAdapterNotifications;
import com.darewro.rider.view.adapters.RecyclerAdapterOrders;
import com.darewro.rider.view.customViews.ItemOffsetDecorationLTBR;
import com.darewro.rider.view.utils.AlertDialogUtils;
import com.darewro.rider.view.utils.AppUtils;
import com.darewro.rider.view.utils.SharedPreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

public class NotificationsActivity extends BaseActivity implements ResponseListenerGeneric {

    SwipeRefreshLayout swipeRefreshLayout = null;
    TextView noLayout;
    private RecyclerView recyclerView;
    private Parcelable recyclerViewState;
    private RecyclerAdapterNotifications adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        initializeViews();
    }

    @Override
    public void initializeViews() {
        swipeRefreshLayout = findViewById(R.id.swipe_container);
        noLayout = findViewById(R.id.noLayoutTextView);

        AppUtils.setMontserrat(noLayout);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(NotificationsActivity.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        //recyclerView.addItemDecoration(new ItemOffsetDecorationLTBR());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        setListeners();
        getNotifications();
    }

    @Override
    public void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getNotifications();
                    }
                }
        );
    }

    public void getNotifications() {

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
                return AppUtils.getStandardHeaders(NotificationsActivity.this);
            }
        };
        String getNotifications = ApiCalls.getNotifications() + "?RiderId=" + SharedPreferenceHelper.getString(SharedPreferenceHelper.UID, NotificationsActivity.this);
        //OrdersListingHandler ordersListingHandler = new OrdersListingHandler(NotificationsActivity.this, getNotifications, this);
        GenericHandler ordersListingHandler = new GenericHandler(NotificationsActivity.this, getNotifications, this);
        JsonObjectRequestCall jsonObjectRequestCall = new JsonObjectRequestCall(initApi.getHeader(), initApi.getBody(), getNotifications, Request.Method.GET, NotificationsActivity.this, ordersListingHandler);
        jsonObjectRequestCall.sendData(false);
    }

    @Override
    public void handleIntent() {

    }

    @Override
    public void onSuccess(String calledApi, String response) {

        try {
            JSONArray jsonArr = new JSONArray(response);

            if (recyclerView != null) {
                recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                adapter = new RecyclerAdapterNotifications(NotificationsActivity.this, jsonArr);
                recyclerView.setAdapter(adapter);
                recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            }
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (noLayout != null) {
                if (jsonArr != null && jsonArr.length() > 0) {
                    noLayout.setVisibility(View.GONE);
                } else {
                    noLayout.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(String calledApi, String errorMessage) {

    }

    @Override
    public void onError(String calledApi, String errorMessage, int errorCode) {
        if(errorCode == 403)
            AlertDialogUtils.showAlertDialog(NotificationsActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), errorMessage, getString(R.string.ok), null, true);

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
            AlertDialogUtils.showAlertDialog(NotificationsActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), getString(R.string.app_update), getString(R.string.ok), null, true);

    }
}