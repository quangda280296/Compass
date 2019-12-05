package com.vmb.ads_in_app.handler;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vmb.ads_in_app.activity.AdsActivity;
import com.vmb.ads_in_app.LibrayData;
import com.vmb.ads_in_app.R;
import com.vmb.ads_in_app.model.AdsConfig;

import java.util.concurrent.TimeUnit;

public class AdsHandler {
    private String TAG = "AdsHandler";

    private static AdsHandler adsHandler;

    public static AdsHandler getInstance() {
        synchronized (AdsHandler.class) {
            if (adsHandler == null) {
                adsHandler = new AdsHandler();
            }
            return adsHandler;
        }
    }

    private AlertDialog closeAppDialog;

    private int adsIndexBanner = 0;
    private int adsIndexPopup = 0;

    private int adsIndexRectangle = 0;

    private CountDownTimer countDownTimer;
    private boolean canShowPopup = true;

    private boolean isShowPopupOpenApp = false;
    private boolean isShowPopupCloseApp = false;


    public void setInstance(AdsHandler adsUtils) {
        AdsHandler.adsHandler = adsUtils;
    }

    public boolean isCanShowPopup() {
        return canShowPopup;
    }

    public int getAdsIndexBanner() {
        return this.adsIndexBanner;
    }

    public void increseAdsIndexBanner() {
        this.adsIndexBanner += 1;
    }

    public int getAdsIndexPopup() {
        return this.adsIndexPopup;
    }

    public void increseAdsIndexPopup() {
        this.adsIndexPopup += 1;
    }

    public int getAdsIndexRectangle() {
        return this.adsIndexRectangle;
    }

    public void increseAdsIndexRectangle() {
        this.adsIndexRectangle = adsIndexRectangle;
    }

    public boolean isShowPopupOpenApp() {
        return this.isShowPopupOpenApp;
    }

    public void setShowPopupOpenApp(boolean showPopupOpenApp) {
        this.isShowPopupOpenApp = showPopupOpenApp;
    }

    public boolean isShowPopupCloseApp() {
        return isShowPopupCloseApp;
    }

    // init Banner
    public void initBanner(Activity activity) {
        initBanner(activity, LibrayData.AdsSize.BANNER, R.id.banner);
    }

    public void initBanner(Activity activity, int id_layout_ads) {
        initBanner(activity, LibrayData.AdsSize.BANNER, id_layout_ads);
    }

    public void initBanner(Activity activity, String adSize) {
        initBanner(activity, adSize, R.id.banner);
    }

    public void initBanner(Activity activity, String adSize, int id_layout_ads) {
        Log.i(TAG, "initBanner()");

        if (AdsConfig.getInstance().getConfig() == null) {
            Log.i(TAG, "config == null");
            return;
        }

        if (AdsConfig.getInstance().getConfig().getShow_banner_ads() == 0) {
            Log.i(TAG, "show_banner_ads == 0");
            return;
        }

        if (AdsConfig.getInstance().getAds() == null || adsIndexBanner >= AdsConfig.getInstance().getAds().size()
                || AdsConfig.getInstance().getAds().get(adsIndexBanner) == null) {
            Log.i(TAG, "Invalid");
            return;
        }

        String type = AdsConfig.getInstance().getAds().get(adsIndexBanner).getType();
        if (type == null) {
            Log.i(TAG, "typeBanner == null");
            return;
        }

        ViewGroup banner_layout = activity.findViewById(id_layout_ads);
        if (banner_layout == null) {
            Log.i(TAG, "banner_layout == null");
            return;
        }

        if (type.equals("facebook")) {
            FBAdsHandler.getInstance().initBannerFB(activity, banner_layout, adSize, false);
            Log.i(TAG, "initBanner == facebook");
        } else if (type.equals("admob")) {
            AdmobHandler.getInstance().initBannerAdmob(activity, banner_layout, adSize, false);
            Log.i(TAG, "initBanner == admob");
        } else if (type.equals("richadx")) {
            DCPublisherHandler.getInstance().initBannerPublisher(activity, banner_layout, adSize, false);
            Log.i(TAG, "initBanner == richadx");
        }
    }

    // init Interstital
    public void initInterstital(Activity activity) {
        initInterstital(activity, true);
    }

