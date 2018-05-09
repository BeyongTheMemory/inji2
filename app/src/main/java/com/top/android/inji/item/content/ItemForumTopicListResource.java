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
import com.top.android.inji.network.api.info.frodo.SimpleItemForumTopic;
import com.top.android.inji.util.FragmentUtils;

import java.util.Collections;
import java.util.List;

import com.top.android.inji.content.MoreBaseListResourceFragment;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.network.api.info.frodo.CollectableItem;
import com.top.android.inji.network.api.info.frodo.ItemForumTopicList;
import com.top.android.inji.network.api.info.frodo.SimpleItemForumTopic;
import com.top.android.inji.util.FragmentUtils;

public class ItemForumTopicListResource
        extends MoreBaseListResourceFragment<ItemForumTopicList, SimpleItemForumTopic> {

    private static final String KEY_PREFIX = ItemForumTopicListResource.class.getName() + '.';

    private static final String EXTRA_ITEM_TYPE = KEY_PREFIX + "item_type";
    private static final String EXTRA_ITEM_ID = KEY_PREFIX + "item_id";
    private static final String EXTRA_EPISODE = KEY_PREFIX + "episode";

    private CollectableItem.Type mItemType;
    private long mItemId;
    private Integer mEpisode;

    private static final String FRAGMENT_TAG_DEFAULT = ItemForumTopicListResource.class.getName();

    private static ItemForumTopicListResource newInstance(CollectableItem.Type itemType,
                                                          long itemId, Integer episode) {
        //noinspection deprecation
        return new ItemForumTopicListResource().setArguments(itemType, itemId, episode);
    }

    public static ItemForumTopicListResource attachTo(CollectableItem.Type itemType, long itemId,
                                                      Integer episode, Fragment fragment,
                                                      String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        ItemForumTopicListResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(itemType, itemId, episode);
            instance.targetAt(fragment, requestCode);
            FragmentUtils.add(instance, activity, tag);
        }
        return instance;
    }

    public static ItemForumTopicListResource attachTo(CollectableItem.Type itemType, long itemId,
                                                      Integer episode, Fragment fragment) {
        return attachTo(itemType, itemId, episode, fragment, FRAGMENT_TAG_DEFAULT,
                REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public ItemForumTopicListResource() {}

    protected ItemForumTopicListResource setArguments(CollectableItem.Type itemType, long itemId,
                                                      Integer episode) {
        Bundle arguments = FragmentUtils.ensureArguments(this);
        arguments.putSerializable(EXTRA_ITEM_TYPE, itemType);
        arguments.putLong(EXTRA_ITEM_ID, itemId);
        arguments.putSerializable(EXTRA_EPISODE, episode);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItemType = (CollectableItem.Type) getArguments().getSerializable(EXTRA_ITEM_TYPE);
        mItemId = getArguments().getLong(EXTRA_ITEM_ID);
        mEpisode = (Integer) getArguments().getSerializable(EXTRA_EPISODE);
    }

    @Override
    protected ApiRequest<ItemForumTopicList> onCreateRequest(Integer start, Integer count) {
        return ApiService.getInstance().getItemForumTopicList(mItemType, mItemId, mEpisode, start,
                count);
    }

    @Override
    protected void onLoadStarted() {
        getListener().onLoadForumTopicListStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean more, int count, boolean successful,
                                  List<SimpleItemForumTopic> response, ApiError error) {
        if (successful) {
            if (more) {
                append(response);
                getListener().onLoadForumTopicListFinished(getRequestCode());
                getListener().onForumTopicListAppended(getRequestCode(),
                        Collections.unmodifiableList(response));
            } else {
                set(response);
                getListener().onLoadForumTopicListFinished(getRequestCode());
                getListener().onForumTopicListChanged(getRequestCode(),
                        Collections.unmodifiableList(get()));
            }
        } else {
            getListener().onLoadForumTopicListFinished(getRequestCode());
            getListener().onLoadForumTopicListError(getRequestCode(), error);
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadForumTopicListStarted(int requestCode);
        void onLoadForumTopicListFinished(int requestCode);
        void onLoadForumTopicListError(int requestCode, ApiError error);
        /**
         * @param newForumTopicList Unmodifiable.
         */
        void onForumTopicListChanged(int requestCode, List<SimpleItemForumTopic> newForumTopicList);
        /**
         * @param appendedForumTopicList Unmodifiable.
         */
        void onForumTopicListAppended(int requestCode,
                                      List<SimpleItemForumTopic> appendedForumTopicList);
    }
}
