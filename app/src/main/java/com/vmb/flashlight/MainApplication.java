package com.vmb.flashlight;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.ads.AudienceNetworkAds;
import com.google.firebase.FirebaseApp;
import com.vmb.ads_in_app.util.PrintKeyHash;
import com.vmb.ads_in_app.util.TimeRegUtil;

import io.fabric.sdk.android.Fabric;
import jack.com.servicekeep.app.VMApplication;

public class MainApplication extends VMApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());
        FirebaseApp.initializeApp(this);
        FacebookSdk.sdkInitialize(this);

        TimeRegUtil.setTimeRegister(this);
        PrintKeyHash.print(this);

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);

        initInfoDevice(Config.CODE_CONTROL_APP, Config.VERSION_APP);
    }
}