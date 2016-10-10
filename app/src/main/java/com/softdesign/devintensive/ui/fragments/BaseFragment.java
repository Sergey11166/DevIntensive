package com.softdesign.devintensive.ui.fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.redmadrobot.chronos.gui.fragment.ChronosSupportFragment;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.Constants;

import rx.subscriptions.CompositeSubscription;

import static com.softdesign.devintensive.utils.UIUtils.showToast;

/**
 * @author Sergey Vorobyev.
 */

public class BaseFragment extends ChronosSupportFragment {

    private static final String TAG = Constants.LOG_TAG_PREFIX + "BaseFragment";
    private static final String IS_PROGRESS_SHOWING_KEY = "IS_PROGRESS_SHOWING_KEY";

    private ProgressDialog mProgressDialog;
    private boolean mIsProgressShowing;
    protected CompositeSubscription mSubscriptions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscriptions = new CompositeSubscription();
        initProgressDialog();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mIsProgressShowing = savedInstanceState.getBoolean(IS_PROGRESS_SHOWING_KEY);
        }
        if (mIsProgressShowing) showProgress();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_PROGRESS_SHOWING_KEY, mIsProgressShowing);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        mProgressDialog.dismiss();
        mSubscriptions.unsubscribe();
        super.onDestroy();
    }

    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(getActivity(), R.style.custom_progress);
        mProgressDialog.setCancelable(false);
        Window window = mProgressDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public void showProgress() {
        mProgressDialog.show();
        mProgressDialog.setContentView(R.layout.progress_splash);
    }

    public void hideProgress() {
        if (mProgressDialog.isShowing()) mProgressDialog.hide();
        mIsProgressShowing = false;
    }

    public void showError(String message, Throwable t) {
        Log.d(TAG, "Message: "+ message + "\n" + "Exception: " + t.toString());
        showToast(message);
    }
}
