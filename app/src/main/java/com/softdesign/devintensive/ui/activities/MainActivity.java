package com.softdesign.devintensive.ui.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

import static com.softdesign.devintensive.utils.Constants.EDIT_MODE_KEY;
import static com.softdesign.devintensive.utils.Constants.LOAD_PROFILE_PHOTO;
import static com.softdesign.devintensive.utils.Constants.LOG_TAG_PREFIX;

/**
 * @author Sergey Vorobyev
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = LOG_TAG_PREFIX + "MainActivity";

    private static final ButterKnife.Action<View> EDIT_MODE_TRUE = (view, index) -> {
        view.setEnabled(true);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
    };

    private static final ButterKnife.Action<View> EDIT_MODE_FALSE = (view, index) -> {
        view.setEnabled(false);
        view.setFocusable(false);
        view.setFocusableInTouchMode(false);
    };

    @BindView(R.id.coordinator_layout) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.appbar_layout) AppBarLayout mAppBarLayout;
    @BindView(R.id.profile_placeholder_layout) View mPlaceHolderLayout;
    @BindView(R.id.collapsing_toolbar_layout) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.profile_info_nested_scroll_view) NestedScrollView mScrollView;
    @BindView(R.id.profile_photo) ImageView mProfilePhoto;

    @BindViews({
            R.id.phone_edit_text,
            R.id.email_edit_text,
            R.id.vk_edit_text,
            R.id.git_edit_text,
            R.id.about_edit_text
    })
    List<EditText> mUserInfoViews;

    private DataManager mDataManager;

    private boolean mCurrentEditorMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mFab.setOnClickListener(this);
        mPlaceHolderLayout.setOnClickListener(this);
        setupToolBar();
        setupDrawer();

        mDataManager = DataManager.getInstance();
        loadUserInfoValue();

        if (savedInstanceState != null) {
            mCurrentEditorMode = savedInstanceState.getBoolean(EDIT_MODE_KEY);
            changeEditMode(mCurrentEditorMode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveUserInfoValue();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putBoolean(EDIT_MODE_KEY, mCurrentEditorMode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                changeEditMode(!mCurrentEditorMode);
                break;
            case R.id.profile_placeholder_layout:
                showDialog(LOAD_PROFILE_PHOTO);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Method show messages by {@link Snackbar}
     * @param message Message to show
     */
    private void showSnackBar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Method for setup {@link #mToolbar}
     */
    private void setupToolBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
            mToolbar.setTitle(R.string.app_name);
        }
    }

    /**
     * Method for setup navigation drawer
     */
    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            showSnackBar(item.getTitle().toString());
            item.setChecked(true);
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return false;
        });
        roundAvatar(navigationView);
    }

    /**
     * Method to round avatar of drawer header
     *
     * @param navigationView Object of {@link NavigationView}
     */

    private void roundAvatar(NavigationView navigationView) {
        Resources res = getResources();
        Bitmap srcBmp = BitmapFactory.decodeResource(res, R.drawable.avatar);
        RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(res, srcBmp);
        rbd.setCircular(true);
        ImageView view = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.avatar);
        view.setImageDrawable(rbd);
    }

    /**
     * Method to change edit mode of {@link #mUserInfoViews )
     * @param mode Activate edit mode if true, deactivate if false
     */
    private void changeEditMode(boolean mode) {
        fullScrollDown();
        mCurrentEditorMode = mode;
        mFab.setImageResource(mCurrentEditorMode ?
                R.drawable.ic_done_black_24dp :
                R.drawable.ic_mode_edit_black_24dp);
        if (mode) {
            ButterKnife.apply(mUserInfoViews, EDIT_MODE_TRUE);
            showProfilePlaceHolder();
            mCollapsingToolbarLayout.setTitle(" ");
        } else {
            ButterKnife.apply(mUserInfoViews, EDIT_MODE_FALSE);
            hideProfilePlaceHolder();
            mCollapsingToolbarLayout.setTitle(getString(R.string.app_name));
        }
    }

    /**
     * Method to load user info from SharedPreferences
     */
    private void loadUserInfoValue() {
        List<String> data = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < mUserInfoViews.size(); i++) {
            mUserInfoViews.get(i).setText(data.get(i));
        }
    }

    /**
     * Method to save user info to SharedPreferences
     */
    private void saveUserInfoValue() {
        List<String> data = new ArrayList<>(5);
        for (EditText view : mUserInfoViews) {
            data.add(view.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(data);
    }

    private void loadPhotoFromGallery() {

    }

    private void takePhotoFromCamera() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: 14.09.2016 Handle exception
        }

        if (photoFile != null) {
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(takePhotoIntent, Constants.REQUEST_CAMERA_PICTURE);
        }
    }

    private void hideProfilePlaceHolder() {
        mPlaceHolderLayout.setVisibility(View.GONE);
    }

    private void showProfilePlaceHolder() {
        mPlaceHolderLayout.setVisibility(View.VISIBLE);
    }

    private void fullScrollDown() {
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.onNestedFling(mCoordinatorLayout, mAppBarLayout, null, 0, -1000000, false);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case LOAD_PROFILE_PHOTO :

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.profile_placeholder_load_photo_dialog_title);
                final String[] items = getResources()
                        .getStringArray(R.array.load_photo_dialog);
                builder.setItems(items, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            loadPhotoFromGallery();
                            break;
                        case 1:
                            takePhotoFromCamera();
                            break;
                        case 2:
                            dialog.cancel();
                            break;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                return dialog;
            default: return  null;
        }
    }

    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
                .format(System.currentTimeMillis());
        String imageFileName = "JPEG_".concat(timestamp);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
}
