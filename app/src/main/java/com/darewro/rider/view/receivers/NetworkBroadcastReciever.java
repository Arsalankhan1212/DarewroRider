package com.darewro.rider.view.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.darewro.rider.view.activities.MainActivity;
import com.darewro.rider.view.utils.AppUtils;
import com.darewro.rider.view.utils.SharedPreferenceHelper;


public class NetworkBroadcastReciever extends BroadcastReceiver {
    private static final String TAG = "NetworkStateReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG, "Network connectivity change");
        if (intent.getExtras() != null) {
            final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

            if (ni != null && ni.isConnectedOrConnecting()) {
                try {
                    System.out.println("connection : -----NetworkBroadCastReciever");
                    if (!SharedPreferenceHelper.getString(SharedPreferenceHelper.ORDER_ID, context).equals("")) {
                        AppUtils.startXMPPService(context);
                    }else{
                        AppUtils.stopXMPPService(context);
                    }
                    AppUtils.pushRiderStatsIfInternetWorking(context);
                } catch (Exception e) {

                }

            }
//            else if (ni != null && ni.isConnected()) {
//
//                if (xmppService != null && xmppService.getXmpp() != null)
//                     xmppService.getXmpp().connect("On Network State Changed");
//            }
            else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                Log.d(TAG, "There's no network connectivity");
            } else {
                Log.d(TAG, "wifi off");
            }
        }
    }

}
