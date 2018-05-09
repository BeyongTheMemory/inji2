/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.profile.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.top.android.inji.network.api.info.dto.UserDTO;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.network.api.info.frodo.Diary;
import com.top.android.inji.network.api.info.frodo.SimpleReview;
import com.top.android.inji.network.api.info.frodo.UserItems;
import com.top.android.inji.ui.BarrierDataAdapter;
import com.top.android.inji.util.ViewUtils;

import java.util.List;

import com.top.android.inji.R;
import com.top.android.inji.network.api.info.apiv2.User;
import com.top.android.inji.network.api.info.dto.UserDTO;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.network.api.info.frodo.Diary;
import com.top.android.inji.network.api.info.frodo.SimpleReview;
import com.top.android.inji.network.api.info.frodo.SimpleUser;
import com.top.android.inji.network.api.info.frodo.UserItems;
import com.top.android.inji.ui.BarrierDataAdapter;
import com.top.android.inji.util.ViewUtils;

public class ProfileDataAdapter extends BarrierDataAdapter<ProfileDataAdapter.ViewHolder> {

    private enum Items {
        INTRODUCTION,
        BROADCASTS,
        FOLLOWSHIP,
        DIARIES,
        BOOKS,
        MOVIES,
        MUSIC,
        REVIEWS
    }

    private ProfileIntroductionLayout.Listener mListener;

    private Data mData;

    public ProfileDataAdapter(ProfileIntroductionLayout.Listener listener) {
        mListener = listener;
    }

    public void setData(Data data) {
        mData = data;
        notifyDataChanged();
    }

    @Override
    public int getTotalItemCount() {
        return Items.values().length;
    }

    @Override
    protected boolean isItemLoaded(int position) {
        if (mData == null) {
            return false;
        }
        if (mData.user == null) {
            return false;
        }
        switch (Items.values()[position]) {
            case INTRODUCTION:
                // HACK: For better visual results, wait until broadcasts are loaded so that we have
                // sufficient height.
                // Fall through!
                //return true;
            case BROADCASTS:
                return mData.broadcastList != null;
            case FOLLOWSHIP:
                return mData.followingList != null;
            case DIARIES:
                return mData.diaryList != null;
            case BOOKS:
                return mData.userItemList != null;
            case MOVIES:
                return mData.userItemList != null;
            case MUSIC:
                return mData.userItemList != null;
            case REVIEWS:
                return mData.reviewList != null;
            default:
                throw new IllegalArgumentException();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes;
        switch (Items.values()[viewType]) {
            case INTRODUCTION:
                layoutRes = com.top.android.inji.R.layout.profile_introduction_item;
                break;
            case BROADCASTS:
                layoutRes = com.top.android.inji.R.layout.profile_broadcasts_item;
                break;
            case FOLLOWSHIP:
                layoutRes = com.top.android.inji.R.layout.profile_followship_item;
                break;
            case DIARIES:
                layoutRes = com.top.android.inji.R.layout.profile_diaries_item;
                break;
            case BOOKS:
                layoutRes = com.top.android.inji.R.layout.profile_books_item;
                break;
            case MOVIES:
                layoutRes = com.top.android.inji.R.layout.profile_movies_item;
                break;
            case MUSIC:
                layoutRes = com.top.android.inji.R.layout.profile_music_item;
                break;
            case REVIEWS:
                layoutRes = com.top.android.inji.R.layout.profile_reviews_item;
                break;
            default:
                throw new IllegalArgumentException();
        }
        View itemView;
        if (ViewUtils.isInLandscape(parent.getContext())) {
            itemView = ViewUtils.inflateWithTheme(layoutRes, parent, com.top.android.inji.R.style.Theme_Douya);
        } else {
            itemView = ViewUtils.inflate(layoutRes, parent);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //todo:查看用户各类兴趣列表
        switch (Items.values()[position]) {
            case INTRODUCTION: {
                ProfileIntroductionLayout layout = ((ProfileIntroductionLayout) holder.getChild());
                layout.bind(mData.user);
                layout.setListener(mListener);
                break;
            }
            case BROADCASTS:
                ((ProfileBroadcastsLayout) holder.getChild()).bind(mData.user,
                        mData.broadcastList);
                break;
            case FOLLOWSHIP:
                ((ProfileFollowshipLayout) holder.getChild()).bind(mData.user,
                        mData.followingList);
                break;
//            case DIARIES:
//                ((ProfileDiariesLayout) holder.getChild()).bind(mData.user, mData.diaryList);
//                break;
//            case BOOKS:
//                ((ProfileBooksLayout) holder.getChild()).bind(mData.user, mData.userItemList);
//                break;
//            case MOVIES:
//                ((ProfileMoviesLayout) holder.getChild()).bind(mData.user, mData.userItemList);
//                break;
//            case MUSIC:
//                ((ProfileMusicLayout) holder.getChild()).bind(mData.user, mData.userItemList);
//                break;
//            case REVIEWS:
//                ((ProfileReviewsLayout) holder.getChild()).bind(mData.user, mData.reviewList);
//                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static class Data {

        public UserDTO user;
        public List<Broadcast> broadcastList;
        public List<UserDTO> followingList;
        public List<Diary> diaryList;
        public List<UserItems> userItemList;
        public List<SimpleReview> reviewList;

        public Data(UserDTO user, List<Broadcast> broadcastList, List<UserDTO> followingList,
                    List<Diary> diaryList, List<UserItems> userItemList,
                    List<SimpleReview> reviewList) {
            this.user = user;
            this.broadcastList = broadcastList;
            this.followingList = followingList;
            this.diaryList = diaryList;
            this.userItemList = userItemList;
            this.reviewList = reviewList;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public View getChild() {
            return ((ViewGroup) itemView).getChildAt(0);
        }
    }
}
