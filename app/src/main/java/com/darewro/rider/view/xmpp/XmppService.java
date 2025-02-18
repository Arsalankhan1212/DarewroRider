package com.darewro.rider.view.xmpp;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.darewro.rider.view.utils.AppUtils;
import com.darewro.rider.view.utils.SharedPreferenceHelper;

/**
 * Created by gakwaya on 4/28/2016.
 */
public class XmppService extends Service {

    private static final String TAG = "XmppService";

    private XmppClient xmpp;

    public XmppClient getXmpp() {
        return xmpp;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder<>(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setCredentials();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(9, AppUtils.createNotification(XmppService.this, null, AppUtils.M_CONNECTION_CHANNEL_ID));
        } else {
            startForeground(9, AppUtils.createNotification(XmppService.this, null, AppUtils.M_CONNECTION_CHANNEL_ID),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);

        }

        Log.d(TAG, "onCreate()");

    }

    private void setCredentials() {
        String userName = SharedPreferenceHelper.getString(XMPPUser.XMPP_ID, XmppService.this);
        String password = SharedPreferenceHelper.getString(XMPPUser.XMPP_PASSWORD, XmppService.this);

//            xmpp.clearInstance();
        xmpp = XmppClient.getInstance(this, userName, password);
        xmpp.connect("onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        setCredentials();
        return Service.START_STICKY;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        if (xmpp != null && xmpp.getConnection() != null) {
            xmpp.getConnection().disconnect();
//            xmpp.clearInstance();
        }
       /* if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(9, AppUtils.createNotification(XmppService.this, null, AppUtils.M_CONNECTION_CHANNEL_ID));
        } else {
            startForeground(9, AppUtils.createNotification(XmppService.this, null, AppUtils.M_CONNECTION_CHANNEL_ID),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);

        }*/
        super.onDestroy();
    }
}
