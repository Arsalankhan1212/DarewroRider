package com.darewro.riderApp.data.api.handlers;

import android.app.Activity;
import android.os.AsyncTask;

import com.darewro.riderApp.presenter.BaseResponseListener;
import com.darewro.riderApp.presenter.ResponseListenerNearestOrdersListing;
import com.darewro.riderApp.view.models.NearestOrder;
import com.darewro.riderApp.view.models.Order;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class NearestOrdersHandler extends ApiBaseResponseHandler {

    ResponseListenerNearestOrdersListing responseListenerOrdersListing;

    public NearestOrdersHandler(Activity activity, String calledApi, BaseResponseListener baseResponseListener) {
        super(activity, calledApi, baseResponseListener);
        this.responseListenerOrdersListing = (ResponseListenerNearestOrdersListing) baseResponseListener;

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

    class BackgroundTask extends AsyncTask<Void, Void, List<NearestOrder>> {
        String ordersJson;
        List<NearestOrder> orderDetails = null;
        List<Order> orders = new ArrayList<>();

        public BackgroundTask(String jsonAsString) {
            this.ordersJson = jsonAsString;
        }

        @Override
        protected List<NearestOrder> doInBackground(Void... voids) {
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
                orderDetails = gson.fromJson(String.valueOf(new JSONArray(ordersJson)), new TypeToken<List<NearestOrder>>() {
                }.getType());

            } catch (Exception e) {
                e.printStackTrace();
            }

            return orderDetails;
        }

        @Override
        protected void onPostExecute(List<NearestOrder> aVoid) {
            super.onPostExecute(aVoid);
            if (aVoid != null) {
                if (responseListenerOrdersListing != null)
                    responseListenerOrdersListing.onSuccess(calledApi, aVoid, true);
            } else {
                if (responseListenerOrdersListing != null)
                    responseListenerOrdersListing.onError(calledApi);
            }
        }

        @Override
        protected void onCancelled(List<NearestOrder> aVoid) {
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
