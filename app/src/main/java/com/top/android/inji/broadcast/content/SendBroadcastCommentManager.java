/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.broadcast.content;

import android.content.Context;

import com.top.android.inji.content.ResourceWriterManager;

import com.top.android.inji.content.ResourceWriterManager;

public class SendBroadcastCommentManager extends ResourceWriterManager<SendBroadcastCommentWriter> {

    private static class InstanceHolder {
        public static final SendBroadcastCommentManager VALUE = new SendBroadcastCommentManager();
    }

    public static SendBroadcastCommentManager getInstance() {
        return InstanceHolder.VALUE;
    }

    public void write(long broadcastId, String comment, Context context) {
        add(new SendBroadcastCommentWriter(broadcastId, comment, this), context);
    }

    public boolean isWriting(long broadcastId) {
        return findWriter(broadcastId) != null;
    }

    public String getComment(long broadcastId) {
        SendBroadcastCommentWriter writer = findWriter(broadcastId);
        return writer != null ? writer.getComment() : null;
    }

    private SendBroadcastCommentWriter findWriter(long broadcastId) {
        for (SendBroadcastCommentWriter writer : getWriters()) {
            if (writer.getBroadcastId() == broadcastId) {
                return writer;
            }
        }
        return null;
    }
}
