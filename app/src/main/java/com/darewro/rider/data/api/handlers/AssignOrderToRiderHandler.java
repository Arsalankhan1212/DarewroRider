package com.darewro.rider.data.api.handlers;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.darewro.rider.data.api.models.allOrders.AllOrder;
import com.darewro.rider.data.api.models.assignOrder.AssignOrderResponse;
import com.darewro.rider.presenter.BaseResponseListener;
import com.darewro.rider.presenter.ResponseListenerAssignOrderToRider;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class AssignOrderToRiderHandler extends ApiBaseResponseHandler {

    ResponseListenerAssignOrderToRider responseListenerOrdersListing;

    public AssignOrderToRiderHandler(Activity activity, String calledApi, BaseResponseListener baseResponseListener) {
        super(activity, calledApi, baseResponseListener);
        this.responseListenerOrdersListing = (ResponseListenerAssignOrderToRider) baseResponseListener;
    }

    @Override
    public void setApiResponse(JSONObject jsonObject, String calledApi) {
        Log.d("TAG111","ASSIGN ORDER, Reponse= "+jsonObject.toString());
        BackgroundTask backgroundTask = new BackgroundTask(jsonObject.toString());
        backgroundTask.execute();
    }

    @Override
    public void setApiResponse(JSONArray jsonArray, String calledApi) {
        Log.d("TAG111","JSON ARRAY, ASSIGN ORDER");
    }

    @Override
    public void setApiResponse(String jsonAsString, String calledApi) {
        // Handle string response if needed
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

    class BackgroundTask extends AsyncTask<Void, Void, AssignOrderResponse> {
        String ordersJson;
        AssignOrderResponse assignOrderResponse;

        public BackgroundTask(String jsonAsString) {
            this.ordersJson = jsonAsString;
        }

        @Override
        protected AssignOrderResponse doInBackground(Void... voids) {
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
                assignOrderResponse = gson.fromJson(ordersJson, AssignOrderResponse.class);
                Log.d("TAG111", "Assign Order, message= "+assignOrderResponse.getResponse());
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("TAG111", "Assign Order Exception= " + e);
            }

            return assignOrderResponse;
        }

        @Override
        protected void onPostExecute(AssignOrderResponse response) {
            super.onPostExecute(response);
            if (response != null) {
                if (responseListenerOrdersListing != null) {
                    responseListenerOrdersListing.onSuccess(response.getResponse(), calledApi);
                }
            } else {
                // Handle failure if no response
                if (responseListenerOrdersListing != null) {
                    responseListenerOrdersListing.onError(calledApi);
                }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (responseListenerOrdersListing != null) {
                responseListenerOrdersListing.onError(calledApi);
            }
        }
    }

}
