/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.broadcast.content;

import android.content.Context;

import com.top.android.inji.content.ResourceWriterManager;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.util.ToastUtils;

import com.top.android.inji.R;
import com.top.android.inji.content.ResourceWriterManager;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.util.ToastUtils;

public class LikeBroadcastManager extends ResourceWriterManager<LikeBroadcastWriter> {

    private static class InstanceHolder {
        public static final LikeBroadcastManager VALUE = new LikeBroadcastManager();
    }

    public static LikeBroadcastManager getInstance() {
        return InstanceHolder.VALUE;
    }

    /**
     * @deprecated Use {@link #write(Broadcast, boolean, Context)} instead.
     */
    public void write(long broadcastId, boolean like, Context context) {
        add(new LikeBroadcastWriter(broadcastId, like, this), context);
    }

    public boolean write(Broadcast broadcast, boolean like, Context context) {
        if (shouldWrite(broadcast, context)) {
            //noinspection deprecation
            write(broadcast.id, like, context);
            return true;
        } else {
            return false;
        }
    }

    private boolean shouldWrite(Broadcast broadcast, Context context) {
        if (broadcast.isAuthorOneself()) {
            ToastUtils.show(com.top.android.inji.R.string.broadcast_like_error_cannot_like_oneself, context);
            return false;
        } else {
            return true;
        }
    }

    public boolean isWriting(long broadcastId) {
        return findWriter(broadcastId) != null;
    }

    public boolean isWritingLike(long broadcastId) {
        LikeBroadcastWriter writer = findWriter(broadcastId);
        return writer != null && writer.isLike();
    }

    private LikeBroadcastWriter findWriter(long broadcastId) {
        for (LikeBroadcastWriter writer : getWriters()) {
            if (writer.getBroadcastId() == broadcastId) {
                return writer;
            }
        }
        return null;
    }
}
