package com.rv150.mobilization.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by ivan on 24.04.17.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;

    public static class Translation implements BaseColumns {
        public static final String TABLE_NAME = "translate";
        public static final String COLUMN_NAME_FROM = "from";
        public static final String COLUMN_NAME_TO = "to";
        public static final String COLUMN_NAME_FAVORITE = "favorite";
    }

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Translator.db";

    private static final String SQL_CREATE_TRANSLATE_TABLE =
            "CREATE TABLE " + Translation.TABLE_NAME + " (" +
                    Translation._ID + " INTEGER PRIMARY KEY," +
                    Translation.COLUMN_NAME_FROM + " TEXT NOT NULL," +
                    Translation.COLUMN_NAME_TO + " TEXT NOT NULL," +
                    Translation.COLUMN_NAME_FAVORITE + " INTEGER DEFAULT 0)";

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TRANSLATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}