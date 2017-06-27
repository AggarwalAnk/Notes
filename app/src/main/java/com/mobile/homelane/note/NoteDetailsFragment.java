package com.mobile.homelane.note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.homelane.NoteEventsListener;
import com.mobile.homelane.R;
import com.mobile.homelane.dao.Note;
import com.mobile.homelane.tasks.DeleteNoteTask;
import com.mobile.homelane.utils.CompressionUtility;

public class NoteDetailsFragment extends Fragment {

    public static final String TAG = NoteDetailsFragment.class.getName();

    public static final String NOTE = "NOTE";

    View rootView;
    ImageView image;
    TextView title;
    TextView content;
    Button editNote;
    Button deleteNote;

    NoteEventsListener listener;
    Note note;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_note_details, container, false);
        image = (ImageView) rootView.findViewById(R.id.image);
        title = (TextView) rootView.findViewById(R.id.title);
        content = (TextView) rootView.findViewById(R.id.content);
        editNote = (Button) rootView.findViewById(R.id.edit_note);
        deleteNote = (Button) rootView.findViewById(R.id.delete_note);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(NOTE)) {
            note = getArguments().getParcelable(NOTE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        title.setText(note.getTitle());
        content.setText(note.getContent());
        if (note.getImageLocalPath() != null) {
            new LoadCompressedImageTask().execute();
        } else {
            image.setVisibility(View.GONE);
        }

        deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DeleteNoteTask(getActivity(), note).execute();
                listener.pressBack();
            }
        });

        editNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEditNoteButtonClick(note);
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(note.getImageLocalPath()), "image/*");
                startActivity(intent);
            }
        });
    }

    private class LoadCompressedImageTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bitmap = CompressionUtility.getCompressedBitmap(note.getImageLocalPath(), getActivity());
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (isAdded() && !isRemoving()) {
                if (bitmap != null) {
                    image.setVisibility(View.VISIBLE);
                    image.setImageBitmap(bitmap);
                } else {
                    image.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NoteEventsListener) {
            listener = (NoteEventsListener) context;
        } else {
            throw new RuntimeException("Activity must implement NoteEventsListener Interface");
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof NoteEventsListener) {
            listener = (NoteEventsListener) activity;
        } else {
            throw new RuntimeException("Activity must implement NoteEventsListener Interface");
        }
    }
}
