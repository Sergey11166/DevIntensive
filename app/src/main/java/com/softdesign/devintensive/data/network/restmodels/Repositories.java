package com.softdesign.devintensive.data.network.restmodels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Repositories implements Parcelable{

    private List<Repo> repo = new ArrayList<>();
    private String updated;

    public Repositories() {
    }

    protected Repositories(Parcel in) {
        repo = in.createTypedArrayList(Repo.CREATOR);
        updated = in.readString();
    }

    public static final Creator<Repositories> CREATOR = new Creator<Repositories>() {
        @Override
        public Repositories createFromParcel(Parcel in) {
            return new Repositories(in);
        }

        @Override
        public Repositories[] newArray(int size) {
            return new Repositories[size];
        }
    };

    public List<Repo> getRepo() {
        return repo;
    }
    public void setRepo(List<Repo> repo) {
        this.repo = repo;
    }

    public String getUpdated() {
        return updated;
    }
    public void setUpdated(String updated) {
        this.updated = updated;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(repo);
        dest.writeString(updated);
    }
}