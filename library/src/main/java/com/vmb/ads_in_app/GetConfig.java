package com.vmb.ads_in_app;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.vmb.ads_in_app.Interface.IAPIControl;
import com.vmb.ads_in_app.Interface.IUpdateNewVersion;
import com.vmb.ads_in_app.LibrayData;
import com.vmb.ads_in_app.R;
import com.vmb.ads_in_app.handler.AdsHandler;
import com.vmb.ads_in_app.model.AdsConfig;
import com.vmb.ads_in_app.util.CountryCodeUtil;
import com.vmb.ads_in_app.util.DeviceUtil;
import com.vmb.ads_in_app.util.GetPackages;
import com.vmb.ads_in_app.util.RetrofitInitiator;
import com.vmb.ads_in_app.util.SharedPreferencesUtil;
import com.vmb.ads_in_app.util.TimeRegUtil;
import com.vmb.ads_in_app.util.shortcut.LoadIconShortcutUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetConfig {

    public static void callAPI(final IUpdateNewVersion listener, final Activity activity, String code, String version, String packg) {
        final String TAG = "callAPI()";

        final List<String> app_package_list = GetPackages.getAll(activity);

        String deviceID = DeviceUtil.getDeviceId(activity);
        String timereg = TimeRegUtil.getTimeRegister(activity);
        String country_code = CountryCodeUtil.getCountryCode(activity);
        if (TextUtils.isEmpty(country_code))
            country_code = Locale.getDefault().getCountry().toUpperCase();
        String phone_name = DeviceUtil.getDeviceName();
        String os_version = DeviceUtil.getDeviceOS();

        Log.i(TAG, "deviceID = " + deviceID);
        Log.i(TAG, "code = " + code);
        Log.i(TAG, "version = " + version);
        Log.i(TAG, "timereg = " + timereg);
        Log.i(TAG, "packg = " + packg);
        Log.i(TAG, "country_code = " + country_code);
        Log.i(TAG, "phone_name = " + phone_name);
        Log.i(TAG, "os_version = " + os_version);

        IAPIControl api = RetrofitInitiator.createService(IAPIControl.class, LibrayData.Url.URL_BASE);
        Call<AdsConfig> call = api.getAds(deviceID, code, version, country_code, timereg, packg, phone_name, os_version);
        call.enqueue(new Callback<AdsConfig>() {
            @Override
            public void onResponse(Call<AdsConfig> call, Response<AdsConfig> response) {
                Log.i(TAG, "onResponse()");
                if (response == null) {
                    Log.i(TAG, "response == null");
                    initAdsWhileServerDown(app_package_list, listener, activity);
                    return;
                }

                if (response.isSuccessful()) {
                    Log.i(TAG, "response.isSuccessful()");

                    if (response.body() == null || activity.isFinishing()) {
                        Log.i(TAG, "response.body() null || activity.isFinishing()");
                        return;
                    }

                    AdsConfig.setInstance(response.body());
                    handle(app_package_list, listener, activity);

                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
                    Log.i(TAG, "json = " + json);

                    // Write to a file
                    try {
                        FileOutputStream fout = activity.openFileOutput(LibrayData.FileName.FILE_ADS_CONFIG, Activity.MODE_PRIVATE);
                        OutputStreamWriter osw = new OutputStreamWriter(fout);
                        osw.write(json);
                        osw.flush();
                        osw.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.i(TAG, e.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i(TAG, e.getMessage());
                    }

                } else {
                    Log.i(TAG, "response.failed");
                    initAdsWhileServerDown(app_package_list, listener, activity);
                }
            }

            @Override
            public void onFailure(Call<AdsConfig> call, Throwable t) {
                Log.i(TAG, "onFailure()");
                initAdsWhileServerDown(app_package_list, listener, activity);
            }
        });
    }

    private static void initAdsWhileServerDown(List<String> app_package_list, IUpdateNewVersion listener, Activity activity) {
        String TAG = "callAPI()";
        Log.i(TAG, "initAdsWhileServerDown()");

        // Read from a file
        try {
            FileInputStream fin = activity.openFileInput(LibrayData.FileName.FILE_ADS_CONFIG);
            InputStreamReader isr = new InputStreamReader(fin);

            char[] inputBuffer = new char[10];
            int charRead;
            String readString = "";

            //---Start reading from file---
            while ((charRead = isr.read(inputBuffer)) > 0) {
                readString += String.copyValueOf(inputBuffer, 0, charRead);
                inputBuffer = new char[10];
            }

            Log.i(TAG, "readString = " + readString);

            Gson gson = new Gson();
            AdsConfig config = gson.fromJson(readString, AdsConfig.class);

            AdsConfig.setInstance(config);
            handle(app_package_list, listener, activity);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, e.getMessage());
        }
    }

    private static void handle(List<String> app_package_list, IUpdateNewVersion listener, Activity activity) {
        String TAG = "callAPI()";

        AdsHandler.getInstance().initBanner(activity);
        AdsHandler.getInstance().initInterstital(activity);
        AdsHandler.getInstance().initConfirmDialog(activity);

        if (AdsConfig.getInstance().getUpdate_status() != 0) {
            if (AdsConfig.getInstance().getUpdate_status() == 2)
                listener.onGetConfig(true);
            else if (AdsConfig.getInstance().getUpdate_status() == 1)
                listener.onGetConfig(false);
        }

        int lenght = AdsConfig.getInstance().getShortcut().size();
        for (int i = 0; i < lenght; i++) {
            String name = AdsConfig.getInstance().getShortcut().get(i).getName();

            String test = SharedPreferencesUtil.getPrefferString(activity, name, "");
            if (TextUtils.isEmpty(test)) {
                String icon = AdsConfig.getInstance().getShortcut().get(i).getIcon();
                String link = AdsConfig.getInstance().getShortcut().get(i).getUrl();
                String packg = AdsConfig.getInstance().getShortcut().get(i).getPackg();

                boolean checkExist = false;
                for (String apl : app_package_list) {
                    if (packg.equals(apl)) {
                        checkExist = true;
                        break;
                    }
                }

                Log.i(TAG, "checkExist = " + checkExist);
                if (!checkExist) {
                    new LoadIconShortcutUtil(activity, name, icon, link).execute();
                }
            }
        }
    }
}