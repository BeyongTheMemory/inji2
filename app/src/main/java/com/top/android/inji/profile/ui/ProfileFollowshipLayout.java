/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.profile.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.top.android.inji.followship.ui.FollowingListActivity;
import com.top.android.inji.network.api.info.dto.UserDTO;
import com.top.android.inji.ui.FriendlyCardView;
import com.top.android.inji.util.ImageUtils;
import com.top.android.inji.util.ViewUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.top.android.inji.R;
import com.top.android.inji.followship.ui.FollowerListActivity;
import com.top.android.inji.followship.ui.FollowingListActivity;
import com.top.android.inji.network.api.info.apiv2.User;
import com.top.android.inji.network.api.info.dto.UserDTO;
import com.top.android.inji.network.api.info.frodo.SimpleUser;
import com.top.android.inji.ui.FriendlyCardView;
import com.top.android.inji.util.ImageUtils;
import com.top.android.inji.util.ViewUtils;

public class ProfileFollowshipLayout extends FriendlyCardView {

    private static final int USER_COUNT_MAX = 5;

    @BindView(com.top.android.inji.R.id.title)
    TextView mTitleText;
    @BindView(com.top.android.inji.R.id.following_list)
    LinearLayout mFollowingList;
    @BindView(com.top.android.inji.R.id.empty)
    View mEmptyView;
    @BindView(com.top.android.inji.R.id.view_more)
    TextView mViewMoreText;
    @BindView(com.top.android.inji.R.id.follower)
    TextView mFollwerText;

    public ProfileFollowshipLayout(Context context) {
        super(context);

        init();
    }

    public ProfileFollowshipLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ProfileFollowshipLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        ViewUtils.inflateInto(com.top.android.inji.R.layout.profile_followship_layout, this);
        ButterKnife.bind(this);
    }

    public void bind(final UserDTO userInfo, List<UserDTO> followingList) {

        final Context context = getContext();
        OnClickListener viewFollowingListListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(FollowingListActivity.makeIntent(userInfo.getId(),
                        context));
            }
        };
        mTitleText.setOnClickListener(viewFollowingListListener);
        mViewMoreText.setOnClickListener(viewFollowingListListener);

        int i = 0;
        for (final UserDTO user : followingList) {

            if (i >= USER_COUNT_MAX) {
                break;
            }

            if (i >= mFollowingList.getChildCount()) {
                ViewUtils.inflateInto(com.top.android.inji.R.layout.profile_user_item, mFollowingList);
            }
            View userLayout = mFollowingList.getChildAt(i);
            UserLayoutHolder holder = (UserLayoutHolder) userLayout.getTag();
            if (holder == null) {
                holder = new UserLayoutHolder(userLayout);
                userLayout.setTag(holder);
            }

            ImageUtils.loadAvatar(holder.avatarImage, user.getHeadUrl());
            holder.nameText.setText(user.getName());
            userLayout.setOnClickListener(view -> context.startActivity(ProfileActivity.makeIntent(
                    user, context)));

            ++i;
        }

        ViewUtils.setVisibleOrGone(mFollowingList, i != 0);
        ViewUtils.setVisibleOrGone(mEmptyView, i == 0);

        //todo:用户关注
        mViewMoreText.setText(context.getString(com.top.android.inji.R.string.view_more_with_count_format,
               10));
//        if (userInfo.followingCount > i) {
//            mViewMoreText.setText(context.getString(R.string.view_more_with_count_format,
//                    userInfo.followingCount));
//        } else {
//            mViewMoreText.setVisibility(GONE);
//        }

        for (int count = mFollowingList.getChildCount(); i < count; ++i) {
            ViewUtils.setVisibleOrGone(mFollowingList.getChildAt(i), false);
        }

        //todo:用户关注
        mFollwerText.setVisibility(GONE);
//        if (userInfo.followerCount > 0) {
//            mFollwerText.setText(context.getString(R.string.profile_follower_count_format,
//                    userInfo.followerCount));
//            mFollwerText.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    context.startActivity(FollowerListActivity.makeIntent(userInfo.getIdOrUid(),
//                            context));
//                }
//            });
//        } else {
//            mFollwerText.setVisibility(GONE);
//        }
    }

    static class UserLayoutHolder {

        @BindView(com.top.android.inji.R.id.avatar)
        public ImageView avatarImage;
        @BindView(com.top.android.inji.R.id.name)
        public TextView nameText;

        public UserLayoutHolder(View broadcastLayout) {
            ButterKnife.bind(this, broadcastLayout);
        }
    }
}
