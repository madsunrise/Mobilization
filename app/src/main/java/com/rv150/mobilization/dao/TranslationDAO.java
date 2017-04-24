package com.rv150.mobilization.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rv150.mobilization.model.Translation;
import com.rv150.mobilization.utils.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivan on 24.04.17.
 */

public class TranslationDAO {
    private DBHelper dbHelper;

    private static TranslationDAO instance;

    private TranslationDAO(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public static TranslationDAO getInstance(Context context) {
        if (instance == null) {
            instance = new TranslationDAO(context);
        }
        return instance;
    }


    public List<Translation> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DBHelper.Translation.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        List<Translation> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            result.add(mapTranslation(cursor));
        }
        cursor.close();
        return result;
    }


    private Translation mapTranslation(final Cursor cursor) {
        long id = cursor.getLong(
                cursor.getColumnIndexOrThrow(DBHelper.Translation._ID));

        String from = cursor.getString(
                cursor.getColumnIndexOrThrow(DBHelper.Translation.COLUMN_NAME_FROM));

        String to = cursor.getString(
                cursor.getColumnIndexOrThrow(DBHelper.Translation.COLUMN_NAME_TO));

        boolean favorite = cursor.getInt(
                cursor.getColumnIndexOrThrow(DBHelper.Translation.COLUMN_NAME_FAVORITE)) != 0;

        Translation result = new Translation();
        result.setId(id);
        result.setFrom(from);
        result.setTo(to);
        result.setFavorite(favorite);
        return result;
    }


    public long insertTranslation(Translation translation) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBHelper.Translation.COLUMN_NAME_TO, translation.getTo());
        values.put(DBHelper.Translation.COLUMN_NAME_FROM, translation.getFrom());
        int favorite = translation.isFavorite() ? 1 : 0;
        values.put(DBHelper.Translation.COLUMN_NAME_FAVORITE, favorite);

        return db.insert(DBHelper.Translation.TABLE_NAME, null, values);
    }


    public void updateTranslation(Translation translation) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBHelper.Translation.COLUMN_NAME_TO, translation.getTo());
        values.put(DBHelper.Translation.COLUMN_NAME_FROM, translation.getFrom());
        int favorite = translation.isFavorite() ? 1 : 0;
        values.put(DBHelper.Translation.COLUMN_NAME_FAVORITE, favorite);

        db.update(DBHelper.Translation.TABLE_NAME, values,
                DBHelper.Translation._ID + " = ?", new String[]{String.valueOf(translation.getId())});
    }
}