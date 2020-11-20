package com.sinhalastickers.srifunsticker.base;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.sinhalastickers.srifunsticker.BuildConfig;
import com.sinhalastickers.srifunsticker.MainActivity;
import com.sinhalastickers.srifunsticker.R;
import com.sinhalastickers.srifunsticker.helpers.Admob;
import com.sinhalastickers.srifunsticker.helpers.DataArchiver;
import com.sinhalastickers.srifunsticker.helpers.ImageManipulation;
import com.sinhalastickers.srifunsticker.helpers.SharedPref;
import com.sinhalastickers.srifunsticker.helpers.StickerBook;
import com.sinhalastickers.srifunsticker.imageeditor.EditImageActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.lang.ref.WeakReference;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SavedPackDetailActivity extends BaseActivity {

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
    public static String newName, newCreator;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private StickerPreviewAdapter stickerPreviewAdapter;
    private int numColumns;
    private StickerPack stickerPack;
    private View divider;
    private WhiteListCheckAsyncTask whiteListCheckAsyncTask;
    private TextView addToWhatsApp;
    private Context context;
    private String Identifier;
    private ImageView deleteButton;
    private boolean showUpButton,isNewlyCreated;
    SharedPref sharedPref;

   private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_pack_detail);

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

        deleteButton = findViewById(R.id.imageViewlete_pack);

        View view=getWindow().getDecorView().getRootView();
        Admob.createLoadBanner(getApplicationContext(), view);

        isNewlyCreated = getIntent().getBooleanExtra("isNewlyCreated", false);

        showUpButton = getIntent().getBooleanExtra(EXTRA_SHOW_UP_BUTTON, false);
        Identifier = getIntent().getStringExtra(EXTRA_STICKER_PACK_DATA);
        stickerPack = StickerBook.getStickerPackById(getIntent().getStringExtra(EXTRA_STICKER_PACK_DATA));

        TextView packNameTextView = findViewById(R.id.pack_name);
        TextView packPublisherTextView = findViewById(R.id.author);
        TextView total_size = findViewById(R.id.total_size);
        ImageView packTrayIcon = findViewById(R.id.tray_image);

        addToWhatsApp = findViewById(R.id.addToWhatsApp);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView = findViewById(R.id.sticker_list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(pageLayoutListener);
        recyclerView.addOnScrollListener(dividerScrollListener);
        divider = findViewById(R.id.divider);
        if (stickerPreviewAdapter == null) {
            stickerPreviewAdapter = new StickerPreviewAdapter(getLayoutInflater(), R.drawable.sticker_error, getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_size), getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_padding), stickerPack);
            recyclerView.setAdapter(stickerPreviewAdapter);
        }
        packNameTextView.setText(stickerPack.name);
        packPublisherTextView.setText("✿" + stickerPack.publisher);
        total_size.setText("✿" + stickerPack.getStickers().size()+ " Stickers");
        packTrayIcon.setImageURI(stickerPack.getTrayImageUri());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(showUpButton);
            getSupportActionBar().setTitle(showUpButton ? R.string.title_activity_sticker_pack_details_multiple_pack : R.string.title_activity_sticker_pack_details_single_pack);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(SavedPackDetailActivity.this)
                        .setNegativeButton(context.getResources().getString(R.string.title_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton(context.getResources().getString(R.string.title_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                StickerBook.deleteStickerPackById(stickerPack.getIdentifier());
                                finish();
                                Intent intent = new Intent(SavedPackDetailActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Toast.makeText(SavedPackDetailActivity.this, context.getResources().getString(R.string.title_pack_deleted), Toast.LENGTH_SHORT).show();
                            }
                        }).create();
                alertDialog.setTitle(context.getResources().getString(R.string.title_are_you_sure));
                alertDialog.setMessage(context.getResources().getString(R.string.deleting_message));
                alertDialog.show();
            }
        });

        addToWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stickerPack.getStickers().size()>=3) {
                    SavedPackDetailActivity.this.addStickerPackToWhatsApp(stickerPack);
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(SavedPackDetailActivity.this)
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

    private void launchInfoActivity(String publisherWebsite, String publisherEmail, String privacyPolicyWebsite, String trayIconUriString) {
        Intent intent = new Intent(SavedPackDetailActivity.this, StickerPackInfoActivity.class);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_ID, stickerPack.identifier);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_WEBSITE, publisherWebsite);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_EMAIL, publisherEmail);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_PRIVACY_POLICY, privacyPolicyWebsite);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_TRAY_ICON, stickerPack.getTrayImageUri().toString());
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        showInterstitial();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.saved_toolbar, menu);
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

        int size = stickerPack.getStickers().size();



        if (item.getItemId() == R.id.action_add_sticker) {
            if (size == 30 ){
                new SweetAlertDialog(this)
                        .setTitleText("Oops...!")
                        .setContentText("You've reached the limit.")
                        .show();
            }else{
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

           if (resultCode == RESULT_OK) {
               Uri resultUri = result.getUri();
               Intent ıntent = new Intent(SavedPackDetailActivity.this, EditImageActivity.class);
               ıntent.putExtra("data", resultUri.toString());
               ıntent.putExtra(EXTRA_STICKER_PACK_DATA, Identifier);
               startActivityForResult(ıntent, 5555);

           } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
               Exception error = result.getError();
           }

        }

        if (resultCode == RESULT_OK && requestCode == 5555) {

            String imagePath = data.getStringExtra("imagePath");
            Uri uri = ImageManipulation.convertImageToWebP(Uri.fromFile(new File(imagePath)),context);
            stickerPack.addSticker( uri , context);
            finish();
            startActivity(getIntent());
            showInterstitial();
        }

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

    @Override
    protected void onPause() {
        super.onPause();
        DataArchiver.writeStickerBookJSON(StickerBook.getAllStickerPacks(), this);
        if (whiteListCheckAsyncTask != null && !whiteListCheckAsyncTask.isCancelled()) {
            whiteListCheckAsyncTask.cancel(true);
        }
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
        private final WeakReference<SavedPackDetailActivity> stickerPackDetailsActivityWeakReference;

        WhiteListCheckAsyncTask(SavedPackDetailActivity stickerPackListActivity) {
            this.stickerPackDetailsActivityWeakReference = new WeakReference<>(stickerPackListActivity);
        }

        @Override
        protected final Boolean doInBackground(StickerPack... stickerPacks) {
            StickerPack stickerPack = stickerPacks[0];
            final SavedPackDetailActivity stickerPackDetailsActivity = stickerPackDetailsActivityWeakReference.get();
            //noinspection SimplifiableIfStatement
            if (stickerPackDetailsActivity == null) {
                return false;
            }
            return WhitelistCheck.isWhitelisted(stickerPackDetailsActivity, stickerPack.identifier);
        }

        @Override
        protected void onPostExecute(Boolean isWhitelisted) {
            final SavedPackDetailActivity stickerPackDetailsActivity = stickerPackDetailsActivityWeakReference.get();
            if (stickerPackDetailsActivity != null) {
                stickerPackDetailsActivity.updateAddUI(isWhitelisted);
            }
        }
    }



    @Override
    protected void onDestroy() {
        DataArchiver.writeStickerBookJSON(StickerBook.getAllStickerPacks(), this);
        super.onDestroy();
    }

}