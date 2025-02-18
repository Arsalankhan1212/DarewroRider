package com.darewro.rider.data.api.handlers;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.darewro.rider.data.api.models.Dashboard;
import com.darewro.rider.presenter.BaseResponseListener;
import com.darewro.rider.presenter.ResponseListenerDashboard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DashboardHandler extends ApiBaseResponseHandler {

    ResponseListenerDashboard responseListenerDashboard;

    public DashboardHandler(Activity activity, String calledApi, BaseResponseListener baseResponseListener) {
        super(activity, calledApi, baseResponseListener);
        this.responseListenerDashboard = (ResponseListenerDashboard) baseResponseListener;

    }


    @Override
    public void setApiResponse(JSONObject jsonObject, String calledApi) {
        responseListenerDashboard.onSuccess(jsonObject.toString());

        BackgroundTask backgroundTask = new BackgroundTask(jsonObject.toString());
        backgroundTask.execute();
    }

    @Override
    public void setApiResponse(JSONArray jsonArray, String calledApi) {
        responseListenerDashboard.onSuccess(jsonArray.toString());
    }

    @Override
    public void setApiResponse(String jsonAsString, String calledApi) {

        BackgroundTask backgroundTask = new BackgroundTask(jsonAsString);
        backgroundTask.execute();

    }

    @Override
    public void setApiError(String calledApi) {
        responseListenerDashboard.onError(calledApi);

    }

    @Override
    public void setApiError(String errorMessage, String calledApi) {
//        responseListenerDashboard.onError(calledApi, errorMessage);
        responseListenerDashboard.onError(calledApi);

    }

    @Override
    public void setApiError(String errorMessage, String calledApi, int errorCode) {
        responseListenerDashboard.onError(calledApi,errorMessage,errorCode);
    }

    class BackgroundTask extends AsyncTask<Void, Void, Dashboard> {
        String response;
        private Dashboard dashboard=null;

        public BackgroundTask(String jsonAsString) {
            this.response = jsonAsString;
        }

        @Override
        protected Dashboard doInBackground(Void... voids) {
            try {

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

                dashboard = gson.fromJson(String.valueOf(new JSONObject(response)), new TypeToken<Dashboard>() {}.getType());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return dashboard;
        }

        @Override
        protected void onPostExecute(Dashboard dashboard) {
            super.onPostExecute(dashboard);
            if (dashboard != null) {
                if (dashboard != null) {
                    responseListenerDashboard.onSuccess(calledApi, dashboard);
                } else {
                    responseListenerDashboard.onError(calledApi);
                }
            } else {
                responseListenerDashboard.onError(calledApi);
            }
        }

        @Override
        protected void onCancelled(Dashboard dashboard) {
            super.onCancelled(dashboard);
            responseListenerDashboard.onError(calledApi);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            responseListenerDashboard.onError(calledApi);
        }
    }


}
