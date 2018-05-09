/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.item.content;

import android.content.Context;

import com.top.android.inji.content.RequestResourceWriter;
import com.top.android.inji.content.ResourceWriterManager;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.network.api.info.frodo.CollectableItem;
import com.top.android.inji.network.api.info.frodo.ItemCollection;
import com.top.android.inji.network.api.info.frodo.ItemCollectionState;
import com.top.android.inji.util.LogUtils;
import com.top.android.inji.util.ToastUtils;

import java.util.List;

import com.top.android.inji.R;
import com.top.android.inji.content.RequestResourceWriter;
import com.top.android.inji.content.ResourceWriterManager;
import com.top.android.inji.eventbus.EventBusUtils;
import com.top.android.inji.eventbus.ItemCollectErrorEvent;
import com.top.android.inji.eventbus.ItemCollectedEvent;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.network.api.info.frodo.CollectableItem;
import com.top.android.inji.network.api.info.frodo.ItemCollection;
import com.top.android.inji.network.api.info.frodo.ItemCollectionState;
import com.top.android.inji.util.LogUtils;
import com.top.android.inji.util.ToastUtils;

class CollectItemWriter extends RequestResourceWriter<CollectItemWriter, ItemCollection> {

    private CollectableItem.Type mItemType;
    private long mItemId;
    private ItemCollectionState mState;
    private int mRating;
    private List<String> mTags;
    private String mComment;
    private List<Long> mGamePlatformIds;
    private boolean mShareToBroadcast;
    private boolean mShareToWeibo;
    private boolean mShareToWeChatMoments;

    CollectItemWriter(CollectableItem.Type itemType, long itemId, ItemCollectionState state,
                      int rating, List<String> tags, String comment, List<Long> gamePlatformIds,
                      boolean shareToBroadcast, boolean shareToWeibo, boolean shareToWeChatMoments,
                      ResourceWriterManager<CollectItemWriter> manager) {
        super(manager);

        mItemType = itemType;
        mItemId = itemId;
        mState = state;
        mRating = rating;
        mTags = tags;
        mComment = comment;
        mGamePlatformIds = gamePlatformIds;
        mShareToBroadcast = shareToBroadcast;
        mShareToWeibo = shareToWeibo;
        mShareToWeChatMoments = shareToWeChatMoments;
    }

    CollectItemWriter(CollectableItem item, ItemCollectionState state, int rating,
                      List<String> tags, String comment, List<Long> gamePlatformIds,
                      boolean shareToBroadcast, boolean shareToWeibo, boolean shareToWeChatMoments,
                      ResourceWriterManager<CollectItemWriter> manager) {
        this(item.getType(), item.id, state, rating, tags, comment, gamePlatformIds,
                shareToBroadcast, shareToWeibo, shareToWeChatMoments, manager);
    }

    public CollectableItem.Type getItemType() {
        return mItemType;
    }

    public long getItemId() {
        return mItemId;
    }

    public ItemCollectionState getState() {
        return mState;
    }

    public int getRating() {
        return mRating;
    }

    public List<String> getTags() {
        return mTags;
    }

    public String getComment() {
        return mComment;
    }

    public List<Long> getGamePlatformIds() {
        return mGamePlatformIds;
    }

    public boolean getShareToBroadcast() {
        return mShareToBroadcast;
    }

    public boolean getShareToWeibo() {
        return mShareToWeibo;
    }

    public boolean getShareToWeChatMoments() {
        return mShareToWeChatMoments;
    }

    @Override
    protected ApiRequest<ItemCollection> onCreateRequest() {
        return ApiService.getInstance().collectItem(mItemType, mItemId, mState, mRating, mTags,
                mComment, mGamePlatformIds, mShareToBroadcast, mShareToWeibo,
                mShareToWeChatMoments);
    }

    @Override
    public void onResponse(ItemCollection response) {

        Context context = getContext();
        ToastUtils.show(context.getString(R.string.item_collect_successful_format,
                mItemType.getName(context)), context);

        EventBusUtils.postAsync(new ItemCollectedEvent(mItemType, mItemId, response, this));

        stopSelf();
    }

    @Override
    public void onErrorResponse(ApiError error) {

        LogUtils.e(error.toString());
        Context context = getContext();
        ToastUtils.show(context.getString(R.string.item_collect_failed_format,
                mItemType.getName(context), ApiError.getErrorString(error)), context);

        EventBusUtils.postAsync(new ItemCollectErrorEvent(mItemType, mItemId, this));

        stopSelf();
    }
}
