/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.followship.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.top.android.inji.util.FragmentUtils;
import com.top.android.inji.util.TransitionUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.top.android.inji.R;
import com.top.android.inji.util.FragmentUtils;
import com.top.android.inji.util.TransitionUtils;

public abstract class FollowshipListActivityFragment extends Fragment {

    private static final String KEY_PREFIX = FollowshipListActivityFragment.class.getName() + '.';

    private static final String EXTRA_user_id = KEY_PREFIX + "user_id";

    @BindView(com.top.android.inji.R.id.toolbar)
    Toolbar mToolbar;

    private String mUserIdOrUid;

    protected FollowshipListActivityFragment setArguments(String userIdOrUid) {
        FragmentUtils.ensureArguments(this)
                .putString(EXTRA_user_id, userIdOrUid);
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mUserIdOrUid = arguments.getString(EXTRA_user_id);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(com.top.android.inji.R.layout.followship_list_activity_fragment, container, false);
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
        activity.setSupportActionBar(mToolbar);

        TransitionUtils.setupTransitionOnActivityCreated(this);

        if (savedInstanceState == null) {
            FragmentUtils.add(onCreateListFragment(), this,
                    com.top.android.inji.R.id.followship_list_fragment);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected String getUserIdOrUid() {
        return mUserIdOrUid;
    }

    abstract protected FollowshipUserListFragment onCreateListFragment();
}
