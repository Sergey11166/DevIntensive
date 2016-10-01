package com.softdesign.devintensive.data.network.restmodels;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("unused")
public class User implements Parcelable {

    private String id;
    private String firstName;
    private String secondName;
    private int v;
    private Repositories repositories;
    private Contacts contacts;
    private ProfileValues profileValues;
    private PublicInfo publicInfo;
    private String specialization;
    private String role;
    private String updated;

    public User() {
    }

    protected User(Parcel in) {
        id = in.readString();
        firstName = in.readString();
        secondName = in.readString();
        v = in.readInt();
        repositories = in.readParcelable(Repositories.class.getClassLoader());
        contacts = in.readParcelable(Contacts.class.getClassLoader());
        profileValues = in.readParcelable(ProfileValues.class.getClassLoader());
        publicInfo = in.readParcelable(PublicInfo.class.getClassLoader());
        specialization = in.readString();
        role = in.readString();
        updated = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(firstName);
        dest.writeString(secondName);
        dest.writeInt(v);
        dest.writeParcelable(repositories, flags);
        dest.writeParcelable(contacts, flags);
        dest.writeParcelable(profileValues, flags);
        dest.writeParcelable(publicInfo, flags);
        dest.writeString(specialization);
        dest.writeString(role);
        dest.writeString(updated);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getId() {
        return id;
    }
    public void setId(String id) {
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
        return publicInfo;
    }
    public void setPublicInfo(PublicInfo publicInfo) {
        this.publicInfo = publicInfo;
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

    /**
     * Create object {@link User} with empty properties
     * @return {@link User}
     */
    public static User createEmptyUser() {
        User user = new User();
        PublicInfo publicInfo = new PublicInfo();
        Contacts constants = new Contacts();
        Repositories repositories = new Repositories();

        user.setPublicInfo(publicInfo);
        user.setContacts(constants);
        user.setRepositories(repositories);

        return user;
    }
}
