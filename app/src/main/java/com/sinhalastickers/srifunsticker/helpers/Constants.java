package com.sinhalastickers.srifunsticker.helpers;

import com.sinhalastickers.srifunsticker.BuildConfig;

public class Constants {

    public static final String BASE_URL = "https://sticker.karawitacc.lk/";
    public static final String PATH_URL = "/";

    /*public static final String BASE_URL = "http://192.168.1.42:8080/";
    public static final String PATH_URL = "pre-sticker-app/";*/


    public static final String PURCHASE_CODE = "8126acd4-3441-4368-f31f-8fy688f8462a";

    //DONT EDIT
    public static final String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
    public static final String STICKER_URL = PATH_URL+"index.php?r=api";
    public static final int FirstLoad = 18 ;

    public static final String IMG_URL = BASE_URL + PATH_URL+"upload/";
    public static final String STICKER_QUERY_URL = PATH_URL+"index.php?r=api/GetStickersByQuery";
    public static final String STICKER_SLIDE_URL = PATH_URL+"index.php?r=api/slides";
    public static final String APP_SETTINGS = PATH_URL+"index.php?r=api/initial";

}
