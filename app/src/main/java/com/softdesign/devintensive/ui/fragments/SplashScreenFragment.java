package com.softdesign.devintensive.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.chronos.operations.LoadUsersFromServerOperation;
import com.softdesign.devintensive.data.chronos.operations.LoadUsersFromServerOperation.Result;
import com.softdesign.devintensive.data.events.UserListResponseEvent;
import com.softdesign.devintensive.data.managers.DataManager;

import de.greenrobot.event.EventBus;

import static com.softdesign.devintensive.utils.Constants.LOG_TAG_PREFIX;

/**
 * @author Sergey Vorobyev.
 */

public class SplashScreenFragment extends BaseFragment {

    private static final String TAG = LOG_TAG_PREFIX + "SplashScreenFragment";
    public static final String FRAGMENT_TAG = "SplashScreenFragment";

    private DataManager mDataManager;

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
            runOperation(new LoadUsersFromServerOperation(), LoadUsersFromServerOperation.OPERATION_TAG);
            showProgress();
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

    @SuppressWarnings("unused")
    public void onOperationFinished(final Result result) {
        hideProgress();
        EventBus.getDefault().post(new UserListResponseEvent());
    }
}
