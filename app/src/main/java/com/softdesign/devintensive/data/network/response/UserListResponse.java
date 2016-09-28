package com.softdesign.devintensive.data.network.response;

import com.softdesign.devintensive.data.network.restmodels.User;

import java.util.List;

/**
 * @author Sergey Vorobyev
 */

@SuppressWarnings("unused")
public class UserListResponse extends AbsResponse {

    private List<User> data;

    public List<User> getData() {
        return data;
    }
}
