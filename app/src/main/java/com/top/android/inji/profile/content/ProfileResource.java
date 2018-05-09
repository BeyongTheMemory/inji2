/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.profile.content;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.top.android.inji.app.TargetedRetainedFragment;
import com.top.android.inji.broadcast.content.TimelineBroadcastListResource;
import com.top.android.inji.diary.content.UserDiaryListResource;
import com.top.android.inji.followship.content.FollowingListResource;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.info.dto.UserDTO;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.network.api.info.frodo.Diary;
import com.top.android.inji.network.api.info.frodo.SimpleReview;
import com.top.android.inji.network.api.info.frodo.SimpleUser;
import com.top.android.inji.network.api.info.frodo.UserItems;
import com.top.android.inji.review.content.BaseReviewListResource;
import com.top.android.inji.review.content.UserReviewListResource;
import com.top.android.inji.user.content.BaseUserListResource;
import com.top.android.inji.user.content.UserResource;
import com.top.android.inji.util.FragmentUtils;

import java.util.List;

import com.top.android.inji.app.TargetedRetainedFragment;
import com.top.android.inji.broadcast.content.TimelineBroadcastListResource;
import com.top.android.inji.diary.content.UserDiaryListResource;
import com.top.android.inji.followship.content.FollowingListResource;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.info.dto.UserDTO;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.network.api.info.apiv2.User;
import com.top.android.inji.network.api.info.frodo.Diary;
import com.top.android.inji.network.api.info.frodo.SimpleReview;
import com.top.android.inji.network.api.info.frodo.SimpleUser;
import com.top.android.inji.network.api.info.frodo.UserItems;
import com.top.android.inji.review.content.UserReviewListResource;
import com.top.android.inji.user.content.UserResource;
import com.top.android.inji.util.FragmentUtils;

