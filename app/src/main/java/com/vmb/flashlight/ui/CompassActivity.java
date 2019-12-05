package com.vmb.flashlight.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.vm.compass.compass2019.R;
import com.vmb.ads_in_app.GetConfig;
import com.vmb.ads_in_app.Interface.IUpdateNewVersion;
import com.vmb.ads_in_app.LibrayData;
import com.vmb.ads_in_app.handler.AdsHandler;
import com.vmb.ads_in_app.model.AdsConfig;
import com.vmb.ads_in_app.util.CountryCodeUtil;
import com.vmb.ads_in_app.util.LanguageUtil;
import com.vmb.ads_in_app.util.NetworkUtil;
import com.vmb.ads_in_app.util.OnTouchClickListener;
import com.vmb.ads_in_app.util.ShareUtil;
import com.vmb.ads_in_app.util.SharedPreferencesUtil;
import com.vmb.ads_in_app.util.ToastUtil;
import com.vmb.flashlight.Config;
import com.vmb.flashlight.service.MyFirebaseInstanceIDService;

public class CompassActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener, IUpdateNewVersion {

    /*public CallbackManager callbackManager;

    private TextView lbl_title;
    private TextView lbl_content;

    private Button btn_a;
    private Button btn_b;
    private Button btn_ok;

    //private int countBack = 0;
    private boolean show_rate = false;
    private boolean require_update = false;

    private LinearLayout layout_dialog;
    private ImageView img_close;*/

    private ImageView img_compass;

    // imageView
    private ImageView img_n;
    private ImageView img_s;
    private ImageView img_e;
    private ImageView img_w;

    private ImageView img_0;
    private ImageView img_30;
    private ImageView img_60;
    private ImageView img_90;
    private ImageView img_120;
    private ImageView img_150;
    private ImageView img_180;
    private ImageView img_210;
    private ImageView img_240;
    private ImageView img_270;
    private ImageView img_300;
    private ImageView img_330;

    // record the compass picture angle turned
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;

