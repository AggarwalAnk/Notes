package com.mobile.homelane;

import com.mobile.homelane.dao.Note;

/**
 * Created by ankit on 27/06/17.
 */

public interface NoteEventsListener {
    void addNewNote();
    void pressBack();
    void onNoteSaved();
    void onNoteDeleted(Note note);
    void onEditNoteButtonClick(Note note);
    void viewNoteDetails(Note note);
}
