package com.softdesign.devintensive.data.network.restmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ProfileValues {

    @Expose
    @SerializedName("homeTask")
    private int homeTask;

    @Expose
    @SerializedName("projects")
    private int projects;

    @Expose
    @SerializedName("linesCode")
    private int linesCode;

    @Expose
    @SerializedName("rait")
    private int rating;

    @Expose
    @SerializedName("updated")
    private String updated;

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
