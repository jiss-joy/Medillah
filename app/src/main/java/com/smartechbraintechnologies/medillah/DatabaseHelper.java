package com.smartechbraintechnologies.medillah;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.database.Query;

import java.util.Queue;

public class DatabaseHelper extends SQLiteOpenHelper {

    //SQLite Database Helper

    private static final String DATABASE_NAME = "Medillah.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "searchHistory_table";
    private static final String COL_1 = "SEARCHES";
    private static final String COL_2 = "PRODUCT_ID";
    private static final String COL_3 = "TIME";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COL_1 + " TEXT," + COL_2 + " TEXT," + COL_3 + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void AddSearch(String search, String productId, String timeInMillis) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_1, search);
        contentValues.put(COL_2, productId);
        contentValues.put(COL_3, timeInMillis);
        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            Log.d("DATABASE MESSAGE", "SQLITE DATABASE INSERT ERROR");
        } else {
            Log.d("DATABASE MESSAGE", "SQLITE DATABASE INSERT SUCCESS");
        }
    }

    Cursor readData() {
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_3 + " DESC LIMIT 3";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    void updateSearch(String productID, String timeInMillis) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_3, timeInMillis);

        long result = db.update(TABLE_NAME, contentValues, "PRODUCT_ID = ?", new String[]{productID});
        if (result == -1) {
            Log.d("DATABASE MESSAGE", "SQLITE DATABASE UPDATE ERROR");
        } else {
            Log.d("DATABASE MESSAGE", "SQLITE DATABASE UPDATE SUCCESS");
        }
    }
}
