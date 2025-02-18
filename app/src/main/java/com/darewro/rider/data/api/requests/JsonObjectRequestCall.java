package com.darewro.rider.data.api.requests;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.darewro.rider.App;
import com.darewro.rider.BuildConfig;
import com.darewro.rider.data.api.ApiCalls;
import com.darewro.rider.data.api.ApiHandler;
import com.darewro.rider.data.api.BaseResponseHandler;
import com.darewro.rider.data.api.Interface.InitApi;
import com.darewro.rider.data.api.ResponseHandler;
import com.darewro.rider.data.api.handlers.ApiBaseResponseHandler;
import com.darewro.rider.data.api.models.User;
import com.darewro.rider.view.utils.AlertDialogUtils;
import com.darewro.rider.view.utils.AppUtils;
import com.darewro.rider.view.utils.MyVolleySingleton;
import com.darewro.rider.view.utils.SharedPreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ahmad Abbas Zaidi on 3/21/2017.
 */

public class JsonObjectRequestCall extends ApiHandler {

    private BaseResponseHandler responseHandler;
    private ProgressDialog progressDialog;
    private Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {

            try {
                responseJson = response;
                Log.d("TAG11","Response= "+responseJson);

                ResponseHandler rh;
                if (isAlternate) {
                    rh = new ResponseHandler(activity, calledApi, alternatebody, header, apiBaseResponseHandler);
                } else {
                    rh = new ResponseHandler(activity, calledApi, body, header, apiBaseResponseHandler);
                }

                responseHandler = rh;
                //if(responseHandler!=null){
                    responseHandler.setResponse(responseJson, response.toString());
                    responseHandler.handleResponse();
                //}
                if(progressDialog!=null)
                    progressDialog.dismiss();
            }
            catch (Exception e)
            {
                AlertDialogUtils.getInstance().hideLoading();
                e.printStackTrace();

                if(progressDialog!=null)
                    progressDialog.dismiss();
            }
        }
    };
    private Response.ErrorListener responseErrorListener = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("TAG111","volly error" +error.toString());
            if(progressDialog!=null)
               progressDialog.dismiss();

            AlertDialogUtils.getInstance().hideLoading();


            if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 401) {

                final InitApi initApi = new InitApi() {
                    @Override
                    public HashMap<String, String> getBody() {
                        HashMap<String, String> body = new HashMap<>();
                        body.put("UserName", "apiuser");
                        body.put("Password", "Lmkt@12345");
                        body.put("SecretKey", "nYA26svXRlbJRccDrxFxqOXztBKXMoZD");
                        return body;
                    }

                    @Override
                    public HashMap<String, Object> getObjBody() {
                        return null;
                    }

                    @Override
                    public HashMap<String, String> getHeader() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("x-api-version", "R-"+BuildConfig.VERSION_NAME);
                        return headers;
                    }
                };

                JsonObjectRequest jsonRequestToken = new JsonObjectRequest(Request.Method.POST, ApiCalls.getGetToken(), new JSONObject(initApi.getBody()), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG11", response.toString());
                        if (response.has("returnCode")) {
                            try {
                                if (response.getString("returnCode").equalsIgnoreCase("200")) {
                                    String token = new JSONObject(response.getString("response")).getString("access_token");
                                    SharedPreferenceHelper.saveString(SharedPreferenceHelper.API_TOKEN, token, activity);

                                    if (response.has("user_object")) {

                                        JSONObject object = response.optJSONObject("user_object");

                                        User user = new User();
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

                                        SharedPreferenceHelper.setUser(user, activity);
                                    }
                                    ResponseHandler rh;
                                    header.put("Authorization", "Bearer " + token);
                                    sendData();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        ResponseHandler rh;
                        if (isAlternate) {
                            rh = new ResponseHandler(activity, calledApi, alternatebody, header, apiBaseResponseHandler);
                        } else {
                            rh = new ResponseHandler(activity, calledApi, body, header, apiBaseResponseHandler);
                        }
                        responseHandler = rh;
                        responseHandler.errorResponse(error.getMessage());

                    }
                }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return initApi.getHeader();
                    }

                };

                if (jsonRequestToken != null) {
                    jsonRequestToken.setRetryPolicy(new DefaultRetryPolicy(
                            60000, 0/* DefaultRetryPolicy.DEFAULT_MAX_RETRIES*/, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    RequestQueue requestQueue = Volley.newRequestQueue(activity.getApplicationContext());
                    jsonRequestToken.setShouldCache(false);
                    requestQueue.add(jsonRequestToken);
                }



//                Toast.makeText(activity,error.networkResponse.statusCode+" ",Toast.LENGTH_LONG).show();
            }else if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 403) {

                String errorMsg = null;
                try {
                    errorMsg = new JSONObject(new String(error.networkResponse.data, StandardCharsets.UTF_8)).getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorMsg = "Your version of app is outdated. A new version of app is available on Playstore. Please continue and update to latest app.";

                }
                ResponseHandler rh;
                if (isAlternate) {
                    rh = new ResponseHandler(activity, calledApi, alternatebody, header, apiBaseResponseHandler);
                } else {
                    rh = new ResponseHandler(activity, calledApi, body, header, apiBaseResponseHandler);
                }
                responseHandler = rh;
                responseHandler.errorResponse(errorMsg,403);

            } else {
                String errorMsg = "";
                if (error instanceof NetworkError) {
                    errorMsg = "Check your internet connection!";
                    //Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    errorMsg = error.getMessage();
                    //Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    errorMsg = "Authentication Error";
                    //Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    errorMsg = error.getMessage();
                    //Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    errorMsg = "Check your internet connection!";
                    //Toast.makeText(activity, "Check your internet connection! ", Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    errorMsg = "Request Timed out!";
                    //Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                }


                ResponseHandler rh;
                if (isAlternate) {
                    rh = new ResponseHandler(activity, calledApi, alternatebody, header, apiBaseResponseHandler);
                } else {
                    rh = new ResponseHandler(activity, calledApi, body, header, apiBaseResponseHandler);
                }
                responseHandler = rh;
                responseHandler.errorResponse(errorMsg);
            }

        }
    };

    public JsonObjectRequestCall(Map<String, String> header, Map<String, String> body, String calledApi, int method, Activity activity, ApiBaseResponseHandler apiBaseResponseHandler) {
        super(header, body, calledApi, method, activity, apiBaseResponseHandler);
        header.put("x-api-version", "R-"+BuildConfig.VERSION_NAME);
        this.header = header;
        this.body = body;
        this.calledApi = calledApi;
        this.method = method;
        this.activity = activity;
        this.apiBaseResponseHandler = apiBaseResponseHandler;

        if (body != null)
            Log.i("body = ", body.toString());
    }

    public JsonObjectRequestCall(Map<String, String> header, Map<String, String> body, String calledApi, int method, Context activity, ApiBaseResponseHandler apiBaseResponseHandler) {
        super(header, body, calledApi, method, activity, apiBaseResponseHandler);
        header.put("x-api-version", "R-"+BuildConfig.VERSION_NAME);
        this.header = header;
        this.body = body;
        this.calledApi = calledApi;
        this.method = method;
        this.activity = activity;
        this.apiBaseResponseHandler = apiBaseResponseHandler;

        if (body != null)
            Log.i("body = ", body.toString());
    }

    public JsonObjectRequestCall(Map<String, String> header, JSONObject body, String calledApi, int method, Context activity, boolean isAlternate, ApiBaseResponseHandler apiBaseResponseHandler) {
        super(header, body, calledApi, method, activity, isAlternate, apiBaseResponseHandler);
        header.put("x-api-version", "R-"+BuildConfig.VERSION_NAME);
        this.header = header;
        this.alternatebody = body;
        this.calledApi = calledApi;
        this.method = method;
        this.activity = activity;
        this.isAlternate = isAlternate;
        this.apiBaseResponseHandler = apiBaseResponseHandler;

        if (alternatebody != null)
            Log.i("alternateBody = ", alternatebody.toString());
    }

    public void sendData() {
        JsonObjectRequest jsonRequest = null;
        if (method == Request.Method.POST) {
            Log.i("Method", "POST, calledApi= "+calledApi);
            if (isAlternate) {

                jsonRequest = new JsonObjectRequest(method, calledApi, alternatebody, responseListener, responseErrorListener) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Log.i("Header", header.toString());
                        return header;
                    }

                };

            } else {

                jsonRequest = new JsonObjectRequest(method, calledApi, new JSONObject(body), responseListener, responseErrorListener) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Log.i("Header", header.toString());
                        return header;
                    }

                };

            }
        } else if (method == Request.Method.GET || method == Request.Method.PUT) {
            Log.i("Method", "get, PUT, calledApi= "+calledApi);

            jsonRequest = new JsonObjectRequest(method, calledApi, null, responseListener, responseErrorListener) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Log.i("Header", header.toString());
                    return header;
                }
            };

        }

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 0/* DefaultRetryPolicy.DEFAULT_MAX_RETRIES*/, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        if (jsonRequest != null) {
            RequestQueue requestQueue = Volley.newRequestQueue(activity.getApplicationContext());
            jsonRequest.setShouldCache(false);
            requestQueue.add(jsonRequest);
        }

        try {
            if(activity instanceof AppCompatActivity||activity instanceof Activity) {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("Loading Please Wait....");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("TAG11","Dialog Exception= "+e.getMessage());
        }
    }

    RequestQueue requestQueue = null;
    public void sendData(boolean isShow) {

        if (!AppUtils.checkNetworkState(activity)) {
//            AppUtils.makeNotification(activity.getResources().getString(R.string.check_internet_connection), activity);
            ResponseHandler rh;
            if (isAlternate) {
                rh = new ResponseHandler(activity, calledApi, alternatebody, header, apiBaseResponseHandler);
            } else {
                rh = new ResponseHandler(activity, calledApi, body, header, apiBaseResponseHandler);
            }
            responseHandler = rh;
            responseHandler.errorResponse();
            return;
        }

        if (isShow) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Loading Please Wait....");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        JsonObjectRequest jsonRequest = null;
        if (method == Request.Method.POST) {
//            Log.i("Method ", "post");
            if (isAlternate) {

                jsonRequest = new JsonObjectRequest(method, calledApi, alternatebody, responseListener, responseErrorListener) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
//                        Log.i("Header", header.toString());
                        return header;
                    }

                };

            } else {

                jsonRequest = new JsonObjectRequest(method, calledApi, new JSONObject(body), responseListener, responseErrorListener) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
//                        Log.i("Header", header.toString());
                        return header;
                    }

                };

            }
        } else if (method == Request.Method.GET || method == Request.Method.PUT) {
//            Log.i("Method ", "get");

            jsonRequest = new JsonObjectRequest(method, calledApi, null, responseListener, responseErrorListener) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Log.i("Header", header.toString());
                    return header;
                }
            };

        }


        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 0/* DefaultRetryPolicy.DEFAULT_MAX_RETRIES*/, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        try {
            if (jsonRequest != null) {
                jsonRequest.setShouldCache(false);
                MyVolleySingleton.getInstance(activity.getApplicationContext()).getRequestQueue().add(jsonRequest);

            }
        } catch (Exception e) {
            Log.d("TAG11","JsonObjectRequestCall.java Api exception= "+e.getMessage());
            e.printStackTrace();
        }
    }
}
