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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.top.android.inji.R;
import com.top.android.inji.content.RequestResourceWriter;
import com.top.android.inji.content.ResourceWriterManager;
import com.top.android.inji.eventbus.BroadcastDeletedEvent;
import com.top.android.inji.eventbus.BroadcastUpdatedEvent;
import com.top.android.inji.eventbus.EventBusUtils;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.util.LogUtils;
import com.top.android.inji.util.ToastUtils;

class DeleteBroadcastWriter extends RequestResourceWriter<DeleteBroadcastWriter, Void> {

    private long mBroadcastId;
    private Broadcast mBroadcast;

    DeleteBroadcastWriter(long broadcastId, Broadcast broadcast,
                          ResourceWriterManager<DeleteBroadcastWriter> manager) {
        super(manager);

        mBroadcastId = broadcastId;
        mBroadcast = broadcast;

        EventBusUtils.register(this);
    }

    DeleteBroadcastWriter(long broadcastId, ResourceWriterManager<DeleteBroadcastWriter> manager) {
        this(broadcastId, null, manager);
    }

    DeleteBroadcastWriter(Broadcast broadcast,
                          ResourceWriterManager<DeleteBroadcastWriter> manager) {
        this(broadcast.id, broadcast, manager);
    }

    public long getBroadcastId() {
        return mBroadcastId;
    }

    @Override
    protected ApiRequest<Void> onCreateRequest() {
        return ApiService.getInstance().deleteBroadcast(mBroadcastId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBusUtils.unregister(this);
    }

    @Override
    public void onResponse(Void response) {

        ToastUtils.show(mBroadcast != null && mBroadcast.isSimpleRebroadcast() ?
                com.top.android.inji.R.string.broadcast_unrebroadcast_successful : com.top.android.inji.R.string.broadcast_delete_successful,
                getContext());

        if (mBroadcast != null) {
            Broadcast rebroadcastedBroadcast;
            if (mBroadcast.parentBroadcast != null) {
                rebroadcastedBroadcast = mBroadcast.parentBroadcast;
            } else if (mBroadcast.getParentBroadcastId() != null) {
                rebroadcastedBroadcast = null;
            } else {
                rebroadcastedBroadcast = mBroadcast.rebroadcastedBroadcast;
            }
            if (rebroadcastedBroadcast != null) {
                --rebroadcastedBroadcast.rebroadcastCount;
                EventBusUtils.postAsync(new BroadcastUpdatedEvent(rebroadcastedBroadcast, this));
            }
        }
        EventBusUtils.postAsync(new BroadcastDeletedEvent(mBroadcastId, this));

        stopSelf();
    }

    @Override
    public void onErrorResponse(ApiError error) {

        LogUtils.e(error.toString());
        Context context = getContext();
        ToastUtils.show(context.getString(mBroadcast != null && mBroadcast.isSimpleRebroadcast() ?
                        com.top.android.inji.R.string.broadcast_unrebroadcast_failed_format
                        : com.top.android.inji.R.string.broadcast_delete_failed_format,
                ApiError.getErrorString(error)), context);

        stopSelf();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastUpdated(BroadcastUpdatedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        Broadcast updatedBroadcast = event.update(mBroadcastId, mBroadcast, this);
        if (updatedBroadcast != null) {
            mBroadcast = updatedBroadcast;
        }
    }
}
