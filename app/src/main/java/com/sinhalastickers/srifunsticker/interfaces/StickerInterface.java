package com.sinhalastickers.srifunsticker.interfaces;

import com.sinhalastickers.srifunsticker.helpers.Constants;
import com.sinhalastickers.srifunsticker.models.Packs;
import com.sinhalastickers.srifunsticker.models.Settings;
import com.sinhalastickers.srifunsticker.models.Slides;
import com.sinhalastickers.srifunsticker.models.Sticker;
import com.sinhalastickers.srifunsticker.models.StickerPackListDTO;
import com.sinhalastickers.srifunsticker.models.Stickers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StickerInterface {

    @GET(Constants.STICKER_URL)
    Call<StickerPackListDTO> getStickers(@Query("limit") String limit);

    @GET(Constants.STICKER_URL)
    Call<StickerPackListDTO> getStickersEndless(@Query("offset") String id,@Query("limit") String limit);

    @GET(Constants.STICKER_URL)
    Call<Sticker> getStickers();

    @GET(Constants.STICKER_URL)
    Call<Packs> getAllPacks();

    @GET(Constants.STICKER_QUERY_URL)
    Call<Stickers> getCatStickers(@Query("query") String query);

    @GET(Constants.STICKER_SLIDE_URL)
    Call<Slides> getAllSticker();

    @GET(Constants.APP_SETTINGS)
    Call<Settings> getSetting(@Query("purchase") String purchase,@Query("identity") String identity);


}
