package com.mobile.homelane.note;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mobile.homelane.NoteEventsListener;
import com.mobile.homelane.db.NotesDataSource;
import com.mobile.homelane.R;
import com.mobile.homelane.dao.Note;
import com.mobile.homelane.utils.CompressionUtility;
import com.mobile.homelane.utils.UriUtils;
import com.mobile.homelane.utils.Utilities;

import java.io.File;
import java.io.IOException;

public class CreateNoteFragment extends Fragment {

    public static final String FILE_NAME_PREFIX = "Note_";
    public static final String FILE_EXTENSION = ".jpg";

    public static final String EDIT_MODE = "EDIT_MODE";
    public static final String NOTE = "NOTE";

    public static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    public static final int REQUEST_CODE_REQUEST_CAMERA_PERMISSION = 2;
    public static final String TAG = CreateNoteFragment.class.getName();

    FloatingActionButton addImageButton;
    EditText title;
    EditText content;
    Button submitButton;
    ImageView image;
    View rootView;

    Uri imageUri;
    ProgressDialog progressDialog;
    NoteEventsListener listener;
    boolean editMode;
    Note addedNote;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(EDIT_MODE)) {
            editMode = bundle.getBoolean(EDIT_MODE);
            addedNote = bundle.getParcelable(NOTE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_create_note, container, false);
        addImageButton = (FloatingActionButton) rootView.findViewById(R.id.add_image_button);
        title = (EditText) rootView.findViewById(R.id.title);
        content = (EditText) rootView.findViewById(R.id.content);
        submitButton = (Button) rootView.findViewById(R.id.submit_button);
        image = (ImageView) rootView.findViewById(R.id.image);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (editMode) {
            title.setText(addedNote.getTitle());
            content.setText(addedNote.getContent());
            imageUri = Uri.parse(addedNote.getImageLocalPath());
            submitButton.setText(getString(R.string.update));
            new LoadCompressedImageTask().execute();
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.getText().length() == 0) {
                    Toast.makeText(getActivity(), getString(R.string.enter_valid_title), Toast.LENGTH_SHORT).show();
                } else if (content.getText().length() == 0) {
                    Toast.makeText(getActivity(), getString(R.string.enter_note_content), Toast.LENGTH_SHORT).show();
                } else {
                    if (!editMode) {
                        Note note = new Note(title.getText().toString().trim(), content.getText().toString(),
                                imageUri == null ? null : imageUri.toString(), System.currentTimeMillis());
                        new AddNoteTask(getActivity()).execute(note);
                    } else {
                        addedNote.setTitle(title.getText().toString().trim());
                        addedNote.setContent(content.getText().toString().trim());
                        addedNote.setImageLocalPath(imageUri.toString());
                        new UpdateNoteTask(getActivity()).execute(addedNote);
                    }
                }
            }
        });

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cameraPermission = ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA);
                int storagePermission = ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (cameraPermission == PackageManager.PERMISSION_GRANTED && storagePermission ==
                        PackageManager.PERMISSION_GRANTED) {
                    Intent intent = getIntentForImageCapture();
                    if (intent != null) {
                        startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE);
                    }
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_REQUEST_CAMERA_PERMISSION);

                }
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(imageUri, "image/*");
                startActivity(intent);
            }
        });

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.saving_note));
    }

    private class AddNoteTask extends AsyncTask<Note, Void, Void> {
        Context context;

        AddNoteTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            if (notes != null && notes.length > 0) {
                for (Note note : notes) {
                    NotesDataSource.getInstance(context).saveNote(note);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isAdded() && !isRemoving()) {
                progressDialog.dismiss();
                listener.onNoteSaved();
                Toast.makeText(getActivity(), getString(R.string.note_saved_successfully), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UpdateNoteTask extends AsyncTask<Note, Void, Void> {
        Context context;

        UpdateNoteTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            if (notes != null && notes.length > 0) {
                for (Note note : notes) {
                    NotesDataSource.getInstance(context).updateNote(note);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isAdded() && !isRemoving()) {
                progressDialog.dismiss();
                listener.onNoteSaved();
                Toast.makeText(getActivity(), getString(R.string.note_saved_successfully), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CAPTURE_IMAGE) {
            setupImage(data);
        }
    }

    public void setupImage(Intent data) {
        if (data == null) {
//            Image is captured from camera
        } else {
//            Image is picked from gallery
            imageUri = Utilities.getNewFileFromGalleryImage(getActivity(), UriUtils.getPath(getActivity(),
                    data.getData()));
        }
        new LoadCompressedImageTask().execute();
    }

    private class LoadCompressedImageTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bitmap = CompressionUtility.getCompressedBitmap(imageUri.toString(), getActivity());
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_REQUEST_CAMERA_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = getIntentForImageCapture();
                    if (intent != null) {
                        startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE);
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.give_camera_permission),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private Intent getIntentForImageCapture() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = null;
        try {
            imageFile = Utilities.createImageFile(getActivity(), FILE_NAME_PREFIX, FILE_EXTENSION);
        } catch (IOException ex) {
            Toast.makeText(getActivity(), getString(R.string.error_clicking_image, ex.getMessage()),
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error while capturing image. Unable to create new file " + ex.getMessage());
        }
        if (imageFile != null) {
            imageUri = Uri.fromFile(imageFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.select_image_from));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
            return chooserIntent;
        }
        return null;
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
