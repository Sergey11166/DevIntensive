package com.softdesign.devintensive.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.entities.UserEntity;
import com.softdesign.devintensive.ui.adapters.helpers.ItemTouchHelperViewHolder;
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

public class UserListRecyclerAdapter extends Adapter<UserListRecyclerAdapter.UserViewHolder> {

    private static final String TAG = LOG_TAG_PREFIX + "UserListAdapter";

    private Context mContext;
    private List<UserEntity> mData;
    private OnItemClickListener mOnItemClickListener;

    public UserListRecyclerAdapter(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        mData = new ArrayList<>();
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
        return new UserViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        UserEntity user = mData.get(position);

        holder.mUserName.setText(user.getFullName());
        holder.mRating.setText(String.valueOf(user.getRating()));
        holder.mCodeLines.setText(String.valueOf(user.getCountCodeLines()));
        holder.mProjects.setText(String.valueOf(user.getCountProjects()));
        holder.mAbout.setText(user.getBio());

        String userPhoto = user.getPhoto();
        if (userPhoto != null && userPhoto.isEmpty()) {
            userPhoto = "null";
            Log.e(TAG, "onBindViewHolder: user with name " + user.getFullName() + " has empty name");
        }
        String finalUserPhoto = userPhoto;
        DataManager.getInstance().getPicasso()
                .load(userPhoto)
                .placeholder(R.drawable.user_bg)
                .resize(holder.mPhotoSize.x, holder.mPhotoSize.y)
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
                                .resize(holder.mPhotoSize.x, holder.mPhotoSize.y)
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
        return mData.size();
    }

    @NonNull
    public List<UserEntity> getData() {
        return mData;
    }

    public void setData(@NonNull List<UserEntity> data) {
        mData = data;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder
            implements OnClickListener, ItemTouchHelperViewHolder {

        @BindView(R.id.user_photo) AspectRatioImageView mUserPhoto;
        @BindView(R.id.username) TextView mUserName;
        @BindView(R.id.rating) TextView mRating;
        @BindView(R.id.code_lines) TextView mCodeLines;
        @BindView(R.id.count_projects) TextView mProjects;
        @BindView(R.id.about) TextView mAbout;
        @BindView(R.id.button_show_more) Button mMore;

        private OnItemClickListener mOnItemClickListener;
        private Point mPhotoSize;

        UserViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mOnItemClickListener = listener;

            mMore.setOnClickListener(this);

            int screenWidth = itemView.getResources().getDisplayMetrics().widthPixels;
            int screenHeight = (int) (screenWidth / mUserPhoto.getAspectRatio());
            mPhotoSize = new Point(screenWidth, screenHeight);

        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) mOnItemClickListener.onItemClick(getAdapterPosition());
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}