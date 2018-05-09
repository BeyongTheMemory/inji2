/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.broadcast.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.top.android.inji.network.api.info.dto.UserDTO;
import com.top.android.inji.ui.AppBarHost;
import com.top.android.inji.ui.AppBarWrapperLayout;
import com.top.android.inji.ui.DoubleClickToolbar;
import com.top.android.inji.ui.WebViewActivity;
import com.top.android.inji.util.DoubanUtils;
import com.top.android.inji.util.FragmentUtils;
import com.top.android.inji.util.ShareUtils;
import com.top.android.inji.util.TransitionUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.top.android.inji.R;
import com.top.android.inji.network.api.info.apiv2.SimpleUser;
import com.top.android.inji.network.api.info.dto.UserDTO;
import com.top.android.inji.ui.AppBarHost;
import com.top.android.inji.ui.AppBarWrapperLayout;
import com.top.android.inji.ui.DoubleClickToolbar;
import com.top.android.inji.ui.WebViewActivity;
import com.top.android.inji.util.DoubanUtils;
import com.top.android.inji.util.FragmentUtils;
import com.top.android.inji.util.ShareUtils;
import com.top.android.inji.util.TransitionUtils;

public class BroadcastListActivityFragment extends Fragment implements AppBarHost {

    private static final String KEY_PREFIX = BroadcastListActivityFragment.class.getName() + '.';

    private static final String EXTRA_USER_ID = KEY_PREFIX + "user_id";
    private static final String EXTRA_USER = KEY_PREFIX + "user";
    private static final String EXTRA_TOPIC = KEY_PREFIX + "topic";

    @BindView(com.top.android.inji.R.id.appBarWrapper)
    AppBarWrapperLayout mAppBarWrapperLayout;
    @BindView(com.top.android.inji.R.id.toolbar)
    DoubleClickToolbar mToolbar;

    private Long mUserId;
    private UserDTO mUser;
    private String mTopic;

    public static BroadcastListActivityFragment newInstance(Long userId, UserDTO user,
                                                            String topic) {
        //noinspection deprecation
        BroadcastListActivityFragment fragment = new BroadcastListActivityFragment();
        Bundle arguments = FragmentUtils.ensureArguments(fragment);
        arguments.putLong(EXTRA_USER_ID, userId);
        arguments.putParcelable(EXTRA_USER, user);
        arguments.putString(EXTRA_TOPIC, topic);
        return fragment;
    }


    public BroadcastListActivityFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mUser = arguments.getParcelable(EXTRA_USER);
        if (mUser != null) {
            mUserId = mUser.getId();
        } else {
            mUserId = arguments.getLong(EXTRA_USER_ID);
        }
        mTopic = arguments.getString(EXTRA_TOPIC);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(com.top.android.inji.R.layout.broadcast_list_activity_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setTitle(getTitle());
        activity.setSupportActionBar(mToolbar);

        TransitionUtils.setupTransitionOnActivityCreated(this);

        if (savedInstanceState == null) {
            FragmentUtils.add(BroadcastListFragment.newInstance(mUserId, mTopic), this,
                    com.top.android.inji.R.id.broadcast_list_fragment);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(com.top.android.inji.R.menu.broadcast_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            case com.top.android.inji.R.id.action_share:
                share();
                return true;
            case com.top.android.inji.R.id.action_view_on_web:
                viewOnWeb();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showAppBar() {
        mAppBarWrapperLayout.show();
    }

    @Override
    public void hideAppBar() {
        mAppBarWrapperLayout.hide();
    }

    @Override
    public void setToolBarOnDoubleClickListener(DoubleClickToolbar.OnDoubleClickListener listener) {
        mToolbar.setOnDoubleClickListener(listener);
    }

    private String getTitle() {
        // TODO: Load user.
        if (mUser != null) {
            return getString(com.top.android.inji.R.string.broadcast_list_title_user_format, mUser.getName());
        } else if (!TextUtils.isEmpty(mTopic)) {
            return getString(com.top.android.inji.R.string.broadcast_list_title_topic_format, mTopic);
        } else {
            return getString(com.top.android.inji.R.string.broadcast_list_title_default);
        }
    }

    private void share() {
        ShareUtils.shareText(makeUrl(), getActivity());
    }

    private void viewOnWeb() {
        startActivity(WebViewActivity.makeIntent(makeUrl(), true, getActivity()));
    }

    private String makeUrl() {
        //noinspection deprecation
        return DoubanUtils.makeBroadcastListUrl(mUser != null ? mUser.getId() : mUserId,
                mTopic);
    }
}
