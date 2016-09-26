package com.softdesign.devintensive.data.network.response;

import com.softdesign.devintensive.data.network.restmodels.UsedData;

/**
 * @author Sergey Vorobyev.
 */

@SuppressWarnings("unused")
public class UserModelResponse extends AbsResponse {

    private UsedData data;

    public UsedData getData() {
        return data;
    }
    public void setData(UsedData data) {
        this.data = data;
    }
}
