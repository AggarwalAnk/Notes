package com.mobile.homelane.note;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.homelane.NoteActionsListener;
import com.mobile.homelane.R;
import com.mobile.homelane.dao.Note;
import com.mobile.homelane.utils.Utilities;

import java.util.List;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder>  {

    private Context context;
    private List<Note> noteList;
    private NoteActionsListener listener;
    private RecyclerView recyclerView;

    public NotesAdapter(Context context, List<Note> noteList, NoteActionsListener listener, RecyclerView recyclerView) {
        this.context = context;
        this.noteList = noteList;
        this.listener = listener;
        this.recyclerView = recyclerView;
    }

    public void updateNoteList(List<Note> noteList){
        this.noteList = noteList;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.list_item_note, parent, false);
        itemView.setOnClickListener(itemClickListener);
        return new NoteViewHolder(itemView);
    }

    private View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int itemPosition = recyclerView.getChildLayoutPosition(view);
            Note note = noteList.get(itemPosition);
            listener.onNoteClicked(note);
        }
    };

    @Override
    public void onBindViewHolder(final NoteViewHolder holder, int position) {
        final Note note = noteList.get(position);
        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());
        holder.createdTime.setText(Utilities.getReadableTime(note.getCreatedAtTime()));
        holder.moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, holder.moreOptions);
                popup.getMenuInflater().inflate(R.menu.menu_note_options,
                        popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.edit :
                                listener.onEditNoteClicked(note);
                                return true;

                            case R.id.delete :
                                listener.onDeleteNoteClicked(note);
                                return true;
                        }
                        return false;
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;
        TextView createdTime;
        ImageView moreOptions;

        NoteViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            content = (TextView) view.findViewById(R.id.content);
            createdTime = (TextView) view.findViewById(R.id.created_time);
            moreOptions = (ImageView) view.findViewById(R.id.more_options);
        }
    }
}
