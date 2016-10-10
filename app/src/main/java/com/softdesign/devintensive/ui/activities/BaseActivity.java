package com.softdesign.devintensive.ui.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.redmadrobot.chronos.gui.activity.ChronosAppCompatActivity;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.Constants;

import rx.subscriptions.CompositeSubscription;

import static com.softdesign.devintensive.utils.UIUtils.showToast;

/**
 * @author Sergey Vorobyev.
 */

public class BaseActivity extends ChronosAppCompatActivity {

    private static final String TAG = Constants.LOG_TAG_PREFIX + "BaseActivity";

    private ProgressDialog mProgressDialog;
    private CompositeSubscription mSubscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscriptions = new CompositeSubscription();
        initProgressDialog();
    }

    @Override
    protected void onDestroy() {
        if (mProgressDialog != null) mProgressDialog.dismiss();
        mSubscriptions.unsubscribe();
        super.onDestroy();
    }

    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(this, R.style.custom_progress);
        mProgressDialog.setCancelable(false);
        Window window = mProgressDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        mProgressDialog.setContentView(R.layout.progress_splash);
    }

    public void showProgress() {
        mProgressDialog.show();
    }

    public void hideProgress() {
        if (mProgressDialog.isShowing()) mProgressDialog.hide();
    }

    public void showError(String message, Throwable t) {
        Log.d(TAG, "Message: "+ message + "\n" + "Exception: " + t.toString());
        showToast(message);
    }
}
