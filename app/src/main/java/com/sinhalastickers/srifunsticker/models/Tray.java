package com.sinhalastickers.srifunsticker.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tray {

    @SerializedName("images")
    @Expose
    private String images;

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

}
