package com.softdesign.devintensive.data.network.response;

import com.softdesign.devintensive.data.network.restmodels.UserListData;

/**
 * @author Sergey Vorobyev
 */

@SuppressWarnings("unused")
public class UserListResponse extends AbsResponse {

    private UserListData data;

    public UserListData getData() {
        return data;
    }
}
