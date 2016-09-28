package com.softdesign.devintensive.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.response.UserListResponse;
import com.softdesign.devintensive.data.network.restmodels.User;
import com.softdesign.devintensive.ui.adapters.UserListRecyclerAdapter;
import com.softdesign.devintensive.ui.adapters.UserListRecyclerAdapter.OnItemClickListener;

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

    public static final String PARCELABLE_USER_KEY = "PARCELABLE_USER_KEY";

    @BindView(R.id.recycler) RecyclerView mRecyclerView;
    Unbinder mUnbinder;

    private UserListRecyclerAdapter mAdapter;
    private DataManager mDataManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mAdapter = new UserListRecyclerAdapter(this);
        mDataManager = DataManager.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        ButterKnife.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new UserListRecyclerAdapter(this);
        loadUsers();
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onItemClick(User user) {
        //Intent i = new Intent(getActivity(), UserDetailActivity.class);
        //Bundle bundle = new Bundle();
        //bundle.putParcelable(PARCELABLE_USER_KEY, user);
        //startActivity(i);
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
                    mAdapter.setData(response.body().getData());
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
