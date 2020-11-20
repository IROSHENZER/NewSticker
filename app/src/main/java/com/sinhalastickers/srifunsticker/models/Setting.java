package com.sinhalastickers.srifunsticker.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Setting {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("app_name")
    @Expose
    private String appName;
    @SerializedName("admob_banner_id")
    @Expose
    private String admobBannerId;
    @SerializedName("admob_interstitial_id")

    @Expose
    private String admobInterstitialId;
    @SerializedName("admob_status")
    @Expose
    private String admobStatus;
    @SerializedName("app_version")
    @Expose
    private String appVersion;
    @SerializedName("app_message")
    @Expose
    private String appMessage;
    @SerializedName("app_purchase_code")
    @Expose
    private String appPurchaseCode;
    @SerializedName("is_maintance")
    @Expose
    private String isMaintance;

    @SerializedName("admob_rewarded_id")
    @Expose
    private String admobRewardedId;

    public String getAdmobRewardedId() {
        return admobRewardedId;
    }


    public void setAdmobRewardedId(String admobRewardedId) {
        this.admobRewardedId = admobRewardedId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAdmobBannerId() {
        return admobBannerId;
    }

    public void setAdmobBannerId(String admobBannerId) {
        this.admobBannerId = admobBannerId;
    }

    public String getAdmobInterstitialId() {
        return admobInterstitialId;
    }

    public void setAdmobInterstitialId(String admobInterstitialId) {
        this.admobInterstitialId = admobInterstitialId;
    }

    public String getAdmobStatus() {
        return admobStatus;
    }

    public void setAdmobStatus(String admobStatus) {
        this.admobStatus = admobStatus;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppMessage() {
        return appMessage;
    }

    public void setAppMessage(String appMessage) {
        this.appMessage = appMessage;
    }

    public String getAppPurchaseCode() {
        return appPurchaseCode;
    }

    public void setAppPurchaseCode(String appPurchaseCode) {
        this.appPurchaseCode = appPurchaseCode;
    }

    public String getIsMaintance() {
        return isMaintance;
    }

    public void setIsMaintance(String isMaintance) {
        this.isMaintance = isMaintance;
    }

}
