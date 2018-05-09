/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.account.content;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.top.android.inji.network.RequestFragment;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.network.api.info.response.AuthenticationResponse;

import com.top.android.inji.network.RequestFragment;
import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.network.api.info.response.AuthenticationResponse;
import com.top.android.inji.util.FragmentUtils;

public class AuthenticateRequest extends RequestFragment<AuthenticateRequest.RequestState,
        AuthenticationResponse> {

    private static final String FRAGMENT_TAG_DEFAULT = AuthenticateRequest.class.getName();

    public static AuthenticateRequest attachTo(Fragment fragment, String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        AuthenticateRequest instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            //noinspection deprecation
            instance = new AuthenticateRequest();
            instance.targetAt(fragment, requestCode);
            FragmentUtils.add(instance, activity, tag);
        }
        return instance;
    }

    public static AuthenticateRequest attachTo(Fragment fragment) {
        return attachTo(fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public AuthenticateRequest() {}

    public void start(Context context,String username, String password) {
        start(new RequestState(context,username, password));
    }

    @Override
    protected ApiRequest<AuthenticationResponse> onCreateRequest(RequestState requestState) {
        return ApiService.getInstance().authenticate(requestState.context,
                requestState.username, requestState.password);
    }

    @Override
    protected void onRequestStarted() {
        getListener().onAuthenticateStarted(getRequestCode());
    }

    @Override
    protected void onRequestFinished(boolean successful, RequestState requestState,
                                     AuthenticationResponse response, ApiError error) {
        getListener().onAuthenticateFinished(getRequestCode());
        if (successful && response.getUserDTO() != null) {
            getListener().onAuthenticateSuccess(getRequestCode(), requestState, response);
        } else {
            getListener().onAuthenticateError(getRequestCode(), requestState, error);
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public static class RequestState {

        public Context context;
        public String username;
        public String password;

        public RequestState(Context context, String username, String password) {
            this.context = context;
            this.username = username;
            this.password = password;
        }
    }

    public interface Listener {
        void onAuthenticateStarted(int requestCode);
        void onAuthenticateFinished(int requestCode);
        void onAuthenticateSuccess(int requestCode, RequestState requestState,
                                   AuthenticationResponse response);
        void onAuthenticateError(int requestCode, RequestState requestState, ApiError error);
    }
}
