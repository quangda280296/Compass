package com.vmb.flashlight.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by keban on 9/1/2017.
 */

public class ConnectionReceiver extends BroadcastReceiver {

    private static ConnectionReceiver receiver;

    public static ConnectionReceiver getInstance() {
        if (receiver == null)
            synchronized (ConnectionReceiver.class) {
                receiver = new ConnectionReceiver();
            }
        return receiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        /*if (FlashModeHandler.getInstance().isGPRS()) {
            NotificationHandler handler = new NotificationHandler(context);
            handler.addNotify();
        }*/
    }
}