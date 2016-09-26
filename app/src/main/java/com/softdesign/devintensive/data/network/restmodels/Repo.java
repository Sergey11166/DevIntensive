package com.softdesign.devintensive.data.network.restmodels;

@SuppressWarnings("unused")
public class Repo {

    private String id;
    private String git;
    private String title;

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
}