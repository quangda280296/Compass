package com.vmb.flashlight.service;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.vmb.ads_in_app.Interface.IAPISendToken;
import com.vmb.ads_in_app.LibrayData;
import com.vmb.ads_in_app.util.CountryCodeUtil;
import com.vmb.ads_in_app.util.DeviceUtil;
import com.vmb.ads_in_app.util.RetrofitInitiator;
import com.vmb.ads_in_app.util.SharedPreferencesUtil;
import com.vmb.ads_in_app.util.TimeRegUtil;
import com.vmb.flashlight.Config;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (TextUtils.isEmpty(refreshedToken))
            return;

        Log.i(TAG, "Refreshed token: " + refreshedToken);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        SharedPreferencesUtil.putPrefferString(getApplicationContext(), LibrayData.KeySharePrefference.NOTI_TOKEN, refreshedToken);
        //SharedPreferencesUtil.putPrefferBool(getApplicationContext(), Config.KeySharePrefference.SEND_TOKEN, false);
        sendRegistrationToServer(getApplicationContext());
    }

    public static void sendRegistrationToServer(final Context context) {
        if (context == null) {
            Log.i(TAG, "context == null");
            return;
        }

        /*final boolean check = SharedPreferencesUtil.getPrefferBool(context, Config.KeySharePrefference.SEND_TOKEN, false);
        if (check) {
            Log.i(TAG, "check == true");
            return;
        }*/

        String token = SharedPreferencesUtil.getPrefferString(context, LibrayData.KeySharePrefference.NOTI_TOKEN, "");
        // TODO: Implement this method to send token to your app server.
        String deviceID = DeviceUtil.getDeviceId(context);
        String code = Config.CODE_CONTROL_APP;
        String version = Config.VERSION_APP;
        String packg = Config.PACKAGE_NAME;
        String timeRegister = TimeRegUtil.getTimeRegister(context);
        String os_version = DeviceUtil.getDeviceOS();
        String phone_name = DeviceUtil.getDeviceName();
        String country = CountryCodeUtil.getCountryCode(context);
        /*if (TextUtils.isEmpty(country)) {
            Log.i(TAG, "country empty");
            return;
        }*/

        Log.i(TAG, "deviceID = " + deviceID);
        Log.i(TAG, "code = " + code);
        Log.i(TAG, "version = " + version);
        Log.i(TAG, "package = " + packg);
        Log.i(TAG, "os_version = " + os_version);
        Log.i(TAG, "phone_name = " + phone_name);
        Log.i(TAG, "country = " + country);
        Log.i(TAG, "timeRegister = " + timeRegister);
        Log.i(TAG, "token_id = " + token);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("deviceID", deviceID)
                .addFormDataPart("code", code)
                .addFormDataPart("version", version)
                .addFormDataPart("package", packg)
                .addFormDataPart("os_version", os_version)
                .addFormDataPart("phone_name", phone_name)
                .addFormDataPart("country", country)
                .addFormDataPart("timeRegister", timeRegister)
                .addFormDataPart("token_id", token)
                .build();

        IAPISendToken api = RetrofitInitiator.createService(IAPISendToken.class, LibrayData.Url.URL_BASE);
        Call<ResponseBody> call = api.postToken(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "onResponse()");
                if (response == null || response.body() == null) {
                    Log.i(TAG, "response == null || response.body() == null");
                    return;
                }

                Log.i(TAG, "response = " + response.toString());
                if (response.isSuccessful()) {
                    SharedPreferencesUtil.putPrefferBool(context, LibrayData.KeySharePrefference.SEND_TOKEN, true);
                    Log.i(TAG, "response.isSuccessful()");
                } else {
                    Log.i(TAG, "response.failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "onFailure()");
            }
        });
    }
}