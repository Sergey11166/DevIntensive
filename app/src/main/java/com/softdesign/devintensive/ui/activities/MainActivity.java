package com.softdesign.devintensive.ui.activities;

import android.content.ContentValues;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.restmodels.Contacts;
import com.softdesign.devintensive.data.network.restmodels.PublicInfo;
import com.softdesign.devintensive.data.network.restmodels.Repo;
import com.softdesign.devintensive.data.network.restmodels.Repositories;
import com.softdesign.devintensive.data.network.restmodels.User;
import com.softdesign.devintensive.ui.dialogs.ChangeProfilePhotoDialog;
import com.softdesign.devintensive.ui.dialogs.NeedGrantPermissionDialog;
import com.softdesign.devintensive.ui.view.transformations.CircleTransformation;
import com.softdesign.devintensive.ui.view.watchers.EmailTextWatcher;
import com.softdesign.devintensive.ui.view.watchers.GithubTextWatcher;
import com.softdesign.devintensive.ui.view.watchers.PhoneTextWatcher;
import com.softdesign.devintensive.ui.view.watchers.VkTextWatcher;
import com.softdesign.devintensive.utils.UIUtils;
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
import butterknife.OnClick;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.softdesign.devintensive.utils.Constants.DIALOG_FRAGMENT_TAG;
import static com.softdesign.devintensive.utils.Constants.LOG_TAG_PREFIX;
import static com.softdesign.devintensive.utils.NavUtils.goToAppSettings;
import static com.softdesign.devintensive.utils.NavUtils.goToCameraApp;
import static com.softdesign.devintensive.utils.NavUtils.goToGalleryApp;
import static com.softdesign.devintensive.utils.NavUtils.goToUrl;
import static com.softdesign.devintensive.utils.NavUtils.openPhoneApp;
import static com.softdesign.devintensive.utils.NavUtils.sendEmail;
import static com.softdesign.devintensive.utils.UIUtils.hideSoftKeyboard;
import static com.softdesign.devintensive.utils.UIUtils.showSoftKeyboard;

/**
 * @author Sergey Vorobyev
 */

public class MainActivity extends BaseActivity {

    private static final String TAG = LOG_TAG_PREFIX + "MainActivity";

    private static final String EDIT_MODE_KEY = "EDIT_MODE_KEY";

    private static final int REQUEST_CODE_CAMERA = 0;
    private static final int REQUEST_CODE_GALLERY = 1;
    private static final int REQUEST_CODE_SETTING_CAMERA = 2;
    private static final int REQUEST_CODE_SETTING_GALLERY = 3;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 0;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 1;

    private static final ButterKnife.Action<View> EDIT_MODE_TRUE = (view, index) -> {
        view.setFocusableInTouchMode(true);
        view.setFocusable(true);
        view.setEnabled(true);
    };

    private static final ButterKnife.Action<View> EDIT_MODE_FALSE = (view, index) -> {
        view.setFocusableInTouchMode(false);
        view.setFocusable(false);
        view.setEnabled(false);
    };

