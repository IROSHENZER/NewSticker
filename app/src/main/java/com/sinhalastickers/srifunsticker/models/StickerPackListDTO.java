package com.sinhalastickers.srifunsticker.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class StickerPackListDTO {

    @SerializedName("list")
    @Expose
    private ArrayList<StickerList> list = null;

    public ArrayList<StickerList> getList() {
        return list;
    }

    public void setList(ArrayList<StickerList> list) {
        this.list = list;
    }

}