package com.sinhalastickers.srifunsticker.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "sticker.db";
    public static final int DATABASE_VERSION = 1;

    SQLiteDatabase db= this.getWritableDatabase();

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE stickers ( _id INTEGER primary key AUTOINCREMENT,sid String, sidentifier String);");
        sqLiteDatabase.execSQL("CREATE TABLE stickerbook ( _id INTEGER primary key AUTOINCREMENT, identifier String, json Text);");
    }


    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS stickers");
        sqLiteDatabase.execSQL("CREATE TABLE stickers ( _id INTEGER primary key AUTOINCREMENT,sid String, sidentifier String);");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS stickerbook");
        sqLiteDatabase.execSQL("CREATE TABLE stickerbook ( _id INTEGER primary key AUTOINCREMENT, identifier String, json Text);");
    }

    public int getAllCount() {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT  * FROM stickers", null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public int getCountById(String sid) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM stickers where sid = " + sid, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public String getIdentifier (String sid){

        String sidentifier = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM stickers where sid = " + sid, null);
        if (cursor.moveToFirst()){
            do {
                sidentifier = cursor.getString(2);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return sidentifier;

    }

    public String getJson (){

        String TABLE = "stickerbook";
        String column = "_id";
        String KEY = "json";
        String selectQuery= "SELECT * FROM " + TABLE +" ORDER BY "+column+" DESC LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String str = "";
        if(cursor.moveToFirst())
            str  =  cursor.getString( cursor.getColumnIndex(KEY) );
        cursor.close();
        return str;

    }




}