    private TextView lbl_degree;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                initGetAds();
            }
        }).run();*/

        /*lbl_title = findViewById(R.id.lbl_title);
        lbl_content = findViewById(R.id.lbl_content);

        btn_a = findViewById(R.id.btn_a);
        btn_b = findViewById(R.id.btn_b);
        btn_ok = findViewById(R.id.btn_ok);

        img_close = findViewById(R.id.img_close);
        layout_dialog = findViewById(R.id.layout_dialog);*/

        img_compass = findViewById(R.id.img_compass);

        // init NEWS
        img_n = findViewById(R.id.img_n);
        img_w = findViewById(R.id.img_w);
        img_e = findViewById(R.id.img_e);
        img_s = findViewById(R.id.img_s);

        img_0 = findViewById(R.id.img_0);
        img_30 = findViewById(R.id.img_30);
        img_60 = findViewById(R.id.img_60);
        img_90 = findViewById(R.id.img_90);
        img_120 = findViewById(R.id.img_120);
        img_150 = findViewById(R.id.img_150);
        img_180 = findViewById(R.id.img_180);
        img_210 = findViewById(R.id.img_210);
        img_240 = findViewById(R.id.img_240);
        img_270 = findViewById(R.id.img_270);
        img_300 = findViewById(R.id.img_300);
        img_330 = findViewById(R.id.img_330);

        lbl_degree = findViewById(R.id.lbl_degree);

        caculate();

        /*callbackManager = CallbackManager.Factory.create();
        int count_play = SharedPreferencesUtil.getPrefferInt(getApplicationContext(),
                LibrayData.KeySharePrefference.COUNT_PLAY, 0);
        count_play++;
        SharedPreferencesUtil.putPrefferInt(getApplicationContext(),
                LibrayData.KeySharePrefference.COUNT_PLAY, count_play);

        boolean rate = SharedPreferencesUtil.getPrefferBool(getApplicationContext(),
                LibrayData.KeySharePrefference.SHOW_RATE, false);
        if (!rate) {
            if (count_play >= 20)
                show_rate = true;
        }*/

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // for the system's orientation sensor registered listeners
        if (mSensorManager != null) {
            boolean check = mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                    SensorManager.SENSOR_DELAY_GAME);
            if (!check)
                ToastUtil.longSnackbar(CompassActivity.this, getString(R.string.not_support_compass));
        }
    }

    public void initGetAds() {
        GetConfig.callAPI(this, CompassActivity.this, Config.CODE_CONTROL_APP, Config.VERSION_APP, Config.PACKAGE_NAME);
        CountryCodeUtil.setCountryCode(getApplicationContext());

        String TAG = "MyFirebaseIDService";
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (TextUtils.isEmpty(refreshedToken))
            return;

        Log.i(TAG, "Refreshed token: " + refreshedToken);
        SharedPreferencesUtil.putPrefferString(getApplicationContext(), LibrayData.KeySharePrefference.NOTI_TOKEN, refreshedToken);

        MyFirebaseInstanceIDService.sendRegistrationToServer(getApplicationContext());
    }

    public void caculate() {
        String TAG = "caculate()";

        // set size image_view in grid_layout
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);

        int widthPixels = displayMetrics.widthPixels;
        Log.i(TAG, "widthPixels = " + widthPixels);

        int radiusDirection = widthPixels / 4;
        Log.i(TAG, "radiusDirection = " + radiusDirection);
        int radiusNumber = widthPixels / 2 - widthPixels / 20;
        Log.i(TAG, "radiusNumber = " + radiusNumber);

        setRadius(img_n, radiusDirection);
        setRadius(img_e, radiusDirection);
        setRadius(img_w, radiusDirection);
        setRadius(img_s, radiusDirection);

        setRadius(img_0, radiusNumber);
        setRadius(img_30, radiusNumber);
        setRadius(img_60, radiusNumber);
        setRadius(img_90, radiusNumber);
        setRadius(img_120, radiusNumber);
        setRadius(img_150, radiusNumber);
        setRadius(img_180, radiusNumber);
        setRadius(img_210, radiusNumber);
        setRadius(img_240, radiusNumber);
        setRadius(img_270, radiusNumber);
        setRadius(img_300, radiusNumber);
        setRadius(img_330, radiusNumber);
    }

    public void setRadius(View view, int radius) {
        layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        layoutParams.circleRadius = radius;
        view.setLayoutParams(layoutParams);
    }

    float degree;
    RotateAnimation ra;

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated
        degree = Math.round(event.values[0]);

        if (337.5 <= degree || degree < 22.5)
            lbl_degree.setText((int) degree + "\u00B0" + " N");
        else if (22.5 <= degree && degree < 67.5)
            lbl_degree.setText((int) degree + "\u00B0" + " NE");
        else if (67.5 <= degree && degree < 112.5)
            lbl_degree.setText((int) degree + "\u00B0" + " E");
        else if (112.5 <= degree && degree < 157.5)
            lbl_degree.setText((int) degree + "\u00B0" + " SE");
        else if (157.5 <= degree && degree < 202.5)
            lbl_degree.setText((int) degree + "\u00B0" + " S");
        else if (202.5 <= degree && degree < 247.5)
            lbl_degree.setText((int) degree + "\u00B0" + " SW");
        else if (247.5 <= degree && degree < 292.5)
            lbl_degree.setText((int) degree + "\u00B0" + " W");
        else if (292.5 <= degree && degree < 337.5)
            lbl_degree.setText((int) degree + "\u00B0" + " NW");

        // create a rotation animation (reverse turn degree degrees)
        ra = new RotateAnimation(
                currentDegree, -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        // how long the animation will take place
        ra.setDuration(500);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        img_compass.startAnimation(ra);

        // Animate direction
        animateNumber(img_n, degree, 0f).start();
        animateNumber(img_e, degree, 90f).start();
        animateNumber(img_s, degree, 180f).start();
        animateNumber(img_w, degree, 270f).start();

        // Animate number
        animateNumber(img_0, degree, 0f).start();
        animateNumber(img_30, degree, 30f).start();
        animateNumber(img_60, degree, 60f).start();
        animateNumber(img_90, degree, 90f).start();
        animateNumber(img_120, degree, 120f).start();
        animateNumber(img_150, degree, 150f).start();
        animateNumber(img_180, degree, 180f).start();
        animateNumber(img_210, degree, 210f).start();
        animateNumber(img_240, degree, 240f).start();
        animateNumber(img_270, degree, 270f).start();
        animateNumber(img_300, degree, 300f).start();
        animateNumber(img_330, degree, 330f).start();

        // update currentDegree
        currentDegree = -degree;
    }

    ValueAnimator anim;
    ConstraintLayout.LayoutParams layoutParams;

    private ValueAnimator animateNumber(final View view, float degree, float plus) {
        anim = ValueAnimator.ofFloat(currentDegree + plus, -degree + plus);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
                layoutParams.circleAngle = (Float) valueAnimator.getAnimatedValue();
                view.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(500);
        //anim.setInterpolator(new LinearInterpolator());
        return anim;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       /* if (callbackManager != null)
            callbackManager.onActivityResult(requestCode, resultCode, data);*/
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("onResume()", "onResume()");

        /*if (show_rate)
            showRate();*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("onPause()", "onPause()");
    }

    @Override
    protected void onDestroy() {
        destroy();
        super.onDestroy();
    }

    public void destroy() {
        // to stop the listener and save battery
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);

        AdsHandler.getInstance().destroyInstance();
    }

    @Override
    public void onBackPressed() {
        Log.i("onKeyBack", "onKeyBack()");
        Log.i("onKeyBack", "onKeyBack()");
        count++;
        if (count >= 2)
            finish();
        else
            ToastUtil.shortToast(getApplicationContext(), "Press again to exit");
        /*if (findViewById(R.id.layout_dialog).getVisibility() == View.VISIBLE) {
            if (require_update)
                return;

            findViewById(R.id.layout_dialog).setVisibility(View.GONE);
            return;
        }*/

        /*countBack++;
        if (countBack == 1)
            ToastUtil.longToast(getApplicationContext(), getString(R.string.press_back_again));
        else if (countBack == 2) {
            AdsHandler.getInstance().displayPopupCloseApp(CompassActivity.this);
        } else {
            finish();
        }*/
        //AdsHandler.getInstance().showCofirmDialog(CompassActivity.this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onGetConfig(boolean require_update) {
        /*this.require_update = require_update;
        showUpdate();*/
    }

    /*public void showRate() {
        show_rate = false;
        SharedPreferencesUtil.putPrefferBool(getApplicationContext(), LibrayData.KeySharePrefference.SHOW_RATE, true);

        lbl_title.setText(R.string.rate_title);
        lbl_content.setText(R.string.rate_content);

        btn_ok.setVisibility(View.GONE);

        btn_a.setText(R.string.share);
        btn_a.setVisibility(View.VISIBLE);
        btn_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isNetworkAvailable(getApplicationContext())) {
                    ToastUtil.shortToast(getApplicationContext(), getString(R.string.no_internet));
                    return;
                }
                ShareUtil.shareFB(CompassActivity.this, callbackManager,
                        Config.CODE_CONTROL_APP, Config.VERSION_APP, Config.PACKAGE_NAME);
            }
        });

        btn_b.setText(R.string.rate);
        btn_b.setVisibility(View.VISIBLE);
        btn_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isNetworkAvailable(getApplicationContext())) {
                    ToastUtil.shortToast(getApplicationContext(), getString(R.string.no_internet));
                    return;
                }
                ShareUtil.rateApp(CompassActivity.this);
            }
        });

        img_close.setOnTouchListener(new OnTouchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_dialog.setVisibility(View.GONE);
            }
        }, getApplicationContext()));

        layout_dialog.setVisibility(View.VISIBLE);
    }

    public void showUpdate() {
        String content = "";
        String title = "";

        if (LanguageUtil.isVietnamese()) {
            content = AdsConfig.getInstance().getUpdate_message_vn();
            if (TextUtils.isEmpty(content))
                content = "Đã có phiên bản mới, bạn vui lòng cập nhật !";

            title = AdsConfig.getInstance().getUpdate_title_vn();
            if (TextUtils.isEmpty(title))
                title = "Thông báo cập nhật";
        } else {
            content = AdsConfig.getInstance().getUpdate_message_en();
            if (TextUtils.isEmpty(content))
                content = "There is a new version, please update soon !";

            title = AdsConfig.getInstance().getUpdate_title_en();
            if (TextUtils.isEmpty(title))
                title = "Update";
        }

        lbl_title.setText(title);
        lbl_content.setText(content);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = AdsConfig.getInstance().getUpdate_url();
                if (TextUtils.isEmpty(url))
                    url = "https://play.google.com/store/apps/developer?id=Fruit+Game+Studio";

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivityForResult(intent, LibrayData.RequestCode.REQUEST_CODE_UPDATE);
            }
        });

        img_close.setOnTouchListener(new OnTouchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (require_update)
                    return;

                layout_dialog.setVisibility(View.GONE);
            }
        }, getApplicationContext()));

        layout_dialog.setVisibility(View.VISIBLE);
    }*/
}