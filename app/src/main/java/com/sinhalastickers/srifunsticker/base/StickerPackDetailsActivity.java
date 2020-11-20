/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.sinhalastickers.srifunsticker.base;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.sinhalastickers.srifunsticker.BuildConfig;
import com.sinhalastickers.srifunsticker.R;
import com.sinhalastickers.srifunsticker.helpers.Admob;
import com.sinhalastickers.srifunsticker.helpers.Constants;
import com.sinhalastickers.srifunsticker.helpers.DataArchiver;
import com.sinhalastickers.srifunsticker.helpers.InternetConnection;
import com.sinhalastickers.srifunsticker.helpers.SharedPref;
import com.sinhalastickers.srifunsticker.helpers.StickerBook;
import com.sinhalastickers.srifunsticker.models.Files;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.MultiFileDownloadListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StickerPackDetailsActivity extends BaseActivity {

    /**
     * Do not change below values of below 3 lines as this is also used by WhatsApp
     */
    public static final String EXTRA_STICKER_PACK_ID = "sticker_pack_id";
    public static final String EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority";
    public static final String EXTRA_STICKER_PACK_NAME = "sticker_pack_name";

    public static final int ADD_PACK = 200;
    public static final String EXTRA_STICKER_PACK_WEBSITE = "sticker_pack_website";
    public static final String EXTRA_STICKER_PACK_EMAIL = "sticker_pack_email";
    public static final String EXTRA_STICKER_PACK_PRIVACY_POLICY = "sticker_pack_privacy_policy";
    public static final String EXTRA_STICKER_PACK_TRAY_ICON = "sticker_pack_tray_icon";
    public static final String EXTRA_SHOW_UP_BUTTON = "show_up_button";
    public static final String EXTRA_STICKER_PACK_DATA = "sticker_pack";
    private static final String TAG = "StickerPackDetails";

    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private StickerPreviewAdapter stickerPreviewAdapter;
    private int numColumns;
    private StickerPack stickerPack;
    private View divider;
    private WhiteListCheckAsyncTask whiteListCheckAsyncTask;
    private ArrayList<Files> sticker_list;
    private int total_size;
    private Context context;
    private Boolean isNewlyCreated;
    private TextView addToWhatsApp;
    private ProgressBar progress_bar_1;
    private LinearLayout DownloadingLayout;
    private TextView InfoText;
    private InterstitialAd interstitialAd;
    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sticker_pack_details);

        FileLoader.with(context);

        View view=getWindow().getDecorView().getRootView();
        Admob.createLoadBanner(getApplicationContext(), view);

        context = this;

        sharedPref = new SharedPref(context);
        interstitialAd = new InterstitialAd(this);
        // interstitialAd.setAdUnitId(getString(R.string.admob_fullscreen_adding_pack_unit_id));
        interstitialAd.setAdUnitId(sharedPref.loadUser("ADMOB_INTERSTITIAL"));
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);


        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Toast.makeText(SavedPackDetailActivity.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
                startAds();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
               /* Toast.makeText(SavedPackDetailActivity.this,
                        "onAdFailedToLoad() with error code: " + errorCode,
                        Toast.LENGTH_SHORT).show();*/
                startAds();
            }

            @Override
            public void onAdClosed() {
                startAds();
            }
        });

        isNewlyCreated = getIntent().getExtras().getBoolean("check");

        boolean showUpButton = getIntent().getBooleanExtra(EXTRA_SHOW_UP_BUTTON, false);
        stickerPack = StickerBook.getStickerPackById(getIntent().getStringExtra(EXTRA_STICKER_PACK_DATA));

        sticker_list = new ArrayList<Files>();
        sticker_list = (ArrayList<Files>) getIntent().getSerializableExtra("sticker_list");
        total_size = sticker_list.size();

        DownloadingLayout = findViewById(R.id.DownloadingLayout);
        InfoText = findViewById(R.id.InfoText);

        progress_bar_1 = findViewById(R.id.progress_bar_1);
        progress_bar_1.setMax(total_size);

        TextView packNameTextView = findViewById(R.id.pack_name);
        TextView packPublisherTextView = findViewById(R.id.author);
        TextView total_size_text = findViewById(R.id.total_size);
        ImageView packTrayIcon = findViewById(R.id.tray_image);

        layoutManager = new GridLayoutManager(this, 1);
        addToWhatsApp = findViewById(R.id.addToWhatsApp);
        recyclerView = findViewById(R.id.sticker_list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(pageLayoutListener);
        recyclerView.addOnScrollListener(dividerScrollListener);
        divider = findViewById(R.id.divider);
        if (stickerPreviewAdapter == null) {
            stickerPreviewAdapter = new StickerPreviewAdapter(getLayoutInflater(), R.drawable.sticker_error, getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_size), getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_padding), stickerPack);
            recyclerView.setAdapter(stickerPreviewAdapter);
        }
        packNameTextView.setText(""+stickerPack.name+" ツ");
        total_size_text.setText("✿" + sticker_list.size() +  " Stickers");
        packPublisherTextView.setText("✿" +  stickerPack.publisher);
        packTrayIcon.setImageURI(stickerPack.getTrayImageUri());
        addToWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stickerPack.getStickers().size()>=3) {
                    StickerPackDetailsActivity.this.addStickerPackToWhatsApp(stickerPack);
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(StickerPackDetailsActivity.this)
                            .setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create();
                    alertDialog.setTitle(getResources().getString(R.string.invalid_action));
                    alertDialog.setMessage(getResources().getString(R.string.invalid_3_stickers));
                    alertDialog.show();
                }
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(showUpButton);
            getSupportActionBar().setTitle(showUpButton ? R.string.title_activity_sticker_pack_details_multiple_pack : R.string.title_activity_sticker_pack_details_single_pack);
        }


        String Url = Constants.BASE_URL  + Constants.PATH_URL + "upload/"  ;

        String[] StickerListIds = new String[total_size];

        for(int i=0;i<total_size;i++) {
            String id = sticker_list.get(i).getImages();
            String URL =  Url + id ;
            StickerListIds[i] = URL;
        }

        String root = context.getFilesDir().getAbsolutePath();
        String path = "/.stickers";
        String root_path = root + path;

        File mediaStorageDir = new File(root_path);
        if (!mediaStorageDir.exists()){
            boolean res_ = mediaStorageDir.mkdirs();
            if (res_){
                Log.d("FolderCreating","It is Succesful");
                File newFile = new File(root + "/.nomedia");
                try {
                    boolean n_res = newFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                mediaStorageDir.mkdirs();
            }
        }

        File nomedia = new File(root + "/.nomedia");

        if (!nomedia.exists()){
            try {
                boolean n_res = nomedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String strFileName = mediaStorageDir.getName();

        if(isNewlyCreated) {
            progress_bar_1.setVisibility(View.VISIBLE);
            if (InternetConnection.checkConnection(context)) {

                FileLoader.multiFileDownload(context)
                        .fromDirectory(strFileName, FileLoader.DIR_EXTERNAL_PRIVATE)
                        .progressListener(new MultiFileDownloadListener() {
                            @Override
                            public void onProgress(File downloadedFile, int progress, int totalFiles) {
                                Log.d("ProgressFile",progress+" >> " + totalFiles);

                                if(isNewlyCreated){
                                    Log.d("isNewlyCreated","No, it is not available. It is creating.");

                                    File loadedFile = downloadedFile;

                                    Uri external = getImageContentUri(context,loadedFile);
                                    Log.d("ExternalUrl",external + " ::::::::");
                                    context.grantUriPermission(context.getPackageName(), external, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    stickerPack.addSticker(external, context);

                                    progress_bar_1.setProgress(progress);
                                    InfoText.setText(progress + " of " + totalFiles + "");

                                    if (progress >= totalFiles){
                                        progress_bar_1.setVisibility(View.INVISIBLE);
                                        DownloadingLayout.setVisibility(View.GONE);

                                        if (stickerPreviewAdapter != null) {
                                            stickerPreviewAdapter.notifyDataSetChanged();
                                        }
                                    }

                                }else{
                                    Log.d("isNewlyCreated","Yes, it is  available. no need creating.");
                                }

                            }

                            public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                                Log.d("isNewlyCreated","Yes, it is  available. no need creating.");
                            }

                            @Override
                            public void onError(Exception e, int progress) {
                                super.onError(e, progress);
                            }

                        }).loadMultiple(StickerListIds);

            } else {
                SweetAlertDialog dialog = new SweetAlertDialog(StickerPackDetailsActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getResources().getString(R.string.error))
                        .setContentText(getResources().getString(R.string.connection_error))
                        .setConfirmText(getResources().getString(R.string.ok));

                dialog.show();

            }

        }else{
            DownloadingLayout.setVisibility(View.GONE);
            progress_bar_1.setVisibility(View.INVISIBLE);
        }

    }

    private void launchInfoActivity(String publisherWebsite, String publisherEmail, String privacyPolicyWebsite, String trayIconUriString) {
        Intent intent = new Intent(StickerPackDetailsActivity.this, StickerPackInfoActivity.class);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_ID, stickerPack.identifier);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_WEBSITE, publisherWebsite);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_EMAIL, publisherEmail);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_PRIVACY_POLICY, privacyPolicyWebsite);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_TRAY_ICON, stickerPack.getTrayImageUri().toString());
        startActivity(intent);
    }

    private void startAds() {
        if (!interstitialAd.isLoading() && !interstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            interstitialAd.loadAd(adRequest);
        }
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            int status = Integer.parseInt(sharedPref.loadUser("ADMOB_STATUS"));
            if(status == 1){
                if (!sharedPref.loadPurchaseModeState()){
                    interstitialAd.show();
                }
            }
        } else {
            // Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
            startAds();
        }
    }


    @Override
    public void onBackPressed() {
        showInterstitial();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_info && stickerPack != null) {
            final String publisherWebsite = stickerPack.publisherWebsite;
            final String publisherEmail = stickerPack.publisherEmail;
            final String privacyPolicyWebsite = stickerPack.privacyPolicyWebsite;
            Uri trayIconUri = stickerPack.getTrayImageUri();
            launchInfoActivity(publisherWebsite, publisherEmail, privacyPolicyWebsite, trayIconUri.toString());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addStickerPackToWhatsApp(StickerPack sp) {
        Intent intent = new Intent();
        intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK");
        Log.w("IS IT A NEW IDENTIFIER?", sp.getIdentifier());
        intent.putExtra(EXTRA_STICKER_PACK_ID, sp.getIdentifier());
        intent.putExtra(EXTRA_STICKER_PACK_AUTHORITY, BuildConfig.CONTENT_PROVIDER_AUTHORITY);
        intent.putExtra(EXTRA_STICKER_PACK_NAME, sp.getName());
        try {
            startActivityForResult(intent, 200);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.error_adding_sticker_pack, Toast.LENGTH_LONG).show();
        }
    }

    private final ViewTreeObserver.OnGlobalLayoutListener pageLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            setNumColumns(recyclerView.getWidth() / recyclerView.getContext().getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_size));
        }
    };

    private void setNumColumns(int numColumns) {
        if (this.numColumns != numColumns) {
            layoutManager.setSpanCount(numColumns);
            this.numColumns = numColumns;
            if (stickerPreviewAdapter != null) {
                stickerPreviewAdapter.notifyDataSetChanged();
            }
        }
    }

    private final RecyclerView.OnScrollListener dividerScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull final RecyclerView recyclerView, final int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            updateDivider(recyclerView);
        }

        @Override
        public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
            super.onScrolled(recyclerView, dx, dy);
            updateDivider(recyclerView);
        }

        private void updateDivider(RecyclerView recyclerView) {
            boolean showDivider = recyclerView.computeVerticalScrollOffset() > 0;
            if (divider != null) {
                divider.setVisibility(showDivider ? View.VISIBLE : View.INVISIBLE);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        DataArchiver.writeStickerBookJSON(StickerBook.getAllStickerPacks(), this);
        whiteListCheckAsyncTask = new WhiteListCheckAsyncTask(this);
        whiteListCheckAsyncTask.execute(stickerPack);
    }



    private void updateAddUI(Boolean isWhitelisted) {
        if (isWhitelisted) {

            addToWhatsApp.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            addToWhatsApp.setText(getResources().getString(R.string.already_added));
            addToWhatsApp.setTextColor(context.getResources().getColor(R.color.white));
           // addButton.setVisibility(View.GONE);
           // alreadyAddedText.setVisibility(View.VISIBLE);
        } else {
            addToWhatsApp.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            addToWhatsApp.setText(getResources().getString(R.string.add_to_whatsapp));
            addToWhatsApp.setTextColor(context.getResources().getColor(R.color.white));
          //  addButton.setVisibility(View.VISIBLE);
          //  alreadyAddedText.setVisibility(View.GONE);
        }
    }

    static class WhiteListCheckAsyncTask extends AsyncTask<StickerPack, Void, Boolean> {
        private final WeakReference<StickerPackDetailsActivity> stickerPackDetailsActivityWeakReference;

        WhiteListCheckAsyncTask(StickerPackDetailsActivity stickerPackListActivity) {
            this.stickerPackDetailsActivityWeakReference = new WeakReference<>(stickerPackListActivity);
        }

        @Override
        protected final Boolean doInBackground(StickerPack... stickerPacks) {
            StickerPack stickerPack = stickerPacks[0];
            final StickerPackDetailsActivity stickerPackDetailsActivity = stickerPackDetailsActivityWeakReference.get();
            //noinspection SimplifiableIfStatement
            if (stickerPackDetailsActivity == null) {
                return false;
            }
            return WhitelistCheck.isWhitelisted(stickerPackDetailsActivity, stickerPack.identifier);
        }

        @Override
        protected void onPostExecute(Boolean isWhitelisted) {
            final StickerPackDetailsActivity stickerPackDetailsActivity = stickerPackDetailsActivityWeakReference.get();
            if (stickerPackDetailsActivity != null) {
                stickerPackDetailsActivity.updateAddUI(isWhitelisted);
            }
        }
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        DataArchiver.writeStickerBookJSON(StickerBook.getAllStickerPacks(), this);
        if (whiteListCheckAsyncTask != null && !whiteListCheckAsyncTask.isCancelled()) {
            whiteListCheckAsyncTask.cancel(true);
        }
    }

    @Override
    protected void onDestroy() {
        DataArchiver.writeStickerBookJSON(StickerBook.getAllStickerPacks(), this);
        super.onDestroy();
    }

}
