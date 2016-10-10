package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.events.UserListResponseEvent;
import com.softdesign.devintensive.ui.fragments.SplashScreenFragment;

import de.greenrobot.event.EventBus;

import static com.softdesign.devintensive.utils.Constants.LOG_TAG_PREFIX;

/**
 * @author Sergey Vorobyev.
 */

public class StartActivity extends BaseActivity {

    private static final String TAG = LOG_TAG_PREFIX + "StartActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_start);

        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            SplashScreenFragment f = (SplashScreenFragment) fm
                    .findFragmentByTag(SplashScreenFragment.FRAGMENT_TAG);
            if (f == null) f = new SplashScreenFragment();
            fm.beginTransaction()
                    .replace(R.id.container_start, f, SplashScreenFragment.FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @SuppressWarnings("unused")
    public void onEvent(UserListResponseEvent event) {
        startActivity(new Intent(this, MainActivity.class));
        StartActivity.this.finish();
    }
}
