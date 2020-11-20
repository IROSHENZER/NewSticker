package com.sinhalastickers.srifunsticker.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.sinhalastickers.srifunsticker.base.StickerPack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataArchiver {

    private static int BUFFER = 8192;

    public static boolean writeStickerBookJSON(List<StickerPack> sb, Context context)
    {
        try {
            SharedPreferences mSettings = context.getSharedPreferences("StickerMaker", Context.MODE_PRIVATE);

            DBHelper dbHelper;
            dbHelper = new DBHelper(context);

            String writeValue = new GsonBuilder()
                    .registerTypeAdapter(Uri.class, new UriSerializer())
                    .create()
                    .toJson(
                    sb,
                    new TypeToken<ArrayList<StickerPack>>() {}.getType());
            SharedPreferences.Editor mEditor = mSettings.edit();
            mEditor.putString("stickerbook", writeValue);
            mEditor.apply();

            String data_identifier = "";
            String data_json = writeValue;
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues datas = new ContentValues();
            datas.put("identifier", data_identifier);
            datas.put("json", data_json);

            long result_ = db.insert("stickerbook",null,datas);

            return result_ != -1;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    public static ArrayList<StickerPack> readStickerPackJSON(Context context)
    {
        SharedPreferences mSettings = context.getSharedPreferences("StickerMaker", Context.MODE_PRIVATE);

        String loadValue = mSettings.getString("stickerbook", "");

        /*DBHelper dbHelper;
        dbHelper = new DBHelper(context);
        String loadValue = dbHelper.getJson();*/

        Log.d("stickerbook",loadValue);

        Type listType = new TypeToken<ArrayList<StickerPack>>(){}.getType();
        return new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriDeserializer())
                .create()
                .fromJson(loadValue, listType);
    }


    public static class UriSerializer implements JsonSerializer<Uri> {
        public JsonElement serialize(Uri src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    public static class UriDeserializer implements JsonDeserializer<Uri> {
        @Override
        public Uri deserialize(final JsonElement src, final Type srcType,
                               final JsonDeserializationContext context) throws JsonParseException {
            return Uri.parse(src.toString().replace("\"", ""));
        }
    }


}
