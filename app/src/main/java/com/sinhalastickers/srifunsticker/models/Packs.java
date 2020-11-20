package com.sinhalastickers.srifunsticker.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Packs {

    @SerializedName("packs")
    @Expose
    private ArrayList<Pack> packs = null;

    public ArrayList<Pack> getPacks() {
        return packs;
    }

    public void setPacks(ArrayList<Pack> packs) {
        this.packs = packs;
    }

}