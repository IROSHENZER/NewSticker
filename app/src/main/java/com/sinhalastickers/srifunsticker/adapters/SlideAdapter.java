package com.sinhalastickers.srifunsticker.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sinhalastickers.srifunsticker.R;
import com.sinhalastickers.srifunsticker.StickersCategoryActivity;
import com.sinhalastickers.srifunsticker.helpers.Constants;
import com.sinhalastickers.srifunsticker.models.Slide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.SingleItemRowHolder> {

    private ArrayList<Slide> models;
    private Context context;

    public SlideAdapter(Context context, ArrayList<Slide> itemsList) {
        this.models = itemsList;
        this.context = context;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.slide_layout_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        String image = models.get(i).getImage();
        String title = models.get(i).getTitle();
        String URL_1 =  Constants.IMG_URL +  image;
        Uri uri_1 = Uri.parse(URL_1);
        Picasso.get().load(uri_1).into(holder.iv_slide);
        Log.d("URL_Slide",URL_1);

        holder.iv_slide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, StickersCategoryActivity.class);
                intent.putExtra("pack_name", title);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != models ? models.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected ImageView iv_slide;
        protected TextView tv_slide;

        public SingleItemRowHolder(View view) {
            super(view);
            this.iv_slide = view.findViewById(R.id.iv_slide);
        }

    }

}