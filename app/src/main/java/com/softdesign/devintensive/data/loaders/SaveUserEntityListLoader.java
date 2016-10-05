package com.softdesign.devintensive.data.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.entities.DaoSession;
import com.softdesign.devintensive.data.storage.entities.RepositoryEntity;
import com.softdesign.devintensive.data.storage.entities.UserEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Vorobyev.
 */

public class SaveUserEntityListLoader extends AsyncTaskLoader<List<UserEntity>> {

    private List<UserEntity> mUserEntityList;
    private List<RepositoryEntity> mRepositoryEntityList;
    private DaoSession mDaoSession;

    public SaveUserEntityListLoader(Context context, List<UserEntity> userEntityList) {
        super(context);
        mDaoSession = DataManager.getInstance().getDaoSession();
        mUserEntityList = userEntityList;

        mRepositoryEntityList = new ArrayList<>(userEntityList.size());
        for (UserEntity userEntity : userEntityList) {
            mRepositoryEntityList.addAll(userEntity.getRepositories());
        }
    }

    @Override
    public List<UserEntity> loadInBackground() {
        mDaoSession.getRepositoryEntityDao().insertOrReplaceInTx(mRepositoryEntityList);
        mDaoSession.getUserEntityDao().insertOrReplaceInTx(mUserEntityList);
        return DataManager.getInstance().getAllUserListFromDb();
    }
}
