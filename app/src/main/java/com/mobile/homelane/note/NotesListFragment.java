package com.mobile.homelane.note;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mobile.homelane.NoteActionsListener;
import com.mobile.homelane.NoteEventsListener;
import com.mobile.homelane.db.NotesDataSource;
import com.mobile.homelane.R;
import com.mobile.homelane.dao.Note;
import com.mobile.homelane.tasks.DeleteNoteTask;

import java.util.List;

public class NotesListFragment extends Fragment implements NoteActionsListener{

    public static final String TAG = NotesListFragment.class.getName();

    View rootView;
    View addNewNoteButton;

    NoteEventsListener listener;
    List<Note> noteList;
    NotesAdapter notesAdapter;
    RecyclerView notesRecyclerView;

    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notes_list, container, false);

        addNewNoteButton = rootView.findViewById(R.id.add_new_note);
        notesRecyclerView = (RecyclerView) rootView.findViewById(R.id.notes_list);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addNewNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.addNewNote();
            }
        });

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.fetching_notes));
        progressDialog.show();

        new FetchNotesTask(getActivity()).execute();
    }

    @Override
    public void onEditNoteClicked(Note note) {
        listener.onEditNoteButtonClick(note);
    }

    @Override
    public void onDeleteNoteClicked(Note note) {
        new DeleteNoteTask(getActivity(), note).execute();
        int position = noteList.indexOf(note);
        noteList.remove(note);
        notesAdapter.updateNoteList(noteList);
        notesAdapter.notifyItemRemoved(position);
        Toast.makeText(getActivity(), getString(R.string.note_deleted_successfully), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNoteClicked(Note note) {
        listener.viewNoteDetails(note);
    }

    private class FetchNotesTask extends AsyncTask<Void, Void, Void>{
        Context context;

        public FetchNotesTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            NotesDataSource notesDataSource = NotesDataSource.getInstance(context);
            noteList = notesDataSource.getAllNotes();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(isAdded() && !isRemoving()) {
                progressDialog.dismiss();
                if (notesAdapter == null) {
                    notesAdapter = new NotesAdapter(context, noteList, NotesListFragment.this, notesRecyclerView);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                    notesRecyclerView.setLayoutManager(mLayoutManager);
                    notesRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    notesRecyclerView.setAdapter(notesAdapter);
                }else {
                    notesAdapter.updateNoteList(noteList);
                    notesAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public void refreshList(){
        progressDialog.show();
        new FetchNotesTask(getActivity()).execute();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof NoteEventsListener){
            listener = (NoteEventsListener) context;
        }else {
            throw new RuntimeException("Activity must implement NoteEventsListener Interface");
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof NoteEventsListener){
            listener = (NoteEventsListener) activity;
        }else {
            throw new RuntimeException("Activity must implement NoteEventsListener Interface");
        }
    }
}
