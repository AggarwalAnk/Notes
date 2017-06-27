package com.mobile.homelane.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.mobile.homelane.dao.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankit on 27/06/17.
 */

public class NotesDataSource {
    private static NotesDataSource INSTANCE;

    private NotesDbHelper dbHelper;

    private NotesDataSource(@NonNull Context context) {
        dbHelper = new NotesDbHelper(context);
    }

    public static NotesDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new NotesDataSource(context);
        }
        return INSTANCE;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<Note>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query(
                NotesDbHelper.TABLE_NAME, null, null, null, null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                int id = c.getInt(c.getColumnIndexOrThrow(NotesDbHelper.COLUMN_NAME_ENTRY_ID));
                String title = c.getString(c.getColumnIndexOrThrow(NotesDbHelper.COLUMN_NAME_NOTE_TITLE));
                String description = c.getString(c.getColumnIndexOrThrow(NotesDbHelper.COLUMN_NAME_NOTE_CONTENT));
                String imagePath = c.getString(c.getColumnIndexOrThrow(NotesDbHelper.COLUMN_NAME_IMAGE_PATH));
                long createdAtTime = c.getLong(c.getColumnIndexOrThrow(NotesDbHelper.COLUMN_NAME_DATE_CREATED));
                Note note = new Note(id, title, description, imagePath, createdAtTime);
                notes.add(note);
            }
        }
        if (c != null) {
            c.close();
        }

        db.close();
        return notes;

    }

    public Note getNote(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = NotesDbHelper.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(id) };

        Cursor c = db.query(
                NotesDbHelper.TABLE_NAME, null, selection, selectionArgs, null, null, null);

        Note note = null;

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String title = c.getString(c.getColumnIndexOrThrow(NotesDbHelper.COLUMN_NAME_NOTE_TITLE));
            String description = c.getString(c.getColumnIndexOrThrow(NotesDbHelper.COLUMN_NAME_NOTE_CONTENT));
            String imagePath = c.getString(c.getColumnIndexOrThrow(NotesDbHelper.COLUMN_NAME_IMAGE_PATH));
            long createdAtTime = c.getLong(c.getColumnIndexOrThrow(NotesDbHelper.COLUMN_NAME_DATE_CREATED));
            note = new Note(id, title, description, imagePath, createdAtTime);
        }
        if (c != null) {
            c.close();
        }

        db.close();

        return note;
    }

    public void saveNote(@NonNull Note note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NotesDbHelper.COLUMN_NAME_NOTE_TITLE, note.getTitle());
        values.put(NotesDbHelper.COLUMN_NAME_NOTE_CONTENT, note.getContent());
        values.put(NotesDbHelper.COLUMN_NAME_IMAGE_PATH, note.getImageLocalPath());
        values.put(NotesDbHelper.COLUMN_NAME_DATE_CREATED, note.getCreatedAtTime());
        db.insert(NotesDbHelper.TABLE_NAME, null, values);
        db.close();
    }

    public void deleteNote(int noteId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = NotesDbHelper.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(noteId) };

        db.delete(NotesDbHelper.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

    public void updateNote(@NonNull Note note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NotesDbHelper.COLUMN_NAME_NOTE_TITLE, note.getTitle());
        values.put(NotesDbHelper.COLUMN_NAME_NOTE_CONTENT, note.getContent());
        values.put(NotesDbHelper.COLUMN_NAME_IMAGE_PATH, note.getImageLocalPath());

        String selection = NotesDbHelper.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(note.getId()) };

        db.update(NotesDbHelper.TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

}
