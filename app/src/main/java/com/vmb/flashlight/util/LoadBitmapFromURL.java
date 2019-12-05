package com.vmb.flashlight.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadBitmapFromURL extends AsyncTask {

    private Context context;
    private String title;
    private String message;
    private String url_store;
    private String url;

    public LoadBitmapFromURL(Context context, String title, String message, String url_store, String url) {
        this.context = context;
        this.title = title;
        this.message = message;
        this.url_store = url_store;
        this.url = url;
    }

    Bitmap bitmap;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i("LoadBitmapFromURL", "url = " + url);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            //Tiến hành tạo đối tượng URL
            URL urlConnection = new URL(url);

            //Mở kết nối
            HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
            connection.setDoInput(true);
            connection.connect();

            //Đọc dữ liệu
            InputStream input = connection.getInputStream();

            //Convert
            bitmap = BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Log.i("LoadBitmapFromURL", "onPostExecute");

        NotificationPushOpenStore notifyDemo = new NotificationPushOpenStore(context, title, message, url_store, bitmap);
        notifyDemo.addNotify();
    }
}