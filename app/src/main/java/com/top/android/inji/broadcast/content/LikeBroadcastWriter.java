/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.broadcast.content;

import android.content.Context;

import com.top.android.inji.content.RequestResourceWriter;
import com.top.android.inji.content.ResourceWriterManager;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.util.LogUtils;
import com.top.android.inji.util.ToastUtils;

import com.top.android.inji.R;
import com.top.android.inji.content.RequestResourceWriter;
import com.top.android.inji.content.ResourceWriterManager;
import com.top.android.inji.eventbus.BroadcastUpdatedEvent;
import com.top.android.inji.eventbus.BroadcastWriteFinishedEvent;
import com.top.android.inji.eventbus.BroadcastWriteStartedEvent;
import com.top.android.inji.eventbus.EventBusUtils;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.util.LogUtils;
import com.top.android.inji.util.ToastUtils;

class LikeBroadcastWriter extends RequestResourceWriter<LikeBroadcastWriter, Broadcast> {

    private long mBroadcastId;
    private boolean mLike;

    LikeBroadcastWriter(long broadcastId, boolean like,
                        ResourceWriterManager<LikeBroadcastWriter> manager) {
        super(manager);

        mBroadcastId = broadcastId;
        mLike = like;
    }

    public long getBroadcastId() {
        return mBroadcastId;
    }

    public boolean isLike() {
        return mLike;
    }

    @Override
    protected ApiRequest<Broadcast> onCreateRequest() {
        return ApiService.getInstance().likeBroadcast(mBroadcastId, mLike);
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBusUtils.postAsync(new BroadcastWriteStartedEvent(mBroadcastId, this));
    }

    @Override
    public void onResponse(Broadcast response) {

        ToastUtils.show(mLike ? com.top.android.inji.R.string.broadcast_like_successful
                : com.top.android.inji.R.string.broadcast_unlike_successful, getContext());

        EventBusUtils.postAsync(new BroadcastUpdatedEvent(response, this));

        stopSelf();
    }

    @Override
    public void onErrorResponse(ApiError error) {

        LogUtils.e(error.toString());
        Context context = getContext();
        ToastUtils.show(context.getString(mLike ? com.top.android.inji.R.string.broadcast_like_failed_format
                        : com.top.android.inji.R.string.broadcast_unlike_failed_format,
                ApiError.getErrorString(error)), context);

        // Must notify to reset pending status. Off-screen items also needs to be invalidated.
        EventBusUtils.postAsync(new BroadcastWriteFinishedEvent(mBroadcastId, this));

        stopSelf();
    }
}
