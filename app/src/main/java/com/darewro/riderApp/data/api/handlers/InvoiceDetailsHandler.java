package com.darewro.riderApp.data.api.handlers;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.darewro.riderApp.data.api.models.OrderComponent;
import com.darewro.riderApp.data.api.models.OrderDetail;
import com.darewro.riderApp.data.api.models.OrderLocation;
import com.darewro.riderApp.presenter.BaseResponseListener;
import com.darewro.riderApp.presenter.ResponseListenerInvoiceDetails;
import com.darewro.riderApp.presenter.ResponseListenerOrdersDetails;
import com.darewro.riderApp.view.models.Invoice;
import com.darewro.riderApp.view.models.Order;
import com.darewro.riderApp.view.models.OrderPartners;
import com.darewro.riderApp.view.utils.AppUtils;
import com.darewro.riderApp.view.utils.LocationUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDetailsHandler extends ApiBaseResponseHandler {

    ResponseListenerInvoiceDetails responseListenerInvoiceDetails;

    public InvoiceDetailsHandler(Activity activity, String calledApi, BaseResponseListener baseResponseListener) {
        super(activity, calledApi, baseResponseListener);
        this.responseListenerInvoiceDetails = (ResponseListenerInvoiceDetails) baseResponseListener;

    }


    @Override
    public void setApiResponse(JSONObject jsonObject, String calledApi) {

        BackgroundTask backgroundTask = new BackgroundTask(jsonObject.toString());
        backgroundTask.execute();
    }

    @Override
    public void setApiResponse(JSONArray jsonArray, String calledApi) {

    }

    @Override
    public void setApiResponse(String jsonAsString, String calledApi) {
        BackgroundTask backgroundTask = new BackgroundTask(jsonAsString);
        backgroundTask.execute();
    }

    @Override
    public void setApiError(String calledApi) {

    }

    @Override
    public void setApiError(String errorMessage, String calledApi) {

    }

    @Override
    public void setApiError(String errorMessage, String calledApi, int errorCode) {
        responseListenerInvoiceDetails.onError(calledApi,errorMessage,errorCode);

    }

    class BackgroundTask extends AsyncTask<Void, Void, Invoice> {
        String ordersJson;
        Invoice invoice = null;
        //OrderDetailTable orderDetail = null;

        public BackgroundTask(String jsonAsString) {

            this.ordersJson = jsonAsString;

            Log.d("TAG111","BackgroundTASK orderJSON= "+ordersJson);
        }

        @Override
        protected Invoice doInBackground(Void... voids) {
            Log.d("TAG111","doInBackground");
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
                invoice = gson.fromJson(String.valueOf(new JSONObject(ordersJson)), new TypeToken<Invoice>() {
                }.getType());

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("TAG111","Invoice exception= "+e.getMessage());
            }

            return invoice;
        }

        @Override
        protected void onPostExecute(Invoice aVoid) {
            super.onPostExecute(aVoid);

            Log.d("TAG111","OnPostExecute= "+ aVoid.getStatus());
            if (aVoid != null) {
                responseListenerInvoiceDetails.onSuccess(calledApi, aVoid);
            } else if (ordersJson != null) {
                responseListenerInvoiceDetails.onSuccess(calledApi, ordersJson);
            } else {
                responseListenerInvoiceDetails.onError(calledApi);
            }
        }

        @Override
        protected void onCancelled(Invoice aVoid) {
            super.onCancelled(aVoid);
            responseListenerInvoiceDetails.onError(calledApi);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            responseListenerInvoiceDetails.onError(calledApi);
        }
    }

}
