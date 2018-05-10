/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.followship.content;

import android.content.Context;

import com.top.android.inji.content.ResourceWriterManager;
import com.top.android.inji.network.api.info.apiv2.User;
import com.top.android.inji.util.ToastUtils;

import com.top.android.inji.R;
import com.top.android.inji.content.ResourceWriterManager;
import com.top.android.inji.network.api.info.apiv2.User;
import com.top.android.inji.util.ToastUtils;

public class FollowUserManager extends ResourceWriterManager<FollowUserWriter> {

    private static class InstanceHolder {
        public static final FollowUserManager VALUE = new FollowUserManager();
    }

    public static FollowUserManager getInstance() {
        return InstanceHolder.VALUE;
    }

    /**
     * @deprecated Use {@link #write(User, boolean, Context)} instead.
     */
    public void write(String userIdOrUid, boolean like, Context context) {
        add(new FollowUserWriter(userIdOrUid, like, this), context);
    }

    public boolean write(User user, boolean like, Context context) {
        if (shouldWrite(user, context)) {
            add(new FollowUserWriter(user, like, this), context);
            return true;
        } else {
            return false;
        }
    }

    private boolean shouldWrite(User user, Context context) {
        if (user.isOneself()) {
            ToastUtils.show(com.top.android.inji.R.string.user_follow_error_cannot_follow_oneself, context);
            return false;
        } else {
            return true;
        }
    }

    public boolean isWriting(String userIdOrUid) {
        return findWriter(userIdOrUid) != null;
    }

    public boolean isWritingFollow(String userIdOrUid) {
        FollowUserWriter writer = findWriter(userIdOrUid);
        return writer != null && writer.isFollow();
    }

    private FollowUserWriter findWriter(String userIdOrUid) {
        for (FollowUserWriter writer : getWriters()) {
            if (writer.hasUserIdOrUid(userIdOrUid)) {
                return writer;
            }
        }
        return null;
    }
}