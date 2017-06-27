package com.mobile.homelane.dao;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ankit on 26/06/17.
 */

public class Note implements Parcelable{
    private int id;
    private String title;
    private String content;
    private String imageLocalPath;
    private long createdAtTime;

    public Note() {
    }

    public Note(String title, String content, String imageLocalPath, long createdAtTime) {
        this.title = title;
        this.content = content;
        this.imageLocalPath = imageLocalPath;
        this.createdAtTime = createdAtTime;
    }

    public Note(int id, String title, String content, String imageLocalPath, long createdAtTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageLocalPath = imageLocalPath;
        this.createdAtTime = createdAtTime;
    }

    protected Note(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
        imageLocalPath = in.readString();
        createdAtTime = in.readLong();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImageLocalPath() {
        return imageLocalPath;
    }

    public long getCreatedAtTime() {
        return createdAtTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImageLocalPath(String imageLocalPath) {
        this.imageLocalPath = imageLocalPath;
    }

    public void setCreatedAtTime(long createdAtTime) {
        this.createdAtTime = createdAtTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeString(imageLocalPath);
        parcel.writeLong(createdAtTime);
    }
}
