/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.broadcast.content;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.top.android.inji.app.TargetedRetainedFragment;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.network.api.info.frodo.BroadcastLikerList;
import com.top.android.inji.user.content.BaseUserListResource;
import com.top.android.inji.util.FragmentUtils;

import java.util.List;

import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.network.api.info.apiv2.SimpleUser;
import com.top.android.inji.network.api.info.frodo.BroadcastLikerList;
import com.top.android.inji.user.content.BaseUserListResource;
import com.top.android.inji.util.FragmentUtils;

public class BroadcastLikerListResource extends BaseUserListResource<BroadcastLikerList> {

    private static final String FRAGMENT_TAG_DEFAULT = BroadcastLikerListResource.class.getName();

    private final String KEY_PREFIX = BroadcastLikerListResource.class.getName() + '.';

    private final String EXTRA_BROADCAST_ID = KEY_PREFIX + "broadcast_id";

    private long mBroadcastId;

    private static BroadcastLikerListResource newInstance(long broadcastId) {
        //noinspection deprecation
        return new BroadcastLikerListResource().setArguments(broadcastId);
    }

    public static BroadcastLikerListResource attachTo(long broadcastId, Fragment fragment,
                                                      String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        BroadcastLikerListResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(broadcastId);
            instance.targetAt(fragment, requestCode);
            FragmentUtils.add(instance, activity, tag);
        }
        return instance;
    }

    public static BroadcastLikerListResource attachTo(long broadcastId, Fragment fragment) {
        return attachTo(broadcastId, fragment, FRAGMENT_TAG_DEFAULT, TargetedRetainedFragment.REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public BroadcastLikerListResource() {}

    protected BroadcastLikerListResource setArguments(long broadcastId) {
        FragmentUtils.ensureArguments(this)
                .putLong(EXTRA_BROADCAST_ID, broadcastId);
        return this;
    }

    protected long getBroadcastId() {
        return mBroadcastId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBroadcastId = getArguments().getLong(EXTRA_BROADCAST_ID);
    }

    @Override
    protected ApiRequest<BroadcastLikerList> onCreateRequest(Integer start, Integer count) {
        return ApiService.getInstance().getBroadcastLikerList(getBroadcastId(), start, count);
    }

    @Override
    protected void onCallRawLoadFinished(boolean more, int count, boolean successful,
                                         BroadcastLikerList response, ApiError error) {
        onRawLoadFinished(more, count, successful, successful ? response.likers : null, error);
    }
}
