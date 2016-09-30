package com.softdesign.devintensive.ui.adapters;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.network.restmodels.User;
import com.softdesign.devintensive.ui.views.AspectRatioImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Sergey Vorobyev
 */

public class UserListRecyclerAdapter
        extends RecyclerView.Adapter<UserListRecyclerAdapter.UserViewHolder> {

    private Context mContext;
    private List<User> mData;
    private Point mPhotoSize;
    private OnItemClickListener mOnItemClickListener;

    public UserListRecyclerAdapter(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
        return new UserViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = mData.get(position);

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
        Picasso.with(mContext)
                .load(user.getPublicInfo().getPhoto())
                .placeholder(R.drawable.user_bg)
                .resize(mPhotoSize.x, mPhotoSize.y)
                .onlyScaleDown()
                .centerCrop()
                .into(holder.mUserPhoto);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    @Nullable
    public List<User> getData() {
        return mData;
    }

    public void setData(@NonNull List<User> data) {
        mData = data;
        notifyDataSetChanged();
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
}
