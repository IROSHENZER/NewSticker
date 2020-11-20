package com.sinhalastickers.srifunsticker.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Sticker {

    @SerializedName("publisher")
    @Expose
    private String publisher;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("identifier")
    @Expose
    private String identifier;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("premium")
    @Expose
    private int premium;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("views")
    @Expose
    private String views;
    @SerializedName("sticker_files")
    @Expose
    private ArrayList<Files> files = null;
    @SerializedName("tray_icon")
    @Expose
    private ArrayList<Tray> trays = null;

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPremium() {
        return premium;
    }

    public void setPremium(int premium) {
        this.premium = premium;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public ArrayList<Files> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<Files> files) {
        this.files = files;
    }

    public ArrayList<Tray> getTrays() {
        return trays;
    }

    public void setTrays(ArrayList<Tray> trays) {
        this.trays = trays;
    }

}