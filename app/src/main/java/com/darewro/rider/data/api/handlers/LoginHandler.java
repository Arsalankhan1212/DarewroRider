package com.darewro.rider.data.api.handlers;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.darewro.rider.presenter.BaseResponseListener;
import com.darewro.rider.presenter.ResponseListenerLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginHandler extends ApiBaseResponseHandler {

    ResponseListenerLogin responseListenerSignup;

    public LoginHandler(Activity activity, String calledApi, BaseResponseListener baseResponseListener) {
        super(activity, calledApi, baseResponseListener);
        this.responseListenerSignup = (ResponseListenerLogin) baseResponseListener;

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
        responseListenerSignup.onError(calledApi,errorMessage,errorCode);
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String response;
        String id = null;
        String token = null;

        public BackgroundTask(String jsonAsString) {
            this.response = jsonAsString;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                JSONObject resp = new JSONObject(response);
                if (resp.has("id")) {
                    id = resp.getString("id");
                    if (id==null) {
                        return null;
                    } else {
                        if (resp.has("token")) {
                            token = resp.getString("token");
                        }
                        if (resp.has("verificationCode")) {
                            return resp.getString("verificationCode");
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String vCode) {
            super.onPostExecute(vCode);
            if (vCode!=null) {
                if (!TextUtils.isEmpty(vCode)) {
                    responseListenerSignup.onSuccess(calledApi,id, vCode,token);
                } else {
                    responseListenerSignup.onError(calledApi);
                }
            } else {
                responseListenerSignup.onError(calledApi);
            }
        }

        @Override
        protected void onCancelled(String aVoid) {
            super.onCancelled(aVoid);
            responseListenerSignup.onError(calledApi);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            responseListenerSignup.onError(calledApi);
        }
    }

}
