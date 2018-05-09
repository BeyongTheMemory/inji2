/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.item.content;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.top.android.inji.content.MoreBaseListResourceFragment;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.network.api.info.frodo.CollectableItem;
import com.top.android.inji.network.api.info.frodo.SimpleItemCollection;
import com.top.android.inji.util.FragmentUtils;

import java.util.Collections;
import java.util.List;

import com.top.android.inji.content.MoreBaseListResourceFragment;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.network.api.info.frodo.CollectableItem;
import com.top.android.inji.network.api.info.frodo.SimpleItemCollection;
import com.top.android.inji.network.api.info.frodo.ItemCollectionList;
import com.top.android.inji.util.FragmentUtils;

public class ItemCollectionListResource
        extends MoreBaseListResourceFragment<ItemCollectionList, SimpleItemCollection> {

    private static final String KEY_PREFIX = ItemCollectionListResource.class.getName() + '.';

    private static final String EXTRA_ITEM_TYPE = KEY_PREFIX + "item_type";
    private static final String EXTRA_ITEM_ID = KEY_PREFIX + "item_id";
    private static final String EXTRA_FOLLOWINGS_FIRST = KEY_PREFIX + "followings_first";

    private CollectableItem.Type mItemType;
    private long mItemId;
    private boolean mFollowingsFirst;

    private static final String FRAGMENT_TAG_DEFAULT = ItemCollectionListResource.class.getName();

    private static ItemCollectionListResource newInstance(CollectableItem.Type itemType,
                                                          long itemId, boolean followingsFirst) {
        //noinspection deprecation
        return new ItemCollectionListResource().setArguments(itemType, itemId, followingsFirst);
    }

    public static ItemCollectionListResource attachTo(CollectableItem.Type itemType, long itemId,
                                                      boolean followingsFirst, Fragment fragment,
                                                      String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        ItemCollectionListResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(itemType, itemId, followingsFirst);
            instance.targetAt(fragment, requestCode);
            FragmentUtils.add(instance, activity, tag);
        }
        return instance;
    }

    public static ItemCollectionListResource attachTo(CollectableItem.Type itemType, long itemId,
                                                      boolean followingsFirst, Fragment fragment) {
        return attachTo(itemType, itemId, followingsFirst, fragment, FRAGMENT_TAG_DEFAULT,
                REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public ItemCollectionListResource() {}

    protected ItemCollectionListResource setArguments(CollectableItem.Type itemType, long itemId,
                                                      boolean followingsFirst) {
        Bundle arguments = FragmentUtils.ensureArguments(this);
        arguments.putSerializable(EXTRA_ITEM_TYPE, itemType);
        arguments.putLong(EXTRA_ITEM_ID, itemId);
        arguments.putBoolean(EXTRA_FOLLOWINGS_FIRST, followingsFirst);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItemType = (CollectableItem.Type) getArguments().getSerializable(EXTRA_ITEM_TYPE);
        mItemId = getArguments().getLong(EXTRA_ITEM_ID);
        mFollowingsFirst = getArguments().getBoolean(EXTRA_FOLLOWINGS_FIRST);
    }

    @Override
    protected ApiRequest<ItemCollectionList> onCreateRequest(Integer start, Integer count) {
        return ApiService.getInstance().getItemCollectionList(mItemType, mItemId, mFollowingsFirst,
                start, count);
    }

    @Override
    protected void onLoadStarted() {
        getListener().onLoadItemCollectionListStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean more, int count, boolean successful,
                                  List<SimpleItemCollection> response, ApiError error) {
        if (successful) {
            if (more) {
                append(response);
                getListener().onLoadItemCollectionListFinished(getRequestCode());
                getListener().onItemCollectionListAppended(getRequestCode(),
                        Collections.unmodifiableList(response));
            } else {
                set(response);
                getListener().onLoadItemCollectionListFinished(getRequestCode());
                getListener().onItemCollectionListChanged(getRequestCode(),
                        Collections.unmodifiableList(get()));
            }
        } else {
            getListener().onLoadItemCollectionListFinished(getRequestCode());
            getListener().onLoadItemCollectionListError(getRequestCode(), error);
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadItemCollectionListStarted(int requestCode);
        void onLoadItemCollectionListFinished(int requestCode);
        void onLoadItemCollectionListError(int requestCode, ApiError error);
        /**
         * @param newItemCollectionList Unmodifiable.
         */
        void onItemCollectionListChanged(int requestCode,
                                         List<SimpleItemCollection> newItemCollectionList);
        /**
         * @param appendedItemCollectionList Unmodifiable.
         */
        void onItemCollectionListAppended(int requestCode,
                                          List<SimpleItemCollection> appendedItemCollectionList);
    }
}
