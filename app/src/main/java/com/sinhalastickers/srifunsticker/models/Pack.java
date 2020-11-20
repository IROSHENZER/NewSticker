package com.sinhalastickers.srifunsticker.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Pack {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("publisher")
    @Expose
    private String publisher;
    @SerializedName("publisher_website")
    @Expose
    private String publisherWebsite;
    @SerializedName("privacy_policy_website")
    @Expose
    private String privacyPolicyWebsite;
    @SerializedName("license_agreement_website")
    @Expose
    private String licenseAgreementWebsite;
    @SerializedName("stickers")
    @Expose
    private ArrayList<Sticker> stickers = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisherWebsite() {
        return publisherWebsite;
    }

    public void setPublisherWebsite(String publisherWebsite) {
        this.publisherWebsite = publisherWebsite;
    }

    public String getPrivacyPolicyWebsite() {
        return privacyPolicyWebsite;
    }

    public void setPrivacyPolicyWebsite(String privacyPolicyWebsite) {
        this.privacyPolicyWebsite = privacyPolicyWebsite;
    }

    public String getLicenseAgreementWebsite() {
        return licenseAgreementWebsite;
    }

    public void setLicenseAgreementWebsite(String licenseAgreementWebsite) {
        this.licenseAgreementWebsite = licenseAgreementWebsite;
    }

    public ArrayList<Sticker> getStickers() {
        return stickers;
    }

    public void setStickers(ArrayList<Sticker> stickers) {
        this.stickers = stickers;
    }

}