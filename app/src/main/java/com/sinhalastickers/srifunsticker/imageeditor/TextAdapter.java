package com.sinhalastickers.srifunsticker.imageeditor;

import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sinhalastickers.srifunsticker.R;

import java.util.ArrayList;

public class TextAdapter extends RecyclerView.Adapter<TextAdapter.MyViewHolder> {

    public OnClickFont onClickLIstner;
    Context context;
    private ArrayList<String> fontArray;

    public TextAdapter(Context context, ArrayList<String> fontArray) {
        this.context = context;
        this.fontArray = fontArray;
    }

    public void setOnClickLIstner(OnClickFont onClickLIstner) {
        this.onClickLIstner = onClickLIstner;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_font_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        holder.textView.setText("Fonts");
        holder.textView.setTypeface(Typeface.createFromAsset(context.getAssets(), fontArray.get(listPosition)));
    }

    @Override
    public int getItemCount() {
        return fontArray.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onClickLIstner != null) {
                onClickLIstner.onFontClick(v, fontArray.get(getAdapterPosition()), getAdapterPosition());
            }
        }
    }
}