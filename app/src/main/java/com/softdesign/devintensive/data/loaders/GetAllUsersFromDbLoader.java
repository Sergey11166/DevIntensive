package com.softdesign.devintensive.data.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.entities.UserEntity;

import java.util.List;

/**
 * @author Sergey Vorobyev.
 */

public class GetAllUsersFromDbLoader extends AsyncTaskLoader<List<UserEntity>> {

    public GetAllUsersFromDbLoader(Context context) {
        super(context);
    }

    @Override
    public List<UserEntity> loadInBackground() {
        return DataManager.getInstance().getAllUserListFromDb();
    }
}
