package com.mobile.homelane.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ankit on 27/06/17.
 */

public class NotesDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Notes.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String PRIMARY_KEY = "PRIMARY KEY";
    private static final String AUTO_INCREMENT = "AUTOINCREMENT";
    private static final String COMMA_SEP = ",";

    public static final String TABLE_NAME = "note";
    public static final String COLUMN_NAME_ENTRY_ID = "id";
    public static final String COLUMN_NAME_NOTE_TITLE = "title";
    public static final String COLUMN_NAME_NOTE_CONTENT = "content";
    public static final String COLUMN_NAME_IMAGE_PATH = "image_path";
    public static final String COLUMN_NAME_DATE_CREATED = "date_created";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_ENTRY_ID + INTEGER_TYPE + " " + PRIMARY_KEY + " " + AUTO_INCREMENT + COMMA_SEP +
                    COLUMN_NAME_NOTE_TITLE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_NOTE_CONTENT + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_IMAGE_PATH + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_DATE_CREATED + INTEGER_TYPE +
                    " )";

    public NotesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }
}
