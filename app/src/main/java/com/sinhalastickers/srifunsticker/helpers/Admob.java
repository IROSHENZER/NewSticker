package com.sinhalastickers.srifunsticker.helpers;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.sinhalastickers.srifunsticker.R;

public class Admob {

    private static AdView mAdView;


    public static void createLoadBanner(final Context context, View view) {

        SharedPref sharedPref = new SharedPref(context);

       /* mAdView = (AdView) view.findViewById(R.id.adView);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(sharedPref.loadUser("ADMOB_BANNER"));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        AdView mAdView = new AdView(context);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(sharedPref.loadUser("ADMOB_BANNER"));
        FrameLayout frameLayout = view.findViewById(R.id.adView);
        frameLayout.addView(mAdView);

        int status = Integer.parseInt(sharedPref.loadUser("ADMOB_STATUS"));
        if(status == 1){
            if (!sharedPref.loadPurchaseModeState()){
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        }else{
            frameLayout.setVisibility(View.GONE);
        }

        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                  super.onAdLoaded();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();

            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
            }

        });

    }

}