    public void initInterstital(final Activity activity, boolean isShowLoadingScreenOpenApp) {
        Log.i(TAG, "initInterstital()");

        if (AdsConfig.getInstance().getAds() == null || adsIndexPopup >= AdsConfig.getInstance().getAds().size()
                || AdsConfig.getInstance().getAds().get(adsIndexPopup) == null) {
            Log.i(TAG, "Invalid");
            return;
        }

        String type = AdsConfig.getInstance().getAds().get(adsIndexPopup).getType();
        if (type == null) {
            Log.i(TAG, "typePopup == null");
            return;
        }

        if (type.equals("facebook")) {
            FBAdsHandler.getInstance().initInterstitialFB(activity, isShowLoadingScreenOpenApp);
            Log.i(TAG, "initInterstital == facebook");
        } else if (type.equals("admob")) {
            AdmobHandler.getInstance().initInterstitialAdmob(activity, isShowLoadingScreenOpenApp);
            Log.i(TAG, "initInterstital == admob");
        } else if (type.equals("richadx")) {
            DCPublisherHandler.getInstance().initInterstitialDC(activity, isShowLoadingScreenOpenApp);
            Log.i(TAG, "initInterstital == richadx");
        }

        // init Countdown
        Log.i(TAG, "initCountDown()");
        initCountDown();
    }

    // display Interstitial
    public void displayInterstitial(Activity activity) {
        displayInterstitial(activity, true);
    }

    public void displayInterstitial(Activity activity, boolean showLoadingScreen) {
        String TAG = "displayInterstitial";
        Log.i(TAG, "displayInterstitial()");

        if (AdsConfig.getInstance().getAds() == null || adsIndexPopup >= AdsConfig.getInstance().getAds().size()
                || AdsConfig.getInstance().getAds().get(adsIndexPopup) == null) {
            Log.i(TAG, "Invalid");

            if (isShowPopupCloseApp) {
                Log.i(TAG, "no Ads loaded, activity.finish()");
                activity.finish();
            }
            return;
        }

        String type = AdsConfig.getInstance().getAds().get(adsIndexPopup).getType();
        if (type == null) {
            Log.i(TAG, "type == null");
            return;
        }

        if (type.equals("facebook")) {
            Log.i(TAG, "typeDisplayPopup == facebook");
            if (!FBAdsHandler.getInstance().getInterstitialAd().isAdLoaded() || !canShowPopup) {
                Log.i(TAG, "FBAdsPopupLoaded == false  || canShowPopup == false");
            } else
                checkShowLoadingScreen(activity, type, showLoadingScreen);
        } else if (type.equals("admob")) {
            Log.i(TAG, "typeDisplayPopup == admob");
            if (!AdmobHandler.getInstance().getInterstitialAd().isLoaded() || !canShowPopup) {
                Log.i(TAG, "AdmobAdsPopupLoaded == false  || canShowPopup == false");
            } else
                checkShowLoadingScreen(activity, type, showLoadingScreen);
        } else if (type.equals("richadx")) {
            Log.i(TAG, "typeDisplayPopup == richadx");
            if (!DCPublisherHandler.getInstance().getInterstitialAd().isLoaded() || !canShowPopup) {
                Log.i(TAG, "!DCAdsPopupLoaded == false  || canShowPopup == false");
            } else
                checkShowLoadingScreen(activity, type, showLoadingScreen);
        }
    }

    private void checkShowLoadingScreen(Activity activity, String type, boolean showLoadingScreen) {
        String TAG = "displayInterstitial";
        if (showLoadingScreen) {
            Log.i(TAG, "showLoadingScreen == true");
            Intent intent = new Intent(activity, AdsActivity.class);
            intent.putExtra(LibrayData.KeyIntentData.KEY_ADS_ACTIVITY, type);
            activity.startActivity(intent);
        } else {
            Log.i(TAG, "showLoadingScreen == false");
            AdsHandler.getInstance().displayPopup(type);
        }
    }

    public void displayPopup(String type) {
        String TAG = "displayInterstitial";
        Log.i(TAG, "displayPopup()");

        if (type.equals("facebook"))
            FBAdsHandler.getInstance().displayInterstitial();
        else if (type.equals("admob"))
            AdmobHandler.getInstance().displayInterstitial();
        else if (type.equals("richadx"))
            DCPublisherHandler.getInstance().displayInterstitial();
    }

