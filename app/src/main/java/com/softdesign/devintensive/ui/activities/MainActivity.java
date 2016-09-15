package com.softdesign.devintensive.ui.activities;

import android.content.ContentValues;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.ui.dialogs.ChangeProfilePhotoDialog;
import com.softdesign.devintensive.ui.dialogs.NeedGrantPermissionDialog;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.softdesign.devintensive.utils.Constants.EDIT_MODE_KEY;
import static com.softdesign.devintensive.utils.Constants.LOG_TAG_PREFIX;
import static com.softdesign.devintensive.utils.Constants.SHOW_DIALOG_FRAGMENT_TAG;

/**
 * @author Sergey Vorobyev
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = LOG_TAG_PREFIX + "MainActivity";

    private static final int REQUEST_CODE_CAMERA = 0;
    private static final int REQUEST_CODE_GALLERY = 1;
    private static final int REQUEST_CODE_SETTING = 2;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 0;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 1;

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

    private boolean mIsEditMode;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;

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
        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .into(mProfilePhoto);

        if (savedInstanceState != null) {
            mIsEditMode = savedInstanceState.getBoolean(EDIT_MODE_KEY);
            changeEditMode(mIsEditMode);
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
    protected void onPause() {
        super.onPause();
        saveUserInfoValue();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putBoolean(EDIT_MODE_KEY, mIsEditMode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                changeEditMode(!mIsEditMode);
                break;
            case R.id.profile_placeholder_layout:
                showChangeProfilePhotoDialog();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (mIsEditMode){
            changeEditMode(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_GALLERY:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();
                }
                insertProfileImage(mSelectedImage);
                break;
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);
                }
                insertProfileImage(mSelectedImage);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
                    permissionCameraGranted();
                } else {
                    showNeedGrantPermissionDialog(R.string.dialog_message_need_grant_camera_permission,
                            (dialog, which) -> checkAndRequestCameraPermission(),
                            (dialog, which) -> dialog.dismiss());
                }
                break;
            case GALLERY_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PERMISSION_GRANTED) {
                    permissionGalleryGranted();
                } else {
                    showNeedGrantPermissionDialog(R.string.dialog_message_need_grant_gallery_permission,
                            (dialog, which) -> checkAndRequestGalleryPermission(),
                            (dialog, which) -> dialog.dismiss());
                }
                break;
        }
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
        mIsEditMode = mode;
        mFab.setImageResource(mIsEditMode ?
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
        checkAndRequestGalleryPermission();
    }

    private void takePhotoFromCamera() {
        checkAndRequestCameraPermission();
    }

    private void checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            permissionCameraGranted();
        } else {
            showNeedGrantPermissionDialog(R.string.dialog_message_need_grant_camera_permission,
                    (dialog, which) -> ActivityCompat.requestPermissions(this,
                            new String[] {CAMERA, WRITE_EXTERNAL_STORAGE},
                            CAMERA_PERMISSION_REQUEST_CODE),
                    (dialog, which) -> dialog.dismiss());
        }

    }

    private void checkAndRequestGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            permissionGalleryGranted();
        } else {
            showNeedGrantPermissionDialog(R.string.dialog_message_need_grant_gallery_permission,
                    (dialog, which) -> ActivityCompat.requestPermissions(this,
                            new String[] {READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_REQUEST_CODE),
                    (dialog, which) -> dialog.dismiss());
        }
    }

    private void permissionCameraGranted() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            mPhotoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: 14.09.2016 Handle exception
        }

        if (mPhotoFile != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        }
    }

    private void permissionGalleryGranted() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.profile_placeholder_chose_photo_from_gallery)),
                REQUEST_CODE_GALLERY);
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

    private void showChangeProfilePhotoDialog() {
        ChangeProfilePhotoDialog d = new ChangeProfilePhotoDialog();
        d.setOnClickListener((dialog, which) -> {
            switch (which) {
                case 0:
                    takePhotoFromCamera();
                    break;
                case 1:
                    loadPhotoFromGallery();
                    break;
                case 2:
                    dialog.dismiss();
                    break;
            }
        });
        d.show(getFragmentManager(), SHOW_DIALOG_FRAGMENT_TAG);
    }

    private void showNeedGrantPermissionDialog(int message,
                                               OnClickListener onPositiveButtonClickListener,
                                               OnClickListener onNegativeButtonClickListener) {

        NeedGrantPermissionDialog d = NeedGrantPermissionDialog.newInstance(message);
        d.setOnPositiveButtonClickListener(onPositiveButtonClickListener);
        d.setOnNegativeButtonClickListener(onNegativeButtonClickListener);
        d.show(getFragmentManager(), SHOW_DIALOG_FRAGMENT_TAG);
    }

    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
                .format(System.currentTimeMillis());
        String imageFileName = "JPEG_".concat(timestamp);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATA, image.getAbsolutePath());
        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        return image;
    }

    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .into(mProfilePhoto);

        if (selectedImage != null) {
            mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
        }
    }

    public void openApplicationSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE_SETTING);
    }
}
