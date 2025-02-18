package com.darewro.rider.data.api.requests;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.darewro.rider.data.api.ApiCalls;
import com.darewro.rider.data.api.ApiHandler;
import com.darewro.rider.data.api.BaseResponseHandler;
import com.darewro.rider.data.api.Interface.InitApi;
import com.darewro.rider.data.api.ResponseHandler;
import com.darewro.rider.data.api.handlers.ApiBaseResponseHandler;
import com.darewro.rider.view.utils.SharedPreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ahmad Abbas Zaidi on 3/21/2017.
 */

public class MultipartRequestCall extends ApiHandler {

    private BaseResponseHandler responseHandler;
    private RequestQueue requestQueue;
    private ProgressDialog progressDialog;

    private Response.Listener<String> responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

            try {
                responseJson = new JSONObject(response);

                ResponseHandler rh;
                if (isAlternate) {
                    rh = new ResponseHandler(activity, calledApi, alternatebody, header, apiBaseResponseHandler);
                } else {
                    rh = new ResponseHandler(activity, calledApi, body, header, apiBaseResponseHandler);
                }

                responseHandler = rh;
                responseHandler.setResponse(responseJson, response);
                responseHandler.handleResponse();
                progressDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
    private Response.ErrorListener responseErrorListener = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(com.android.volley.VolleyError error) {
            Log.i("Server volley error = ", error.toString());
            if (progressDialog != null)
                progressDialog.dismiss();


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
                        return headers;
                    }
                };

                JsonObjectRequest jsonRequestToken = new JsonObjectRequest(Request.Method.POST, ApiCalls.getGetToken(), new JSONObject(initApi.getBody()), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("ResponseJson = ", response.toString());
                        if (response.has("returnCode")) {
                            try {
                                if (response.getString("returnCode").equalsIgnoreCase("200")) {
                                    String token = new JSONObject(response.getString("response")).getString("access_token");
                                    SharedPreferenceHelper.saveString(SharedPreferenceHelper.API_TOKEN, token, activity);
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
                    public void onErrorResponse(com.android.volley.VolleyError error) {
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
                    public Map<String, String> getHeaders() {
                        return initApi.getHeader();
                    }

                };

                if (jsonRequestToken != null) {
                    jsonRequestToken.setRetryPolicy(new DefaultRetryPolicy(
                            30000, 0/* DefaultRetryPolicy.DEFAULT_MAX_RETRIES*/, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    RequestQueue requestQueue = Volley.newRequestQueue(activity.getApplicationContext());
                    requestQueue.add(jsonRequestToken);
                }


//                Toast.makeText(activity,error.networkResponse.statusCode+" ",Toast.LENGTH_LONG).show();
            } else {
                String errorMsg = error.getMessage();
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

//                Toast.makeText(activity,error.networkResponse.statusCode+" "+errorMsg,Toast.LENGTH_LONG).show();


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
    public MultipartRequestCall(Map<String, String> header, Map<String, String> body, String calledApi, int method, Activity activity, ApiBaseResponseHandler apiBaseResponseHandler) {
        super(header, body, calledApi, method, activity, apiBaseResponseHandler);
        if (body != null)
            Log.i("body = ", body.toString());
    }

    public MultipartRequestCall(Map<String, String> header, JSONObject body, String calledApi, int method, Activity activity, boolean isAlternate, ApiBaseResponseHandler apiBaseResponseHandler) {
        super(header, body, calledApi, method, activity, isAlternate, apiBaseResponseHandler);
        if (alternatebody != null)
            Log.i("alternateBody = ", alternatebody.toString());
    }

    public void sendData() {
//        SimpleMultiPartRequest simpleMultiPartRequest = null;
//        if (method == Request.Method.POST) {
//            simpleMultiPartRequest = new SimpleMultiPartRequest(method, calledApi, responseListener, responseErrorListener);
//            for (String str : body.keySet()) {
//                if(str.equalsIgnoreCase("ChatFileImage"))
//                {simpleMultiPartRequest.addFile(str,body.get(str));}
//                else
//                {
//                    simpleMultiPartRequest.addStringParam(str,body.get(str));
//                }
//            }
//        }
//
//        simpleMultiPartRequest.setRetryPolicy(new DefaultRetryPolicy(1800000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));



//        if (simpleMultiPartRequest != null) {
//            if (requestQueue == null)
//                requestQueue = Volley.newRequestQueue(activity.getApplicationContext());
//            requestQueue.add(simpleMultiPartRequest);
//        }
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading Please Wait....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

}
