package com.softdesign.devintensive.data.network.restmodels;

import com.softdesign.devintensive.data.storage.entities.UserEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Vorobyev
 */

@SuppressWarnings("unused")
public class UserListData {

    private List<User> users;

    public List<User> getUsers() {
        return users;
    }
    public void setUsers(List<User> users) {
        this.users = users;
    }
}
