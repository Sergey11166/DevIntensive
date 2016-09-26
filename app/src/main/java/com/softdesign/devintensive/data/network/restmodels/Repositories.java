package com.softdesign.devintensive.data.network.restmodels;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Repositories {

    private List<Repo> repo = new ArrayList<>();
    private String updated;

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
}