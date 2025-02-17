package com.darewro.riderApp.view.pushServices;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.darewro.riderApp.view.services.NotificationSoundService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.darewro.riderApp.data.api.ApiCalls;
import com.darewro.riderApp.view.activities.DialogActivity;
import com.darewro.riderApp.view.activities.FireBaseViewNotificationActivity;
import com.darewro.riderApp.view.utils.AppUtils;
import com.darewro.riderApp.view.utils.SharedPreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DarewroFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    private static final String CHANNEL_ID = "custom_channel_id";

    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        Log.d(TAG, "Firebase Token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i("remoteMessage", remoteMessage.toString());

        if (remoteMessage.getData().get("EventType") != null) {
            String eventType = remoteMessage.getData().get("EventType");
            Log.d(TAG, "EventType: " + eventType);

            // Start sound service for notifications
            Intent soundIntent = new Intent(this, NotificationSoundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplicationContext().startService(soundIntent); // No need for foreground service
            } else {
                startService(soundIntent);
            }


            if (eventType.equals("ForceLogout")) {
                AppUtils.forceLogout(getApplicationContext());
            }
            else if (eventType.equals("RiderNotification")) {
                String message = remoteMessage.getData().get("message");
                String title = remoteMessage.getData().get("title");
                String payload = remoteMessage.getData().get("Payload");

                Log.d(TAG, "Firebase Message: " + message);

                Intent i = new Intent(getApplicationContext(), FireBaseViewNotificationActivity.class);
                i.putExtra("title", title);
                i.putExtra("message", message);
                if (payload != null && !payload.isEmpty()) {
                    i.putExtra("payload", payload);
                }
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                AppUtils.sendCustomNotification(getApplicationContext(), title, message, i, payload, getApplicationContext());
                getApplicationContext().startActivity(i);
            }
            else if (eventType.equals("AssignedOrderAcceptanceBenefitPushEvent")) {
                String message = remoteMessage.getData().get("message");
                try {
                    JSONObject payload = new JSONObject(remoteMessage.getData().get("Payload"));
                    String title = remoteMessage.getData().get("title");
                    String acceptMessage = payload.getString("pushNotiAcceptMessage").replace("###", "\n");
                    String rejectMessage = payload.getString("pushNotiRejectMessage").replace("###", "\n");

                    SharedPreferenceHelper.saveString(payload.getString("orderID") + "_acceptance", acceptMessage, getApplicationContext());
                    SharedPreferenceHelper.saveString(payload.getString("orderID") + "_rejection", rejectMessage, getApplicationContext());

                    Intent i = new Intent(getApplicationContext(), DialogActivity.class);
                    i.putExtra("eventType", eventType);
                    i.putExtra("title", title);
                    i.putExtra("message", message);
                    i.putExtra("orderId", payload.getString("orderID"));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    AppUtils.sendOrderNotification(getApplicationContext(), title, message, i, payload.getString("orderID"), getApplicationContext());
                    getApplicationContext().startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                String message = remoteMessage.getData().get("message");
                String title = remoteMessage.getData().get("title");
                String orderId = remoteMessage.getData().get("Payload");

                Log.d(TAG, "Firebase: " + message);
                Intent i = new Intent(getApplicationContext(), DialogActivity.class);
                i.putExtra("eventType", eventType);
                i.putExtra("title", title);
                i.putExtra("message", message);
                i.putExtra("orderId", orderId);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                AppUtils.sendOrderNotification(getApplicationContext(), title, message, i, orderId, getApplicationContext());
                getApplicationContext().startActivity(i);
            }
        }
    }

    private void sendRegistrationToServer(String token) {
        SharedPreferenceHelper.saveString(SharedPreferenceHelper.FIRE_BASE_TOKEN, token, getApplicationContext());
    }
}