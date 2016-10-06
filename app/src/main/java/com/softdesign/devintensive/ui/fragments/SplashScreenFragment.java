package com.softdesign.devintensive.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.loaders.SaveUserEntityListLoader;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.response.UserListResponse;
import com.softdesign.devintensive.data.network.restmodels.User;
import com.softdesign.devintensive.data.storage.entities.UserEntity;
import com.softdesign.devintensive.ui.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.softdesign.devintensive.utils.Constants.LOG_TAG_PREFIX;
import static com.softdesign.devintensive.utils.NetworkStatusChecker.isNetworkAvailable;
import static com.softdesign.devintensive.utils.UIUtils.showToast;

/**
 * @author Sergey Vorobyev.
 */

public class SplashScreenFragment extends BaseFragment implements LoaderCallbacks<List<UserEntity>> {

    private static final String TAG = LOG_TAG_PREFIX + "SplashScreenFragment";
    public static final String FRAGMENT_TAG = "SplashScreenFragment";

    private DataManager mDataManager;
    private List<UserEntity> mLastUserEntityListFromServer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setRetainInstance(true);
        mDataManager = DataManager.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_splash_screen, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        if (!isAuthorized()) {
            Log.d(TAG, "User isn't authorized");
            FragmentManager fm = getActivity().getSupportFragmentManager();
            AuthFragment f = (AuthFragment) fm
                    .findFragmentByTag(AuthFragment.FRAGMENT_TAG);
            if (f == null) f = new AuthFragment();
            fm.beginTransaction()
                    .replace(R.id.container_start, f, AuthFragment.FRAGMENT_TAG)
                    .commit();
        } else {
            Log.d(TAG, "User is authorized");
            loadAllUsersFromServer();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    private boolean isAuthorized() {
        return !mDataManager.getPreferencesManager().getAuthToken().isEmpty();
    }

    private void startMainActivity() {
        startActivity(new Intent(getContext(), MainActivity.class));
        getActivity().finish();
    }

    private void loadAllUsersFromServer() {
        if (!isNetworkAvailable(getContext())) {
            showToast(getContext(), getString(R.string.error_no_connection));
            return;
        }
        showProgress();
        mDataManager.getUserListFromNetwork().enqueue(new Callback<UserListResponse>() {
            @Override
            public void onResponse(Call<UserListResponse> call, Response<UserListResponse> response) {
                hideProgress();
                if (response.isSuccessful()) {

                    List<User> userListResponse = response.body().getData().getUsers();
                    mLastUserEntityListFromServer = new ArrayList<>(userListResponse.size());
                    for (User user : userListResponse) {
                        UserEntity userEntity = new UserEntity(user);
                        mLastUserEntityListFromServer.add(userEntity);
                    }
                    saveUsersToDb();
                } else {
                    showToast(getContext(), getString(R.string.error_unknown_error));
                    startMainActivity();
                }
            }

            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
                hideProgress();
                showError(getString(R.string.error_unknown_error), t);
                startMainActivity();
            }
        });
    }

    @Override
    public Loader<List<UserEntity>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case R.id.loader_save_users_to_db:
                return new SaveUserEntityListLoader(getActivity(), mLastUserEntityListFromServer);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<UserEntity>> loader, List<UserEntity> data) {
        switch (loader.getId()) {
            case R.id.loader_save_users_to_db:
                startMainActivity();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<UserEntity>> loader) {}

    private void initLoader(int id) {
        getActivity().getSupportLoaderManager().restartLoader(id, Bundle.EMPTY, this).forceLoad();
    }

    private void saveUsersToDb() {
        initLoader(R.id.loader_save_users_to_db);
    }
}
