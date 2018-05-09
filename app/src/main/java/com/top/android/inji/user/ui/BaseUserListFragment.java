/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.user.ui;

import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.info.frodo.SimpleUser;
import com.top.android.inji.user.content.BaseUserListResource;

import java.util.List;

import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.info.frodo.SimpleUser;
import com.top.android.inji.ui.BaseListFragment;
import com.top.android.inji.user.content.BaseUserListResource;

public abstract class BaseUserListFragment extends BaseListFragment<SimpleUser>
        implements BaseUserListResource.Listener {

    @Override
    public void onLoadUserListStarted(int requestCode) {
        onLoadListStarted();
    }

    @Override
    public void onLoadUserListFinished(int requestCode) {
        onLoadListFinished();
    }

    @Override
    public void onLoadUserListError(int requestCode, ApiError error) {
        onLoadListError(error);
    }

    @Override
    public void onUserListChanged(int requestCode, List<SimpleUser> newUserList) {
        onListChanged(newUserList);
    }

    @Override
    public void onUserListAppended(int requestCode, List<SimpleUser> appendedUserList) {
        onListAppended(appendedUserList);
    }
}