public class ProfileResource extends TargetedRetainedFragment implements UserResource.Listener,
        TimelineBroadcastListResource.Listener, BaseUserListResource.Listener,
        UserDiaryListResource.Listener, UserItemListResource.Listener,
        BaseReviewListResource.Listener {

    private static final String KEY_PREFIX = ProfileResource.class.getName() + '.';

    private static final String EXTRA_USER_ID = KEY_PREFIX + "user_id";
    private static final String EXTRA_USER = KEY_PREFIX + "user";

    private Long mUserId;

    private UserDTO mUser;

    private UserResource mUserResource;
    private TimelineBroadcastListResource mBroadcastListResource;
    private FollowingListResource mFollowingListResource;
    private UserDiaryListResource mDiaryListResource;
    private UserItemListResource mUserItemListResource;
    private UserReviewListResource mReviewListResource;

    private boolean mHasError;

    private static final String FRAGMENT_TAG_DEFAULT = ProfileResource.class.getName();

    private static ProfileResource newInstance(
            Long userId, UserDTO user) {
        //noinspection deprecation
        return new ProfileResource().setArguments(userId, user);
    }

    public static ProfileResource attachTo(
            Long userId, UserDTO user,
            Fragment fragment, String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        ProfileResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(userId, user);
            instance.targetAt(fragment, requestCode);
            FragmentUtils.add(instance, activity, tag);
        }
        return instance;
    }

    public static ProfileResource attachTo(
            Long userId,
            UserDTO user,
            Fragment fragment) {
        return attachTo(userId, user, fragment, FRAGMENT_TAG_DEFAULT,
                REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public ProfileResource() {}

    protected ProfileResource setArguments(
            Long userId,
            UserDTO user) {
        Bundle arguments = FragmentUtils.ensureArguments(this);
        arguments.putLong(EXTRA_USER_ID, userId);
        arguments.putParcelable(EXTRA_USER, user);
        return this;
    }

    public Long getUserId() {
        // Can be called before onCreate() is called.
        ensureArguments();
        return mUserId;
    }

    public UserDTO getUser() {
        // Can be called before onCreate() is called.
        ensureArguments();
        return mUser;
    }

    public boolean hasUser() {
        // Can be called before onCreate() is called.
        ensureArguments();
        return mUser != null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ensureArguments();

        mUserResource = UserResource.attachTo(mUserId, mUser, this);
      //  ensureResourcesIfHasSimpleUser();
    }

    private void ensureArguments() {
        if (mUserId != null) {
            return;
        }
        Bundle arguments = getArguments();
        mUser = arguments.getParcelable(EXTRA_USER);
        if (mUser != null) {
            mUserId = mUser.getId();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mUserResource.detach();
        if (mBroadcastListResource != null) {
            mBroadcastListResource.detach();
        }
        if (mFollowingListResource != null) {
            mFollowingListResource.detach();
        }
        if (mDiaryListResource != null) {
            mDiaryListResource.detach();
        }
        if (mUserItemListResource != null) {
            mUserItemListResource.detach();
        }
        if (mReviewListResource != null) {
            mReviewListResource.detach();
        }

        Bundle arguments = getArguments();
        arguments.putLong(EXTRA_USER_ID, mUserId);
        arguments.putParcelable(EXTRA_USER, mUser);
    }

    @Override
    public void onLoadUserStarted(int requestCode) {}

    @Override
    public void onLoadUserFinished(int requestCode) {}

    @Override
    public void onLoadUserError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onUserChanged(int requestCode, UserDTO newUser) {
        mUser = newUser;
        mUserId = newUser.getId();
        getListener().onUserChanged(getRequestCode(), newUser);
        notifyChangedIfLoaded();
        //ensureResourcesIfHasSimpleUser();
    }

    @Override
    public void onUserWriteStarted(int requestCode) {
        getListener().onUserWriteStarted(getRequestCode());
    }

    @Override
    public void onUserWriteFinished(int requestCode) {
        getListener().onUserWriteFinished(getRequestCode());
    }

    @Override
    public void onLoadBroadcastListStarted(int requestCode) {}

    @Override
    public void onLoadBroadcastListFinished(int requestCode) {}

    @Override
    public void onLoadBroadcastListError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onBroadcastListChanged(int requestCode, List<Broadcast> newBroadcastList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onBroadcastListAppended(int requestCode, List<Broadcast> appendedBroadcastList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onBroadcastChanged(int requestCode, int position, Broadcast newBroadcast) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onBroadcastRemoved(int requestCode, int position) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onBroadcastWriteStarted(int requestCode, int position) {}

    @Override
    public void onBroadcastWriteFinished(int requestCode, int position) {}

    @Override
    public void onLoadUserListStarted(int requestCode) {}

    @Override
    public void onLoadUserListFinished(int requestCode) {}

    @Override
    public void onLoadUserListError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onUserListChanged(int requestCode, List<SimpleUser> newUserList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onUserListAppended(int requestCode, List<SimpleUser> appendedUserList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onLoadDiaryListStarted(int requestCode) {}

    @Override
    public void onLoadDiaryListFinished(int requestCode) {}

    @Override
    public void onLoadDiaryListError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onDiaryListChanged(int requestCode, List<Diary> newDiaryList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onDiaryListAppended(int requestCode, List<Diary> appendedDiaryList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onDiaryChanged(int requestCode, int position, Diary newDiary) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onDiaryRemoved(int requestCode, int position) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onLoadUserItemListStarted(int requestCode) {}

    @Override
    public void onLoadUserItemListFinished(int requestCode) {}

    @Override
    public void onLoadUserItemListError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onUserItemListChanged(int requestCode, List<UserItems> newUserItemList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onLoadReviewListStarted(int requestCode) {}

    @Override
    public void onLoadReviewListFinished(int requestCode) {}

    @Override
    public void onLoadReviewListError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onReviewListChanged(int requestCode, List<SimpleReview> newReviewList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onReviewListAppended(int requestCode, List<SimpleReview> appendedReviewList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onReviewChanged(int requestCode, int position, SimpleReview newReview) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onReviewRemoved(int requestCode, int position) {
        notifyChangedIfLoaded();
    }

    public boolean isAnyLoaded() {
        return hasUser()
                || (mBroadcastListResource != null && mBroadcastListResource.has())
                || (mFollowingListResource != null && mFollowingListResource.has())
                || (mDiaryListResource != null && mDiaryListResource.has())
                || (mUserItemListResource != null && mUserItemListResource.has())
                || (mReviewListResource != null && mReviewListResource.has());
    }

    public void notifyChangedIfLoaded() {
        //todo：载入跟随用户列表
        getListener().onChanged(getRequestCode(),
                getUser(),
                mBroadcastListResource != null ? mBroadcastListResource.get() : null,
               null,
                mDiaryListResource != null ? mDiaryListResource.get() : null,
                mUserItemListResource != null ? mUserItemListResource.get() : null,
                mReviewListResource != null ? mReviewListResource.get() : null);
    }

    private void notifyError(ApiError error) {
        if (!mHasError) {
            mHasError = true;
            getListener().onLoadError(getRequestCode(), error);
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadError(int requestCode, ApiError error);
        void onUserChanged(int requestCode, UserDTO newUser);
        void onUserWriteStarted(int requestCode);
        void onUserWriteFinished(int requestCode);
        void onChanged(int requestCode, UserDTO newUser, List<Broadcast> newBroadcastList,
                       List<UserDTO> newFollowingList, List<Diary> newDiaryList,
                       List<UserItems> newUserItemList, List<SimpleReview> newReviewList);
    }
}
