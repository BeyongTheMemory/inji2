/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.review.content;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;

import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.network.api.info.frodo.ReviewList;
import com.top.android.inji.util.FragmentUtils;

public class UserReviewListResource extends BaseReviewListResource {

    private static final String KEY_PREFIX = UserReviewListResource.class.getName() + '.';

    private final String EXTRA_user_id = KEY_PREFIX + "user_id";

    private String mUserIdOrUid;

    private static final String FRAGMENT_TAG_DEFAULT = UserReviewListResource.class.getName();

    private static UserReviewListResource newInstance(String userIdOrUid) {
        //noinspection deprecation
        return new UserReviewListResource().setArguments(userIdOrUid);
    }

    public static UserReviewListResource attachTo(String userIdOrUid, Fragment fragment, String tag,
                                                  int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        UserReviewListResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(userIdOrUid);
            instance.targetAt(fragment, requestCode);
            FragmentUtils.add(instance, activity, tag);
        }
        return instance;
    }

    public static UserReviewListResource attachTo(String userIdOrUid, Fragment fragment) {
        return attachTo(userIdOrUid, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public UserReviewListResource() {}

    protected UserReviewListResource setArguments(String userIdOrUid) {
        FragmentUtils.ensureArguments(this)
                .putString(EXTRA_user_id, userIdOrUid);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserIdOrUid = getArguments().getString(EXTRA_user_id);
    }

    @Override
    protected ApiRequest<ReviewList> onCreateRequest(Integer start, Integer count) {
        return ApiService.getInstance().getUserReviewList(mUserIdOrUid, start, count);
    }
}
