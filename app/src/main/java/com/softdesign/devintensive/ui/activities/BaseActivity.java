package com.softdesign.devintensive.ui.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.Constants;

import static com.softdesign.devintensive.utils.UIUtils.showToast;

/**
 * @author Sergey Vorobyev.
 */

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = Constants.LOG_TAG_PREFIX + "BaseActivity";

    ProgressDialog mProgressDialog;

    public void showProgress() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, R.style.custom_progress);
            mProgressDialog.setCancelable(false);
            assert mProgressDialog.getWindow() != null;
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        mProgressDialog.show();
        mProgressDialog.setContentView(R.layout.progress_splash);
    }

    public void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.hide();
    }

    public void showError(String message, Throwable t) {
        Log.d(TAG, "Message: "+ message + "\n" + "Exception: " + t.toString());
        showToast(this, message);
    }
}
