package com.softdesign.devintensive.data.network.restmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Sergey Vorobyev.
 */

@SuppressWarnings("unused")
public class ImageData {

    @Expose
    @SerializedName("photo")
    private String photo;

    @Expose
    @SerializedName("updated")
    private String updated;

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