    @BindView(R.id.collapsing_toolbar_layout) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.coordinator_layout) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.profile_placeholder_layout) View mPlaceHolderLayout;
    @BindView(R.id.appbar_layout) AppBarLayout mAppBarLayout;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.profile_photo) ImageView mProfilePhoto;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    @BindViews({
            R.id.phone_edit_text,
            R.id.email_edit_text,
            R.id.vk_edit_text,
            R.id.github_edit_text,
            R.id.about_edit_text
    })
    List<EditText> mEditTextList;

    @BindViews({
            R.id.phone_text_input_layout,
            R.id.email_text_input_layout,
            R.id.vk_text_input_layout,
            R.id.github_text_input_layout,
            R.id.about_text_input_layout
    })
    List<TextInputLayout> mTextInputLayoutList;

    private DataManager mDataManager;

    private boolean mIsEditMode;
    private File mPhotoFile;
    private Uri mSelectedImage;
    private  Point mPhotoSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();

        if (!isAuthorized()) {
            Intent i = new Intent(this, AuthActivity.class);
            startActivity(i);
            MainActivity.this.finish();
            return;
        }

        mPhotoSize = new Point(getResources().getDisplayMetrics().widthPixels,
                getResources().getDimensionPixelSize(R.dimen.size_profile_photo_240));

        setupInfoLayouts();
        setupToolBar();
        setupDrawer();

        loadUser();

        if (savedInstanceState != null) {
            mIsEditMode = savedInstanceState.getBoolean(EDIT_MODE_KEY);
            changeEditMode(mIsEditMode);
        }
    }

    private boolean isAuthorized() {
        return !mDataManager.getPreferencesManager().getAuthToken().isEmpty();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) mDrawerLayout.openDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveUser();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putBoolean(EDIT_MODE_KEY, mIsEditMode);
    }

    @OnClick({
            R.id.fab,
            R.id.ic_vk_right,
            R.id.ic_phone_right,
            R.id.ic_email_right,
            R.id.ic_github_right,
            R.id.profile_placeholder_layout
    })
    @SuppressWarnings("unused")
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                changeEditMode(!mIsEditMode);
                break;
            case R.id.profile_placeholder_layout:
                showChangeProfilePhotoDialog();
                break;
            case R.id.ic_phone_right:
                if (!mTextInputLayoutList.get(0).isErrorEnabled())
                    openPhoneApp(this, mEditTextList.get(0).getText().toString());
                break;
            case R.id.ic_email_right:
                if (!mTextInputLayoutList.get(1).isErrorEnabled())
                    sendEmail(this, mEditTextList.get(1).getText().toString());
                break;
            case R.id.ic_vk_right:
                if (!mTextInputLayoutList.get(2).isErrorEnabled())
                    goToUrl(this, mEditTextList.get(2).getText().toString());
                break;
            case R.id.ic_github_right:
                if (!mTextInputLayoutList.get(3).isErrorEnabled())
                    goToUrl(this, mEditTextList.get(3).getText().toString());
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
                if (resultCode == RESULT_OK && data != null) mSelectedImage = data.getData();
                loadImageFromUriToView(mSelectedImage);
                break;
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK && mPhotoFile != null) mSelectedImage = Uri.fromFile(mPhotoFile);
                loadImageFromUriToView(mSelectedImage);
                break;
            case REQUEST_CODE_SETTING_CAMERA:
                checkAndRequestCameraPermission();
                break;
            case REQUEST_CODE_SETTING_GALLERY:
                checkAndRequestGalleryPermission();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
                    onCameraPermissionGranted();
                } else {
                    showNeedGrantPermissionDialog(R.string.dialog_message_need_grant_camera_permission,
                            (dialog, which) -> checkAndRequestCameraPermission(),
                            (dialog, which) -> dialog.dismiss(),
                            (dialog, which) -> goToAppSettings(this, REQUEST_CODE_SETTING_CAMERA));
                }
                break;
            case GALLERY_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PERMISSION_GRANTED) {
                    onGalleryPermissionGranted();
                } else {
                    showNeedGrantPermissionDialog(R.string.dialog_message_need_grant_gallery_permission,
                            (dialog, which) -> checkAndRequestGalleryPermission(),
                            (dialog, which) -> dialog.dismiss(),
                            (dialog, which) -> goToAppSettings(this, REQUEST_CODE_SETTING_GALLERY));
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
     * Setup {@link #mToolbar}
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
     * Setup navigation drawer
     */
    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            showSnackBar(item.getTitle().toString());
            item.setChecked(true);
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return false;
        });

        User user = mDataManager.getPreferencesManager().loadUser();

        TextView username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);
        TextView email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email);
        ImageView avatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.avatar);

        username.setText(user.getFirstName() + " " + user.getSecondName());
        email.setText(user.getContacts().getEmail());
        Picasso.with(this)
                .load(user.getPublicInfo().getAvatar())
                .resizeDimen(R.dimen.size_avatar, R.dimen.size_avatar)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(avatar);
    }

    /**
     * Change edit mode
     * @param mode Activate edit mode if true, deactivate if false
     */
    private void changeEditMode(boolean mode) {
        fullScrollDown();
        mIsEditMode = mode;
        mFab.setImageResource(mIsEditMode ?
                R.drawable.ic_done_black_24dp :
                R.drawable.ic_mode_edit_black_24dp);
        if (mode) {
            ButterKnife.apply(mEditTextList, EDIT_MODE_TRUE);
            showProfilePlaceHolder();
            mCollapsingToolbarLayout.setTitle(" ");
        } else {
            ButterKnife.apply(mEditTextList, EDIT_MODE_FALSE);
            hideProfilePlaceHolder();
            mCollapsingToolbarLayout.setTitle(getString(R.string.app_name));
        }
    }

    private void saveUser() {
        User user = mDataManager.getPreferencesManager().loadUser();
        if (user == null) {
            user = new User();
            PublicInfo publicInfo = new PublicInfo();
            Contacts constants = new Contacts();
            Repositories repositories = new Repositories();

            user.setPublicInfo(publicInfo);
            user.setContacts(constants);
            user.setRepositories(repositories);
        }

        if (mSelectedImage != null) user.getPublicInfo().setPhoto(mSelectedImage.toString());
        user.getContacts().setPhone(mEditTextList.get(0).getText().toString());
        user.getContacts().setEmail(mEditTextList.get(1).getText().toString());
        user.getContacts().setVk(mEditTextList.get(2).getText().toString());
        List<Repo> repos = new ArrayList<>();
        Repo repo = new Repo();
        repo.setGit(mEditTextList.get(3).getText().toString());
        repos.add(repo);
        user.getRepositories().setRepo(repos);
        user.getPublicInfo().setBio(mEditTextList.get(4).getText().toString());
        mDataManager.getPreferencesManager().saveUser(user);
    }

    private void loadUser() {
        User user = mDataManager.getPreferencesManager().loadUser();
        if (user == null) return;

        String photo = user.getPublicInfo().getPhoto();
        Uri photoUri = photo != null ? Uri.parse(photo) : Uri.parse("");
        loadImageFromUriToView(photoUri);

        String phone = user.getContacts().getPhone();
        String email = user.getContacts().getEmail();
        String vk = user.getContacts().getVk();
        List<Repo> repos = user.getRepositories().getRepo();
        String bio = user.getPublicInfo().getBio();

        mEditTextList.get(0).setText(!phone.isEmpty() ? phone : "");
        mEditTextList.get(1).setText(!email.isEmpty() ? email : "");
        mEditTextList.get(2).setText(!vk.isEmpty() ? vk : "");
        mEditTextList.get(3).setText(repos.size() > 0 ? repos.get(0).getGit() : "");
        mEditTextList.get(4).setText(!bio.isEmpty() ? bio : "");
    }

    private void loadPhotoFromGallery() {
        checkAndRequestGalleryPermission();
    }

    private void takePhotoFromCamera() {
        checkAndRequestCameraPermission();
    }

    /**
     * Check if camera permissions not granted make request permissions
     */
    private void checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            onCameraPermissionGranted();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {CAMERA, WRITE_EXTERNAL_STORAGE},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Check if gallery permissions not granted make request permissions
     */
    private void checkAndRequestGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            onGalleryPermissionGranted();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {READ_EXTERNAL_STORAGE},
                    GALLERY_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Call when camera permissions have been granted
     */
    private void onCameraPermissionGranted() {
        try {
            mPhotoFile = createImageFile();
        } catch (IOException e) {
            Log.e(TAG, "Error creation file", e);
            UIUtils.showToast(this, getString(R.string.error_toast_creation_file));
        }
        goToCameraApp(this, mPhotoFile, REQUEST_CODE_CAMERA);
    }

    /**
     * Call when gallery permissions have been granted
     */
    private void onGalleryPermissionGranted() {
        goToGalleryApp(this, REQUEST_CODE_GALLERY);
    }

    private void hideProfilePlaceHolder() {
        mPlaceHolderLayout.setVisibility(View.GONE);
    }

    private void showProfilePlaceHolder() {
        mPlaceHolderLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Scroll down always when change edit mode
     */
    private void fullScrollDown() {
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.onNestedFling(mCoordinatorLayout, mAppBarLayout, null, 0, -1000000, false);
        }
    }

    /**
     * Show {@link ChangeProfilePhotoDialog}. Called when we want change profile photo
     */
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
        d.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
    }

    /**
     * Show {@link NeedGrantPermissionDialog} to explain to users why needed permissions.
     * @param message Message for users
     * @param onPositiveButtonClickListener Actions to handle press on positive button
     * @param onNegativeButtonClickListener Actions to handle press on negative button
     */
    private void showNeedGrantPermissionDialog(int message,
                                               OnClickListener onPositiveButtonClickListener,
                                               OnClickListener onNegativeButtonClickListener,
                                               OnClickListener onNeutralButtonClickListener) {

        NeedGrantPermissionDialog d = NeedGrantPermissionDialog.newInstance(message);
        d.setOnPositiveButtonClickListener(onPositiveButtonClickListener);
        d.setOnNegativeButtonClickListener(onNegativeButtonClickListener);
        d.setOnNeutralButtonClickListener(onNeutralButtonClickListener);
        d.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
    }

    /**
     * Create image file to save photo when we took photo
     *
     * @return Object of {@link File}
     * @throws IOException
     */
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

    private void setupInfoLayouts() {
        final View.OnFocusChangeListener listener = (view, hasFocus) -> {
            if (hasFocus) {
                if (view instanceof EditText) {
                    EditText input = (EditText) view;
                    if (!input.isEnabled() && !input.isFocusable()) return;
                    input.setSelection(input.getText().length());
                    showSoftKeyboard(input);
                }
            } else {
                hideSoftKeyboard(view);
            }
        };
        for (EditText input: mEditTextList) input.setOnFocusChangeListener(listener);

        mEditTextList.get(0).addTextChangedListener(
                new PhoneTextWatcher(mTextInputLayoutList.get(0), mEditTextList.get(0)));

        mEditTextList.get(1).addTextChangedListener(
                new EmailTextWatcher(mTextInputLayoutList.get(1), mEditTextList.get(1)));

        mEditTextList.get(2).addTextChangedListener(
                new VkTextWatcher(mTextInputLayoutList.get(2), mEditTextList.get(2)));

        mEditTextList.get(3).addTextChangedListener(
                new GithubTextWatcher(mTextInputLayoutList.get(3), mEditTextList.get(3)));
    }

    private void loadImageFromUriToView(Uri uri) {
        Picasso.with(this)
                .load(uri)
                .placeholder(R.drawable.user_bg)
                .resize(mPhotoSize.x, mPhotoSize.y)
                .onlyScaleDown()
                .centerCrop()
                .into(mProfilePhoto);
    }
}
