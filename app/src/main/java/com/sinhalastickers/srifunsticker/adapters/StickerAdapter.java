package com.sinhalastickers.srifunsticker.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sinhalastickers.srifunsticker.R;
import com.sinhalastickers.srifunsticker.base.SavedPackDetailActivity;
import com.sinhalastickers.srifunsticker.base.StickerPack;
import com.sinhalastickers.srifunsticker.base.StickerPackDetailsActivity;
import com.sinhalastickers.srifunsticker.helpers.Constants;
import com.sinhalastickers.srifunsticker.helpers.KiloNova;
import com.sinhalastickers.srifunsticker.helpers.StickerBook;
import com.sinhalastickers.srifunsticker.models.Files;
import com.sinhalastickers.srifunsticker.models.Pack;
import com.sinhalastickers.srifunsticker.models.Sticker;
import com.sinhalastickers.srifunsticker.models.Tray;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.SingleItemRowHolder> {

    private ArrayList<Sticker> models;
    private ArrayList<Pack> dataList;
    private ProgressBar progress_bar_explore;
    private TextView loadingPack;
    private Context context;
    private int index;

    private String publisher;
    private String website;
    private String policy;
    private String licence;
    private String id;

    public StickerAdapter(Context context, ArrayList<Sticker> itemsList, ArrayList<Pack> dataList, int index, ProgressBar progress_bar_explore, TextView loadingPack) {
        this.models = itemsList;
        this.context = context;
        this.index = index;
        this.dataList = dataList;
        this.progress_bar_explore = progress_bar_explore;
        this.loadingPack = loadingPack;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.section_single, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        Sticker sticker      = models.get(i);
        publisher            = dataList.get(index).getPublisher();
        website              = dataList.get(index).getPublisherWebsite();
        policy               = dataList.get(index).getPrivacyPolicyWebsite();
        licence              = dataList.get(index).getLicenseAgreementWebsite();
        id                   = dataList.get(index).getId();

        ArrayList<Files> files  = sticker.getFiles();
        ArrayList<Tray> tray    = sticker.getTrays();

        String image_1 = files.get(0).getImages();

        String URL_1 =  Constants.IMG_URL +  image_1;

        Log.d("URL_1",URL_1);

        Uri uri_1 = Uri.parse(URL_1);

        Picasso.get().load(uri_1).into(holder.iv_1);

        holder.tvTitle.setText(sticker.getTitle());
        holder.tvPublisher.setText("("+ sticker.getFiles().size() + " Stickers)");

        String tray_icon = tray.get(0).getImages();

        File file = context.getExternalFilesDir(null);
        File tray_icon_file = new File(file,"/.stickers/" + tray_icon);

        String Url = Constants.BASE_URL  + Constants.PATH_URL + "upload/" + tray_icon;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tray_icon_file.exists()){
                    StickerPack specific_pack = StickerBook.getStickerPackByCheck(models.get(i).getIdentifier());
                    if (specific_pack != null){
                        String sidentifier = specific_pack.getIdentifier();
                        Intent intent = new Intent(context, SavedPackDetailActivity.class);
                        intent.putExtra(SavedPackDetailActivity.EXTRA_SHOW_UP_BUTTON, true);
                        intent.putExtra(SavedPackDetailActivity.EXTRA_STICKER_PACK_DATA,sidentifier);
                        context.startActivity(intent);
                    }else{
                        c_createStickerPack(sticker,Url,i, sticker.getFiles());
                    }
                }else{
                    c_createStickerPack(sticker,Url,i,sticker.getFiles());
                }

            }
        });

    }

    private void c_createStickerPack(Sticker sticker, String Url, int i, ArrayList<Files> finalSticker_list) {

        String root = context.getFilesDir().getAbsolutePath();
        String path = "/.stickers";
        String root_path = root + path;

        File mediaStorageDir = new File(root_path);

        String strFileName = mediaStorageDir.getName();

        FileLoader.with(context)
                .load(Url,false) //2nd parameter is optioal, pass true to force load from network
                .fromDirectory(strFileName, FileLoader.DIR_EXTERNAL_PRIVATE)
                .asFile(new FileRequestListener<File>() {
                    @Override
                    public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                        File loadedFile = response.getBody();
                        // do something with the file
                        Uri external = KiloNova.getImageContentUri(context,loadedFile);
                        Log.d("ExternalUrl",external + " ADAPTER");
                        String newId = UUID.randomUUID().toString();
                        context.grantUriPermission(context.getPackageName(), external, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        StickerPack sp = new StickerPack(
                                models.get(i).getIdentifier(),
                                newId,
                                models.get(i).getTitle(),
                                dataList.get(i).getPublisher(),
                                external,
                                context.getResources().getString(R.string.app_email),
                                context.getResources().getString(R.string.app_website),
                                context.getResources().getString(R.string.app_policy),
                                context.getResources().getString(R.string.app_licence),
                                context);
                        StickerBook.addStickerPackExisting(sp);


                        Log.d("Identifier","isLoaded: No, id : " + newId);
                        Intent intent = new Intent(context, StickerPackDetailsActivity.class);
                        intent.putExtra(StickerPackDetailsActivity.EXTRA_SHOW_UP_BUTTON, true);
                        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_DATA, newId);
                        intent.putExtra("isNewlyCreated", true);
                        intent.putExtra("check", true);
                        intent.putExtra("sticker_list", finalSticker_list);
                        intent.putExtra("total", models.get(i).getFiles().size()+"");
                        intent.putExtra("pack_identifier", models.get(i).getIdentifier());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    }

                    @Override
                    public void onError(FileLoadRequest request, Throwable t) {
                    }
                });

    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != models ? models.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle,tvPublisher;
        protected ImageView iv_1,iv_2,iv_3,iv_4;
        protected LinearLayout sticker_bg_layout;


        public SingleItemRowHolder(View view) {
            super(view);

            this.tvTitle = view.findViewById(R.id.tvTitle);
            this.tvPublisher = view.findViewById(R.id.tvPublisher);
            this.iv_1 = view.findViewById(R.id.imageView);

        }

    }





}