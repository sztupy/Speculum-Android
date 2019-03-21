package com.nielsmasdorp.speculum.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.assent.Assent;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nielsmasdorp.speculum.R;
import com.nielsmasdorp.speculum.SpeculumApplication;
import com.nielsmasdorp.speculum.models.Configuration;
import com.nielsmasdorp.speculum.models.TravelDetails;
import com.nielsmasdorp.speculum.models.TravelRoute;
import com.nielsmasdorp.speculum.models.Weather;
import com.nielsmasdorp.speculum.presenters.MainPresenter;
import com.nielsmasdorp.speculum.receiver.LockScreenAdminReceiver;
import com.nielsmasdorp.speculum.receiver.MotionDetectorReceiver;
import com.nielsmasdorp.speculum.util.ASFObjectStore;
import com.nielsmasdorp.speculum.util.Constants;
import com.nielsmasdorp.speculum.views.MainView;
import com.nielsmasdorp.speculum.views.NoteView;
import com.nielsmasdorp.speculum.views.PrecipitationView;
import com.nielsmasdorp.speculum.views.RouteView;
import com.nielsmasdorp.speculum.views.WeatherDetailsView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Niels Masdorp (NielsMasdorp)
 */
public class MainActivity extends AppCompatActivity implements MainView, View.OnSystemUiVisibilityChangeListener {

    private static final String LOG_TAG = "MainActivity";

    @BindView(R.id.time_layout) View llTimeLayout;
    @BindView(R.id.weather_main_layout) View llWeatherMainLayout;
    @BindView(R.id.weather_precipitation) PrecipitationView llWeatherPrecipitation;
    @BindView(R.id.weather_details) WeatherDetailsView llWeatherDetails;
    @BindView(R.id.divider_1) View llDivider1;
    @BindView(R.id.weather_stats_layout) View llWeatherStatsLayout;
    @BindView(R.id.divider_2) View llDivider2;
    @BindView(R.id.calendar_layout) View llCalendarLayout;
    @BindView(R.id.divider_3) View llDivider3;
    @BindView(R.id.routes_1_layout) RouteView llRoutes1Layout;
    @BindView(R.id.divider_4) View llDivider4;
    @BindView(R.id.routes_2_layout) RouteView llRoutes2Layout;
    @BindView(R.id.divider_5) View llDivider5;
    @BindView(R.id.calendar_field_layout) View llCalendarFieldLayout;

    @BindView(R.id.iv_current_weather) ImageView ivWeatherCondition;
    @BindView(R.id.tv_current_temp) TextView tvWeatherTemperature;
    @BindView(R.id.tv_last_updated) TextView tvWeatherLastUpdated;
    @BindView(R.id.calendar) CalendarView cvCalendarView;
    @BindView(R.id.notes) NoteView notesView;

    @Nullable @BindView(R.id.tv_stats_wind) TextView tvWeatherWind;
    @Nullable @BindView(R.id.tv_stats_humidity) TextView tvWeatherHumidity;
    @Nullable @BindView(R.id.tv_stats_visibility) TextView tvWeatherVisibility;
    @Nullable @BindView(R.id.tv_calendar_event) TextView tvCalendarEvent;

    @Nullable
    BroadcastReceiver motionDetectorReceiver;

    @BindString(R.string.last_updated) String lastUpdated;

    @BindInt(android.R.integer.config_mediumAnimTime) int mediumAnimTime;
    @BindInt(android.R.integer.config_shortAnimTime) int shortAnimTime;

    @Inject
    MainPresenter presenter;

    @Inject
    ASFObjectStore<Configuration> objectStore;

    @Nullable
    PowerManager.WakeLock wakeLock;

    @Nullable
    AnimatorSet animator = null;

    View[] layoutOrder;

    enum PageState {
        HIDDEN,
        OPEN_ANIM,
        OPEN,
        CLOSE_ANIM
    }

    private long activatedTime = 0;
    private PageState pageState = PageState.HIDDEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((SpeculumApplication) getApplication()).createMainComponent(this).inject(this);
        Assent.setActivity(this, this);

        Configuration configuration = new Configuration.Builder().
                celsius(true).
                location(getString(R.string.maps_location)).
                pollingDelay(10).
                build();

        ViewStub viewStub = (ViewStub) findViewById(R.id.stub_verbose);
        if (null != viewStub) viewStub.inflate();

        ButterKnife.bind(this);

        motionDetectorReceiver = new MotionDetectorReceiver();
        IntentFilter motionDetectorFilter = new IntentFilter("org.motion.detector.ACTION_GLOBAL_BROADCAST");
        this.registerReceiver(motionDetectorReceiver, motionDetectorFilter);

        presenter.setConfiguration(configuration);
        presenter.start(Assent.isPermissionGranted(Assent.READ_CALENDAR));

        notesView.setMainView(this);

