package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.softdesign.devintensive.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Sergey Vorobyev.
 */

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.login_button})
    @SuppressWarnings("unused")
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                startActivity(new Intent(this, MainActivity.class));
                LoginActivity.this.finish();
                break;
            case R.id.ic_vk:
                break;
            case R.id.ic_fb:
                break;
            case R.id.ic_google:
                break;
        }
    }
}
