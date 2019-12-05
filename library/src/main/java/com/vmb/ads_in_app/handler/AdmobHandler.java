package com.vmb.ads_in_app.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.vmb.ads_in_app.activity.AdsActivity;
import com.vmb.ads_in_app.LibrayData;
import com.vmb.ads_in_app.R;
import com.vmb.ads_in_app.model.AdsConfig;

public class AdmobHandler {
    private static AdmobHandler admobUtil;

    private InterstitialAd interstitialAd;

    public static AdmobHandler getInstance() {
        synchronized (AdmobHandler.class) {
            if (admobUtil == null) {
                admobUtil = new AdmobHandler();
            }
            return admobUtil;
        }
    }

    public void setInstance(AdmobHandler admobUtil) {
        AdmobHandler.admobUtil = admobUtil;
    }

    public InterstitialAd getInterstitialAd() {
        return this.interstitialAd;
    }

    public void initBannerAdmob(final Activity activity, final ViewGroup banner_layout,
                                final String adSize, final boolean isInitConfirm) {
        final String TAG_BANNER = "initBannerAdmob";
        Log.i(TAG_BANNER, "initBannerAdmob()");

        if (activity == null) {
            Log.i(TAG_BANNER, "activity == null");
            return;
        }

        if (banner_layout.getChildCount() > 0) {
            Log.i(TAG_BANNER, "childCount = " + banner_layout.getChildCount());
            banner_layout.removeAllViewsInLayout();
        }

        int index = AdsHandler.getInstance().getAdsIndexBanner();
        Log.i(TAG_BANNER, "index = " + index);
        if (AdsConfig.getInstance().getAds() == null || index >= AdsConfig.getInstance().getAds().size()
                || AdsConfig.getInstance().getAds().get(index) == null
                || AdsConfig.getInstance().getAds().get(index).getKey() == null) {
            Log.i(TAG_BANNER, "Invalid");
            return;
        }

        String bannerId = "UNKNOWN";
        String nativeId = "UNKNOWN";

        if (AdsConfig.getInstance().getAds().get(index) != null) {
            bannerId = AdsConfig.getInstance().getAds().get(index).getKey().getBanner();
            nativeId = AdsConfig.getInstance().getAds().get(index).getKey().getThumbai();

            String appId = AdsConfig.getInstance().getAds().get(index).getKey().getAppid();
            MobileAds.initialize(activity, appId);
            Log.i(TAG_BANNER, "appId = " + appId);
        }

        Log.i(TAG_BANNER, "bannerId = " + bannerId);
        Log.i(TAG_BANNER, "nativeId = " + nativeId);

        if (adSize.equals(LibrayData.AdsSize.NATIVE_ADS)) {
            Log.i(TAG_BANNER, "AdSize = NATIVE_ADS");

            AdLoader adLoader = new AdLoader.Builder(activity, nativeId)
                    .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                        @Override
                        public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                            Log.i(TAG_BANNER, "onUnifiedNativeAdLoaded()");
                            // Show the ad.

                            TemplateView template = (TemplateView) LayoutInflater
                                    .from(activity)
                                    .inflate(R.layout.layout_template, null);
                            template.setNativeAd(unifiedNativeAd);
                            banner_layout.addView(template);
                            banner_layout.setVisibility(View.VISIBLE);
                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            // Handle the failure by logging, altering the UI, and so on.
                            switch (errorCode) {
                                case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                                    Log.i(TAG_BANNER, "onAdFailedToLoad(): ERROR_CODE_INTERNAL_ERROR");
                                    break;
                                case AdRequest.ERROR_CODE_INVALID_REQUEST:
                                    Log.i(TAG_BANNER, "onAdFailedToLoad(): ERROR_CODE_INVALID_REQUEST");
                                    break;
                                case AdRequest.ERROR_CODE_NETWORK_ERROR:
                                    Log.i(TAG_BANNER, "onAdFailedToLoad(): ERROR_CODE_NETWORK_ERROR");
                                    break;
                                case AdRequest.ERROR_CODE_NO_FILL:
                                    Log.i(TAG_BANNER, "onAdFailedToLoad(): ERROR_CODE_NO_FILL");
                                    break;
                            }

                            int index;
                            if (isInitConfirm) {
                                AdsHandler.getInstance().increseAdsIndexRectangle();
                                index = AdsHandler.getInstance().getAdsIndexRectangle();
                            } else {
                                AdsHandler.getInstance().increseAdsIndexBanner();
                                index = AdsHandler.getInstance().getAdsIndexBanner();
                            }

                            if (AdsConfig.getInstance().getAds() == null || index >= AdsConfig.getInstance().getAds().size()
                                    || AdsConfig.getInstance().getAds().get(index) == null) {
                                Log.i(TAG_BANNER, "Invalid");
                                return;
                            }

                            String type = AdsConfig.getInstance().getAds().get(index).getType();
                            if (type == null) {
                                Log.i(TAG_BANNER, "type == null");
                                return;
                            }

                            if (type.equals("facebook"))
                                FBAdsHandler.getInstance().initBannerFB(activity, banner_layout, adSize, isInitConfirm);
                            else if (type.equals("admob"))
                                AdmobHandler.getInstance().initBannerAdmob(activity, banner_layout, adSize, isInitConfirm);
                            else if (type.equals("richadx"))
                                DCPublisherHandler.getInstance().initBannerPublisher(activity, banner_layout, adSize, isInitConfirm);
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder()
                            // Methods in the NativeAdOptions.Builder class can be
                            // used here to specify individual options settings.
                            .build())
                    .build();

            AdRequest adRequest = new AdRequest.Builder().build();
            adLoader.loadAd(adRequest);
            return;

        } else {
            AdView adView = new AdView(activity);

            if (adSize.equals(LibrayData.AdsSize.MEDIUM_RECTANGLE)) {
                adView.setAdUnitId(bannerId);
                adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
                Log.i(TAG_BANNER, "AdSize = MEDIUM_RECTANGLE");

            } else if (adSize.equals(LibrayData.AdsSize.BANNER)) {
                adView.setAdUnitId(bannerId);
                DisplayMetrics displayMetrics = new DisplayMetrics();

                WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                wm.getDefaultDisplay().getMetrics(displayMetrics);

                int widthPixels = displayMetrics.widthPixels;
                int densityDpi = displayMetrics.densityDpi;
                Log.i(TAG_BANNER, "widthPixels = " + widthPixels);

                int widthDP = (widthPixels * DisplayMetrics.DENSITY_DEFAULT) / densityDpi;
                Log.i(TAG_BANNER, "widthDP = " + widthDP);

                if (widthDP >= 600) {
                    adView.setAdSize(AdSize.LARGE_BANNER);
                    Log.i(TAG_BANNER, "AdSize = LARGE_BANNER");
                } else {
                    adView.setAdSize(AdSize.BANNER);
                    Log.i(TAG_BANNER, "AdSize = BANNER");
                }
            }

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    Log.i(TAG_BANNER, "onAdClosed()");
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    switch (i) {
                        case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                            Log.i(TAG_BANNER, "onAdFailedToLoad(): ERROR_CODE_INTERNAL_ERROR");
                            break;
                        case AdRequest.ERROR_CODE_INVALID_REQUEST:
                            Log.i(TAG_BANNER, "onAdFailedToLoad(): ERROR_CODE_INVALID_REQUEST");
                            break;
                        case AdRequest.ERROR_CODE_NETWORK_ERROR:
                            Log.i(TAG_BANNER, "onAdFailedToLoad(): ERROR_CODE_NETWORK_ERROR");
                            break;
                        case AdRequest.ERROR_CODE_NO_FILL:
                            Log.i(TAG_BANNER, "onAdFailedToLoad(): ERROR_CODE_NO_FILL");
                            break;
                    }

                    int index;
                    if (isInitConfirm) {
                        AdsHandler.getInstance().increseAdsIndexRectangle();
                        index = AdsHandler.getInstance().getAdsIndexRectangle();
                    } else {
                        AdsHandler.getInstance().increseAdsIndexBanner();
                        index = AdsHandler.getInstance().getAdsIndexBanner();
                    }

                    if (AdsConfig.getInstance().getAds() == null || index >= AdsConfig.getInstance().getAds().size()
                            || AdsConfig.getInstance().getAds().get(index) == null) {
                        Log.i(TAG_BANNER, "Invalid");
                        return;
                    }

                    String type = AdsConfig.getInstance().getAds().get(index).getType();
                    if (type == null) {
                        Log.i(TAG_BANNER, "type == null");
                        return;
                    }

                    if (type.equals("facebook"))
                        FBAdsHandler.getInstance().initBannerFB(activity, banner_layout, adSize, isInitConfirm);
                    else if (type.equals("admob"))
                        AdmobHandler.getInstance().initBannerAdmob(activity, banner_layout, adSize, isInitConfirm);
                    else if (type.equals("richadx"))
                        DCPublisherHandler.getInstance().initBannerPublisher(activity, banner_layout, adSize, isInitConfirm);
                }