    /*// display Popup Close App
    public void displayPopupCloseApp(Activity activity) {
        displayPopupCloseApp(activity, true);
    }

    public void displayPopupCloseApp(Activity activity, boolean showLoadingScreen) {
        String TAG = "displayPopupCloseApp";

        Log.i(TAG, "checkShowPopupCloseApp()");
        this.isShowPopupCloseApp = true;

        if (AdsConfig.getInstance().getConfig() == null) {
            Log.i(TAG, "AdsConfig.getInstance().getConfig() == null");
            activity.finish();
            return;
        }

        if (AdsConfig.getInstance().getConfig().getClose_app_show_popup() == 0) {
            Log.i(TAG, "show_close_app == 0");
            activity.finish();
            return;
        }

        if (AdsConfig.getInstance().getAds() == null || adsIndexPopup >= AdsConfig.getInstance().getAds().size()
                || AdsConfig.getInstance().getAds().get(adsIndexPopup) == null) {
            Log.i(TAG, "Invalid");
            Log.i(TAG, "no Ads loaded, activity.finish()");
            activity.finish();
            return;
        }

        String type = AdsConfig.getInstance().getAds().get(adsIndexPopup).getType();
        if (type == null) {
            Log.i(TAG, "type == null");
            return;
        }

        if (type.equals("facebook")) {
            Log.i(TAG, "typeDisplayPopup == facebook");
            if (!FBAdsHandler.getInstance().getInterstitialAd().isAdLoaded()) {
                Log.i(TAG, "FBAdsPopupLoaded == false");
                activity.finish();
            } else
                checkShowLoadingScreen(activity, type, showLoadingScreen);
        } else if (type.equals("admob")) {
            Log.i(TAG, "typeDisplayPopup == admob");
            if (!AdmobHandler.getInstance().getInterstitialAd().isLoaded()) {
                Log.i(TAG, "AdmobPopupLoaded == false");
                activity.finish();
            } else
                checkShowLoadingScreen(activity, type, showLoadingScreen);
        } else {
            Log.i(TAG, "typeDisplayPopup == richadx");
            if (!DCPublisherHandler.getInstance().getInterstitialAd().isLoaded()) {
                Log.i(TAG, "DCPublisherAdsPopupLoaded == false");
                activity.finish();
            } else
                checkShowLoadingScreen(activity, type, showLoadingScreen);
        }
    }*/

    // Count Down
    private void initCountDown() {
        final String TAG = "initCountDown";

        Log.i(TAG, "initCountDown()");
        if (AdsConfig.getInstance().getConfig() == null) {
            Log.i(TAG, "AdsConfig.getInstance().getConfig() == null");
            return;
        }

        int time_start_show_popup = AdsConfig.getInstance().getConfig().getTime_start_show_popup();
        countDownTimer = new CountDownTimer(TimeUnit.SECONDS.toMillis(time_start_show_popup), TimeUnit.SECONDS.toMillis(1)) {
            public void onTick(long millisUntilFinished) {
                Log.i(TAG, "millisUntilFinished == " + millisUntilFinished);
            }

            public void onFinish() {
                Log.i(TAG, "onFinish()");
                canShowPopup = true;
            }
        };

        canShowPopup = false;
        countDownTimer.start();
    }

    public void restartCountDown() {
        final String TAG = "restartCountDown";

        Log.i(TAG, "restartCountDown()");
        if (AdsConfig.getInstance().getConfig() == null) {
            Log.i(TAG, "AdsConfig.getInstance().getConfig() == null");
            return;
        }

        int offset_time_show_popup = AdsConfig.getInstance().getConfig().getOffset_time_show_popup();
        countDownTimer = new CountDownTimer(TimeUnit.SECONDS.toMillis(offset_time_show_popup), TimeUnit.SECONDS.toMillis(1)) {
            public void onTick(long millisUntilFinished) {
                Log.i(TAG, "millisUntilFinished == " + millisUntilFinished);
            }

            public void onFinish() {
                Log.i(TAG, "onFinish()");
                canShowPopup = true;
            }
        };

        canShowPopup = false;
        countDownTimer.start();
    }

    private void cancelDownCount() {
        if (countDownTimer == null) {
            Log.i(TAG, "countDownTimer == null");
            return;
        }
        canShowPopup = true;
        countDownTimer.cancel();
    }

