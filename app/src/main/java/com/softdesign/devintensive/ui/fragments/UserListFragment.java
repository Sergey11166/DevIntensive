package com.softdesign.devintensive.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.chronos.operations.LoadUserListOperationFromDb;
import com.softdesign.devintensive.data.chronos.operations.SearchUsersByNameInDbOperation;
import com.softdesign.devintensive.data.dto.UserDTO;
import com.softdesign.devintensive.data.storage.entities.UserEntity;
import com.softdesign.devintensive.ui.activities.UserDetailsActivity;
import com.softdesign.devintensive.ui.adapters.UserListRecyclerAdapter;
import com.softdesign.devintensive.ui.adapters.UserListRecyclerAdapter.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.softdesign.devintensive.utils.AppConfig.SEARCH_DELAY;

/**
 * @author Sergey Vorobyev
 */

public class UserListFragment extends BaseFragment implements OnItemClickListener {

    public static final String FRAGMENT_TAG = "UserListFragment";
    public static final String PARCELABLE_USER_KEY = "PARCELABLE_USER_KEY";
    private static final String SEARCH_KEY = "SEARCH_KEY";

    @BindView(R.id.recycler) RecyclerView mRecyclerView;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    Unbinder mUnbinder;
    private SearchView mSearchView;

    private UserListRecyclerAdapter mAdapter;
    private String mSavedQuery;
    private String mLastQuery;
    private boolean mIsSearching;
    private Handler mHandler;
    private Runnable searchRunnable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mAdapter = new UserListRecyclerAdapter(this);
        searchRunnable = () -> runOperation(new SearchUsersByNameInDbOperation(mLastQuery),
                SearchUsersByNameInDbOperation.OPERATION_TAG);
        mHandler = new Handler();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        DrawerLayout drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        setupDrawer(activity, drawerLayout);
        mToolbar.setTitle(getString(R.string.drawer_menu_my_team));

        mRecyclerView.setAdapter(mAdapter);
        if (mAdapter.getData().isEmpty()) {
            loadAllUsersFromDb();
        } else {
            mAdapter.notifyDataSetChanged();
        }

        if (savedInstanceState != null) {
            mSavedQuery = savedInstanceState.getString(SEARCH_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSavedQuery = mSearchView.getQuery().toString();
        outState.putString(SEARCH_KEY, mSavedQuery);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        MenuItemCompat.setOnActionExpandListener(searchItem, new OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mIsSearching = true;
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mIsSearching = false;
                return true;
            }
        });
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setQueryHint(getString(R.string.user_list_hint_tape_user_name));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {return false;}
            @Override
            public boolean onQueryTextChange(String newText) {
                mLastQuery = newText;
                searchUsersByNameFromDb();
                return true;
            }
        });

        if (mIsSearching && mSavedQuery != null) {
            searchItem.expandActionView();
            mSearchView.setQuery(mSavedQuery, true);
        }
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onItemClick(int position) {
        List<UserEntity> users = mAdapter.getData();
        if (!users.isEmpty()) {
            UserDTO userDTO = new UserDTO(users.get(position));
            Intent i = new Intent(getActivity(), UserDetailsActivity.class);
            i.putExtra(PARCELABLE_USER_KEY, userDTO);
            startActivity(i);
        }
    }

    private void setupDrawer(Activity activity, DrawerLayout drawerLayout) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, mToolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void showUsers(List<UserEntity> users) {
        mAdapter.setData(users);
    }

    private void loadAllUsersFromDb() {
        if (mIsSearching) {
            runOperation(new SearchUsersByNameInDbOperation(mLastQuery), SearchUsersByNameInDbOperation.OPERATION_TAG);
        } else {
            runOperation(new LoadUserListOperationFromDb(), LoadUserListOperationFromDb.OPERATION_TAG);
        }
    }

    @SuppressWarnings("unused")
    public void onOperationFinished(final LoadUserListOperationFromDb.Result result) {
        showUsers(result.getOutput());
    }

    @SuppressWarnings("unused")
    public void onOperationFinished(final SearchUsersByNameInDbOperation.Result result) {
        showUsers(result.getOutput());
    }

    private void searchUsersByNameFromDb() {
        mHandler.removeCallbacks(searchRunnable);
        mHandler.postDelayed(searchRunnable, mLastQuery.isEmpty() ? 0 : SEARCH_DELAY);
    }
}
