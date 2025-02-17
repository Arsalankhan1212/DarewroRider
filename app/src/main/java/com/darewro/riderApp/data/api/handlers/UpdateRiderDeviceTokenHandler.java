package com.darewro.riderApp.data.api.handlers;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.darewro.riderApp.data.api.models.User;
import com.darewro.riderApp.presenter.BaseResponseListener;
import com.darewro.riderApp.presenter.ResponseListenerUpdateRiderDeviceToken;
import com.darewro.riderApp.view.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class UpdateRiderDeviceTokenHandler extends ApiBaseResponseHandler {

    ResponseListenerUpdateRiderDeviceToken responseListenerUpdateDeviceToken;

    public UpdateRiderDeviceTokenHandler(Activity activity, String calledApi, BaseResponseListener baseResponseListener) {
        super(activity, calledApi, baseResponseListener);
        this.responseListenerUpdateDeviceToken = (ResponseListenerUpdateRiderDeviceToken) baseResponseListener;

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
        responseListenerUpdateDeviceToken.onError(calledApi,errorMessage,errorCode);
    }

    class BackgroundTask extends AsyncTask<Void, Void, User> {
        String response;
       boolean isAlreadyExists;
        private User user = null;

        public BackgroundTask(String jsonAsString) {
            this.response = jsonAsString;
        }

        @Override
        protected User doInBackground(Void... voids) {
//            {"isAlreadyExsist":false,"verificationCode":"0182"}
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



                JSONObject resp = new JSONObject(response);
                if(resp.has("isAlreadyExist"))
                {
                    isAlreadyExists = resp.getBoolean("isAlreadyExist");
                    if(isAlreadyExists)
                    {
                        JSONObject object = resp.optJSONObject("rider");

                        user = new User();
                        user.setId(object.optString("id"));
                        user.setUserName(object.optString("userName"));
                        user.setEmail(object.optString("email"));
                        user.setMsisdn(object.optString("msisdn"));
                        user.setPicturePath(object.optString("picturePath"));
                        user.setDeviceToken(object.optString("deviceToken"));
                        user.setBikeNumber(object.optString("bikeNumber"));
                        user.setRating(object.optString("rating"));
                        user.setAvailable(object.optBoolean("isAvailable"));
                        user.setRiderShiftSettings(object.optJSONObject("riderShiftSettings").toString());
                        user.setMsisdn(AppUtils.getFormattedPhoneNumber(user.getMsisdn()));
                    }
                    else
                    {
                        user = null;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);

                if (user!=null) {
                    responseListenerUpdateDeviceToken.onSuccess(calledApi,isAlreadyExists,user);
                } else {
                    if (!isAlreadyExists&&user==null) {
                        responseListenerUpdateDeviceToken.onSuccess(calledApi,isAlreadyExists,user);
                    }
                    else
                    {
                        responseListenerUpdateDeviceToken.onError(calledApi);
                    }
                }
        }

        @Override
        protected void onCancelled(User user) {
            super.onCancelled(user);
            responseListenerUpdateDeviceToken.onError(calledApi);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            responseListenerUpdateDeviceToken.onError(calledApi);
        }
    }

}