        layoutOrder = new View[]{
                llTimeLayout, llWeatherMainLayout, llWeatherDetails, llDivider1, llWeatherStatsLayout, llDivider2,
                llCalendarLayout, llDivider3, llRoutes1Layout, llDivider4, llRoutes2Layout, llDivider5, llCalendarFieldLayout
        };
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void hideSystemUI() {
        View mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        mDecorView.setOnSystemUiVisibilityChangeListener(this);
    }

    @Override
    @SuppressWarnings("all")
    public void displayCurrentWeather(Weather weather) {
        // Current simple weather
        this.ivWeatherCondition.setImageResource(weather.getIconId());
        this.tvWeatherTemperature.setText(weather.getTemperature());
        this.tvWeatherLastUpdated.setText(lastUpdated + " " + weather.getLastUpdated());

        // More weather info
        this.tvWeatherWind.setText(weather.getWindInfo());
        this.tvWeatherHumidity.setText(weather.getHumidityInfo());
        this.tvWeatherVisibility.setText(weather.getVisibilityInfo());

        this.llWeatherDetails.setWeather(weather);
        this.llWeatherPrecipitation.setWeather(weather);
    }

    @Override
    public void updateTimeRemaining() {
        cvCalendarView.setDate(System.currentTimeMillis());

        long remaining = activatedTime - System.currentTimeMillis() + 30000;

        Log.i(LOG_TAG, "TICK " + remaining);
        if (remaining < 0 && pageState.equals(PageState.OPEN)) {
            initiateCloseAnimation();
        }
    }

    @Override
    public void updateRoutes(int id, TravelDetails routeList) {
        if (id==0) {
            llRoutes1Layout.updateRoutes(routeList);
        } else {
            llRoutes2Layout.updateRoutes(routeList);
        }
    }

    @Override
    @SuppressWarnings("all")
    public void displayCalendarEvents(String events) {
        this.tvCalendarEvent.setText(events);
        if (this.llCalendarLayout.getVisibility() != View.VISIBLE)
            this.llCalendarLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Assent.setActivity(this, this);
        hideSystemUI();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        acquireWakeLock();
        Log.i(LOG_TAG, "OnResume Called");
        if (pageState == PageState.HIDDEN || pageState == PageState.CLOSE_ANIM) {
            initiateShowAnimation();
        }
        presenter.foreground();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "OnPause Called");

        if (isFinishing())
            Assent.setActivity(this, null);

        releaseWakeLock();
        presenter.background();
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
            hideSystemUI();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (motionDetectorReceiver != null) {
            this.unregisterReceiver(motionDetectorReceiver);
            motionDetectorReceiver = null;
        }
        presenter.finish();
        ((SpeculumApplication) getApplication()).releaseMainComponent();
    }

    private void initiateShowAnimation() {
        if (pageState == PageState.CLOSE_ANIM) {
            if (animator != null) {
                animator.cancel();
                animator = null;
            }
        }

        animator = new AnimatorSet();

        for (int i = 0; i< layoutOrder.length; i++) {
            layoutOrder[i].setVisibility(View.VISIBLE);
            if (pageState == PageState.HIDDEN) {
                layoutOrder[i].setAlpha(0);
            }

            ObjectAnimator anim = ObjectAnimator.ofFloat(layoutOrder[i], "alpha", 1);
            anim.setDuration(mediumAnimTime);

            animator.play(anim).after(pageState == PageState.HIDDEN ? shortAnimTime * i : 0);
        }

        pageState = PageState.OPEN_ANIM;

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animator = null;
                pageState = PageState.OPEN;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    private void initiateCloseAnimation() {
        pageState = PageState.CLOSE_ANIM;

        animator = new AnimatorSet();

        for (int i = 0; i<layoutOrder.length; i++) {
            layoutOrder[i].setVisibility(View.VISIBLE);
            layoutOrder[i].setAlpha(1);

            ObjectAnimator anim = ObjectAnimator.ofFloat(layoutOrder[i], "alpha", 0);
            anim.setDuration(mediumAnimTime);

            animator.play(anim).after(shortAnimTime * (layoutOrder.length - i));
        }

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                long remaining = activatedTime - System.currentTimeMillis() + 30000;

                if (remaining < 0) {
                    pageState = PageState.HIDDEN;
                    animator = null;
                    DevicePolicyManager mDPM = (DevicePolicyManager) MainActivity.this.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    if (mDPM.isAdminActive(new ComponentName(MainActivity.this, LockScreenAdminReceiver.class))) {
                        Log.i(LOG_TAG, "LOCK");
                        mDPM.lockNow();
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    private void acquireWakeLock() {
        if (wakeLock == null) {
            wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "app:" + LOG_TAG);
        }
        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
        }
        ping();
    }

    private void releaseWakeLock() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    @Override
    public void ping() {
        activatedTime = System.currentTimeMillis();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ping();
        return super.onTouchEvent(event);
    }
}
