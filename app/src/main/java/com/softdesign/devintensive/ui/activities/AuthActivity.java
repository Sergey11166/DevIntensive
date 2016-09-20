package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.View;

import com.softdesign.devintensive.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.softdesign.devintensive.utils.NavUtils.goToUrl;

/**
 * @author Sergey Vorobyev.
 */

public class AuthActivity extends BaseActivity {

    @BindView(R.id.username_til) TextInputLayout mUsernameLayout;
    @BindView(R.id.password_til) TextInputLayout mPasswordLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
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
                loginSuccess();
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

    private void loginSuccess() {
        startActivity(new Intent(this, MainActivity.class));
        AuthActivity.this.finish();
    }
}
