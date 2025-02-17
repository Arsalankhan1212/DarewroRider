package com.darewro.riderApp.view.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyVolleySingleton {
    private static MyVolleySingleton instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private MyVolleySingleton(Context context) {
        ctx = context;
        requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
    }

    public static synchronized MyVolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new MyVolleySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
