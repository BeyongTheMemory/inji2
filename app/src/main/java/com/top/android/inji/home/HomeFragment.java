/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.top.android.inji.R;
import com.top.android.inji.broadcast.ui.HomeBroadcastListFragment;
import com.top.android.inji.ui.AppBarHost;
import com.top.android.inji.ui.AppBarWrapperLayout;
import com.top.android.inji.ui.DoubleClickToolbar;
import com.top.android.inji.ui.NotYetImplementedFragment;
import com.top.android.inji.ui.TabFragmentPagerAdapter;

public class HomeFragment extends Fragment implements AppBarHost {

    @BindView(R.id.appBarWrapper)
    AppBarWrapperLayout mAppBarWrapperLayout;
    @BindView(R.id.toolbar)
    DoubleClickToolbar mToolbar;
    @BindView(R.id.tab)
    TabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private TabFragmentPagerAdapter mTabAdapter;

    public static HomeFragment newInstance() {
        //noinspection deprecation
        return new HomeFragment();
    }

    /**
     * @deprecated Use {@link #newInstance()} instead.
     */
    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);

        //todo：在这里增删栏目
        //1.热帖 2.附件的人
        mTabAdapter = new TabFragmentPagerAdapter(this);
        mTabAdapter.addTab(HomeBroadcastListFragment::newInstance, getString(R.string.home_broadcast));
       // mTabAdapter.addTab(NotYetImplementedFragment::newInstance, getString(R.string.home_discover));
       // mTabAdapter.addTab(NotYetImplementedFragment::newInstance, getString(R.string.home_topic));
       // mTabAdapter.addTab(NotYetImplementedFragment::newInstance, getString(R.string.home_online));
        mViewPager.setOffscreenPageLimit(mTabAdapter.getCount() - 1);
        mViewPager.setAdapter(mTabAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
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
}
