/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.broadcast.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.top.android.inji.broadcast.content.DeleteBroadcastManager;
import com.top.android.inji.broadcast.content.LikeBroadcastManager;
import com.top.android.inji.broadcast.content.RebroadcastBroadcastManager;
import com.top.android.inji.broadcast.content.TimelineBroadcastListResource;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.ui.AppBarHost;
import com.top.android.inji.ui.FastSmoothScrollStaggeredGridLayoutManager;
import com.top.android.inji.ui.FriendlyFloatingActionButton;
import com.top.android.inji.ui.OnVerticalScrollWithPagingTouchSlopListener;
import com.top.android.inji.ui.SimpleAdapter;
import com.top.android.inji.util.CardUtils;
import com.top.android.inji.util.RecyclerViewUtils;
import com.top.android.inji.util.TooltipUtils;
import com.top.android.inji.util.TransitionUtils;

import butterknife.BindView;
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;

import com.top.android.inji.R;
import com.top.android.inji.broadcast.content.DeleteBroadcastManager;
import com.top.android.inji.broadcast.content.LikeBroadcastManager;
import com.top.android.inji.broadcast.content.RebroadcastBroadcastManager;
import com.top.android.inji.broadcast.content.TimelineBroadcastListResource;
import com.top.android.inji.link.NotImplementedManager;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.ui.AppBarHost;
import com.top.android.inji.ui.FastSmoothScrollStaggeredGridLayoutManager;
import com.top.android.inji.ui.FriendlyFloatingActionButton;
import com.top.android.inji.ui.OnVerticalScrollWithPagingTouchSlopListener;
import com.top.android.inji.ui.SimpleAdapter;
import com.top.android.inji.util.CardUtils;
import com.top.android.inji.util.RecyclerViewUtils;
import com.top.android.inji.util.TooltipUtils;
import com.top.android.inji.util.TransitionUtils;

public abstract class BaseTimelineBroadcastListFragment extends BaseBroadcastListFragment
        implements TimelineBroadcastListResource.Listener, BroadcastAdapter.Listener,
        ConfirmUnrebroadcastBroadcastDialogFragment.Listener {

    @BindView(com.top.android.inji.R.id.send)
    FriendlyFloatingActionButton mSendFab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(com.top.android.inji.R.layout.broadcast_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int extraPaddingTop = getExtraPaddingTop();
        mSwipeRefreshLayout.setProgressViewOffset(extraPaddingTop);
        mList.setPadding(mList.getPaddingLeft(), mList.getPaddingTop() + extraPaddingTop,
                mList.getPaddingRight(), mList.getPaddingBottom());
    }

    protected abstract int getExtraPaddingTop();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CustomTabsHelperFragment.attachTo(this);

        TooltipUtils.setup(mSendFab);
        mSendFab.setOnClickListener(view -> onSendBroadcast());
    }

    @Override
    protected RecyclerView.LayoutManager onCreateLayoutManager() {
        return new FastSmoothScrollStaggeredGridLayoutManager(
                CardUtils.getColumnCount(getActivity()), StaggeredGridLayoutManager.VERTICAL);
    }

    @Override
    protected SimpleAdapter<Broadcast, ?> onCreateAdapter() {
        return new BroadcastAdapter(this);
    }

    @Override
    protected void onAttachScrollListener() {
        AppBarHost appBarHost = (AppBarHost) getParentFragment();
        mList.addOnScrollListener(
                new OnVerticalScrollWithPagingTouchSlopListener(getActivity()) {
                    @Override
                    public void onScrolled(int dy) {
                        if (!RecyclerViewUtils.hasFirstChildReachedTop(mList)) {
                            onShow();
                        }
                    }
                    @Override
                    public void onScrolledUp() {
                        onShow();
                    }
                    private void onShow() {
                        appBarHost.showAppBar();
                        mSendFab.show();
                    }
                    @Override
                    public void onScrolledDown() {
                        if (RecyclerViewUtils.hasFirstChildReachedTop(mList)) {
                            appBarHost.hideAppBar();
                            mSendFab.hide();
                        }
                    }
                    @Override
                    public void onScrolledToBottom() {
                        mResource.load(true);
                    }
                });
        appBarHost.setToolBarOnDoubleClickListener(view -> {
            mList.smoothScrollToPosition(0);
            return true;
        });
    }

    @Override
    public void onBroadcastWriteStarted(int requestCode, int position) {
        onItemWriteStarted(position);
    }

    @Override
    public void onBroadcastWriteFinished(int requestCode, int position) {
        onItemWriteStarted(position);
    }

    @Override
    public void onLikeBroadcast(Broadcast broadcast, boolean like) {
        LikeBroadcastManager.getInstance().write(broadcast, like, getActivity());
    }

    @Override
    public void onRebroadcastBroadcast(Broadcast broadcast, boolean rebroadcast, boolean quick) {
        if (rebroadcast) {
            if (quick) {
                RebroadcastBroadcastManager.getInstance().write(broadcast.getEffectiveBroadcast(),
                        null, getActivity());
            } else {
                startActivity(RebroadcastBroadcastActivity.makeIntent(broadcast, getActivity()));
            }
        } else {
            if (quick) {
                DeleteBroadcastManager.getInstance().write(broadcast, getActivity());
            } else {
                ConfirmUnrebroadcastBroadcastDialogFragment.show(broadcast, this);
            }
        }
    }

    @Override
    public void unrebroadcastBroadcast(Broadcast broadcast) {
        DeleteBroadcastManager.getInstance().write(broadcast, getActivity());
    }

    @Override
    public void onCommentBroadcast(Broadcast broadcast, View sharedView) {
        // Open ime for comment if there is none; otherwise we always let the user see what others
        // have already said first, to help to make the world a better place.
        openBroadcast(broadcast, sharedView, broadcast.canComment() && broadcast.commentCount == 0);
    }

    @Override
    public void onOpenBroadcast(Broadcast broadcast, View sharedView) {
        openBroadcast(broadcast, sharedView, false);
    }

    private void openBroadcast(Broadcast broadcast, View sharedView, boolean showSendComment) {
        Activity activity = getActivity();
        Intent intent = BroadcastActivity.makeIntent(broadcast, showSendComment,
                activity.getTitle().toString(), activity);
        Bundle options = TransitionUtils.makeActivityOptionsBundle(activity, sharedView);
        ActivityCompat.startActivity(activity, intent, options);
    }

    protected void onSendBroadcast() {
        Activity activity = getActivity();
        activity.startActivity(SendBroadcastActivity.makeIntent(activity));
    }
}
