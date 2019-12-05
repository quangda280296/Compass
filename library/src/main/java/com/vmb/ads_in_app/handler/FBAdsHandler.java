package com.vmb.ads_in_app.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeAdView;
import com.vmb.ads_in_app.activity.AdsActivity;
import com.vmb.ads_in_app.LibrayData;
import com.vmb.ads_in_app.model.AdsConfig;

public class FBAdsHandler {
    private static FBAdsHandler fbAdsUtils;

    private InterstitialAd interstitialAd;

    public static FBAdsHandler getInstance() {
        synchronized (FBAdsHandler.class) {
            if (fbAdsUtils == null) {
                fbAdsUtils = new FBAdsHandler();
            }
            return fbAdsUtils;
        }
    }

    public void setInstance(FBAdsHandler fbAdsUtils) {
        FBAdsHandler.fbAdsUtils = fbAdsUtils;
    }

    public InterstitialAd getInterstitialAd() {
        return this.interstitialAd;
    }

    public void initBannerFB(final Activity activity, final ViewGroup banner_layout, final String adSize, final boolean isInitConfirm) {
        final String TAG_BANNER = "initBannerFacebook";
        Log.i(TAG_BANNER, "initBannerFB()");

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(activity);

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
        }

        Log.i(TAG_BANNER, "bannerId = " + bannerId);
        Log.i(TAG_BANNER, "nativeId = " + nativeId);

        if (adSize.equals(LibrayData.AdsSize.NATIVE_ADS)) {
            Log.i(TAG_BANNER, "AdSize = NATIVE_ADS");

            final NativeAd nativeAd = new NativeAd(activity, nativeId);
            nativeAd.setAdListener(new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                    Log.i(TAG_BANNER, "onMediaDownloaded");
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    Log.i(TAG_BANNER, "onError(): " + adError.getErrorMessage());

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
                public void onAdLoaded(Ad ad) {
                    Log.i(TAG_BANNER, "onAdLoaded");
                    View adView = NativeAdView.render(activity, nativeAd);
                    banner_layout.addView(adView);
                    banner_layout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    Log.i(TAG_BANNER, "onAdClicked");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    Log.i(TAG_BANNER, "onLoggingImpression");
                }
            });
            // Request an ad
            nativeAd.loadAd();
            return;

        } else {
            AdView adView = null;

            if (adSize.equals(LibrayData.AdsSize.MEDIUM_RECTANGLE)) {
                adView = new AdView(activity, nativeId, AdSize.RECTANGLE_HEIGHT_250);
                Log.i(TAG_BANNER, "AdSize = MEDIUM_RECTANGLE");

            } else if (adSize.equals(LibrayData.AdsSize.BANNER)) {
                DisplayMetrics displayMetrics = new DisplayMetrics();

                WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                wm.getDefaultDisplay().getMetrics(displayMetrics);

                int widthPixels = displayMetrics.widthPixels;
                int densityDpi = displayMetrics.densityDpi;
                Log.i(TAG_BANNER, "widthPixels = " + widthPixels);

                int widthDP = (widthPixels * DisplayMetrics.DENSITY_DEFAULT) / densityDpi;
                Log.i(TAG_BANNER, "widthDP = " + widthDP);

                if (widthDP >= 600) {
                    adView = new AdView(activity, bannerId, AdSize.BANNER_HEIGHT_90);
                    Log.i(TAG_BANNER, "AdSize = LARGE_BANNER");
                } else {
                    adView = new AdView(activity, bannerId, AdSize.BANNER_HEIGHT_50);
                    Log.i(TAG_BANNER, "AdSize = BANNER");
                }
            }

            adView.setAdListener(new AdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {
                    Log.i(TAG_BANNER, "onError(): " + adError.getErrorMessage());

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
                public void onAdLoaded(Ad ad) {
                    Log.i(TAG_BANNER, "onAdLoaded()");
                    banner_layout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    Log.i(TAG_BANNER, "onAdClicked()");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    Log.i(TAG_BANNER, "onLoggingImpression()");
                }
            });

            banner_layout.addView(adView);
            adView.loadAd();
        }
    }

    public void initInterstitialFB(final Activity activity, final boolean isShowLoadingScreenOpenApp) {
        final String TAG_POPUP = "initInterstitialFB";
        Log.i(TAG_POPUP, "initInterstitialFB()");

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

        interstitialAd = new InterstitialAd(activity, popupId);
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                Log.i(TAG_POPUP, "onInterstitialDisplayed()");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                Log.i(TAG_POPUP, "onInterstitialDismissed()");

                if (AdsHandler.getInstance().isShowPopupCloseApp()) {
                    activity.finish();
                    return;
                }

                loadPopup();
            }

            @Override
            public void onAdLoaded(Ad ad) {
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
                        intent.putExtra(LibrayData.KeyIntentData.KEY_ADS_ACTIVITY, "facebook");
                        activity.startActivity(intent);
                    } else {
                        interstitialAd.show();
                    }
                    AdsHandler.getInstance().setShowPopupOpenApp(true);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.i(TAG_POPUP, "onAdClicked()");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.i(TAG_POPUP, "onLoggingImpression()");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.i(TAG_POPUP, "onError(): " + adError.getErrorMessage());

                AdsHandler.getInstance().increseAdsIndexPopup();

                int index = AdsHandler.getInstance().getAdsIndexPopup();
                Log.i(TAG_POPUP, "index =" + index);

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
        });

        loadPopup();
    }

    private void loadPopup() {
        // load interstitial ads
        AdSettings.addTestDevice("3317faed-bdce-4ace-b983-1741d9d55e3d");
        interstitialAd.loadAd();
    }

    public void displayInterstitial() {
        String TAG = "displayFB";

        if (interstitialAd == null) {
            Log.i(TAG, "interstitialAd == null");
            return;
        }

        Log.i(TAG, "displayInterstitial()");

        boolean check = AdsHandler.getInstance().isCanShowPopup();
        Log.i(TAG, "check = " + check);

        if (!check)
            return;

        if (interstitialAd.isAdLoaded()) {
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