    /*// others
    public void setVisibility(boolean isShow) {
        final String TAG = "setVisibility()";

        if (AdsConfig.getInstance().getAds() == null || adsIndexPopup >= AdsConfig.getInstance().getAds().size()
                || AdsConfig.getInstance().getAds().get(adsIndexPopup) == null) {
            Log.i(TAG, "Invalid");
            return;
        }

        String type = AdsConfig.getInstance().getAds().get(adsIndexPopup).getType();
        if (type == null) {
            Log.i(TAG, "type == null");
            return;
        }

        if (type.equals("facebook"))
            FBAdsHandler.getInstance().setVisibility(isShow);
        else if (type.equals("admob"))
            AdmobHandler.getInstance().setVisibility(isShow);
        else
            DCPublisherHandler.getInstance().setVisibility(isShow);
    }*/

    public void initConfirmDialog(Activity activity) {
        initConfirmDialog(activity, LibrayData.AdsSize.NATIVE_ADS);
    }

    public void initConfirmDialog(final Activity activity, final String adSize) {
        Log.i(TAG, "initConfirmDialog()");

        LayoutInflater inflater = LayoutInflater.from(activity);
        final View alertLayout = inflater.inflate(R.layout.dialog_confirm_exit, null);

        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setView(alertLayout);
        closeAppDialog = alert.create();

        Button btn_yes = alertLayout.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAppDialog.dismiss();
                activity.finish();
            }
        });

        Button btn_no = alertLayout.findViewById(R.id.btn_no);
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAppDialog.dismiss();
            }
        });

        /*closeAppDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                int width_dialog = closeAppDialog.getWindow().getDecorView().getWidth();
                Log.i(TAG, "width_dialog = " + width_dialog);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                wm.getDefaultDisplay().getMetrics(displayMetrics);
                int densityDpi = displayMetrics.densityDpi;

                int widthDP = 330;
                int width_rectangle = widthDP * densityDpi / DisplayMetrics.DENSITY_DEFAULT;

                Log.i(TAG, "width_rectangle = " + width_rectangle);
                float percent = (float) width_rectangle / (float) width_dialog;
                Log.i(TAG, "percent = " + percent);

                TextView lbl_title = closeAppDialog.findViewById(R.id.lbl_title);
                setPercent(lbl_title, percent);

                ViewGroup rectangle = closeAppDialog.findViewById(R.id.rectangle);
                setPercent(rectangle, percent);
            }
        });*/

        if (AdsConfig.getInstance().getConfig() == null) {
            Log.i(TAG, "config == null");
            return;
        }

        if (AdsConfig.getInstance().getConfig().getClose_app_show_popup() == 0) {
            Log.i(TAG, "close_app_show_popup == 0");
            return;
        }

        if (AdsConfig.getInstance().getAds() == null || adsIndexRectangle >= AdsConfig.getInstance().getAds().size()
                || AdsConfig.getInstance().getAds().get(adsIndexRectangle) == null) {
            Log.i(TAG, "Invalid");
            return;
        }

        String type = AdsConfig.getInstance().getAds().get(adsIndexRectangle).getType();
        if (type == null) {
            Log.i(TAG, "typeBanner == null");
            return;
        }

        ViewGroup rectangle = alertLayout.findViewById(R.id.rectangle);
        if (type.equals("facebook")) {
            FBAdsHandler.getInstance().initBannerFB(activity, rectangle, adSize, true);
            Log.i(TAG, "initConfirm == facebook");
        } else if (type.equals("admob")) {
            AdmobHandler.getInstance().initBannerAdmob(activity, rectangle, adSize, true);
            Log.i(TAG, "initConfirm == admob");
        } else if (type.equals("richadx")) {
            DCPublisherHandler.getInstance().initBannerPublisher(activity, rectangle, adSize, true);
            Log.i(TAG, "initConfirm == richadx");
        }
    }

    private void setPercent(View view, float percent) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        layoutParams.matchConstraintPercentWidth = percent;
        view.setLayoutParams(layoutParams);
    }

    public void showCofirmDialog(Activity activity) {
        if (closeAppDialog == null) {
            Log.i(TAG, "closeAppDialog == null");
            activity.finish();
            return;
        }
        closeAppDialog.show();
    }

    public void destroyInstance() {
        AdsHandler.getInstance().cancelDownCount();
        AdsHandler.getInstance().setInstance(null);

        AdmobHandler.getInstance().setInstance(null);
        FBAdsHandler.getInstance().setInstance(null);
        DCPublisherHandler.getInstance().setInstance(null);
    }
}