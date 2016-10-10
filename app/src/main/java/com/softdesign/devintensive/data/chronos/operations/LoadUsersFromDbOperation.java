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

public class LoadUsersFromDbOperation extends ChronosOperation<List<UserEntity>> {

    public static final String OPERATION_TAG = "LoadUsersFromDbOperation";

    @Nullable
    @Override
    public List<UserEntity> run() {
        return DataManager.getInstance().getAllUsersFromDb();
    }

    @NonNull
    @Override
    public Class<? extends ChronosOperationResult<List<UserEntity>>> getResultClass() {
        return Result.class;
    }

    public final static class Result extends ChronosOperationResult<List<UserEntity>> {}
}
