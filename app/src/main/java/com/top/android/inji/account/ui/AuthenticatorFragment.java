/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.account.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.top.android.inji.account.content.AuthenticateRequest;
import com.top.android.inji.account.info.AccountContract;
import com.top.android.inji.account.util.AccountUtils;
import com.top.android.inji.account.util.AuthenticatorUtils;
import com.top.android.inji.network.api.ApiContract;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.info.dto.UserDTO;
import com.top.android.inji.network.api.info.response.AuthenticationResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.top.android.inji.R;
import com.top.android.inji.account.content.AuthenticateRequest;
import com.top.android.inji.account.info.AccountContract;
import com.top.android.inji.account.util.AccountUtils;
import com.top.android.inji.account.util.AuthenticatorUtils;
import com.top.android.inji.network.api.ApiContract.Response.Error.Codes;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.info.response.AuthenticationResponse;
import com.top.android.inji.network.api.info.dto.UserDTO;
import com.top.android.inji.util.FragmentUtils;
import com.top.android.inji.util.LogUtils;
import com.top.android.inji.util.ToastUtils;
import com.top.android.inji.util.ViewUtils;

import static com.top.android.inji.account.ui.AuthenticatorActivity.AUTH_MODE_ADD;
import static com.top.android.inji.account.ui.AuthenticatorActivity.AUTH_MODE_CONFIRM;
import static com.top.android.inji.account.ui.AuthenticatorActivity.AUTH_MODE_NEW;
import static com.top.android.inji.account.ui.AuthenticatorActivity.AUTH_MODE_UPDATE;
import static com.top.android.inji.account.ui.AuthenticatorActivity.AuthMode;

public class AuthenticatorFragment extends Fragment implements AuthenticateRequest.Listener {

    private static final String KEY_PREFIX = AuthenticatorFragment.class.getName() + '.';

    private static final String EXTRA_AUTH_MODE = KEY_PREFIX + "auth_mode";
    private static final String EXTRA_USERNAME = KEY_PREFIX + "username";

    private static final String AUTH_TOKEN_TYPE = AccountContract.AUTH_TOKEN_TYPE_FRODO;

    @BindView(R.id.form)
    View mFormLayout;
    @BindView(R.id.username_layout)
    TextInputLayout mUsernameLayout;
    @BindView(R.id.username)
    EditText mUsernameEdit;
    @BindView(R.id.password_layout)
    TextInputLayout mPasswordLayout;
    @BindView(R.id.password)
    EditText mPasswordEdit;
    @BindView(R.id.sign_in)
    Button mSignInButton;
    @BindView(R.id.sign_up)
    Button mSignUpButton;
    @BindView(R.id.progress)
    ProgressBar mProgress;

    private AuthenticateRequest mAuthenticateRequest;

    @AuthenticatorActivity.AuthMode
    private String mAuthMode;
    private String mExtraUsername;

    public static AuthenticatorFragment newInstance(String authMode, String username) {
        //noinspection deprecation
        AuthenticatorFragment fragment = new AuthenticatorFragment();
        Bundle arguments = FragmentUtils.ensureArguments(fragment);
        arguments.putString(EXTRA_AUTH_MODE, authMode);
        arguments.putString(EXTRA_USERNAME, username);
        return fragment;
    }

    /**
     * @deprecated Use {@link #newInstance(String, String)} instead.
     */
    public AuthenticatorFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection WrongConstant
        mAuthMode = getArguments().getString(EXTRA_AUTH_MODE);
        mExtraUsername = getArguments().getString(EXTRA_USERNAME);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.authenticator_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAuthenticateRequest = AuthenticateRequest.attachTo(this);

        // TODO: Make the card slide in from bottom.

        updateViews(false);

