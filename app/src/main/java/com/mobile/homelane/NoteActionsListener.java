package com.mobile.homelane;

import com.mobile.homelane.dao.Note;

/**
 * Created by ankit on 27/06/17.
 */

public interface NoteActionsListener {
    void onEditNoteClicked(Note note);
    void onDeleteNoteClicked(Note note);
    void onNoteClicked(Note note);
}