                @Override
                public void onAdLeftApplication() {
                    Log.i(TAG_BANNER, "onAdLeftApplication()");
                }

                @Override
                public void onAdOpened() {
                    Log.i(TAG_BANNER, "onAdOpened()");
                }

                @Override
                public void onAdLoaded() {
                    Log.i(TAG_BANNER, "onAdLoaded()");
                    banner_layout.setVisibility(View.VISIBLE);
                }
            });

            banner_layout.addView(adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
    }

    public void initInterstitialAdmob(final Activity activity, final boolean isShowLoadingScreenOpenApp) {
        final String TAG_POPUP = "initInterstitialAdmob";
        Log.i(TAG_POPUP, "initInterstitialAdmob()");

        if (activity == null)
            return;

        int index = AdsHandler.getInstance().getAdsIndexPopup();
        Log.i(TAG_POPUP, "index = " + index);
        if (AdsConfig.getInstance().getAds() == null || index >= AdsConfig.getInstance().getAds().size()
                || AdsConfig.getInstance().getAds().get(index) == null
                || AdsConfig.getInstance().getAds().get(index).getKey() == null) {
            Log.i(TAG_POPUP, "Invalid");
            return;
        }

        String popupId = "UNKNOWN";
        if (AdsConfig.getInstance().getAds().get(index) != null) {
            popupId = AdsConfig.getInstance().getAds().get(index).getKey().getPopup();
        }
        Log.i(TAG_POPUP, "popupId = " + popupId);

        interstitialAd = new InterstitialAd(activity);
        interstitialAd.setAdUnitId(popupId);

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLeftApplication() {
                Log.i(TAG_POPUP, "onAdLeftApplication()");
            }

            @Override
            public void onAdClosed() {
                Log.i(TAG_POPUP, "onAdClosed()");

                if (AdsHandler.getInstance().isShowPopupCloseApp()) {
                    activity.finish();
                    return;
                }

                loadPopup();
            }

            @Override
            public void onAdLoaded() {
                Log.i(TAG_POPUP, "onAdLoaded()");

                if (AdsConfig.getInstance().getConfig() == null) {
                    Log.i(TAG_POPUP, "AdsConfig.getInstance().getConfig() == null");
                    return;
                }

                if (AdsConfig.getInstance().getConfig().getOpen_app_show_popup() == 0) {
                    Log.i(TAG_POPUP, "show_open_app == 0");
                    return;
                }

                if (!AdsHandler.getInstance().isShowPopupOpenApp()) {
                    if (isShowLoadingScreenOpenApp) {
                        Intent intent = new Intent(activity, AdsActivity.class);
                        intent.putExtra(LibrayData.KeyIntentData.KEY_ADS_ACTIVITY, "admob");
                        activity.startActivity(intent);
                    } else {
                        interstitialAd.show();
                    }
                    AdsHandler.getInstance().setShowPopupOpenApp(true);
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                switch (i) {
                    case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                        Log.i(TAG_POPUP, "onAdFailedToLoad(): ERROR_CODE_INTERNAL_ERROR");
                        break;
                    case AdRequest.ERROR_CODE_INVALID_REQUEST:
                        Log.i(TAG_POPUP, "onAdFailedToLoad(): ERROR_CODE_INVALID_REQUEST");
                        break;
                    case AdRequest.ERROR_CODE_NETWORK_ERROR:
                        Log.i(TAG_POPUP, "onAdFailedToLoad(): ERROR_CODE_NETWORK_ERROR");
                        break;
                    case AdRequest.ERROR_CODE_NO_FILL:
                        Log.i(TAG_POPUP, "onAdFailedToLoad(): ERROR_CODE_NO_FILL");
                        break;
                }

                AdsHandler.getInstance().increseAdsIndexPopup();

                int index = AdsHandler.getInstance().getAdsIndexPopup();
                if (AdsConfig.getInstance().getAds() == null || index >= AdsConfig.getInstance().getAds().size()
                        || AdsConfig.getInstance().getAds().get(index) == null) {
                    Log.i(TAG_POPUP, "Invalid");
                    return;
                }

                String type = AdsConfig.getInstance().getAds().get(index).getType();
                if (type == null) {
                    Log.i(TAG_POPUP, "type == null");
                    return;
                }

                if (type.equals("facebook"))
                    FBAdsHandler.getInstance().initInterstitialFB(activity, isShowLoadingScreenOpenApp);
                else if (type.equals("admob"))
                    AdmobHandler.getInstance().initInterstitialAdmob(activity, isShowLoadingScreenOpenApp);
                else if (type.equals("richadx"))
                    DCPublisherHandler.getInstance().initInterstitialDC(activity, isShowLoadingScreenOpenApp);
            }

            @Override
            public void onAdOpened() {
                Log.i(TAG_POPUP, "onAdOpened()");
            }
        });

        loadPopup();
    }

    private void loadPopup() {
        // Create ad request.
        AdRequest adRequestFull = new AdRequest.Builder().build();
        // Begin loading your interstitial.
        interstitialAd.loadAd(adRequestFull);
    }

    public void displayInterstitial() {
        String TAG = "displayAdmob";

        if (interstitialAd == null) {
            Log.i(TAG, "interstitialAd == null");
            return;
        }

        Log.i(TAG, "displayInterstitial()");

        boolean check = AdsHandler.getInstance().isCanShowPopup();
        Log.i(TAG, "check = " + check);

        if (!check)
            return;

        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
            AdsHandler.getInstance().restartCountDown();
            Log.i(TAG, "displayInterstitial() = true");
        } else {
            Log.i(TAG, "interstitialAd.loadFailed()");
        }
    }

    /*public void setVisibility(boolean isShow) {
        if (adView != null) {
            adView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }*/
}