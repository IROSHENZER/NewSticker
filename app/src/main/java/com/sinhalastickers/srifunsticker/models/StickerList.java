package com.sinhalastickers.srifunsticker.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class StickerList implements Serializable {

    @SerializedName("total_stickers")
    @Expose
    private Integer totalStickers;
    @SerializedName("total_size")
    @Expose
    private String totalSize;
    @SerializedName("s_id")
    @Expose
    private String sId;
    @SerializedName("s_pack")
    @Expose
    private String sPack;
    @SerializedName("s_identifier")
    @Expose
    private String sIdentifier;
    @SerializedName("s_publisher")
    @Expose
    private String sPublisher;
    @SerializedName("s_publisher_website")
    @Expose
    private String sPublisherWebsite;
    @SerializedName("s_privacy_policy_website")
    @Expose
    private String sPrivacyPolicyWebsite;
    @SerializedName("s_license_agreement_website")
    @Expose
    private String sLicenseAgreementWebsite;
    @SerializedName("s_price")
    @Expose
    private String sPrice;
    @SerializedName("s_view")
    @Expose
    private String sView;
    @SerializedName("s_download")
    @Expose
    private String sDownload;
    @SerializedName("s_created")
    @Expose
    private String sCreated;
    @SerializedName("s_updated")
    @Expose
    private String sUpdated;
    @SerializedName("s_date")
    @Expose
    private String sDate;
    @SerializedName("s_tray_icons")
    @Expose
    private ArrayList<STrayIcon> sTrayIcons = null;
    @SerializedName("s_sticker_images")
    @Expose
    private ArrayList<SStickerImage> sStickerImages = null;

    public Integer getTotalStickers() {
        return totalStickers;
    }

    public void setTotalStickers(Integer totalStickers) {
        this.totalStickers = totalStickers;
    }

    public String getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;
    }

    public String getSId() {
        return sId;
    }

    public void setSId(String sId) {
        this.sId = sId;
    }

    public String getSPack() {
        return sPack;
    }

    public void setSPack(String sPack) {
        this.sPack = sPack;
    }

    public String getSIdentifier() {
        return sIdentifier;
    }

    public void setSIdentifier(String sIdentifier) {
        this.sIdentifier = sIdentifier;
    }

    public String getSPublisher() {
        return sPublisher;
    }

    public void setSPublisher(String sPublisher) {
        this.sPublisher = sPublisher;
    }

    public String getSPublisherWebsite() {
        return sPublisherWebsite;
    }

    public void setSPublisherWebsite(String sPublisherWebsite) {
        this.sPublisherWebsite = sPublisherWebsite;
    }

    public String getSPrivacyPolicyWebsite() {
        return sPrivacyPolicyWebsite;
    }

    public void setSPrivacyPolicyWebsite(String sPrivacyPolicyWebsite) {
        this.sPrivacyPolicyWebsite = sPrivacyPolicyWebsite;
    }

    public String getSLicenseAgreementWebsite() {
        return sLicenseAgreementWebsite;
    }

    public void setSLicenseAgreementWebsite(String sLicenseAgreementWebsite) {
        this.sLicenseAgreementWebsite = sLicenseAgreementWebsite;
    }

    public String getSPrice() {
        return sPrice;
    }

    public void setSPrice(String sPrice) {
        this.sPrice = sPrice;
    }

    public String getSView() {
        return sView;
    }

    public void setSView(String sView) {
        this.sView = sView;
    }

    public String getSDownload() {
        return sDownload;
    }

    public void setSDownload(String sDownload) {
        this.sDownload = sDownload;
    }

    public String getSCreated() {
        return sCreated;
    }

    public void setSCreated(String sCreated) {
        this.sCreated = sCreated;
    }

    public String getSUpdated() {
        return sUpdated;
    }

    public void setSUpdated(String sUpdated) {
        this.sUpdated = sUpdated;
    }

    public String getSDate() {
        return sDate;
    }

    public void setSDate(String sDate) {
        this.sDate = sDate;
    }

    public ArrayList<STrayIcon> getSTrayIcons() {
        return sTrayIcons;
    }

    public void setSTrayIcons(ArrayList<STrayIcon> sTrayIcons) {
        this.sTrayIcons = sTrayIcons;
    }

    public ArrayList<SStickerImage> getSStickerImages() {
        return sStickerImages;
    }

    public void setSStickerImages(ArrayList<SStickerImage> sStickerImages) {
        this.sStickerImages = sStickerImages;
    }

}