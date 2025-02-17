package com.darewro.riderApp.view.xmpp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.darewro.riderApp.view.utils.AppWideVariables;
import com.darewro.riderApp.view.utils.SharedPreferenceHelper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.locationtech.jts.operation.valid.IndexedNestedRingTester;

import java.io.IOException;

/**
 * Created by gakwaya on 4/28/2016.
 */
public class XmppConnectionService extends Service {

    private static final String TAG = "XmppService";

    public static XmppConnection.ConnectionState sConnectionState;
    public static XmppConnection.LoggedInState sLoggedInState;
    private boolean mActive; //Stores whether or not the thread is active
    private Thread mThread;
    private Handler mTHandler; //We use this handler to post messages to
    private String username;


    private String password;    //the background thread.
    private XmppConnection mConnection;


    public static XmppConnection.ConnectionState getState() {
        if (sConnectionState == null) {
            return XmppConnection.ConnectionState.DISCONNECTED;
        }
        return sConnectionState;
    }

    public static XmppConnection.LoggedInState getLoggedInState() {
        if (sLoggedInState == null) {
            return XmppConnection.LoggedInState.LOGGED_OUT;
        }
        return sLoggedInState;
    }

    public XmppConnection getmConnection() {
        return mConnection;
    }

    public void resetConnection() {
        mConnection = null;
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
        start();
        Log.d(TAG, "onCreate()");

    }

    private void initConnection() {
        Log.d(TAG, "initConnection()");
        if (mConnection == null) {
            mConnection = XmppConnection.getInstance(this, username, password);
        }
        try {
            mConnection.connect();
        } catch (IOException | SmackException | XMPPException e) {
            Log.d(TAG, "Something went wrong while connecting ,make sure the credentials are right and try again");
            e.printStackTrace();
            //Stop the service all together.
            stopSelf();
        }
    }

    public void start() {
        Log.d(TAG, " Service Start() function called.");
        if (!mActive) {
            mActive = true;
            if (mThread == null || !mThread.isAlive()) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Looper.prepare();
                        mTHandler = new Handler();
                        initConnection();
                        //THE CODE HERE RUNS IN A BACKGROUND THREAD.
                        Looper.loop();

                    }
                });
                mThread.start();
            }
        }
    }

    public void stop() {
        Log.d(TAG, "stop()");
        mActive = false;
        mTHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mConnection != null && mConnection.getmConnection() != null) {
                    mConnection.getmConnection().disconnect();
//            xmpp.clearInstance();
                }
                if (mConnection != null) {
                    mConnection.disconnect();
                }
                mConnection = null;
            }
        });
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
//        handleIntent(intent);
//        setCredentials();
//        start();
        return Service.START_STICKY;
        //RETURNING START_STICKY CAUSES OUR CODE TO STICK AROUND WHEN THE APP ACTIVITY HAS DIED.
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        return super.onUnbind(intent);
    }

    private void setCredentials() {
        try {
            username = SharedPreferenceHelper.getString(XMPPUser.XMPP_ID,XmppConnectionService.this);
            password = SharedPreferenceHelper.getString(XMPPUser.XMPP_PASSWORD,XmppConnectionService.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        startForeground(2, AppUtils.createNotification(this, "Service Created....", AppUtils.M_SERVICE_CHANNEL_ID));
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        stop();
        super.onDestroy();
//        startForeground(2, AppUtils.createNotification(this, "Service Destroyed....", AppUtils.M_SERVICE_CHANNEL_ID));

    }

}
