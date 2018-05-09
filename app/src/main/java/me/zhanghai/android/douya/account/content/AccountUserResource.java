/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.content;

import android.accounts.Account;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.network.api.info.apiv2.SimpleUser;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.network.api.info.dto.UserDTO;
import me.zhanghai.android.douya.user.content.UserResource;
import me.zhanghai.android.douya.util.FragmentUtils;

public class AccountUserResource extends UserResource {

    private static final String KEY_PREFIX = AccountUserResource.class.getName() + '.';

    private final String EXTRA_ACCOUNT = KEY_PREFIX + "account";

    private Account mAccount;

    private static final String FRAGMENT_TAG_DEFAULT = AccountUserResource.class.getName();

    private static AccountUserResource newInstance(Account account) {
        //noinspection deprecation
        return new AccountUserResource().setArguments(account);
    }

    public static AccountUserResource attachTo(Account account, Fragment fragment, String tag,
                                               int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        AccountUserResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(account);
            instance.targetAt(fragment, requestCode);
            FragmentUtils.add(instance, activity, tag);
        }
        return instance;
    }

    public static AccountUserResource attachTo(Account account, Fragment fragment) {
        return attachTo(account, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    @SuppressWarnings("deprecation")
    public AccountUserResource() {}

    protected AccountUserResource setArguments(Account account) {
        UserDTO user = makePartialUser(account);
        super.setArguments(user.getId(), user);
        FragmentUtils.ensureArguments(this)
                .putParcelable(EXTRA_ACCOUNT, account);
        return this;
    }

    private UserDTO makePartialUser(Account account) {
        UserDTO userDTO = AccountUtils.getUser(account);
        return userDTO;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccount = getArguments().getParcelable(EXTRA_ACCOUNT);
    }

    @Override
    protected void loadOnStart() {
        //todo：原逻辑中在这里做了重新获取用户信息的操作，不知道为毛
        // Always load, so that we can ever get refreshed.
        super.loadOnStart();
    }

    @Override
    protected void onLoadSuccess(UserDTO user) {
        super.onLoadSuccess(user);

        AccountUtils.setUserName(mAccount, user.getName());
        AccountUtils.setUser(mAccount, user);
    }


}
