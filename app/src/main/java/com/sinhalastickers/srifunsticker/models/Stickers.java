package com.sinhalastickers.srifunsticker.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Stickers {

    @SerializedName("stickers")
    @Expose
    private ArrayList<Sticker> stickers = null;

    public ArrayList<Sticker> getStickers() {
        return stickers;
    }

    public void setStickers(ArrayList<Sticker> stickers) {
        this.stickers = stickers;
    }

}