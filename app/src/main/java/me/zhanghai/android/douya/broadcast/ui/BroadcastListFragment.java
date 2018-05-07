/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.BindDimen;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.broadcast.content.TimelineBroadcastListResource;
import me.zhanghai.android.douya.util.FragmentUtils;

public class BroadcastListFragment extends BaseTimelineBroadcastListFragment {

    private static final String KEY_PREFIX = BroadcastListFragment.class.getName() + '.';

    private static final String EXTRA_USER_ID = KEY_PREFIX + "user_id";
    private static final String EXTRA_TOPIC = KEY_PREFIX + "topic";

    @BindDimen(R.dimen.toolbar_height)
    int mToolbarHeight;

    private Long mUserId;
    private String mTopic;

    public static BroadcastListFragment newInstance(Long userId, String topic) {
        //noinspection deprecation
        BroadcastListFragment fragment = new BroadcastListFragment();
        Bundle arguments = FragmentUtils.ensureArguments(fragment);
        arguments.putLong(EXTRA_USER_ID, userId);
        arguments.putString(EXTRA_TOPIC, topic);
        return fragment;
    }

    public BroadcastListFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mUserId = arguments.getLong(EXTRA_USER_ID);
        mTopic = arguments.getString(EXTRA_TOPIC);
    }

    @Override
    protected int getExtraPaddingTop() {
        return mToolbarHeight;
    }

    @Override
    protected TimelineBroadcastListResource onAttachResource() {
        return TimelineBroadcastListResource.attachTo(mUserId, mTopic, this);
    }

    @Override
    protected void onSendBroadcast() {
        Activity activity = getActivity();
        activity.startActivity(SendBroadcastActivity.makeTopicIntent(mTopic, activity));
    }
}
