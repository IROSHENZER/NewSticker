package com.sinhalastickers.srifunsticker.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.sinhalastickers.srifunsticker.R;
import com.sinhalastickers.srifunsticker.StickersCategoryActivity;
import com.sinhalastickers.srifunsticker.models.Pack;

import java.util.ArrayList;

public class PackAdapter extends RecyclerView.Adapter<PackAdapter.ItemRowHolder> {

    private ArrayList<Pack> dataList;
    private Context mContext;
    private ProgressBar progress_bar_explore;
    private TextView loadingPack;
    private RecyclerView.RecycledViewPool recycledViewPool;

    public PackAdapter(Context context, ArrayList<Pack> dataList, ProgressBar progress_bar_explore,TextView loadingPack) {
        this.dataList = dataList;
        this.mContext = context;
        this.progress_bar_explore = progress_bar_explore;
        this.loadingPack = loadingPack;
        recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(ItemRowHolder itemRowHolder, int i) {

        final String sectionName = dataList.get(i).getTitle();
        final String sectionPublisher = dataList.get(i).getPublisher();

        ArrayList stickers = dataList.get(i).getStickers();

        itemRowHolder.itemTitle.setText(sectionName);

        StickerAdapter itemListDataAdapter = new StickerAdapter(mContext, stickers, dataList,i,progress_bar_explore,loadingPack);

        itemRowHolder.recycler_view_list.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false);

        itemRowHolder.recycler_view_list.setLayoutManager(linearLayoutManager);

        itemRowHolder.recycler_view_list.setHasFixedSize(true);
        itemRowHolder.recycler_view_list.setAdapter(itemListDataAdapter);

        itemRowHolder.recycler_view_list.setRecycledViewPool(recycledViewPool);

        new GravitySnapHelper(Gravity.START).attachToRecyclerView( itemRowHolder.recycler_view_list);


        itemRowHolder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, StickersCategoryActivity.class);
                intent.putExtra("pack_name", sectionName);
                intent.putExtra("pack_publisher", sectionPublisher);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

            }
        });


       /* Glide.with(mContext)
                .load(feedItem.getImageURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.bg)
                .into(feedListRowHolder.thumbView);*/
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView itemTitle;

        protected RecyclerView recycler_view_list;

        protected TextView btnMore;



        public ItemRowHolder(View view) {
            super(view);

            this.itemTitle = view.findViewById(R.id.itemTitle);
            this.recycler_view_list = view.findViewById(R.id.recycler_view_list);
            this.btnMore= view.findViewById(R.id.btnMore);


        }

    }

}