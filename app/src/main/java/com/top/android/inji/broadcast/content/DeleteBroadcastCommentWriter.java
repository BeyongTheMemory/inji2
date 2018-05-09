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
import com.top.android.inji.util.LogUtils;
import com.top.android.inji.util.ToastUtils;

import com.top.android.inji.R;
import com.top.android.inji.content.RequestResourceWriter;
import com.top.android.inji.eventbus.CommentDeletedEvent;
import com.top.android.inji.eventbus.EventBusUtils;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.util.LogUtils;
import com.top.android.inji.util.ToastUtils;

class DeleteBroadcastCommentWriter extends RequestResourceWriter<DeleteBroadcastCommentWriter, Void> {

    private long mBroadcastId;
    private long mCommentId;

    DeleteBroadcastCommentWriter(long broadcastId, long commentId,
                                 DeleteBroadcastCommentManager manager) {
        super(manager);

        mBroadcastId = broadcastId;
        mCommentId = commentId;
    }

    public long getBroadcastId() {
        return mBroadcastId;
    }

    public long getCommentId() {
        return mCommentId;
    }

    @Override
    protected ApiRequest<Void> onCreateRequest() {
        return ApiService.getInstance().deleteBroadcastComment(mBroadcastId, mCommentId);
    }

    @Override
    public void onResponse(Void response) {

        ToastUtils.show(com.top.android.inji.R.string.broadcast_comment_delete_successful, getContext());

        EventBusUtils.postAsync(new CommentDeletedEvent(mCommentId, this));

        stopSelf();
    }

    @Override
    public void onErrorResponse(ApiError error) {

        LogUtils.e(error.toString());
        Context context = getContext();
        ToastUtils.show(context.getString(com.top.android.inji.R.string.broadcast_comment_delete_failed_format,
                ApiError.getErrorString(error)), context);

        stopSelf();
    }
}
