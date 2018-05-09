/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.followship.ui;

import android.os.Bundle;

import com.top.android.inji.ui.SimpleAdapter;
import com.top.android.inji.user.ui.BaseUserListFragment;
import com.top.android.inji.user.ui.UserAdapter;
import com.top.android.inji.util.FragmentUtils;

import com.top.android.inji.network.api.info.frodo.SimpleUser;
import com.top.android.inji.ui.SimpleAdapter;
import com.top.android.inji.user.ui.BaseUserListFragment;
import com.top.android.inji.user.ui.UserAdapter;
import com.top.android.inji.util.FragmentUtils;

public abstract class FollowshipUserListFragment extends BaseUserListFragment {

    // Not static because we are to be subclassed.
    private final String KEY_PREFIX = getClass().getName() + '.';

    private final String EXTRA_user_id = KEY_PREFIX + "user_id";

    private String mUserIdOrUid;

    protected FollowshipUserListFragment setArguments(String userIdOrUid) {
        FragmentUtils.ensureArguments(this)
                .putString(EXTRA_user_id, userIdOrUid);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserIdOrUid = getArguments().getString(EXTRA_user_id);
    }

    @Override
    protected SimpleAdapter<SimpleUser, ?> onCreateAdapter() {
        return new UserAdapter();
    }

    protected String getUserIdOrUid() {
        return mUserIdOrUid;
    }
}
