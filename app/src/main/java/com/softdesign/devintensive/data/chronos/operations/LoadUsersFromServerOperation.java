package com.softdesign.devintensive.data.chronos.operations;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.entities.UserEntity;
import com.softdesign.devintensive.utils.Constants;

import java.util.List;

import rx.Observable;
import rx.Subscription;

import static com.softdesign.devintensive.utils.UIUtils.handleResponseError;

/**
 * @author Sergey Vorobyev.
 */

public class LoadUsersFromServerOperation extends ChronosOperation<Void> {

    public static final String TAG = Constants.LOG_TAG_PREFIX + "LoadUsersFromServerOperation";
    public static final String OPERATION_TAG = "LoadUsersFromServerOperation";

    private Subscription mSubscription;
    private DataManager mDataManager;
    private Handler mHandler;
    private int count;

    public LoadUsersFromServerOperation() {
        mDataManager = DataManager.getInstance();
        mHandler = new Handler();
    }

    @Nullable
    @Override
    public Void run() {
        if (mDataManager.getDaoSession().getUserEntityDao().count() == 0) {
            loadUsersFromServer();
        }
        return null;
    }

    private int getCount() {
        return count++;
    }

    private void loadUsersFromServer() {
        count = 0;
        mSubscription = mDataManager.getUserListFromNetwork()
                .flatMap(response -> Observable.from(response.getData().getUsers()))
                .flatMap(user -> Observable.just(new UserEntity(user, getCount())))
                .toList()
                .subscribe(this::saveUsersToDb, this::handleError, () -> mSubscription.unsubscribe());
    }

    private void saveUsersToDb(List<UserEntity> userEntityList) {
        mDataManager.saveUsersToDb(userEntityList);
    }

    private void handleError(Throwable throwable) {
        mSubscription.unsubscribe();
        handleResponseError(mHandler, TAG, throwable);
    }

    @NonNull
    @Override
    public Class<? extends ChronosOperationResult<Void>> getResultClass() {
        return Result.class;
    }

    public final static class Result extends ChronosOperationResult<Void> {}
}