        if (savedInstanceState == null && !TextUtils.isEmpty(mExtraUsername)) {
            mUsernameEdit.setText(mExtraUsername);
        }
        ViewUtils.hideTextInputLayoutErrorOnTextChange(mUsernameEdit, mUsernameLayout);
        ViewUtils.hideTextInputLayoutErrorOnTextChange(mPasswordEdit, mPasswordLayout);
        mPasswordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    authenticate();
                    return true;
                }
                return false;
            }
        });
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticate();
            }
        });
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo:跳转至注册页面
                //NotImplementedManager.signUp(getActivity());
            }
        });

        Activity activity = getActivity();
        switch (mAuthMode) {
            case AuthenticatorActivity.AUTH_MODE_NEW:
                activity.setTitle(R.string.auth_title_new);
                break;
            case AuthenticatorActivity.AUTH_MODE_ADD:
                activity.setTitle(R.string.auth_title_add);
                break;
            case AuthenticatorActivity.AUTH_MODE_UPDATE:
                activity.setTitle(R.string.auth_title_update);
                // See the source of setKeyListener(null), it just satisfies our need.
                mUsernameEdit.setKeyListener(null);
                mPasswordEdit.requestFocus();
                break;
            case AuthenticatorActivity.AUTH_MODE_CONFIRM:
                activity.setTitle(R.string.auth_title_confirm);
                mUsernameEdit.setKeyListener(null);
                mPasswordEdit.requestFocus();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private void authenticate() {

        // Store values at the time of login attempt.
        String username = mUsernameEdit.getText().toString();
        String password = mPasswordEdit.getText().toString();

        boolean cancel = false;
        View errorView = null;

        if (TextUtils.isEmpty(username)) {
            mUsernameLayout.setError(getString(R.string.auth_error_empty_username));
            errorView = mUsernameEdit;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordLayout.setError(getString(R.string.auth_error_empty_password));
            if (errorView == null) {
                errorView = mPasswordEdit;
            }
            cancel = true;
        }

        if (cancel) {
            errorView.requestFocus();
        } else {
            mAuthenticateRequest.start(getContext(), username, password);
        }
    }

    @Override
    public void onAuthenticateStarted(int requestCode) {
        // FIXME
        mUsernameLayout.setError(null);
        mPasswordLayout.setError(null);
        updateViews(true);
    }

    @Override
    public void onAuthenticateFinished(int requestCode) {
        // DISABLED: Do so only when failed, otherwise leave the progress bar running till we
        // finish.
        //updateSignInUpViews(true);
    }

    @Override
    public void onAuthenticateSuccess(int requestCode,
                                      AuthenticateRequest.RequestState requestState,
                                      AuthenticationResponse response) {

        //登陆成功后的逻辑
        Account account = new Account(requestState.username, AccountContract.ACCOUNT_TYPE);

        switch (mAuthMode) {
            case AuthenticatorActivity.AUTH_MODE_NEW:
                AccountUtils.addAccountExplicitly(account, requestState.password);
                AccountUtils.setActiveAccount(account);
                break;
            case AuthenticatorActivity.AUTH_MODE_ADD:
                AccountUtils.addAccountExplicitly(account, requestState.password);
                break;
            case AuthenticatorActivity.AUTH_MODE_UPDATE:
            case AuthenticatorActivity.AUTH_MODE_CONFIRM:
                AccountUtils.setPassword(account, requestState.password);
                break;
        }

        //设置账号信息
        UserDTO userDTO = response.getUserDTO();
        AccountUtils.setUserName(account, userDTO.getName());
        AccountUtils.setUserId(account, userDTO.getId());
        AccountUtils.setUser(userDTO);

        Intent intent;
        switch (mAuthMode) {
            case AuthenticatorActivity.AUTH_MODE_NEW:
            case AuthenticatorActivity.AUTH_MODE_ADD:
            case AuthenticatorActivity.AUTH_MODE_UPDATE:
                intent = makeSuccessIntent(requestState.username);
                break;
            case AuthenticatorActivity.AUTH_MODE_CONFIRM:
                intent = makeBooleanIntent(true);
                break;
            default:
                throw new IllegalArgumentException();
        }
        AppCompatAccountAuthenticatorActivity activity =
                (AppCompatAccountAuthenticatorActivity) getActivity();
        activity.setAccountAuthenticatorResult(intent.getExtras());
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }

    @Override
    public void onAuthenticateError(int requestCode, AuthenticateRequest.RequestState requestState,
                                    ApiError error) {

        updateViews(true);

        LogUtils.e(error.toString());
        if (error.bodyJson != null && error.code != ApiContract.Response.Error.Codes.Custom.INVALID_ERROR_RESPONSE) {
            String errorString = error.getErrorString();
            Activity activity = getActivity();
            switch (error.code) {
                case ApiContract.Response.Error.Codes.Token.INVALID_APIKEY:
                case ApiContract.Response.Error.Codes.Token.APIKEY_IS_BLOCKED:
                case ApiContract.Response.Error.Codes.Token.INVALID_REQUEST_URI:
                case ApiContract.Response.Error.Codes.Token.INVALID_CREDENCIAL2:
                case ApiContract.Response.Error.Codes.Token.REQUIRED_PARAMETER_IS_MISSING:
                case ApiContract.Response.Error.Codes.Token.CLIENT_SECRET_MISMATCH:
                    ToastUtils.show(errorString, activity);
                    startActivity(AuthenticatorUtils.makeSetApiKeyIntent((activity)));
                    break;
                case ApiContract.Response.Error.Codes.Token.USER_HAS_BLOCKED:
                case ApiContract.Response.Error.Codes.Token.USER_LOCKED:
                    ToastUtils.show(errorString, activity);
                    startActivity(AuthenticatorUtils.makeWebsiteIntent(activity));
                    break;
                case ApiContract.Response.Error.Codes.Token.NOT_TRIAL_USER:
                case ApiContract.Response.Error.Codes.Token.INVALID_USER:
                    mUsernameLayout.setError(errorString);
                    break;
                default:
                    mPasswordLayout.setError(errorString);
                    break;
            }
        } else if (error.response != null) {
            mPasswordLayout.setError(getString(R.string.auth_error_invalid_response));
        } else {
            mPasswordLayout.setError(getString(R.string.auth_error_unknown));
        }
    }

    private void updateViews(boolean animate) {
        boolean authenticating = mAuthenticateRequest.isRequesting();
        mFormLayout.setEnabled(!authenticating);
        if (animate) {
            if (authenticating) {
                ViewUtils.fadeOutThenFadeIn(mFormLayout, mProgress);
            } else {
                ViewUtils.fadeOutThenFadeIn(mProgress, mFormLayout);
            }
        } else {
            ViewUtils.setVisibleOrInvisible(mFormLayout, !authenticating);
            ViewUtils.setVisibleOrInvisible(mProgress, authenticating);
        }
    }

    private Intent makeSuccessIntent(String accountName) {
        Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, accountName);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AccountContract.ACCOUNT_TYPE);
        return intent;
    }

    private Intent makeBooleanIntent(boolean result) {
        Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, result);
        return intent;
    }
}
