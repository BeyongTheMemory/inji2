/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.profile.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.top.android.inji.link.NotImplementedManager;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.info.apiv2.SimpleUser;
import com.top.android.inji.network.api.info.apiv2.User;
import com.top.android.inji.network.api.info.dto.UserDTO;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.network.api.info.frodo.Diary;
import com.top.android.inji.network.api.info.frodo.SimpleReview;
import com.top.android.inji.network.api.info.frodo.UserItems;
import com.top.android.inji.profile.content.ProfileResource;
import com.top.android.inji.profile.util.ProfileUtils;
import com.top.android.inji.ui.ContentStateLayout;
import com.top.android.inji.ui.CopyTextDialogFragment;
import com.top.android.inji.ui.DoubleClickToolbar;
import com.top.android.inji.ui.WebViewActivity;
import com.top.android.inji.util.DoubanUtils;
import com.top.android.inji.util.FragmentUtils;
import com.top.android.inji.util.LogUtils;
import com.top.android.inji.util.ShareUtils;
import com.top.android.inji.util.ToastUtils;
import com.top.android.inji.util.ViewUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;

import com.top.android.inji.R;
import com.top.android.inji.link.NotImplementedManager;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.info.dto.UserDTO;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.network.api.info.apiv2.User;
import com.top.android.inji.network.api.info.frodo.Diary;
import com.top.android.inji.network.api.info.frodo.SimpleReview;
import com.top.android.inji.network.api.info.frodo.UserItems;
import com.top.android.inji.profile.content.ProfileResource;
import com.top.android.inji.profile.util.ProfileUtils;
import com.top.android.inji.ui.ContentStateLayout;
import com.top.android.inji.ui.CopyTextDialogFragment;
import com.top.android.inji.ui.DoubleClickToolbar;
import com.top.android.inji.ui.WebViewActivity;
import com.top.android.inji.util.DoubanUtils;
import com.top.android.inji.util.FragmentUtils;
import com.top.android.inji.util.LogUtils;
import com.top.android.inji.util.ShareUtils;
import com.top.android.inji.util.ToastUtils;
import com.top.android.inji.util.ViewUtils;

