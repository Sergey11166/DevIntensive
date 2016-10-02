package com.softdesign.devintensive.ui.adapters;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.restmodels.User;
import com.softdesign.devintensive.ui.views.AspectRatioImageView;
import com.squareup.picasso.Callback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.softdesign.devintensive.utils.Constants.LOG_TAG_PREFIX;
import static com.squareup.picasso.NetworkPolicy.OFFLINE;

/**
 * @author Sergey Vorobyev
 */

public class UserListRecyclerAdapter
        extends RecyclerView.Adapter<UserListRecyclerAdapter.UserViewHolder>
        implements Filterable {

    private static final String TAG = LOG_TAG_PREFIX + "UserListAdapter";

    private Context mContext;
    private List<User> mFilteredData;
    private List<User> mAllData;
    private Point mPhotoSize;
    private NameFilter mFilter;
    private OnItemClickListener mOnItemClickListener;

    public UserListRecyclerAdapter(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        mFilteredData = new ArrayList<>();
        mAllData = new ArrayList<>();
        mFilter = new NameFilter(this);
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
        return new UserViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = mFilteredData.get(position);

        holder.mUserName.setText(user.getFirstName() + " " + user.getSecondName());
        holder.mRating.setText(String.valueOf(user.getProfileValues().getRating()));
        holder.mCodeLines.setText(String.valueOf(user.getProfileValues().getLinesCode()));
        holder.mProjects.setText(String.valueOf(user.getProfileValues().getProjects()));
        holder.mAbout.setText(user.getPublicInfo().getBio());

        if (mPhotoSize == null) {
            int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
            int screenHeight = (int) (screenWidth / holder.mUserPhoto.getAspectRatio());
            mPhotoSize = new Point(screenWidth, screenHeight);
        }
        String userPhoto = user.getPublicInfo().getPhoto();
        if (userPhoto != null && userPhoto.isEmpty()) {
            userPhoto = "null";
            Log.e(TAG, "onBindViewHolder: user with name "
                    + user.getFirstName() + " " + user.getSecondName() + " has empty name");
        }
        String finalUserPhoto = userPhoto;
        DataManager.getInstance().getPicasso()
                .load(userPhoto)
                .placeholder(R.drawable.user_bg)
                .resize(mPhotoSize.x, mPhotoSize.y)
                .onlyScaleDown()
                .centerCrop()
                .networkPolicy(OFFLINE)
                .into(holder.mUserPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "user photo loaded from cache");
                    }

                    @Override
                    public void onError() {
                        DataManager.getInstance().getPicasso()
                                .load(finalUserPhoto)
                                .placeholder(R.drawable.user_bg)
                                .resize(mPhotoSize.x, mPhotoSize.y)
                                .onlyScaleDown()
                                .centerCrop()
                                .into(holder.mUserPhoto, new Callback() {
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

    @Override
    public int getItemCount() {
        return mFilteredData.size();
    }

    @NonNull
    public List<User> getFilteredData() {
        return mFilteredData;
    }

    @NonNull
    public List<User> getData() {
        return mAllData;
    }

    public void setData(@NonNull List<User> data) {
        mAllData = new ArrayList<>(data);
        mFilteredData = new ArrayList<>(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return mFilter;
    }


    static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.user_photo) AspectRatioImageView mUserPhoto;
        @BindView(R.id.username) TextView mUserName;
        @BindView(R.id.rating) TextView mRating;
        @BindView(R.id.code_lines) TextView mCodeLines;
        @BindView(R.id.count_projects) TextView mProjects;
        @BindView(R.id.about) TextView mAbout;
        @BindView(R.id.button_show_more) Button mMore;

        private OnItemClickListener mOnItemClickListener;

        UserViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mOnItemClickListener = listener;

            mMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) mOnItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class NameFilter extends Filter {

        private UserListRecyclerAdapter mAdapter;

        public NameFilter(@NonNull UserListRecyclerAdapter adapter) {
            super();
            mAdapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            mAdapter.getFilteredData().clear();
            FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                mAdapter.getFilteredData().addAll(mAdapter.getData());
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (final User user : mAdapter.getData()) {
                    if (user.getFirstName().toLowerCase().startsWith(filterPattern)) {
                        mAdapter.getFilteredData().add(user);
                    }
                    if (user.getSecondName().toLowerCase().startsWith(filterPattern)) {
                        mAdapter.getFilteredData().add(user);
                    }
                }
            }
            results.values = mAdapter.getFilteredData();
            results.count = mAdapter.getFilteredData().size();
            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
