/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.broadcast.content;

import android.content.Context;

import com.top.android.inji.content.RequestResourceWriter;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.network.api.info.frodo.Comment;
import com.top.android.inji.util.LogUtils;
import com.top.android.inji.util.ToastUtils;

import com.top.android.inji.R;
import com.top.android.inji.content.RequestResourceWriter;
import com.top.android.inji.eventbus.BroadcastCommentSendErrorEvent;
import com.top.android.inji.eventbus.BroadcastCommentSentEvent;
import com.top.android.inji.eventbus.EventBusUtils;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.network.api.info.frodo.Comment;
import com.top.android.inji.util.LogUtils;
import com.top.android.inji.util.ToastUtils;

class SendBroadcastCommentWriter extends RequestResourceWriter<SendBroadcastCommentWriter, Comment> {

    private long mBroadcastId;
    private String  mComment;

    SendBroadcastCommentWriter(long broadcastId, String comment,
                               SendBroadcastCommentManager manager) {
        super(manager);

        mBroadcastId = broadcastId;
        mComment = comment;
    }

    public long getBroadcastId() {
        return mBroadcastId;
    }

    public String getComment() {
        return mComment;
    }

    @Override
    protected ApiRequest<Comment> onCreateRequest() {
        return ApiService.getInstance().sendBroadcastComment(mBroadcastId, mComment);
    }

    @Override
    public void onResponse(Comment response) {

        ToastUtils.show(com.top.android.inji.R.string.broadcast_send_comment_successful, getContext());

        EventBusUtils.postAsync(new BroadcastCommentSentEvent(mBroadcastId, response, this));

        stopSelf();
    }

    @Override
    public void onErrorResponse(ApiError error) {

        LogUtils.e(error.toString());
        Context context = getContext();
        ToastUtils.show(context.getString(com.top.android.inji.R.string.broadcast_send_comment_failed_format,
                ApiError.getErrorString(error)), context);

        EventBusUtils.postAsync(new BroadcastCommentSendErrorEvent(mBroadcastId, this));

        stopSelf();
    }
}
