package com.softdesign.devintensive.data.network.restmodels;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("unused")
public class PublicInfo implements Parcelable {

    private String bio;
    private String avatar;
    private String photo;
    private String updated;

    public PublicInfo() {
    }

    protected PublicInfo(Parcel in) {
        bio = in.readString();
        avatar = in.readString();
        photo = in.readString();
        updated = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bio);
        dest.writeString(avatar);
        dest.writeString(photo);
        dest.writeString(updated);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PublicInfo> CREATOR = new Creator<PublicInfo>() {
        @Override
        public PublicInfo createFromParcel(Parcel in) {
            return new PublicInfo(in);
        }

        @Override
        public PublicInfo[] newArray(int size) {
            return new PublicInfo[size];
        }
    };

    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUpdated() {
        return updated;
    }
    public void setUpdated(String updated) {
        this.updated = updated;
    }
}
