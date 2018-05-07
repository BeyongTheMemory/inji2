/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.followship.content;

import me.zhanghai.android.douya.user.content.UserListResource;
import me.zhanghai.android.douya.util.FragmentUtils;

public abstract class FollowshipUserListResource extends UserListResource {

    // Not static because we are to be subclassed.
    private final String KEY_PREFIX = getClass().getName() + '.';

    private final String EXTRA_user_id = KEY_PREFIX + "user_id";

    protected FollowshipUserListResource setArguments(String userIdOrUid) {
        FragmentUtils.ensureArguments(this)
                .putString(EXTRA_user_id, userIdOrUid);
        return this;
    }

    protected String getUserIdOrUid() {
        return getArguments().getString(EXTRA_user_id);
    }
}
