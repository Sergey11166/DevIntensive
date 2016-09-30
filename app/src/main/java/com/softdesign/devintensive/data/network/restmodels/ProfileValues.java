package com.softdesign.devintensive.data.network.restmodels;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("unused")
public class ProfileValues implements Parcelable {

    private int homeTask;
    private int projects;
    private int linesCode;
    private int rating;
    private String updated;

    public ProfileValues() {
    }

    protected ProfileValues(Parcel in) {
        homeTask = in.readInt();
        projects = in.readInt();
        linesCode = in.readInt();
        rating = in.readInt();
        updated = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(homeTask);
        dest.writeInt(projects);
        dest.writeInt(linesCode);
        dest.writeInt(rating);
        dest.writeString(updated);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProfileValues> CREATOR = new Creator<ProfileValues>() {
        @Override
        public ProfileValues createFromParcel(Parcel in) {
            return new ProfileValues(in);
        }

        @Override
        public ProfileValues[] newArray(int size) {
            return new ProfileValues[size];
        }
    };

    public int getHomeTask() {
        return homeTask;
    }
    public void setHomeTask(int homeTask) {
        this.homeTask = homeTask;
    }

    public int getProjects() {
        return projects;
    }
    public void setProjects(int projects) {
        this.projects = projects;
    }

    public int getLinesCode() {
        return linesCode;
    }
    public void setLinesCode(int linesCode) {
        this.linesCode = linesCode;
    }

    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getUpdated() {
        return updated;
    }
    public void setUpdated(String updated) {
        this.updated = updated;
    }
}
