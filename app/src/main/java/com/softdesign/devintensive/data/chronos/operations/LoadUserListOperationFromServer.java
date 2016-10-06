package com.softdesign.devintensive.data.chronos.operations;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.response.UserListResponse;
import com.softdesign.devintensive.data.network.restmodels.User;
import com.softdesign.devintensive.data.storage.entities.DaoSession;
import com.softdesign.devintensive.data.storage.entities.RepositoryEntity;
import com.softdesign.devintensive.data.storage.entities.UserEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Vorobyev.
 */

public class LoadUserListOperationFromServer extends ChronosOperation<List<UserEntity>> {

    public static final String OPERATION_TAG = "LoadUserListOperationFromServer";

    private DataManager mDataManager;
    private DaoSession mDaoSession;

    public LoadUserListOperationFromServer() {
        mDataManager = DataManager.getInstance();
        mDaoSession = mDataManager.getDaoSession();
    }

    @Nullable
    @Override
    public List<UserEntity> run() {
        UserListResponse response;
        try {
            response = mDataManager.getUserListFromNetwork().execute().body();
            List<User> userListResponse = response.getData().getUsers();
            List<RepositoryEntity> repositoryEntityList = new ArrayList<>(userListResponse.size());
            List<UserEntity> userEntityList = new ArrayList<>(userListResponse.size());

            for (User user : userListResponse) {
                UserEntity userEntity = new UserEntity(user);
                userEntityList.add(userEntity);
                repositoryEntityList.addAll(userEntity.getRepositories());
            }

            mDaoSession.getRepositoryEntityDao().insertOrReplaceInTx(repositoryEntityList);
            mDaoSession.getUserEntityDao().insertOrReplaceInTx(userEntityList);
            return mDataManager.getAllUserListFromDb();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    @Override
    public Class<? extends ChronosOperationResult<List<UserEntity>>> getResultClass() {
        return Result.class;
    }

    public final static class Result extends ChronosOperationResult<List<UserEntity>> {}
}
