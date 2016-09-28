package com.softdesign.devintensive.data.network.restmodels;

import java.util.List;

/**
 * @author Sergey Vorobyev
 */

@SuppressWarnings("unused")
public class UserData {

    private List<User> data;

    public List<User> getData() {
        return data;
    }
    public void setData(List<User> data) {
        this.data = data;
    }
}
