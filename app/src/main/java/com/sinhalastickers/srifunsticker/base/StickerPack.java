/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.sinhalastickers.srifunsticker.base;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.sinhalastickers.srifunsticker.helpers.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StickerPack implements Parcelable {
    Uri trayImageUri;
    String check;
    public String identifier;
    public String name;
    public String publisher;
    String trayImageFile;
    final String publisherEmail;
    final String publisherWebsite;
    final String privacyPolicyWebsite;
    final String licenseAgreementWebsite;

    String iosAppStoreLink;
    private List<Sticker> stickers;
    private long totalSize;
    String androidPlayStoreLink;
    private boolean isWhitelisted;
    private int stickersAddedIndex = 0;

    public StickerPack(String check,String identifier, String name, String publisher, Uri trayImageUri, String publisherEmail, String publisherWebsite, String privacyPolicyWebsite, String licenseAgreementWebsite, Context context) {
        this.androidPlayStoreLink = Constants.PLAY_STORE_URL;
        this.check = check;
        this.identifier = identifier;
        this.name = name;
        this.publisher = publisher;
        this.trayImageFile = "trayimage";
        this.trayImageUri = trayImageUri;
        this.publisherEmail = publisherEmail;
        this.publisherWebsite = publisherWebsite;
        this.privacyPolicyWebsite = privacyPolicyWebsite;
        this.licenseAgreementWebsite = licenseAgreementWebsite;
        this.stickers = new ArrayList<>();
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setIsWhitelisted(boolean isWhitelisted) {
        this.isWhitelisted = isWhitelisted;
    }

    public boolean getIsWhitelisted() {
        return isWhitelisted;
    }

    public String getCheck() {
        return check;
    }

    protected StickerPack(Parcel in) {
        identifier = in.readString();
        name = in.readString();
        publisher = in.readString();
        trayImageFile = in.readString();
        publisherEmail = in.readString();
        publisherWebsite = in.readString();
        privacyPolicyWebsite = in.readString();
        licenseAgreementWebsite = in.readString();
        iosAppStoreLink = in.readString();
        stickers = in.createTypedArrayList(Sticker.CREATOR);
        totalSize = in.readLong();
        androidPlayStoreLink = in.readString();
        isWhitelisted = in.readByte() != 0;
    }

    public static final Creator<StickerPack> CREATOR = new Creator<StickerPack>() {
        @Override
        public StickerPack createFromParcel(Parcel in) {
            return new StickerPack(in);
        }

        @Override
        public StickerPack[] newArray(int size) {
            return new StickerPack[size];
        }
    };

    public void addSticker(Uri uri, Context context){
        String index = String.valueOf(stickersAddedIndex);
        this.stickers.add(new Sticker(
                index,
                uri,
                new ArrayList<String>()));
        stickersAddedIndex++;
    }


    public void deleteSticker(Sticker sticker){
        new File(sticker.getUri().getPath()).delete();
        this.stickers.remove(sticker);
    }

    public Sticker getSticker(int index){
        return this.stickers.get(index);
    }

    public Sticker getStickerById(int index){
        for(Sticker s : this.stickers){
            if(s.getImageFileName().equals(String.valueOf(index))){
                return s;
            }
        }
        return null;
    }

    public void setAndroidPlayStoreLink(String androidPlayStoreLink) {
        this.androidPlayStoreLink = androidPlayStoreLink;
    }

    public void setIosAppStoreLink(String iosAppStoreLink) {
        this.iosAppStoreLink = iosAppStoreLink;
    }

    public List<Sticker> getStickers() {
        return stickers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(identifier);
        dest.writeString(name);
        dest.writeString(publisher);
        dest.writeString(trayImageFile);
        dest.writeString(publisherEmail);
        dest.writeString(publisherWebsite);
        dest.writeString(privacyPolicyWebsite);
        dest.writeString(licenseAgreementWebsite);
        dest.writeString(iosAppStoreLink);
        dest.writeTypedList(stickers);
        dest.writeLong(totalSize);
        dest.writeString(androidPlayStoreLink);
        dest.writeByte((byte) (isWhitelisted ? 1 : 0));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public Uri getTrayImageUri() {
        return trayImageUri;
    }

}
