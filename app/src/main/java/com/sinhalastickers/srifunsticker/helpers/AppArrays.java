package com.sinhalastickers.srifunsticker.helpers;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.sinhalastickers.srifunsticker.R;

import java.util.ArrayList;

public class AppArrays {

    public static ArrayList<String> getFontArray(Context applicationContext) {
        try {
            ArrayList<String> fontList = new ArrayList<>();
            AssetManager assetManager = applicationContext.getAssets();
            String[] foldersFontsList = assetManager.list("All_Fonts");
//            String[] foldersFontsList = assetManager.list(applicationContext.getString(R.string.txt_assetsFontFolderName));
            Log.e("---------", "---foldersFontsList----" + foldersFontsList.length);

            if (foldersFontsList != null) {
                for (String fontName : foldersFontsList) {
                    fontList.add(applicationContext.getString(R.string.txt_assetsFontFolderName) + fontName);
                }
                return fontList;
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

}
