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

    public List<UserEntity> toUserEntityList() {
        List<UserEntity> result = new ArrayList<>(users.size());
        for (User user: users) result.add(new UserEntity(user));
        return result;
    }

    public List<User> getUsers() {
        return users;
    }
    public void setUsers(List<User> users) {
        this.users = users;
    }
}
