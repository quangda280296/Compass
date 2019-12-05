package com.vmb.ads_in_app.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.vmb.ads_in_app.LibrayData;
import com.vmb.ads_in_app.R;

import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by jacky on 11/21/17.
 */

public class ShareUtil {

    public static void shareApp(Activity activity) {
        try {
            String appPackageName = activity.getPackageName();
            String shareBody = "https://play.google.com/store/apps/details?id=" + appPackageName;
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            activity.startActivityForResult(Intent.createChooser(sharingIntent, "Share App"),
                    LibrayData.RequestCode.REQUEST_CODE_SHARE_APP);
        } catch (Exception e) {

        }
    }

    public static void shareFB(final Activity activity, final String path, final CallbackManager callbackManager,
                               final String code, final String version, final String packg) {
        final String TAG = "shareFB()";

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            showShareFB(activity, path, callbackManager, code, version, packg);
            return;
        }

        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList(""));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        Log.i(TAG, "onSuccess");
                        showShareFB(activity, path, callbackManager, code, version, packg);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(activity, R.string.login_cancel, Toast.LENGTH_SHORT).show();
                        Log.i("facebookLogin()", "onCancel");
                    }

                    @Override
                    public void onError(FacebookException e) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i("facebookLogin()", "onError: " + e.getMessage());
                    }
                });
    }

    public static void showShareFB(final Activity activity, String path, CallbackManager callbackManager,
                                   final String code, final String version, final String packg) {
        final String TAG = "showShareFB()";

        ShareDialog shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                ToastUtil.shortToast(activity, activity.getString(R.string.share_success));
                Log.i(TAG, "onSuccess");

                final String deviceId = DeviceUtil.getDeviceId(activity);
                final String country_code = CountryCodeUtil.getCountryCode(activity);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String timeRegister = TimeRegUtil.getTimeRegister(activity);
                            if (TextUtils.isEmpty(timeRegister))
                                timeRegister = String.valueOf(System.currentTimeMillis() / 1000);

                            String url = "http://gamemobileglobal.com/api/log_share_app.php?"
                                    + "deviceID=" + deviceId
                                    + "&code=" + code
                                    + "&version=" + version
                                    + "&country=" + country_code
                                    + "&timereg=" + timeRegister
                                    + "&package=" + packg;

                            Log.i(TAG, "url_control.php = " + url);
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(url)
                                    .build();
                            client.newCall(request).execute();

                        } catch (Exception e) {
                            Log.i(TAG, "catch Exception");
                        }
                    }
                }).start();
            }

            @Override
            public void onCancel() {
                ToastUtil.shortToast(activity, activity.getString(R.string.share_cancel));
                Log.i(TAG, "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                ToastUtil.shortToast(activity, activity.getString(R.string.share_error) + "\n" + error.getMessage());
                Log.i(TAG, "onError: " + error.getMessage());
            }
        });

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(path))
                    /*.setShareHashtag(new ShareHashtag.Builder()
                            .setHashtag(activity.getString(R.string.app_name))
                            .build())
                    .setQuote(activity.getString(R.string.app_name))*/
                    .build();
            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
        }
    }

    public static void shareFB(final Activity activity, final CallbackManager callbackManager,
                               final String code, final String version, final String packg) {
        final String TAG = "shareFB()";

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            showShareFB(activity, callbackManager, code, version, packg);
            return;
        }

        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList(""));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        Log.i(TAG, "onSuccess");
                        showShareFB(activity, callbackManager, code, version, packg);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(activity, R.string.login_cancel, Toast.LENGTH_SHORT).show();
                        Log.i("facebookLogin()", "onCancel");
                    }

                    @Override
                    public void onError(FacebookException e) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i("facebookLogin()", "onError: " + e.getMessage());
                    }
                });
    }

    public static void showShareFB(final Activity activity, CallbackManager callbackManager,
                                   final String code, final String version, final String packg) {
        final String TAG = "showShareFB()";

        ShareDialog shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                ToastUtil.shortToast(activity, activity.getString(R.string.share_success));
                Log.i(TAG, "onSuccess");

                final String deviceId = DeviceUtil.getDeviceId(activity);
                final String country_code = CountryCodeUtil.getCountryCode(activity);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String timeRegister = TimeRegUtil.getTimeRegister(activity);
                            if (TextUtils.isEmpty(timeRegister))
                                timeRegister = String.valueOf(System.currentTimeMillis() / 1000);

                            String url = "http://gamemobileglobal.com/api/log_share_app.php?"
                                    + "deviceID=" + deviceId
                                    + "&code=" + code
                                    + "&version=" + version
                                    + "&country=" + country_code
                                    + "&timereg=" + timeRegister
                                    + "&package=" + packg;

                            Log.i(TAG, "url_control.php = " + url);
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(url)
                                    .build();
                            client.newCall(request).execute();

                        } catch (Exception e) {
                            Log.i(TAG, "catch Exception");
                        }
                    }
                }).start();
            }

            @Override
            public void onCancel() {
                ToastUtil.shortToast(activity, activity.getString(R.string.share_cancel));
                Log.i(TAG, "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                ToastUtil.shortToast(activity, activity.getString(R.string.share_error) + "\n" + error.getMessage());
                Log.i(TAG, "onError: " + error.getMessage());
            }
        });

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=" + packg))
                    /*.setShareHashtag(new ShareHashtag.Builder()
                            .setHashtag(activity.getString(R.string.app_name) + " - Brightest Flashlight App")
                            .build())
                    .setQuote(activity.getString(R.string.app_name) + " - Brightest Flashlight App")*/
                    .build();
            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
        }
    }

    public static void rateApp(Activity activity) {
        try {
            String appPackageName = activity.getPackageName();
            try {
                activity.startActivityForResult(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + appPackageName)), LibrayData.RequestCode.REQUEST_CODE_RATE_APP);
            } catch (Exception e) {
                activity.startActivityForResult(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)),
                        LibrayData.RequestCode.REQUEST_CODE_RATE_APP);
            }
        } catch (Exception e) {

        }
    }
}