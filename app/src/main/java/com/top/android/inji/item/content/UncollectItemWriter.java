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
import com.top.android.inji.util.LogUtils;
import com.top.android.inji.util.ToastUtils;

import com.top.android.inji.R;
import com.top.android.inji.content.RequestResourceWriter;
import com.top.android.inji.content.ResourceWriterManager;
import com.top.android.inji.eventbus.EventBusUtils;
import com.top.android.inji.eventbus.ItemUncollectedEvent;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.network.api.info.frodo.CollectableItem;
import com.top.android.inji.network.api.info.frodo.ItemCollection;
import com.top.android.inji.util.LogUtils;
import com.top.android.inji.util.ToastUtils;

class UncollectItemWriter extends RequestResourceWriter<UncollectItemWriter, ItemCollection> {

    private CollectableItem.Type mItemType;
    private long mItemId;

    UncollectItemWriter(CollectableItem.Type itemType, long itemId,
                        ResourceWriterManager<UncollectItemWriter> manager) {
        super(manager);

        mItemType = itemType;
        mItemId = itemId;
    }

    UncollectItemWriter(CollectableItem item, ResourceWriterManager<UncollectItemWriter> manager) {
        this(item.getType(), item.id, manager);
    }

    public CollectableItem.Type getItemType() {
        return mItemType;
    }

    public long getItemId() {
        return mItemId;
    }

    @Override
    protected ApiRequest<ItemCollection> onCreateRequest() {
        return ApiService.getInstance().uncollectItem(mItemType, mItemId);
    }

    @Override
    public void onResponse(ItemCollection response) {

        Context context = getContext();
        ToastUtils.show(context.getString(R.string.item_uncollect_successful_format,
                mItemType.getName(context)), context);

        EventBusUtils.postAsync(new ItemUncollectedEvent(mItemType, mItemId, this));

        stopSelf();
    }

    @Override
    public void onErrorResponse(ApiError error) {

        LogUtils.e(error.toString());
        Context context = getContext();
        ToastUtils.show(context.getString(R.string.item_uncollect_failed_format,
                mItemType.getName(context), ApiError.getErrorString(error)), context);

        stopSelf();
    }
}
