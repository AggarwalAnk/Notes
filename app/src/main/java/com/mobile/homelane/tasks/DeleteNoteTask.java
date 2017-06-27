package com.mobile.homelane.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mobile.homelane.db.NotesDataSource;
import com.mobile.homelane.dao.Note;

/**
 * Created by ankit on 27/06/17.
 */

public class DeleteNoteTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private Note note;

    public DeleteNoteTask(Context context, Note note) {
        this.context = context;
        this.note = note;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        NotesDataSource notesDataSource = NotesDataSource.getInstance(context);
        notesDataSource.deleteNote(note.getId());
        return null;
    }
}