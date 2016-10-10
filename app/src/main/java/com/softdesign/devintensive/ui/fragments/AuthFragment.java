package com.softdesign.devintensive.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.request.UserLoginRequest;
import com.softdesign.devintensive.data.network.response.AuthResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.softdesign.devintensive.utils.Constants.LOG_TAG_PREFIX;
import static com.softdesign.devintensive.utils.NavUtils.goToUrl;
import static com.softdesign.devintensive.utils.NetworkStatusChecker.isNetworkAvailable;
import static com.softdesign.devintensive.utils.UIUtils.handleResponseError;
import static com.softdesign.devintensive.utils.UIUtils.showToast;

/**
 * @author Sergey Vorobyev.
 */

public class AuthFragment extends BaseFragment {

    private static final String TAG = LOG_TAG_PREFIX + "AuthFragment";
    public static final String FRAGMENT_TAG = "AuthFragment";

    @BindView(R.id.username_et) EditText mUsernameET;
    @BindView(R.id.password_et) EditText mPasswordET;
    private Unbinder mUnbinder;

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
        View view = inflater.inflate(R.layout.fragment_auth, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        mUnbinder.unbind();
    }

    @OnClick({
            R.id.google_iv,
            R.id.vk_iv,
            R.id.fb_iv,
            R.id.login_btn,
            R.id.forgot_tv
    })
    @SuppressWarnings("unused")
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                signIn();
                break;
            case R.id.forgot_tv:
                rememberPassword();
                break;
            case R.id.vk_iv:
                break;
            case R.id.fb_iv:
                break;
            case R.id.google_iv:
                break;
        }
    }

    private void rememberPassword() {
        goToUrl(getActivity(), "https://google.com");
    }

    private void loginSuccess(AuthResponse response) {
        hideProgress();
        Log.d(TAG, "login success");
        mDataManager.getPreferencesManager().saveAuthToken(response.getData().getToken());
        mDataManager.getPreferencesManager().saveUserId(response.getData().getUser().getId());
        mDataManager.getPreferencesManager().saveUser(response.getData().getUser());

        FragmentManager fm = getActivity().getSupportFragmentManager();
        SplashScreenFragment f = (SplashScreenFragment) fm
                .findFragmentByTag(SplashScreenFragment.FRAGMENT_TAG);
        if (f == null) f = new SplashScreenFragment();
        fm.beginTransaction()
                .replace(R.id.container_start, f, SplashScreenFragment.FRAGMENT_TAG)
                .commit();
    }

    private void signIn() {
        if (!isNetworkAvailable(getActivity())) {
            showToast(getString(R.string.error_no_connection));
            return;
        }

        UserLoginRequest request = new UserLoginRequest();
        request.setEmail(mUsernameET.getText().toString());
        request.setPassword(mPasswordET.getText().toString());
        showProgress();

        mSubscriptions.add(mDataManager.loginUser(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::loginSuccess, this::handleError));
    }

    private void handleError(Throwable throwable) {
        hideProgress();
        handleResponseError(TAG, throwable);
    }
}
