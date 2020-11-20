package com.sinhalastickers.srifunsticker.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sinhalastickers.srifunsticker.R;
import com.sinhalastickers.srifunsticker.base.SavedPackDetailActivity;
import com.sinhalastickers.srifunsticker.base.StickerPack;
import com.sinhalastickers.srifunsticker.base.StickerPackDetailsActivity;
import com.sinhalastickers.srifunsticker.helpers.Constants;
import com.sinhalastickers.srifunsticker.helpers.KiloNova;
import com.sinhalastickers.srifunsticker.helpers.StickerBook;
import com.sinhalastickers.srifunsticker.models.Files;
import com.sinhalastickers.srifunsticker.models.Sticker;
import com.sinhalastickers.srifunsticker.models.Tray;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.ViewHolder> {

    private ArrayList<Sticker> stickers;
    private Context context;
    private ProgressBar progress_bar_explore;
    private TextView loadingPack;
    private String publisher;

    public QueryAdapter(String publisher, Context context, ArrayList<Sticker> stickers, ProgressBar progress_bar_explore, TextView loadingPack) {
        this.context = context;
        this.stickers = stickers;
        this.publisher = publisher;
        this.progress_bar_explore = progress_bar_explore;
        this.loadingPack = loadingPack;
    }

    @Override
    public QueryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sticker_item_of_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final QueryAdapter.ViewHolder viewHolder, final int i) {

        Sticker sticker      = stickers.get(i);
        viewHolder.sticker_name.setText(stickers.get(i).getTitle());
        Log.d("imageUrl",stickers.get(i).getTitle());

        ArrayList<Files> files  = stickers.get(i).getFiles();

        ArrayList<Tray> tray    = sticker.getTrays();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Constants.IMG_URL +  files.get(0).getImages())
                .setAutoPlayAnimations(true)
                .build();
        viewHolder.draweeView.setController(controller);

        DraweeController controller_1 = Fresco.newDraweeControllerBuilder()
                .setUri(Constants.IMG_URL +  files.get(1).getImages())
                .setAutoPlayAnimations(true)
                .build();
        viewHolder.draweeView_1.setController(controller_1);

        DraweeController controller_2 = Fresco.newDraweeControllerBuilder()
                .setUri(Constants.IMG_URL +  files.get(2).getImages())
                .setAutoPlayAnimations(true)
                .build();
        viewHolder.draweeView_2.setController(controller_2);
        DraweeController controller_3 = Fresco.newDraweeControllerBuilder()
                .setUri(Constants.IMG_URL +  files.get(3).getImages())
                .setAutoPlayAnimations(true)
                .build();
        viewHolder.draweeView_3.setController(controller_3);

        String tray_icon = tray.get(0).getImages();
        File file = context.getExternalFilesDir(null);
        File tray_icon_file = new File(file,"/.stickers/" + tray_icon);

        String Url = Constants.BASE_URL  + Constants.PATH_URL + "upload/" + tray_icon;

       viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               if (tray_icon_file.exists()){
                   StickerPack specific_pack = StickerBook.getStickerPackByCheck(stickers.get(i).getIdentifier());
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
                                stickers.get(i).getIdentifier(),
                                newId,
                                stickers.get(i).getTitle(),
                                sticker.getPublisher()+" ",
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
                        intent.putExtra("total", stickers.get(i).getFiles().size()+"");
                        intent.putExtra("pack_identifier", stickers.get(i).getIdentifier());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    }

                    @Override
                    public void onError(FileLoadRequest request, Throwable t) {
                    }
                });

    }

    @Override
    public int getItemCount() {
        return (null != stickers ? stickers.size() : 0);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView sticker_name;
        private SimpleDraweeView draweeView,draweeView_1,draweeView_2,draweeView_3;
        public ViewHolder(View view) {
            super(view);
            sticker_name = itemView.findViewById(R.id.sticker_name);
            draweeView = view.findViewById(R.id.sticker_item_image);
            draweeView_1 = view.findViewById(R.id.sticker_item_image_1);
            draweeView_2 = view.findViewById(R.id.sticker_item_image_2);
            draweeView_3 = view.findViewById(R.id.sticker_item_image_3);
        }
    }







}