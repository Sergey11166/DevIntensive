package com.softdesign.devintensive.data.network.restmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class User {
    @Expose
    @SerializedName("_id")
    private int id;

    @Expose
    @SerializedName("firstName")
    private String firstName;

    @Expose
    @SerializedName("secondName")
    private String secondName;

    @Expose
    @SerializedName("__v")
    private int v;

    @Expose
    @SerializedName("repositories")
    private Repositories repositories;

    @Expose
    @SerializedName("contacts")
    private Contacts contacts;

    @Expose
    @SerializedName("profileValues")
    private ProfileValues profileValues;

    @Expose
    @SerializedName("publicInfo")
    private PublicInfo PublicInfo;

    @Expose
    @SerializedName("specialization")
    private String specialization;

    @Expose
    @SerializedName("role")
    private String role;

    @Expose
    @SerializedName("updated")
    private String updated;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }
    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public int getV() {
        return v;
    }
    public void setV(int v) {
        this.v = v;
    }

    public Repositories getRepositories() {
        return repositories;
    }
    public void setRepositories(Repositories repositories) {
        this.repositories = repositories;
    }

    public Contacts getContacts() {
        return contacts;
    }
    public void setContacts(Contacts contacts) {
        this.contacts = contacts;
    }

    public ProfileValues getProfileValues() {
        return profileValues;
    }
    public void setProfileValues(ProfileValues profileValues) {
        this.profileValues = profileValues;
    }

    public PublicInfo getPublicInfo() {
        return PublicInfo;
    }
    public void setPublicInfo(PublicInfo publicInfo) {
        PublicInfo = publicInfo;
    }

    public String getSpecialization() {
        return specialization;
    }
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getUpdated() {
        return updated;
    }
    public void setUpdated(String updated) {
        this.updated = updated;
    }
}
