package com.softdesign.devintensive.data.chronos.operations;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.response.UserListResponse;
import com.softdesign.devintensive.data.network.restmodels.User;
import com.softdesign.devintensive.data.storage.entities.UserEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Vorobyev.
 */

public class LoadUsersFromServerOperation extends ChronosOperation<Void> {

    public static final String OPERATION_TAG = "LoadUsersFromServerOperation";

    private DataManager mDataManager;

    public LoadUsersFromServerOperation() {
        mDataManager = DataManager.getInstance();
    }

    @Nullable
    @Override
    public Void run() {
        if (mDataManager.getDaoSession().getUserEntityDao().count() == 0) {
            mDataManager.saveUsersToDb(loadUsersFromServer());
        }
        return null;
    }

    private List<UserEntity> loadUsersFromServer() {
        List<UserEntity> result = new ArrayList<>();
        try {
            UserListResponse response = mDataManager.getUserListFromNetwork().execute().body();
            List<User> users = response.getData().getUsers();
            for (User user : users)
                result.add(new UserEntity(user, users.indexOf(user)));
        } catch (IOException ignored) {}
        return result;
    }

    @NonNull
    @Override
    public Class<? extends ChronosOperationResult<Void>> getResultClass() {
        return Result.class;
    }

    public final static class Result extends ChronosOperationResult<Void> {}
}
