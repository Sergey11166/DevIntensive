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
import com.softdesign.devintensive.data.network.response.ImageUploadedResponse;
import com.softdesign.devintensive.data.network.restmodels.Contacts;
import com.softdesign.devintensive.data.network.restmodels.PublicInfo;
import com.softdesign.devintensive.data.network.restmodels.Repo;
import com.softdesign.devintensive.data.network.restmodels.Repositories;
import com.softdesign.devintensive.data.network.restmodels.User;
import com.softdesign.devintensive.ui.dialogs.ChangeImageDialog;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.softdesign.devintensive.utils.Constants.DIALOG_FRAGMENT_TAG;
import static com.softdesign.devintensive.utils.Constants.LOG_TAG_PREFIX;
import static com.softdesign.devintensive.utils.IOUtils.filePathFromUri;
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

    // Activity requests
    private static final int REQUEST_CODE_CAMERA_AVATAR = 0;
    private static final int REQUEST_CODE_GALLERY_AVATAR = 1;
    private static final int REQUEST_CODE_CAMERA_USER_PHOTO = 2;
    private static final int REQUEST_CODE_GALLERY_USER_PHOTO = 3;
    private static final int REQUEST_CODE_SETTING_CAMERA_AVATAR = 4;
    private static final int REQUEST_CODE_SETTING_GALLERY_AVATAR = 5;
    private static final int REQUEST_CODE_SETTING_CAMERA_USER_PHOTO = 6;
    private static final int REQUEST_CODE_SETTING_GALLERY_USER_PHOTO = 7;

    // Permissions requests
    private static final int AVATAR_CAMERA_PERMISSION_REQUEST_CODE = 0;
    private static final int AVATAR_GALLERY_PERMISSION_REQUEST_CODE = 1;
    private static final int USER_PHOTO_CAMERA_PERMISSION_REQUEST_CODE = 2;
    private static final int USER_PHOTO_GALLERY_PERMISSION_REQUEST_CODE = 3;

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
    @BindView(R.id.rating) TextView mRating;
    @BindView(R.id.code_lines) TextView mLinesCode;
    @BindView(R.id.count_projects) TextView mCountProjects;
    private ImageView mAvatar;

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
    private File mImageFile;
    private Uri mSelectedUserPhoto;
    private Uri mSelectedAvatar;
    private Point mPhotoSize;

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
                if (mIsEditMode) {
                    saveUser();
                    uploadUserPhoto();
                }
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
            loadUser();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_GALLERY_USER_PHOTO:
                if (resultCode == RESULT_OK && data != null) mSelectedUserPhoto = data.getData();
                loadImageUserPhoto(mSelectedUserPhoto);
                break;
            case REQUEST_CODE_GALLERY_AVATAR:
                if (resultCode == RESULT_OK && data != null) mSelectedAvatar = data.getData();
                loadImageAvatar(mSelectedAvatar);
                uploadAvatar();
                break;
            case REQUEST_CODE_CAMERA_USER_PHOTO:
                if (resultCode == RESULT_OK && mImageFile != null) mSelectedUserPhoto = Uri.fromFile(mImageFile);
                loadImageUserPhoto(mSelectedUserPhoto);
                break;
            case REQUEST_CODE_CAMERA_AVATAR:
                if (resultCode == RESULT_OK && mImageFile != null) mSelectedAvatar = Uri.fromFile(mImageFile);
                loadImageAvatar(mSelectedAvatar);
                uploadAvatar();
                break;
            case REQUEST_CODE_SETTING_CAMERA_USER_PHOTO:
                checkAndRequestCameraPermission(USER_PHOTO_CAMERA_PERMISSION_REQUEST_CODE);
                break;
            case REQUEST_CODE_SETTING_CAMERA_AVATAR:
                checkAndRequestCameraPermission(AVATAR_CAMERA_PERMISSION_REQUEST_CODE);
                break;
            case REQUEST_CODE_SETTING_GALLERY_USER_PHOTO:
                checkAndRequestGalleryPermission(USER_PHOTO_GALLERY_PERMISSION_REQUEST_CODE);
                break;
            case REQUEST_CODE_SETTING_GALLERY_AVATAR:
                checkAndRequestGalleryPermission(AVATAR_GALLERY_PERMISSION_REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case USER_PHOTO_CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
                    onCameraPermissionGranted(REQUEST_CODE_CAMERA_USER_PHOTO);
                } else {
                    showNeedGrantPermissionDialog(R.string.dialog_message_need_grant_camera_permission,
                            (dialog, which) -> checkAndRequestCameraPermission(USER_PHOTO_CAMERA_PERMISSION_REQUEST_CODE),
                            (dialog, which) -> dialog.dismiss(),
                            (dialog, which) -> goToAppSettings(this, REQUEST_CODE_SETTING_CAMERA_USER_PHOTO));
                }
                break;
            case AVATAR_CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
                    onCameraPermissionGranted(REQUEST_CODE_CAMERA_AVATAR);
                } else {
                    showNeedGrantPermissionDialog(R.string.dialog_message_need_grant_camera_permission,
                            (dialog, which) -> checkAndRequestCameraPermission(AVATAR_CAMERA_PERMISSION_REQUEST_CODE),
                            (dialog, which) -> dialog.dismiss(),
                            (dialog, which) -> goToAppSettings(this, REQUEST_CODE_SETTING_CAMERA_AVATAR));
                }
                break;
            case USER_PHOTO_GALLERY_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PERMISSION_GRANTED) {
                    onGalleryPermissionGranted(REQUEST_CODE_GALLERY_USER_PHOTO);
                } else {
                    showNeedGrantPermissionDialog(R.string.dialog_message_need_grant_gallery_permission,
                            (dialog, which) -> checkAndRequestGalleryPermission(USER_PHOTO_GALLERY_PERMISSION_REQUEST_CODE),
                            (dialog, which) -> dialog.dismiss(),
                            (dialog, which) -> goToAppSettings(this, REQUEST_CODE_SETTING_GALLERY_USER_PHOTO));
                }
                break;
            case AVATAR_GALLERY_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PERMISSION_GRANTED) {
                    onGalleryPermissionGranted(REQUEST_CODE_GALLERY_AVATAR);
                } else {
                    showNeedGrantPermissionDialog(R.string.dialog_message_need_grant_gallery_permission,
                            (dialog, which) -> checkAndRequestGalleryPermission(AVATAR_GALLERY_PERMISSION_REQUEST_CODE),
                            (dialog, which) -> dialog.dismiss(),
                            (dialog, which) -> goToAppSettings(this, REQUEST_CODE_SETTING_GALLERY_AVATAR));
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
        mAvatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.avatar);
        mAvatar.setOnClickListener(v -> showChangeAvatarDialog());

        username.setText(user.getFirstName() + " " + user.getSecondName());
        email.setText(user.getContacts().getEmail());
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

    /**
     * Create object {@link User} with empty properties
     * @return {@link User}
     */
    private User createEmptyUser() {
        User user = new User();
        PublicInfo publicInfo = new PublicInfo();
        Contacts constants = new Contacts();
        Repositories repositories = new Repositories();

        user.setPublicInfo(publicInfo);
        user.setContacts(constants);
        user.setRepositories(repositories);

        return user;
    }

    /**
     * Save object {@link User} to {@link android.content.SharedPreferences}
     */
    private void saveUser() {
        User user = mDataManager.getPreferencesManager().loadUser();
        if (user == null) user = createEmptyUser();

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

    /**
     * Load object {@link User} from {@link android.content.SharedPreferences}
     */
    private void loadUser() {
        User user = mDataManager.getPreferencesManager().loadUser();
        if (user == null) return;

        String photo = user.getPublicInfo().getPhoto();
        mSelectedUserPhoto = (photo != null) ? Uri.parse(photo) : Uri.parse("");
        loadImageUserPhoto(mSelectedUserPhoto);

        String avatar = user.getPublicInfo().getAvatar();
        mSelectedAvatar = (avatar != null) ? Uri.parse(avatar) : Uri.parse("");
        loadImageAvatar(mSelectedAvatar);

        int rating = user.getProfileValues().getRating();
        int linesCode = user.getProfileValues().getLinesCode();
        int countProjects = user.getProfileValues().getProjects();

        mRating.setText(String.valueOf(rating));
        mLinesCode.setText(String.valueOf(linesCode));
        mCountProjects.setText(String.valueOf(countProjects));

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

    /**
     * Check if camera permissions not granted make request permissions
     */
    private void checkAndRequestCameraPermission(int requestCode) {
        if (ContextCompat.checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            onCameraPermissionGranted(requestCode);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {CAMERA, WRITE_EXTERNAL_STORAGE},
                    requestCode);
        }
    }

    /**
     * Check if gallery permissions not granted make request permissions
     */
    private void checkAndRequestGalleryPermission(int requestCode) {
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            onGalleryPermissionGranted(requestCode);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {READ_EXTERNAL_STORAGE},
                    requestCode);
        }
    }

    /**
     * Call when camera permissions have been granted
     */
    private void onCameraPermissionGranted(int requestCode) {
        try {
            mImageFile = createImageFile();
        } catch (IOException e) {
            showError(getString(R.string.error_creation_file), e);
            UIUtils.showToast(this, getString(R.string.error_toast_creation_file));
        }
        goToCameraApp(this, mImageFile, requestCode);
    }

    /**
     * Call when gallery permissions have been granted
     */
    private void onGalleryPermissionGranted(int requestCode) {
        goToGalleryApp(this, requestCode);
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
     * Show {@link ChangeImageDialog}. Called when we want change profile photo
     */
    private void showChangeProfilePhotoDialog() {
        ChangeImageDialog d = new ChangeImageDialog();
        d.setOnClickListener((dialog, which) -> {
            switch (which) {
                case 0:
                    checkAndRequestCameraPermission(USER_PHOTO_CAMERA_PERMISSION_REQUEST_CODE);
                    break;
                case 1:
                    checkAndRequestGalleryPermission(USER_PHOTO_GALLERY_PERMISSION_REQUEST_CODE);
                    break;
                case 2:
                    dialog.dismiss();
                    break;
            }
        });
        d.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
    }

    /**
     * Show {@link ChangeImageDialog}. Called when we want change avatar
     */
    private void showChangeAvatarDialog() {
        ChangeImageDialog d = new ChangeImageDialog();
        d.setOnClickListener((dialog, which) -> {
            switch (which) {
                case 0:
                    checkAndRequestCameraPermission(AVATAR_CAMERA_PERMISSION_REQUEST_CODE);
                    break;
                case 1:
                    checkAndRequestGalleryPermission(AVATAR_GALLERY_PERMISSION_REQUEST_CODE);
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

    /**
     * Setup user info fields
     */
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

    /**
     * Load image from server or cache or disc to {@link #mProfilePhoto} by Picasso library
     * @param uri Object {@link Uri} of image
     */
    private void loadImageUserPhoto(Uri uri) {
        if (mPhotoSize == null) {
            mPhotoSize = new Point(getResources().getDisplayMetrics().widthPixels,
                    getResources().getDimensionPixelSize(R.dimen.size_profile_photo_240));
        }
        Picasso.with(this)
                .load(uri)
                .placeholder(R.drawable.user_bg)
                .resize(mPhotoSize.x, mPhotoSize.y)
                .onlyScaleDown()
                .centerCrop()
                .into(mProfilePhoto);
    }

    /**
     * Load image from server or cache or disc to {@link #mAvatar} by Picasso library
     * @param uri Object {@link Uri} of image
     */
    private void loadImageAvatar(Uri uri) {
        Picasso.with(this)
                .load(uri)
                .placeholder(R.drawable.ic_account_circle)
                .resizeDimen(R.dimen.size_avatar, R.dimen.size_avatar)
                .onlyScaleDown()
                .centerCrop()
                .transform(new CircleTransformation())
                .into(mAvatar);
    }

    /**
     * Upload avatar to server
     */
    private void uploadAvatar() {
        if (mSelectedAvatar != null) {
            File file = new File(filePathFromUri(mSelectedAvatar));

            User user = mDataManager.getPreferencesManager().loadUser();
            if (user == null) user = createEmptyUser();
            final User finalUser = user;

            showProgress();
            mDataManager.uploadAvatar(file).enqueue(new Callback<ImageUploadedResponse>() {
                @Override
                public void onResponse(Call<ImageUploadedResponse> call, Response<ImageUploadedResponse> response) {
                    hideProgress();
                    if (response.code() == 200) {
                        finalUser.getPublicInfo().setAvatar(response.body().getData().getPhoto());
                        finalUser.getPublicInfo().setUpdated(response.body().getData().getUpdated());
                        mDataManager.getPreferencesManager().saveUser(finalUser);
                    }
                }

                @Override
                public void onFailure(Call<ImageUploadedResponse> call, Throwable t) {
                    hideProgress();
                    showError(getString(R.string.error_upload_image), t);
                }
            });
        }
    }

    /**
     * Upload user photo to server
     */
    private void uploadUserPhoto() {
        if (mSelectedUserPhoto != null) {
            File file = new File(filePathFromUri(mSelectedUserPhoto));

            User user = mDataManager.getPreferencesManager().loadUser();
            if (user == null) user = createEmptyUser();
            final User finalUser = user;

            showProgress();
            mDataManager.uploadUserPhoto(file).enqueue(new Callback<ImageUploadedResponse>() {
                @Override
                public void onResponse(Call<ImageUploadedResponse> call, Response<ImageUploadedResponse> response) {
                    hideProgress();
                    if (response.code() == 200) {
                        finalUser.getPublicInfo().setPhoto(response.body().getData().getPhoto());
                        finalUser.getPublicInfo().setUpdated(response.body().getData().getUpdated());
                        mDataManager.getPreferencesManager().saveUser(finalUser);
                    }
                }

                @Override
                public void onFailure(Call<ImageUploadedResponse> call, Throwable t) {
                    hideProgress();
                    showError(getString(R.string.error_upload_image), t);
                }
            });
        }
    }
}
