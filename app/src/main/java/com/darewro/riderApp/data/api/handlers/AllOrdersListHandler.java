package com.darewro.riderApp.data.api.handlers;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.darewro.riderApp.data.api.models.allOrders.AllOrder;
import com.darewro.riderApp.data.api.models.allOrders.AllOrderResponse;
import com.darewro.riderApp.presenter.BaseResponseListener;
import com.darewro.riderApp.presenter.ResponseListenerAllOrdersListing;
import com.darewro.riderApp.view.models.Order;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AllOrdersListHandler extends ApiBaseResponseHandler {

    ResponseListenerAllOrdersListing responseListenerOrdersListing;

    public AllOrdersListHandler(Activity activity, String calledApi, BaseResponseListener baseResponseListener) {
        super(activity, calledApi, baseResponseListener);
        this.responseListenerOrdersListing = (ResponseListenerAllOrdersListing) baseResponseListener;

    }


    @Override
    public void setApiResponse(JSONObject jsonObject, String calledApi) {

    }

    @Override
    public void setApiResponse(JSONArray jsonArray, String calledApi) {
        BackgroundTask backgroundTask = new BackgroundTask(jsonArray.toString());
        backgroundTask.execute();
    }

    @Override
    public void setApiResponse(String jsonAsString, String calledApi) {


    }

    @Override
    public void setApiError(String calledApi) {
        responseListenerOrdersListing.onError(calledApi);

    }

    @Override
    public void setApiError(String errorMessage, String calledApi) {
        responseListenerOrdersListing.onError(calledApi, errorMessage);

    }

    @Override
    public void setApiError(String errorMessage, String calledApi, int errorCode) {
        responseListenerOrdersListing.onError(calledApi, errorMessage, errorCode);

    }

    class BackgroundTask extends AsyncTask<Void, Void, List<AllOrder>> {
        String ordersJson;
        List<AllOrderResponse> orderDetails = null;
        List<AllOrder> orders = new ArrayList<>();

        public BackgroundTask(String jsonAsString) {
            this.ordersJson = jsonAsString;
        }

        @Override
        protected List<AllOrder> doInBackground(Void... voids) {
            ExclusionStrategy exclusionStrategy = new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    return false;
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return clazz == Field.class || clazz == Method.class;
                }
            };

            Gson gson = new GsonBuilder()
                    .addSerializationExclusionStrategy(exclusionStrategy)
                    .addDeserializationExclusionStrategy(exclusionStrategy)
                    .create();

            try {
                List<AllOrder> responseList = gson.fromJson(ordersJson, new TypeToken<List<AllOrder>>() {}.getType());
                if (responseList != null) {
                    // Filter orders with status "new"
                    for (AllOrder order : responseList) {
                        if (order.getStatus() != null && order.getStatus().equals(Order.NEW_TEXT)) {
                            orders.add(order);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("TAG111", "All Order Exception= " + e);
            }
            return orders;
        }

        @Override
        protected void onPostExecute(List<AllOrder> aVoid) {
            super.onPostExecute(aVoid);
            if (aVoid != null) {
                if (responseListenerOrdersListing != null)
                    responseListenerOrdersListing.onSuccess(aVoid, calledApi);
            } else {
                if (responseListenerOrdersListing != null)
                    responseListenerOrdersListing.onError(calledApi);
            }
        }

        @Override
        protected void onCancelled(List<AllOrder> aVoid) {
            super.onCancelled(aVoid);
            if (responseListenerOrdersListing != null)
                responseListenerOrdersListing.onError(calledApi);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            responseListenerOrdersListing.onError(calledApi);
        }
    }


}
