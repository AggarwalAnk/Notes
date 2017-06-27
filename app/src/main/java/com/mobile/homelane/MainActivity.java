package com.mobile.homelane;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.mobile.homelane.dao.Note;
import com.mobile.homelane.note.CreateNoteFragment;
import com.mobile.homelane.note.NoteDetailsFragment;
import com.mobile.homelane.note.NotesListFragment;

public class MainActivity extends AppCompatActivity implements NoteEventsListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, new NotesListFragment(),
                NotesListFragment.TAG).commitAllowingStateLoss();
    }

    private void refreshNotes(){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(NotesListFragment.TAG);
        if (fragment != null && fragment instanceof NotesListFragment) {
            final NotesListFragment notesListFragment = (NotesListFragment) fragment;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    notesListFragment.refreshList();
                }
            }, 500);
        }
    }

    @Override
    public void addNewNote() {
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, new CreateNoteFragment())
                .addToBackStack(NotesListFragment.TAG).commitAllowingStateLoss();
    }

    @Override
    public void pressBack() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
            refreshNotes();
        }
    }

    private void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry firstEntry = manager.getBackStackEntryAt(0);
            manager.popBackStack(firstEntry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void onNoteSaved() {
        pressBack();
    }

    @Override
    public void onNoteDeleted(Note note) {
        refreshNotes();
    }

    @Override
    public void onEditNoteButtonClick(Note note) {
        clearBackStack();
        CreateNoteFragment fragment = new CreateNoteFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(CreateNoteFragment.NOTE, note);
        bundle.putBoolean(CreateNoteFragment.EDIT_MODE, true);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, fragment)
                .addToBackStack(CreateNoteFragment.TAG).commitAllowingStateLoss();
    }

    @Override
    public void viewNoteDetails(Note note) {
        NoteDetailsFragment fragment = new NoteDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(NoteDetailsFragment.NOTE, note);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, fragment)
                .addToBackStack(NoteDetailsFragment.TAG).commitAllowingStateLoss();
    }
}
