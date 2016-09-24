package com.softdesign.devintensive.data.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.softdesign.devintensive.data.network.restmodels.ImageData;

/**
 * @author Sergey Vorobyev.
 */

@SuppressWarnings("unused")
public class ImageUploadedResponse extends AbsResponse {

    @Expose
    @SerializedName("data")
    private ImageData data;

    public ImageData getData() {
        return data;
    }
    public void setData(ImageData data) {
        this.data = data;
    }
}
