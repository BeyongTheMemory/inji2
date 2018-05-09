/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.user.content;

import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.info.frodo.SimpleUser;

import java.util.List;

import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.info.frodo.SimpleUser;

public abstract class RawUserListResource extends BaseUserListResource<List<SimpleUser>> {

    @Override
    protected void onCallRawLoadFinished(boolean more, int count, boolean successful,
                                         List<SimpleUser> response, ApiError error) {
        onRawLoadFinished(more, count, successful, response, error);
    }
}
