package com.vmb.flashlight.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vmb.ads_in_app.util.PushBackNotify;
import com.vmb.flashlight.Config;
import com.vmb.flashlight.util.LoadBitmapFromURL;
import com.vmb.flashlight.util.NotificationUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.i(TAG, "Message data payload: " + remoteMessage.getData());

            // Handle message within 10 seconds
            handleNow(remoteMessage);

        }// if (remoteMessage.getData().size() > 0)

        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "Message NotificationHandler Body: " + remoteMessage.getNotification().getBody());
        }
    }

    public void handleNow(RemoteMessage remoteMessage) {
        if (remoteMessage == null || remoteMessage.getData() == null) {
            Log.i(TAG, "remoteMessage == null || remoteMessage.getData() == null");
            return;
        }

        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");
        String pushId = remoteMessage.getData().get("pushId");
        String pushType = remoteMessage.getData().get("pushType");

        Log.i(TAG, "title = " + title);
        Log.i(TAG, "message = " + message);
        Log.i(TAG, "pushId = " + pushId);
        Log.i(TAG, "pushType = " + pushType);

        if (pushType.equals("statistical")) {
            PushBackNotify.push(MyFirebaseMessagingService.this, pushId,
                    Config.CODE_CONTROL_APP, Config.VERSION_APP, Config.PACKAGE_NAME);
        } else if (pushType.equals("ads")) {
            String json = remoteMessage.getData().get("data");
            try {
                JSONObject jsonObject = new JSONObject(json);
                String url_store = jsonObject.getString("url_store");
                String icon = jsonObject.getString("icon");

                Log.i(TAG, "url_store = " + url_store);
                Log.i(TAG, "icon = " + icon);

                new LoadBitmapFromURL(MyFirebaseMessagingService.this, title, message, url_store, icon).execute();

            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(TAG, e.getMessage());
            }
        } else {
            NotificationUtil notifyDemo = new NotificationUtil(MyFirebaseMessagingService.this, title, message);
            notifyDemo.addNotify();
        }
    }
}