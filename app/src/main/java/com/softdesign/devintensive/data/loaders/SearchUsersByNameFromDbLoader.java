package com.softdesign.devintensive.data.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.entities.UserEntity;

import java.util.List;

/**
 * @author Sergey Vorobyev.
 */

public class SearchUsersByNameFromDbLoader extends AsyncTaskLoader<List<UserEntity>> {

    private String mQuery;

    public SearchUsersByNameFromDbLoader(Context context, String query) {
        super(context);
        mQuery = query;
    }

    @Override
    public List<UserEntity> loadInBackground() {
        return DataManager.getInstance().getUserListByNameFromDb(mQuery);
    }
}
