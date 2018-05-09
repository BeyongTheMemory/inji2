/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.navigation.ui;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.widget.TextViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.top.android.inji.account.info.AccountContract;
import com.top.android.inji.account.util.AccountUtils;
import com.top.android.inji.network.api.info.dto.UserDTO;
import com.top.android.inji.util.AppUtils;
import com.top.android.inji.util.ImageUtils;
import com.top.android.inji.util.IntentUtils;
import com.top.android.inji.util.TintHelper;
import com.top.android.inji.util.ViewUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import com.top.android.inji.R;
import com.top.android.inji.account.info.AccountContract;
import com.top.android.inji.account.util.AccountUtils;
import com.top.android.inji.network.api.info.apiv2.SimpleUser;
import com.top.android.inji.network.api.info.apiv2.User;
import com.top.android.inji.network.api.info.dto.UserDTO;
import com.top.android.inji.util.AppUtils;
import com.top.android.inji.util.ImageUtils;
import com.top.android.inji.util.IntentUtils;
import com.top.android.inji.util.TintHelper;
import com.top.android.inji.util.ViewUtils;

public class NavigationAccountListLayout extends LinearLayout {

    @BindView(com.top.android.inji.R.id.account_list)
    LinearLayout mAccountList;
    @BindView(com.top.android.inji.R.id.divider)
    View mDividerView;
    @BindViews({com.top.android.inji.R.id.add_account, com.top.android.inji.R.id.remove_current_account, com.top.android.inji.R.id.manage_accounts})
    TextView[] mMenuItems;
    @BindView(com.top.android.inji.R.id.add_account)
    TextView mAddAccountItem;
    @BindView(com.top.android.inji.R.id.remove_current_account)
    TextView mRemoveCurrentAccountItem;
    @BindView(com.top.android.inji.R.id.manage_accounts)
    TextView mManageAccountsItem;

    private Adapter mAdapter;
    private Listener mListener;

    public NavigationAccountListLayout(Context context) {
        super(context);

        init();
    }

    public NavigationAccountListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public NavigationAccountListLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NavigationAccountListLayout(Context context, AttributeSet attrs, int defStyleAttr,
                                       int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private void init() {

        ViewUtils.inflateInto(com.top.android.inji.R.layout.navigation_account_list_layout, this);
        ButterKnife.bind(this);

        final Context context = getContext();
        ColorStateList iconTintList = ViewUtils.getColorStateListFromAttrRes(
                android.R.attr.textColorSecondary, context);
        for (TextView menuItem : mMenuItems) {
            Drawable icon = menuItem.getCompoundDrawables()[0];
            icon = TintHelper.tintDrawable(icon, iconTintList);
            TextViewCompat.setCompoundDrawablesRelative(menuItem, icon, null, null, null);
        }

        mAddAccountItem.setOnClickListener(view -> AccountUtils.addAccount(
                AppUtils.getActivityFromContext(context)));
        mRemoveCurrentAccountItem.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onRemoveCurrentAccount();
            }
        });
        mManageAccountsItem.setOnClickListener(view -> AppUtils.startActivity(
                IntentUtils.makeSyncSettingsWithAccountType(AccountContract.ACCOUNT_TYPE),
                context));
    }

    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void bind() {

        List<Account> accountList = new ArrayList<>(Arrays.asList(AccountUtils.getAccounts()));
        accountList.remove(AccountUtils.getActiveAccount());
        Account recentOneAccount = AccountUtils.getRecentOneAccount();
        if (recentOneAccount != null) {
            accountList.remove(recentOneAccount);
        }
        Account recentTwoAccount = AccountUtils.getRecentTwoAccount();
        if (recentTwoAccount != null) {
            accountList.remove(recentTwoAccount);
        }
        if (recentOneAccount != null) {
            accountList.add(recentOneAccount);
        }
        if (recentTwoAccount != null) {
            accountList.add(recentTwoAccount);
        }

        int i = 0;
        for (final Account account : accountList) {

            if (i >= mAccountList.getChildCount()) {
                ViewUtils.inflateInto(com.top.android.inji.R.layout.navigation_account_item, mAccountList);
            }
            View accountLayout = mAccountList.getChildAt(i);
            accountLayout.setVisibility(VISIBLE);
            AccountLayoutHolder holder = (AccountLayoutHolder) accountLayout.getTag();
            if (holder == null) {
                holder = new AccountLayoutHolder(accountLayout);
                accountLayout.setTag(holder);
            }

            UserDTO user = mAdapter.getUser(account);
            if (user != null) {
                ImageUtils.loadNavigationAccountListAvatar(holder.avatarImage,
                        user.getHeadUrl());
            } else {
                holder.avatarImage.setImageResource(com.top.android.inji.R.drawable.avatar_icon_40dp);
            }
            holder.nameText.setText(mAdapter.getUser(account).getName());
            accountLayout.setOnClickListener(view -> {
                if (mListener != null) {
                    mListener.switchToAccount(account);
                }
            });

            ++i;
        }

        ViewUtils.setVisibleOrGone(mDividerView, i > 0);

        for (int count = mAccountList.getChildCount(); i < count; ++i) {
            mAccountList.getChildAt(i).setVisibility(GONE);
        }
    }

    class AccountLayoutHolder {

        @BindView(com.top.android.inji.R.id.avatar)
        ImageView avatarImage;
        @BindView(com.top.android.inji.R.id.name)
        TextView nameText;

        public AccountLayoutHolder(View accountLayout) {
            ButterKnife.bind(this, accountLayout);
        }
    }

    public interface Adapter {
        UserDTO getUser(Account account);
    }

    public interface Listener {
        void switchToAccount(Account account);
        void onRemoveCurrentAccount();
    }
}
