package com.softdesign.devintensive.data.network.restmodels;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("unused")
public class Repo implements Parcelable {

    private String id;
    private String git;
    private String title;

    public Repo() {
    }

    protected Repo(Parcel in) {
        id = in.readString();
        git = in.readString();
        title = in.readString();
    }

    public static final Creator<Repo> CREATOR = new Creator<Repo>() {
        @Override
        public Repo createFromParcel(Parcel in) {
            return new Repo(in);
        }

        @Override
        public Repo[] newArray(int size) {
            return new Repo[size];
        }
    };

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getGit() {
        return git;
    }
    public void setGit(String git) {
        this.git = git;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(git);
        dest.writeString(title);
    }
}