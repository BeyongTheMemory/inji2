/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.user.content;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.top.android.inji.content.ResourceFragment;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.network.api.info.dto.UserDTO;
import com.top.android.inji.util.FragmentUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.top.android.inji.content.ResourceFragment;
import com.top.android.inji.eventbus.EventBusUtils;
import com.top.android.inji.eventbus.UserUpdatedEvent;
import com.top.android.inji.eventbus.UserWriteFinishedEvent;
import com.top.android.inji.eventbus.UserWriteStartedEvent;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.network.api.info.apiv2.SimpleUser;
import com.top.android.inji.network.api.info.apiv2.User;
import com.top.android.inji.network.api.info.dto.UserDTO;
import com.top.android.inji.util.FragmentUtils;

public class UserResource extends ResourceFragment<UserDTO, UserDTO> {

    // Not static because we are to be subclassed.
    private final String KEY_PREFIX = getClass().getName() + '.';

    private final String EXTRA_USER_ID = KEY_PREFIX + "user_id";
    private final String EXTRA_USER = KEY_PREFIX + "user";

    private Long mUserId;
    //private SimpleUser mSimpleUser;
    private UserDTO mExtraUser;

    private static final String FRAGMENT_TAG_DEFAULT = UserResource.class.getName();

    private static UserResource newInstance(Long userId, UserDTO user) {
        //noinspection deprecation
        return new UserResource().setArguments(userId, user);
    }

    public static UserResource attachTo(Long userId, UserDTO user,
                                        Fragment fragment, String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        UserResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(userId, user);
            instance.targetAt(fragment, requestCode);
            FragmentUtils.add(instance, activity, tag);
        }
        return instance;
    }

    public static UserResource attachTo(Long userId, UserDTO user,
                                        Fragment fragment) {
        return attachTo(userId, user, fragment, FRAGMENT_TAG_DEFAULT,
                REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public UserResource() {}

    protected UserResource setArguments(Long userId, UserDTO user) {
        Bundle arguments = FragmentUtils.ensureArguments(this);
        arguments.putLong(EXTRA_USER_ID, userId);
        arguments.putParcelable(EXTRA_USER, user);
        return this;
    }

    public Long getUserId() {
        ensureArguments();
        return mUserId;
    }


    @Override
    public UserDTO get() {
        UserDTO user = super.get();
        if (user == null) {
            // Can be called before onCreate() is called.
            ensureArguments();
            user = mExtraUser;
        }
        return user;
    }

    @Override
    protected void set(UserDTO user) {
        super.set(user);

        user = get();
        if (user != null) {
            mUserId = user.getId();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ensureArguments();
    }

    private void ensureArguments() {
        if (mUserId != null) {
            return;
        }
        Bundle arguments = getArguments();
        mExtraUser = arguments.getParcelable(EXTRA_USER);
        if (mExtraUser != null) {
            mUserId = mExtraUser.getId();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (has()) {
            UserDTO user = get();
            setArguments(user.getId(), user);
        }
    }

    @Override
    protected ApiRequest<UserDTO> onCreateRequest() {
        /**
         * todo：这里查询不到信息，修改
         */
        return ApiService.getInstance().getUser(mUserId+"");
    }

    @Override
    protected void onLoadStarted() {
        getListener().onLoadUserStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean successful, UserDTO response, ApiError error) {
        if (successful) {
            set(response);
            onLoadSuccess(response);
            getListener().onLoadUserFinished(getRequestCode());
            getListener().onUserChanged(getRequestCode(), get());
            EventBusUtils.postAsync(new UserUpdatedEvent(response, this));
        } else {
            getListener().onLoadUserFinished(getRequestCode());
            getListener().onLoadUserError(getRequestCode(), error);
        }
    }

    protected void onLoadSuccess(UserDTO user) {}

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUserUpdated(UserUpdatedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }
        /**
         * todo:更新用户信息
         */

//        if (event.mUser.isIdOrUid(mUserId)) {
//            set(event.mUser);
//            getListener().onUserChanged(getRequestCode(), get());
//        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUserWriteStarted(UserWriteStartedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        // Only call listener when we have the data.
        if (mExtraUser != null) {
            getListener().onUserWriteStarted(getRequestCode());
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUserWriteFinished(UserWriteFinishedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        // Only call listener when we have the data.
        if (mExtraUser != null) {
            getListener().onUserWriteFinished(getRequestCode());
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadUserStarted(int requestCode);
        void onLoadUserFinished(int requestCode);
        void onLoadUserError(int requestCode, ApiError error);
        void onUserChanged(int requestCode, UserDTO newUser);
        void onUserWriteStarted(int requestCode);
        void onUserWriteFinished(int requestCode);
    }
}
