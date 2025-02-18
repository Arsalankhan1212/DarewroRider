package com.darewro.rider.data.api.requests;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.darewro.rider.data.api.ApiHandler;
import com.darewro.rider.data.api.BaseResponseHandler;
import com.darewro.rider.data.api.ResponseHandler;
import com.darewro.rider.data.api.handlers.ApiBaseResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Ahmad Abbas Zaidi on 3/21/2017.
 */

public class StringRequestCall extends ApiHandler {

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
        public void onErrorResponse(VolleyError error) {
            Log.i("volley error = ", error.toString());
            progressDialog.dismiss();

            String errorMsg = "";
            if (error instanceof NetworkError) {
                errorMsg = "Check your internet connection!";
                // Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            } else if (error instanceof ServerError) {
                errorMsg = error.getMessage();
                //  Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            } else if (error instanceof AuthFailureError) {
                errorMsg = "Authentication Error";
                // Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            } else if (error instanceof ParseError) {
                errorMsg = error.getMessage();
                //  Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            } else if (error instanceof NoConnectionError) {
                errorMsg = "Check your internet connection!";
                //  Toast.makeText(activity, "Check your internet connection! ", Toast.LENGTH_SHORT).show();
            } else if (error instanceof TimeoutError) {
                errorMsg = "Request Timed out!";
                //  Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
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
    };

    public StringRequestCall(Map<String, String> header, Map<String, String> body, String calledApi, int method, Activity activity, ApiBaseResponseHandler apiBaseResponseHandler) {
        super(header, body, calledApi, method, activity, apiBaseResponseHandler);
        if (body != null)
            Log.i("body = ", body.toString());
    }

    public StringRequestCall(Map<String, String> header, JSONObject body, String calledApi, int method, Activity activity, boolean isAlternate, ApiBaseResponseHandler apiBaseResponseHandler) {
        super(header, body, calledApi, method, activity, isAlternate, apiBaseResponseHandler);
        if (alternatebody != null)
            Log.i("alternateBody = ", alternatebody.toString());
    }

    public void sendData() {
        StringRequest stringRequest = null;
        if (method == Request.Method.POST) {


            stringRequest = new StringRequest(method, calledApi, responseListener, responseErrorListener) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Log.i("Header", header.toString());
                    return header;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Log.i("Body", isAlternate ? alternatebody.toString() : body.toString());
                    return body;
                }

            };
        } else if (method == Request.Method.GET) {
            stringRequest = new StringRequest(method, calledApi, responseListener, responseErrorListener) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Log.i("Header", header.toString());
                    return header;
                }
            };
        }
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if (stringRequest != null) {
            if (requestQueue == null)
                requestQueue = Volley.newRequestQueue(activity.getApplicationContext());
            requestQueue.add(stringRequest);
        }
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading Please Wait....");
        progressDialog.show();
    }

}