public class ProfileFragment extends Fragment implements ProfileResource.Listener,
        ProfileHeaderLayout.Listener, ProfileIntroductionLayout.Listener,
        ConfirmUnfollowUserDialogFragment.Listener {

    private static final String KEY_PREFIX = ProfileFragment.class.getName() + '.';

    private static final String EXTRA_USER_ID = KEY_PREFIX + "user_id";
    private static final String EXTRA_USER = KEY_PREFIX + "user";

    @BindView(com.top.android.inji.R.id.scroll)
    ProfileLayout mScrollLayout;
    @BindView(com.top.android.inji.R.id.header)
    ProfileHeaderLayout mHeaderLayout;
    @BindView(com.top.android.inji.R.id.dismiss)
    View mDismissView;
    @BindView(com.top.android.inji.R.id.toolbar)
    DoubleClickToolbar mToolbar;
    @BindViews({
            com.top.android.inji.R.id.profile_header_animate_changes_layout_1,
            com.top.android.inji.R.id.profile_header_animate_changes_layout_2
    })
    ViewGroup[] mAnimateChangesLayouts;
    @BindView(com.top.android.inji.R.id.contentState)
    ContentStateLayout mContentStateLayout;
    @BindView(com.top.android.inji.R.id.content)
    RecyclerView mContentList;

    private Long mUserId;
    private UserDTO mUser;

    private ProfileResource mResource;

    private ProfileAdapter mAdapter;

    public static ProfileFragment newInstance(String userIdOrUid, SimpleUser simpleUser,
                                              User user) {
        //noinspection deprecation
        ProfileFragment fragment = new ProfileFragment();
        Bundle arguments = FragmentUtils.ensureArguments(fragment);
        arguments.putString(EXTRA_USER_ID, userIdOrUid);
        arguments.putParcelable(EXTRA_USER, user);
        return fragment;
    }

    /**
     * @deprecated Use {@link #newInstance(String, SimpleUser, User)} instead.
     */
    public ProfileFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mUserId = arguments.getLong(EXTRA_USER_ID);
        mUser = arguments.getParcelable(EXTRA_USER);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        int layoutRes = ProfileUtils.shouldUseWideLayout(inflater.getContext()) ?
                com.top.android.inji.R.layout.profile_fragment_wide : com.top.android.inji.R.layout.profile_fragment;
        return inflater.inflate(layoutRes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CustomTabsHelperFragment.attachTo(this);
        mResource = ProfileResource.attachTo(mUserId, mUser, this);

        mScrollLayout.setListener(new ProfileLayout.Listener() {
            @Override
            public void onEnterAnimationEnd() {}
            @Override
            public void onExitAnimationEnd() {
                getActivity().finish();
            }
        });
        if (savedInstanceState == null) {
            mScrollLayout.enter();
        }

        mDismissView.setOnClickListener(view -> exit());

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setTitle(null);

        if (mResource.hasUser()) {
            mHeaderLayout.bindUser(mResource.getUser());
        }
        mHeaderLayout.setListener(this);
        mToolbar.setOnDoubleClickListener(view -> {
            if (!mScrollLayout.isHeaderCollapsed()) {
                return false;
            }
            mScrollLayout.animateHeaderViewScroll(false);
            return true;
        });

        if (ViewUtils.hasSw600Dp(activity)) {
            mContentList.setLayoutManager(new StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL));
        } else {
            mContentList.setLayoutManager(new LinearLayoutManager(activity));
        }
        mAdapter = new ProfileAdapter(this);
        mContentList.setAdapter(mAdapter);

        mContentStateLayout.setLoading();
        if (mResource.isAnyLoaded()) {
            mResource.notifyChangedIfLoaded();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mResource.detach();
    }

    public void onBackPressed() {
        exit();
    }

    private void exit() {
        mScrollLayout.exit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(com.top.android.inji.R.menu.profile, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        updateOptionsMenu();
    }

    private void updateOptionsMenu() {
        // TODO: Block or unblock.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case com.top.android.inji.R.id.action_send_doumail:
                sendDoumail();
                return true;
            case com.top.android.inji.R.id.action_blacklist:
                // TODO
                NotImplementedManager.showNotYetImplementedToast(getActivity());
                return true;
            case com.top.android.inji.R.id.action_report_abuse:
                // TODO
                NotImplementedManager.showNotYetImplementedToast(getActivity());
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
    public void onLoadError(int requestCode, ApiError error) {
        LogUtils.e(error.toString());
        if (mAdapter.getItemCount() > 0) {
            mAdapter.setError();
        } else {
            mContentStateLayout.setError();
        }
        Activity activity = getActivity();
        ToastUtils.show(ApiError.getErrorString(error), activity);
    }

    @Override
    public void onUserChanged(int requestCode, UserDTO newUser) {
        // WORKAROUND: Fix for LayoutTransition visual glitch when view is scrolling.
        if (!mScrollLayout.isHeaderOpen()) {
            for (ViewGroup animateChangesLayout : mAnimateChangesLayouts) {
                animateChangesLayout.setLayoutTransition(null);
            }
        }
        mHeaderLayout.bindUser(newUser);
        updateOptionsMenu();
    }

    @Override
    public void onUserWriteStarted(int requestCode) {
        mHeaderLayout.bindUser(mResource.getUser());
    }

    @Override
    public void onUserWriteFinished(int requestCode) {
        mHeaderLayout.bindUser(mResource.getUser());
    }


    @Override
    public void onChanged(int requestCode, UserDTO newUser, List<Broadcast> newBroadcastList,
                          List<UserDTO> newFollowingList, List<Diary> newDiaryList,
                          List<UserItems> newUserItemList, List<SimpleReview> newReviewList) {
        mAdapter.setData(new ProfileDataAdapter.Data(newUser, newBroadcastList, newFollowingList,
                newDiaryList, newUserItemList, newReviewList));
        if (mAdapter.getItemCount() > 0) {
            mContentStateLayout.setLoaded(true);
        }
        updateOptionsMenu();
    }

    private void sendDoumail() {
        //todo：发送私信
        Long userId = mResource.getUserId();
        NotImplementedManager.sendDoumail(userId, getActivity());
    }

    private void share() {
        ShareUtils.shareText(makeUrl(), getActivity());
    }

    private void viewOnWeb() {
        startActivity(WebViewActivity.makeIntent(makeUrl(), true, getActivity()));
    }

    private String makeUrl() {
        //todo:关注用户？
        return DoubanUtils.makeUserUrl(mResource.getUserId());
//        if (mResource.hasSimpleUser()) {
//            return mResource.getSimpleUser().getUrl();
//        } else {
//            return DoubanUtils.makeUserUrl(mResource.getUserId());
//        }
    }

    @Override
    public void onEditProfile(UserDTO user) {
        NotImplementedManager.editProfile(getActivity());
    }

    @Override
    public void onFollowUser(UserDTO user, boolean follow) {
        //todo：关注用户
//        if (follow) {
//            FollowUserManager.getInstance().write(user, true, getActivity());
//        } else {
//            ConfirmUnfollowUserDialogFragment.show(this);
//        }
    }

    @Override
    public void onUnfollowUser() {
        //todo：取关
        //FollowUserManager.getInstance().write(mResource.getUser(), false, getActivity());
    }

    @Override
    public void onCopyText(String text) {
        CopyTextDialogFragment.show(text, this);
    }
}
