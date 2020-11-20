package com.sinhalastickers.srifunsticker.base;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sinhalastickers.srifunsticker.BuildConfig;
import com.sinhalastickers.srifunsticker.MainActivity;
import com.sinhalastickers.srifunsticker.R;
import com.sinhalastickers.srifunsticker.clients.ApiClient;
import com.sinhalastickers.srifunsticker.helpers.Constants;
import com.sinhalastickers.srifunsticker.helpers.SharedPref;
import com.sinhalastickers.srifunsticker.interfaces.StickerInterface;
import com.sinhalastickers.srifunsticker.models.Setting;
import com.sinhalastickers.srifunsticker.models.Settings;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    SharedPref sharedPref;
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private RelativeLayout update_layout;
    private Button update_btn, not_now_btn;
    private ProgressBar bar;
    private String identity;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        sharedPref = new SharedPref(this);

        update_layout = findViewById(R.id.update_layout);
        update_btn = findViewById(R.id.btn_update);
        not_now_btn = findViewById(R.id.btn_not_now);
        bar = findViewById(R.id.progress_bar);

        update_layout.setVisibility(View.GONE);

        identity = sharedPref.loadUser("identity");

        if (identity.equals(" ")){
            Log.d("StatusIdentity","it is first");
        }else{
            Log.d("StatusIdentity","it is not first" + identity);
        }

        getInitial();

    }

    private void getInitial() {

        StickerInterface apiService =
                ApiClient.getClient().create(StickerInterface.class);

        retrofit2.Call<Settings> call = apiService.getSetting(Constants.PURCHASE_CODE,identity);

        call.enqueue(new Callback<Settings>() {
            @Override
            public void onResponse(retrofit2.Call<Settings> call, Response<Settings> response) {

                Settings jsonResponse = response.body();

                ArrayList<Setting> settings = jsonResponse.getSettings();

                int status = Integer.parseInt(settings.get(0).getStatus());


                if(status == 200){
                    int admob_status = Integer.parseInt(settings.get(0).getAdmobStatus());
                    int maintance = Integer.parseInt(settings.get(0).getIsMaintance());
                    Log.d("CONNECTION_SUCCESS","The server and the application matched.");
                    sharedPref.setUser("ADMOB_BANNER",settings.get(0).getAdmobBannerId()+"");
                    sharedPref.setUser("ADMOB_INTERSTITIAL",settings.get(0).getAdmobInterstitialId()+"");
                    sharedPref.setUser("ADMOB_REWARDED",settings.get(0).getAdmobRewardedId()+"");
                    sharedPref.setUser("ADMOB_STATUS",settings.get(0).getAdmobStatus()+"");
                    sharedPref.setUser("APP_VERSION",settings.get(0).getAppVersion()+"");
                    sharedPref.setUser("APP_MESSAGE",settings.get(0).getAppMessage()+"");
                    sharedPref.setUser("APP_MAINTANCE",settings.get(0).getIsMaintance()+"");

                    if (maintance == 1){
                        Toast.makeText(getApplicationContext(),"The application is currently being updated. Please try again later.", Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                SplashActivity.this.finish();
                            }
                        }, SPLASH_DISPLAY_LENGTH);
                    }else{

                        int build_version = BuildConfig.VERSION_CODE;
                        int app_version   = Integer.parseInt(settings.get(0).getAppVersion());

                        if (app_version > build_version){

                                    update_layout.setVisibility(View.VISIBLE);

                                    update_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            openAppRating(getApplicationContext());
                                            sharedPref.setVersionChaneModeState(true);
                                        }
                                    });

                                    not_now_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                                            SplashActivity.this.startActivity(mainIntent);
                                            SplashActivity.this.finish();
                                        }
                                    });
                        }else{
                            update_layout.setVisibility(View.GONE);
                            new Handler().postDelayed(new Runnable(){
                                @Override
                                public void run() {
                                    /* Create an Intent that will start the Menu-Activity. */
                                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                                    SplashActivity.this.startActivity(mainIntent);
                                    SplashActivity.this.finish();
                                }
                            }, SPLASH_DISPLAY_LENGTH);
                        }

                    }

                }else{
                    Toast.makeText(getApplicationContext(),"The server and the application could not match.", Toast.LENGTH_LONG).show();
                    Log.d("CONNECTION_SUCCESS","The server and the application could not match.");
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run() {
                           SplashActivity.this.finish();
                        }
                    }, SPLASH_DISPLAY_LENGTH);
                }


            }

            @Override
            public void onFailure(retrofit2.Call<Settings> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }

    public static void openAppRating(Context context) {
        // you can also use BuildConfig.APPLICATION_ID
        String appId = context.getPackageName();
        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + appId));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getPackageManager()
                .queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp : otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName
                    .equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                // make sure it does NOT open in the stack of your activity
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // task reparenting if needed
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                // if the Google Play was already open in a search result
                //  this make sure it still go to the app page you requested
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // this make sure only the Google Play app is allowed to
                // intercept the intent
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;

            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appId));
            context.startActivity(webIntent);
        }
    }
}
