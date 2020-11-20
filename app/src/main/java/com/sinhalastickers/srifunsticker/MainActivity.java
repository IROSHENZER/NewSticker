package com.sinhalastickers.srifunsticker;

import android.Manifest;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.ads.MobileAds;
import com.sinhalastickers.srifunsticker.base.SavedPackDetailActivity;
import com.sinhalastickers.srifunsticker.base.StickerPack;
import com.sinhalastickers.srifunsticker.fragments.ExploreFragment;
import com.sinhalastickers.srifunsticker.fragments.SavedFragment;
import com.sinhalastickers.srifunsticker.helpers.ImageManipulation;
import com.sinhalastickers.srifunsticker.helpers.SharedPref;
import com.sinhalastickers.srifunsticker.helpers.StickerBook;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements RewardedVideoAdListener, BillingProcessor.IBillingHandler {

    private static long back_pressed;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static String newName, newCreator;
    Context context;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private RewardedVideoAd mRewardedVideoAd;
    private SharedPref sharedPref;
    private BillingProcessor bp;
    private Boolean admob_status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = new SharedPref(this);

        bp = new BillingProcessor(this, null, this);
        if(!bp.isInitialized()){
            bp.initialize();
        }

        sharedPref.setPurchaseModeState(admob_status);

        MobileAds.initialize(this,  sharedPref.loadUser("ADMOB_APP_ID"));

        context = this;
        StickerBook.init(context);
        Fresco.initialize(context);

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
        mRewardedVideoAd.setRewardedVideoAdListener((RewardedVideoAdListener) context);

        loadRewardedVideoAd();

        requiredPermission();

        fab =  findViewById(R.id.fab);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int status = Integer.parseInt(sharedPref.loadUser("ADMOB_STATUS"));
                if(status == 1){
                    if (mRewardedVideoAd.isLoaded()) {
                        if (!sharedPref.loadPurchaseModeState()){
                            mRewardedVideoAd.show();
                        }
                    }else {
                        addNewStickerPackInInterface();
                    }
                }else{
                    addNewStickerPackInInterface();
                }

            }
        });


    }


    private void loadRewardedVideoAd() {
        Log.d("IDDDDDDDDD",sharedPref.loadUser("ADMOB_REWARDED"));
        mRewardedVideoAd.loadAd(sharedPref.loadUser("ADMOB_REWARDED"),
                new AdRequest.Builder().build());
    }

    private void requiredPermission() {

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                if(!checkIfBatteryOptimizationIgnored()){


                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Battery Optimization?")
                            .setContentText("In order for the app to work correctly, please disable the optimization for " + context.getResources().getString(R.string.app_name)+ " after clicking the button.\"")
                            .setConfirmText(getResources().getString(R.string.ok))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                                    startActivityForResult(intent, 4113);
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getResources().getString(R.string.permission_denied))
                        .setContentText(getResources().getString(R.string.if_you_dont_allow))
                        .setConfirmText(getResources().getString(R.string.ok))
                        .show();

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
            }
        });
    }

    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if (data!=null && requestCode==2319){
             Uri uri = data.getData();
             getContentResolver().takePersistableUriPermission(Objects.requireNonNull(uri), Intent.FLAG_GRANT_READ_URI_PERMISSION);
             createNewStickerPackAndOpenIt(newName, newCreator, uri);
        }

        if (requestCode==4113){
            if(!checkIfBatteryOptimizationIgnored()){

                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Battery Optimization?")
                        .setContentText("In order for the app to work correctly, please disable the optimization for " + context.getResources().getString(R.string.app_name)+ " after clicking the button.\"")
                        .setConfirmText(getResources().getString(R.string.ok))
                        .show();
            }else{
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Thank you :) ")
                        .setContentText("You have granted the required permissions.")
                        .setConfirmText(getResources().getString(R.string.ok))
                        .show();
            }
        }
    }

    private void createNewStickerPackAndOpenIt(String name, String creator, Uri trayImage){

        String newId_1 = UUID.randomUUID().toString();
        Uri external = ImageManipulation.convertIconTrayToWebP(trayImage,context);
        Log.d("ExternalUrl",external + " ADAPTER");
        String newId = UUID.randomUUID().toString();
        context.grantUriPermission(context.getPackageName(), external, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        StickerPack sp = new StickerPack(
                newId_1,
                newId,
                name,
                creator,
                external,
                context.getResources().getString(R.string.app_email),
                context.getResources().getString(R.string.app_website),
                context.getResources().getString(R.string.app_policy),
                context.getResources().getString(R.string.app_licence),
                context);
        StickerBook.addStickerPackExisting(sp);

        Intent intent = new Intent(context, SavedPackDetailActivity.class);
        intent.putExtra(SavedPackDetailActivity.EXTRA_SHOW_UP_BUTTON, true);
        intent.putExtra(SavedPackDetailActivity.EXTRA_STICKER_PACK_DATA,newId);
        intent.putExtra("isNewlyCreated", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    private void addNewStickerPackInInterface(){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Create New Sticker Pack");
        dialog.setMessage("Please specify title and creator for the pack.");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nameBox = new EditText(this);
        nameBox.setLines(1);
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.setMargins(50, 0, 50, 10);
        nameBox.setLayoutParams(buttonLayoutParams);
        nameBox.setHint("Pack Name");
        nameBox.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        layout.addView(nameBox);

        final EditText creatorBox = new EditText(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            creatorBox.setAutofillHints("name");
        }
        creatorBox.setLines(1);
        creatorBox.setLayoutParams(buttonLayoutParams);
        creatorBox.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        creatorBox.setHint("Creator");
        layout.addView(creatorBox);

        dialog.setView(layout);

        dialog.setPositiveButton("OK", null);

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        final AlertDialog ad = dialog.create();

        ad.show();

        Button b = ad.getButton(AlertDialog.BUTTON_POSITIVE);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(nameBox.getText())){
                    nameBox.setError("Package name is required!");
                }

                if(TextUtils.isEmpty(creatorBox.getText())){
                    creatorBox.setError("Creator is required!");
                }

                if(!TextUtils.isEmpty(nameBox.getText()) && !TextUtils.isEmpty(creatorBox.getText())) {
                    ad.dismiss();
                    createDialogForPickingIconImage(nameBox, creatorBox);
                }
            }
        });

        creatorBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    b.performClick();
                }
                return false;
            }
        });
    }

    private void createDialogForPickingIconImage(EditText nameBox, EditText creatorBox){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick your pack's icon image");
        builder.setMessage("Now you will pick the new sticker pack's icon image.")
                .setCancelable(false)
                .setPositiveButton("Let's go", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        openFileTray(nameBox.getText().toString(), creatorBox.getText().toString());
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void openFileTray(String name, String creator) {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.setType("image/*");
        newName = name;
        newCreator = creator;
        startActivityForResult(i, 2319);
    }


    private boolean checkIfBatteryOptimizationIgnored(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            return pm.isIgnoringBatteryOptimizations(packageName);
        } else {
            return true;
        }
    }



    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ExploreFragment(), getResources().getString(R.string.online_stickers));
        adapter.addFragment(new SavedFragment(), getResources().getString(R.string.saved_stickers));
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onRewarded(RewardItem reward) {
       /* Toast.makeText(this, "onRewarded! currency: " + reward.getType() + "  amount: " +
                reward.getAmount(), Toast.LENGTH_SHORT).show();*/
        loadRewardedVideoAd();
        addNewStickerPackInInterface();
        Toast.makeText(this, "Congratulations, you can make your own stickers.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.d("RewardedAds","onRewardedVideoAdLeftApplication");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
        Log.d("RewardedAds","onRewardedVideoAdClosed");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Log.d("RewardedAds","onRewardedVideoAdFailedToLoad");
    }

    @Override
    public void onRewardedVideoAdLoaded() {
       Log.d("RewardedAds","onRewardedVideoAdLoaded");
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.d("RewardedAds","onRewardedVideoAdOpened");
    }

    @Override
    public void onRewardedVideoStarted() {
        Log.d("RewardedAds","onRewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoCompleted() {
        Log.d("RewardedAds","onRewardedVideoCompleted");
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {


    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("onQueryTextChange", newText);

                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);
                    Intent intent = new Intent(context, StickersCategoryActivity.class);
                    intent.putExtra("pack_name", query);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                // Not implemented here
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);

        /* if (item.getItemId() == R.id.all_menu_share) {
          /*  Intent ıntent = new Intent(MainActivity.this, PremiumActivity.class);
            startActivity(ıntent);
            shareApp(getApplicationContext());
            return true;
        }*/
        if (item.getItemId() == R.id.all_menu_rate) {
            openAppRating(getApplicationContext());
            return true;
        }



       if (item.getItemId() == R.id.admin_name_string) {
           openWebSite(context.getResources().getString(R.string.admin_link));
            return true;

       }

          /* if (item.getItemId() == R.id.all_menu_dmca_policy) {
            openWebSite(context.getResources().getString(R.string.all_menu_dmca_policy_link));
            return true;
        }*/
        if (item.getItemId() == R.id.all_menu_privacy) {
            openWebSite(context.getResources().getString(R.string.privacy_policy_link));
            return true;
        }
        /*   if (item.getItemId() == R.id.all_menu_about) {
            openWebSite(context.getResources().getString(R.string.all_menu_about_link));
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    private void openWebSite(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
        startActivity(intent);
    }

    public static void shareApp(Context context) {
        final String appPackageName = context.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.check_out_the_app) + appPackageName);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
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

    @Override
    public void onBackPressed() {

        if (back_pressed + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
        }
        else{
            Toast.makeText(getBaseContext(), "Press once again to exit",
                    Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }

    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }

}
