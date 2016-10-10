package com.softdesign.devintensive.data.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.softdesign.devintensive.data.network.restmodels.Repo;
import com.softdesign.devintensive.data.network.restmodels.User;
import com.softdesign.devintensive.data.storage.entities.RepositoryEntity;
import com.softdesign.devintensive.data.storage.entities.UserEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Vorobyev.
 */
@SuppressWarnings("unused")
public class UserDTO implements Parcelable {

    private String id;
    private String fullName;
    private int rating;
    private int countCodeLines;
    private int countProjects;
    private String bio;
    private String photo;
    private List<String> repos;

    public UserDTO() {
    }

    public UserDTO(User user) {
        id = user.getId();
        fullName = user.getFirstName() + " " + user.getSecondName();
        rating = user.getProfileValues().getRating();
        countCodeLines = user.getProfileValues().getLinesCode();
        countProjects = user.getProfileValues().getProjects();
        bio = user.getPublicInfo().getBio();
        photo = user.getPublicInfo().getPhoto();
        repos = new ArrayList<>(user.getRepositories().getRepo().size());
        for (Repo repo : user.getRepositories().getRepo()) repos.add(repo.getGit());
    }

    public UserDTO(UserEntity entity) {
        id = entity.getRemoteId();
        fullName = entity.getFullName();
        rating = entity.getRating();
        countCodeLines = entity.getCountCodeLines();
        countProjects = entity.getCountProjects();
        bio = entity.getBio();
        photo = entity.getPhoto();
        repos = new ArrayList<>(entity.getRepositories().size());
        for (RepositoryEntity r : entity.getRepositories()) repos.add(r.getRepositoryName());
    }

    protected UserDTO(Parcel in) {
        id = in.readString();
        fullName = in.readString();
        rating = in.readInt();
        countCodeLines = in.readInt();
        countProjects = in.readInt();
        bio = in.readString();
        photo = in.readString();
        repos = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(fullName);
        dest.writeInt(rating);
        dest.writeInt(countCodeLines);
        dest.writeInt(countProjects);
        dest.writeString(bio);
        dest.writeString(photo);
        dest.writeStringList(repos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserDTO> CREATOR = new Creator<UserDTO>() {
        @Override
        public UserDTO createFromParcel(Parcel in) {
            return new UserDTO(in);
        }

        @Override
        public UserDTO[] newArray(int size) {
            return new UserDTO[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getCountCodeLines() {
        return countCodeLines;
    }

    public void setCountCodeLines(int countCodeLines) {
        this.countCodeLines = countCodeLines;
    }

    public int getCountProjects() {
        return countProjects;
    }

    public void setCountProjects(int countProjects) {
        this.countProjects = countProjects;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<String> getRepos() {
        return repos;
    }

    public void setRepos(List<String> repos) {
        this.repos = repos;
    }
}
