package com.softdesign.devintensive.data.chronos.operations;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.entities.UserEntity;

import java.util.List;

/**
 * @author Sergey Vorobyev.
 */

public class DeleteUsersIntoDbOperation extends ChronosOperation<Void> {

    public static final String OPERATION_TAG = "DeleteUsersIntoDbOperation";

    private List<UserEntity> mUserEntityList;
    private DataManager mDataManager;

    public DeleteUsersIntoDbOperation(List<UserEntity> userEntityList) {
        mUserEntityList = userEntityList;
        mDataManager = DataManager.getInstance();
    }

    @Nullable
    @Override
    public Void run() {
        mDataManager.removeUsersFromDb(mUserEntityList);
        return null;
    }

    @NonNull
    @Override
    public Class<? extends ChronosOperationResult<Void>> getResultClass() {
        return Result.class;
    }

    public final static class Result extends ChronosOperationResult<Void> {}
}
