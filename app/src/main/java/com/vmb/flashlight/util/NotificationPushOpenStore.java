package com.vmb.flashlight.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.vm.compass.compass2019.R;
import com.vmb.ads_in_app.LibrayData;

import java.util.Calendar;

/**
 * Created by keban on 6/15/2018.
 */

public class NotificationPushOpenStore {

    private Context context;
    private String title;
    private String message;
    private String url_store;
    private Bitmap bitmap;

    public NotificationPushOpenStore(Context context, String title, String message, String url_store, Bitmap bitmap) {
        this.context = context;
        this.title = title;
        this.message = message;
        this.url_store = url_store;
        this.bitmap = bitmap;
    }

    public void addNotify() {
        thread.run();
    }

    // Handle notification
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url_store));
            PendingIntent launchIntent =
                    PendingIntent.getActivity(context, LibrayData.RequestCode.REQUEST_CODE_NOTIFICATON, intent, 0);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.drawable.ic_notification_google_play)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(launchIntent)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setPriority(Notification.PRIORITY_MAX);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setVisibility(Notification.VISIBILITY_SECRET);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.layout_notificartion_open_store);
                contentView.setImageViewBitmap(R.id.notification_item_image, bitmap);

                Calendar calendar = Calendar.getInstance();
                String n;
                int m = calendar.get(Calendar.MINUTE);
                if (m < 10)
                    n = "0" + m;
                else
                    n = "" + m;
                contentView.setTextViewText(R.id.notification_item_time, calendar.get(Calendar.HOUR_OF_DAY) + ":" + n);

                if (!TextUtils.isEmpty(title))
                    contentView.setTextViewText(R.id.notification_item_title, title);
                if (!TextUtils.isEmpty(message))
                    contentView.setTextViewText(R.id.notification_item_message, message);
                builder.setContent(contentView);
            } else {
                RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.layout_notificartion_open_store_below);
                contentView.setImageViewBitmap(R.id.notification_item_image, bitmap);

                Calendar calendar = Calendar.getInstance();
                String n;
                int m = calendar.get(Calendar.MINUTE);
                if (m < 10)
                    n = "0" + m;
                else
                    n = "" + m;
                contentView.setTextViewText(R.id.notification_item_time, calendar.get(Calendar.HOUR_OF_DAY) + ":" + n);

                if (!TextUtils.isEmpty(title))
                    contentView.setTextViewText(R.id.notification_item_title, title);
                if (!TextUtils.isEmpty(message))
                    contentView.setTextViewText(R.id.notification_item_message, message);
                builder.setContent(contentView);
            }

            //define a notification manager
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String channel_Id = context.getString(R.string.default_notification_channel_id);

                NotificationChannel notificationChannel =
                        new NotificationChannel(channel_Id, title, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription(message);

                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                notificationChannel.setSound(defaultSoundUri, att);

                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableLights(true);

                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationChannel.enableVibration(true);

                notificationManager.createNotificationChannel(notificationChannel);
                builder.setChannelId(channel_Id);
            }

            Notification notification = builder.build();
            notificationManager.notify(LibrayData.ID_NOTIFICATION, notification);
        }
    });

    private Bitmap getBitmapIcon(Context context, Bitmap bitmap) {
        int width = getSizeImage(context);
        return Bitmap.createScaledBitmap(bitmap, width, width, true);
    }

    private int getSizeImage(Context context) {
        return (int) context.getResources().getDimension(R.dimen.image_size);
    }
}