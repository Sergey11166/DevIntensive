package com.softdesign.devintensive.data.network.response;

import com.softdesign.devintensive.data.network.restmodels.ImageData;

/**
 * @author Sergey Vorobyev.
 */

@SuppressWarnings("unused")
public class ImageUploadedResponse extends AbsResponse {

    private ImageData data;

    public ImageData getData() {
        return data;
    }
}
