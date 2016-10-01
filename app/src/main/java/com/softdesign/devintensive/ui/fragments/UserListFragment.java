package com.softdesign.devintensive.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
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
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.response.UserListResponse;
import com.softdesign.devintensive.data.network.restmodels.User;
import com.softdesign.devintensive.ui.activities.UserDetailsActivity;
import com.softdesign.devintensive.ui.adapters.UserListRecyclerAdapter;
import com.softdesign.devintensive.ui.adapters.UserListRecyclerAdapter.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.softdesign.devintensive.utils.NetworkStatusChecker.isNetworkAvailable;
import static com.softdesign.devintensive.utils.UIUtils.showToast;

/**
 * @author Sergey Vorobyev
 */

public class UserListFragment extends BaseFragment implements OnItemClickListener {

    public static final String FRAGMENT_TAG = "UserListFragment";
    public static final String PARCELABLE_USER_KEY = "PARCELABLE_USER_KEY";
    private static final String SEARCH_KEY = "SEARCH_KEY";

    @BindView(R.id.recycler) RecyclerView mRecyclerView;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    private Unbinder mUnbinder;
    private SearchView mSearchView;

    private UserListRecyclerAdapter mAdapter;
    private DataManager mDataManager;
    private String mQueryText;
    private boolean mIsSearching;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mAdapter = new UserListRecyclerAdapter(this);
        mDataManager = DataManager.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
            loadUsers();
        } else {
            mAdapter.notifyDataSetChanged();
        }

        if (savedInstanceState != null) {
            mQueryText = savedInstanceState.getString(SEARCH_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mQueryText = mSearchView.getQuery().toString();
        outState.putString(SEARCH_KEY, mQueryText);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
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
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {return false;}

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });

        if (mIsSearching && mQueryText != null) {
            searchItem.expandActionView();
            mSearchView.setQuery(mQueryText, true);
        }
    }



    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onItemClick(int position) {
        List<User> users = mAdapter.getFilteredData();
        if (!users.isEmpty()) {
            User user = users.get(position);
            Intent i = new Intent(getActivity(), UserDetailsActivity.class);
            i.putExtra(PARCELABLE_USER_KEY, user);
            startActivity(i);
        }
    }

    private void setupDrawer(Activity activity, DrawerLayout drawerLayout) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, mToolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void loadUsers() {
        if (!isNetworkAvailable(getContext())) {
            showToast(getContext(), getString(R.string.error_no_connection));
            return;
        }
        showProgress();
        mDataManager.getUserList().enqueue(new Callback<UserListResponse>() {
            @Override
            public void onResponse(Call<UserListResponse> call, Response<UserListResponse> response) {
                hideProgress();
                if (response.code() == 200) {
                    mAdapter.setData(response.body().getData().getUsers());
                } else {
                    showToast(getContext(), getString(R.string.error_unknown_error));
                }
            }

            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
                hideProgress();
                showError(getString(R.string.error_unknown_error), t);
            }
        });
    }
}
