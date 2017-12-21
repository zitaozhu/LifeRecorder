package com.example.zhuxiaodong.liferecorder.DatabaseObjects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhuxiaodong on 2017/12/6.
 */

public class NoteItem implements Parcelable {
    public String title;
    public String content;
    public String location;

    public NoteItem(String title, String content, String location) {
        this.title = title;
        this.content = content;
        this.location = location;
    }

    public NoteItem() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.location);
    }

    protected NoteItem(Parcel in) {
        this.title = in.readString();
        this.content = in.readString();
        this.location = in.readString();
    }

    public static final Parcelable.Creator<NoteItem> CREATOR = new Parcelable.Creator<NoteItem>() {
        @Override
        public NoteItem createFromParcel(Parcel source) {
            return new NoteItem(source);
        }

        @Override
        public NoteItem[] newArray(int size) {
            return new NoteItem[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getLocation() {
        return location;
    }

    public static Creator<NoteItem> getCREATOR() {
        return CREATOR;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
