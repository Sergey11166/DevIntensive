package com.softdesign.devintensive.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.response.ImageUploadedResponse;
import com.softdesign.devintensive.data.network.restmodels.Repo;
import com.softdesign.devintensive.data.network.restmodels.User;
import com.softdesign.devintensive.ui.dialogs.ChangeImageDialog;
import com.softdesign.devintensive.ui.dialogs.NeedGrantPermissionDialog;
import com.softdesign.devintensive.ui.view.watchers.EmailTextWatcher;
import com.softdesign.devintensive.ui.view.watchers.GithubTextWatcher;
import com.softdesign.devintensive.ui.view.watchers.PhoneTextWatcher;
import com.softdesign.devintensive.ui.view.watchers.VkTextWatcher;
import com.softdesign.devintensive.utils.IOUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static android.content.DialogInterface.OnClickListener;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.softdesign.devintensive.data.network.restmodels.User.createEmptyUser;
import static com.softdesign.devintensive.ui.activities.MainActivity.REQUEST_CODE_CAMERA_USER_PHOTO;
import static com.softdesign.devintensive.ui.activities.MainActivity.REQUEST_CODE_GALLERY_USER_PHOTO;
import static com.softdesign.devintensive.ui.activities.MainActivity.REQUEST_CODE_SETTING_CAMERA_USER_PHOTO;
import static com.softdesign.devintensive.ui.activities.MainActivity.REQUEST_CODE_SETTING_GALLERY_USER_PHOTO;
import static com.softdesign.devintensive.ui.activities.MainActivity.USER_PHOTO_CAMERA_PERMISSION_REQUEST_CODE;
import static com.softdesign.devintensive.ui.activities.MainActivity.USER_PHOTO_GALLERY_PERMISSION_REQUEST_CODE;
import static com.softdesign.devintensive.utils.Constants.DIALOG_FRAGMENT_TAG;
import static com.softdesign.devintensive.utils.IOUtils.filePathFromUri;
import static com.softdesign.devintensive.utils.NavUtils.goToAppSettings;
import static com.softdesign.devintensive.utils.NavUtils.goToCameraApp;
import static com.softdesign.devintensive.utils.NavUtils.goToGalleryApp;
import static com.softdesign.devintensive.utils.NavUtils.goToUrl;
import static com.softdesign.devintensive.utils.NavUtils.openPhoneApp;
import static com.softdesign.devintensive.utils.NavUtils.sendEmail;
import static com.softdesign.devintensive.utils.UIUtils.hideSoftKeyboard;
import static com.softdesign.devintensive.utils.UIUtils.showToast;

/**
 * @author Sergey Vorobyev.
 */

public class ProfileFragment extends BaseFragment implements ActionMode.Callback {

