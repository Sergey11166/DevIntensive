package com.softdesign.devintensive.ui.activities;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.dto.UserDTO;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.ui.adapters.RepositoriesAdapter;
import com.squareup.picasso.Callback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.softdesign.devintensive.ui.fragments.UserListFragment.PARCELABLE_USER_KEY;
import static com.softdesign.devintensive.utils.Constants.LOG_TAG_PREFIX;
import static com.softdesign.devintensive.utils.NavUtils.goToUrl;
import static com.squareup.picasso.NetworkPolicy.OFFLINE;

/**
 * @author Sergey Vorobyev.
 */

public class UserDetailsActivity extends BaseActivity implements OnItemClickListener {

    private static final String TAG = LOG_TAG_PREFIX + "UserDetailsActivity";

    @BindView(R.id.collapsing_toolbar_layout) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.profile_photo) ImageView mUserPhoto;
    @BindView(R.id.rating) TextView mRating;
    @BindView(R.id.code_lines) TextView mCodeLines;
    @BindView(R.id.count_projects) TextView mProjects;
    @BindView(R.id.about) TextView mAbout;
    @BindView(R.id.list_repositories) ListView mRepositories;
    private Unbinder mUnbinder;

    private UserDTO mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        mUnbinder = ButterKnife.bind(this);
        setupToolbar();

        mUser = getIntent().getExtras().getParcelable(PARCELABLE_USER_KEY);
        if (mUser != null) initUI();
    }

    @Override
    protected void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        goToUrl(this, mUser.getRepos().get(position));
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initUI() {

        mRepositories.setAdapter(new RepositoriesAdapter(this, mUser.getRepos()));
        mRepositories.setOnItemClickListener(this);
        mCollapsingToolbarLayout.setTitle(mUser.getFullName());
        mRating.setText(String.valueOf(mUser.getRating()));
        mCodeLines.setText(String.valueOf(mUser.getCountCodeLines()));
        mProjects.setText(String.valueOf(mUser.getCountProjects()));
        mAbout.setText(String.valueOf(mUser.getBio()));

        Point photoSize = new Point(getResources().getDisplayMetrics().widthPixels,
                    getResources().getDimensionPixelSize(R.dimen.size_profile_photo_240));

        DataManager.getInstance().getPicasso()
                .load(mUser.getPhoto())
                .placeholder(R.drawable.user_bg)
                .resize(photoSize.x, photoSize.y)
                .onlyScaleDown()
                .centerCrop()
                .networkPolicy(OFFLINE)
                .into(mUserPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "user photo loaded from cache");
                    }

                    @Override
                    public void onError() {
                        DataManager.getInstance().getPicasso()
                                .load(mUser.getPhoto())
                                .placeholder(R.drawable.user_bg)
                                .resize(photoSize.x, photoSize.y)
                                .onlyScaleDown()
                                .centerCrop()
                                .into(mUserPhoto, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d(TAG, "user photo loaded from server");
                                    }

                                    @Override
                                    public void onError() {
                                        Log.d(TAG, "Can't load user photo from server");
                                    }
                                });
                    }
                });
    }
}
