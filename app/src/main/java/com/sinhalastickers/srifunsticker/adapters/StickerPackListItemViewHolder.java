package com.sinhalastickers.srifunsticker.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sinhalastickers.srifunsticker.R;

public class StickerPackListItemViewHolder extends RecyclerView.ViewHolder {


    View container;
    TextView titleView;
    TextView publisherView;
    TextView tv_added;
    public LinearLayout imageRowView;

    StickerPackListItemViewHolder(final View itemView) {
        super(itemView);
        container = itemView;
        titleView = itemView.findViewById(R.id.sticker_pack_title);
        tv_added = itemView.findViewById(R.id.tv_added);
        publisherView = itemView.findViewById(R.id.sticker_pack_publisher);
        imageRowView = itemView.findViewById(R.id.sticker_packs_list_item_image_list);
    }
}