    public static final String FRAGMENT_TAG = "ProfileFragment";

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
    @BindView(R.id.profile_photo) ImageView mProfilePhoto;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.rating) TextView mRating;
    @BindView(R.id.code_lines) TextView mLinesCode;
    @BindView(R.id.count_projects) TextView mCountProjects;
    private Unbinder mUnbinder;

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

    private ActionMode mActionMode;
    private boolean mIsCancelChanges;
    private boolean mIsEditMode;

    private boolean isChangedUserPhoto;
    private File mImageFile;
    private Uri mSelectedUserPhoto;
    private Point mPhotoSize;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mDataManager = DataManager.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        DrawerLayout drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        setupDrawer(activity, drawerLayout);

        changeEditMode(mIsEditMode);
        if (mIsEditMode) startActionMode();
        setupInfoLayouts();
        loadUser();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
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
                if (!mIsEditMode) {
                    mIsCancelChanges = false;
                    mActionMode.finish();
                } else {
                    mIsCancelChanges = true;
                    startActionMode();
                }
                break;
            case R.id.profile_placeholder_layout:
                showChangeProfilePhotoDialog();
                break;
            case R.id.ic_phone_right:
                if (!mTextInputLayoutList.get(0).isErrorEnabled())
                    openPhoneApp(getActivity(), mEditTextList.get(0).getText().toString());
                break;
            case R.id.ic_email_right:
                if (!mTextInputLayoutList.get(1).isErrorEnabled())
                    sendEmail(getActivity(), mEditTextList.get(1).getText().toString());
                break;
            case R.id.ic_vk_right:
                if (!mTextInputLayoutList.get(2).isErrorEnabled())
                    goToUrl(getActivity(), mEditTextList.get(2).getText().toString());
                break;
            case R.id.ic_github_right:
                if (!mTextInputLayoutList.get(3).isErrorEnabled())
                    goToUrl(getActivity(), mEditTextList.get(3).getText().toString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_GALLERY_USER_PHOTO:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedUserPhoto = data.getData();
                    isChangedUserPhoto = true;
                }
                loadImageUserPhoto(mSelectedUserPhoto);
                break;
            case REQUEST_CODE_CAMERA_USER_PHOTO:
                if (resultCode == RESULT_OK && mImageFile != null) {
                    mSelectedUserPhoto = Uri.fromFile(mImageFile);
                    isChangedUserPhoto = true;
                }
                loadImageUserPhoto(mSelectedUserPhoto);
                break;
            case REQUEST_CODE_SETTING_CAMERA_USER_PHOTO:
                checkAndRequestCameraPermission();
                break;
            case REQUEST_CODE_SETTING_GALLERY_USER_PHOTO:
                checkAndRequestGalleryPermission();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case USER_PHOTO_CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
                    onCameraPermissionGranted();
                } else {
                    showNeedGrantPermissionDialog(R.string.dialog_message_need_grant_camera_permission,
                            (dialog, which) -> checkAndRequestCameraPermission(),
                            (dialog, which) -> dialog.dismiss(),
                            (dialog, which) -> goToAppSettings(getActivity(), REQUEST_CODE_SETTING_CAMERA_USER_PHOTO));
                }
                break;
            case USER_PHOTO_GALLERY_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PERMISSION_GRANTED) {
                    onGalleryPermissionGranted();
                } else {
                    showNeedGrantPermissionDialog(R.string.dialog_message_need_grant_gallery_permission,
                            (dialog, which) -> checkAndRequestGalleryPermission(),
                            (dialog, which) -> dialog.dismiss(),
                            (dialog, which) -> goToAppSettings(getActivity(), REQUEST_CODE_SETTING_GALLERY_USER_PHOTO));
                }
                break;
        }
    }

    private void setupDrawer(Activity activity, DrawerLayout drawerLayout) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, mToolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
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
    private void checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), CAMERA) == PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            onCameraPermissionGranted();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[] {CAMERA, WRITE_EXTERNAL_STORAGE},
                    USER_PHOTO_CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Check if gallery permissions not granted make request permissions
     */
    private void checkAndRequestGalleryPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            onGalleryPermissionGranted();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[] {READ_EXTERNAL_STORAGE},
                    USER_PHOTO_GALLERY_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Call when camera permissions have been granted
     */
    private void onCameraPermissionGranted() {
        try {
            mImageFile = IOUtils.createImageFile(getActivity());
        } catch (IOException e) {
            showError(getString(R.string.error_creation_file), e);
            showToast(getActivity(), getString(R.string.error_toast_creation_file));
        }
        goToCameraApp(getActivity(), mImageFile, REQUEST_CODE_CAMERA_USER_PHOTO);
    }

    /**
     * Call when gallery permissions have been granted
     */
    private void onGalleryPermissionGranted() {
        goToGalleryApp(getActivity(), REQUEST_CODE_GALLERY_USER_PHOTO);
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
                    checkAndRequestCameraPermission();
                    break;
                case 1:
                    checkAndRequestGalleryPermission();
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
     * Setup user info fields
     */
    private void setupInfoLayouts() {
        final View.OnFocusChangeListener listener = (view, hasFocus) -> {
            if (hasFocus) {
                if (view instanceof EditText) {
                    EditText input = (EditText) view;
                    if (!input.isEnabled() && !input.isFocusable()) return;
                    input.setSelection(input.getText().length());
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
        Picasso.with(getActivity())
                .load(uri)
                .placeholder(R.drawable.user_bg)
                .resize(mPhotoSize.x, mPhotoSize.y)
                .onlyScaleDown()
                .centerCrop()
                .into(mProfilePhoto);
    }

    /**
     * Upload user photo to server
     */
    private void uploadUserPhoto() {
        if (isChangedUserPhoto && mSelectedUserPhoto != null) {
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

    private void startActionMode() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        mActionMode = activity.startSupportActionMode(this);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        if (!mIsCancelChanges) {
            saveUser();
            uploadUserPhoto();
        } else {
            loadUser();
            changeEditMode(false);
        }
    }
}
