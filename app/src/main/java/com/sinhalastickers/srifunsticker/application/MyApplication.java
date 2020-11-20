package com.sinhalastickers.srifunsticker.application;

import androidx.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.ads.MobileAds;
import com.sinhalastickers.srifunsticker.R;
import com.onesignal.OneSignal;
import io.fabric.sdk.android.Fabric;

public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        // Fresco Initialization
        Fresco.initialize(this);

        // MobileAds Initialization
        MobileAds.initialize(this, getString(R.string.google_app_id));

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }
}