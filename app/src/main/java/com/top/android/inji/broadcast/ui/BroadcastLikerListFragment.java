/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.broadcast.ui;

import android.os.Bundle;

import com.top.android.inji.broadcast.content.BroadcastLikerListResource;
import com.top.android.inji.content.MoreListResourceFragment;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.network.api.info.frodo.SimpleUser;
import com.top.android.inji.ui.SimpleAdapter;
import com.top.android.inji.user.ui.BaseUserListFragment;
import com.top.android.inji.user.ui.UserAdapter;
import com.top.android.inji.util.FragmentUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import com.top.android.inji.broadcast.content.BroadcastLikerListResource;
import com.top.android.inji.content.MoreListResourceFragment;
import com.top.android.inji.eventbus.BroadcastUpdatedEvent;
import com.top.android.inji.eventbus.EventBusUtils;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.network.api.info.frodo.SimpleUser;
import com.top.android.inji.ui.SimpleAdapter;
import com.top.android.inji.user.ui.BaseUserListFragment;
import com.top.android.inji.user.ui.UserAdapter;
import com.top.android.inji.util.FragmentUtils;

public class BroadcastLikerListFragment extends BaseUserListFragment {

    private final String KEY_PREFIX = BroadcastLikerListFragment.class.getName() + '.';

    private final String EXTRA_BROADCAST = KEY_PREFIX + "broadcast";

    private Broadcast mBroadcast;

    public static BroadcastLikerListFragment newInstance(Broadcast broadcast) {
        //noinspection deprecation
        return new BroadcastLikerListFragment().setArguments(broadcast);
    }

    /**
     * @deprecated Use {@link #newInstance(Broadcast)} instead.
     */
    public BroadcastLikerListFragment() {}

    protected BroadcastLikerListFragment setArguments(Broadcast broadcast) {
        FragmentUtils.ensureArguments(this)
                .putParcelable(EXTRA_BROADCAST, broadcast);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBroadcast = getArguments().getParcelable(EXTRA_BROADCAST);

        EventBusUtils.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBusUtils.unregister(this);
    }

    @Override
    protected MoreListResourceFragment<?, List<SimpleUser>> onAttachResource() {
        return BroadcastLikerListResource.attachTo(mBroadcast.id, this);
    }

    @Override
    protected SimpleAdapter<SimpleUser, ?> onCreateAdapter() {
        return new UserAdapter();
    }

    @Override
    protected void onListUpdated(List<SimpleUser> userList) {
        if (mBroadcast.likeCount < userList.size()) {
            mBroadcast.likeCount = userList.size();
            EventBusUtils.postAsync(new BroadcastUpdatedEvent(mBroadcast, this));
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastUpdated(BroadcastUpdatedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        Broadcast updatedBroadcast = event.update(mBroadcast, this);
        if (updatedBroadcast != null) {
            mBroadcast = updatedBroadcast;
        }
    }
}
