/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.user.content;

import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.info.frodo.UserList;

import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.info.frodo.UserList;

public abstract class UserListResource extends BaseUserListResource<UserList> {

    @Override
    protected void onCallRawLoadFinished(boolean more, int count, boolean successful,
                                         UserList response, ApiError error) {
        onRawLoadFinished(more, count, successful, response != null ? response.users : null, error);
    }
}
