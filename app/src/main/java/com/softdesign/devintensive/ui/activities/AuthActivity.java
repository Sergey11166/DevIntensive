package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.request.UserLoginRequest;
import com.softdesign.devintensive.data.network.response.UserModelResponse;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.softdesign.devintensive.utils.NavUtils.goToUrl;
import static com.softdesign.devintensive.utils.UIUtils.showToast;

/**
 * @author Sergey Vorobyev.
 */

public class AuthActivity extends BaseActivity {

    @BindView(R.id.username_et) EditText mUsernameET;
    @BindView(R.id.password_et) EditText mPasswordET;
    private Unbinder mUnbinder;

    private DataManager mDataManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        mUnbinder = ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();
    }

    @Override
    protected void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
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
        goToUrl(this, "https://google.com");
    }

    private void loginSuccess(Response<UserModelResponse> response) {
        mDataManager.getPreferencesManager().saveAuthToken(response.body().getData().getToken());
        mDataManager.getPreferencesManager().saveUserId(response.body().getData().getUser().getId());
        mDataManager.getPreferencesManager().saveUser(response.body().getData().getUser());
        startMainActivity();
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        AuthActivity.this.finish();
    }

    private void signIn() {
        if (!NetworkStatusChecker.isNetworkAvailable(this)) {
            showToast(this, getString(R.string.error_no_connection));
            return;
        }

        UserLoginRequest request = new UserLoginRequest();
        request.setEmail(mUsernameET.getText().toString());
        request.setPassword(mPasswordET.getText().toString());
        showProgress();
        mDataManager.loginUser(request).enqueue(new Callback<UserModelResponse>() {
            @Override
            public void onResponse(Call<UserModelResponse> call, Response<UserModelResponse> response) {
                hideProgress();
                if (response.code() == 200) {
                    loginSuccess(response);
                } else if (response.code() == 403) {
                    showToast(AuthActivity.this, getString(R.string.error_wrong_username_or_password));
                } else {
                    showToast(AuthActivity.this, getString(R.string.error_unknown_error));
                }
            }

            @Override
            public void onFailure(Call<UserModelResponse> call, Throwable t) {
                hideProgress();
                showError(getString(R.string.error_unknown_error), t);
            }
        });
    }
}
