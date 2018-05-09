/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.broadcast.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.top.android.inji.broadcast.content.BroadcastResource;
import com.top.android.inji.broadcast.content.RebroadcastBroadcastManager;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.ui.ConfirmDiscardContentDialogFragment;
import com.top.android.inji.ui.FragmentFinishable;
import com.top.android.inji.util.FragmentUtils;
import com.top.android.inji.util.LogUtils;
import com.top.android.inji.util.ToastUtils;
import com.top.android.inji.util.ViewUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;

import com.top.android.inji.R;
import com.top.android.inji.broadcast.content.BroadcastResource;
import com.top.android.inji.broadcast.content.RebroadcastBroadcastManager;
import com.top.android.inji.eventbus.BroadcastRebroadcastErrorEvent;
import com.top.android.inji.eventbus.BroadcastRebroadcastedEvent;
import com.top.android.inji.eventbus.EventBusUtils;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.ui.ConfirmDiscardContentDialogFragment;
import com.top.android.inji.ui.FragmentFinishable;
import com.top.android.inji.util.FragmentUtils;
import com.top.android.inji.util.LogUtils;
import com.top.android.inji.util.ToastUtils;
import com.top.android.inji.util.ViewUtils;

public class RebroadcastBroadcastFragment extends Fragment implements BroadcastResource.Listener,
        ConfirmDiscardContentDialogFragment.Listener {

    private static final String KEY_PREFIX = RebroadcastBroadcastFragment.class.getName() + '.';

    private static final String EXTRA_BROADCAST_ID = KEY_PREFIX + "broadcast_id";
    private static final String EXTRA_BROADCAST = KEY_PREFIX + "broadcast";
    private static final String EXTRA_TEXT = KEY_PREFIX + "text";

    @BindView(com.top.android.inji.R.id.toolbar)
    Toolbar mToolbar;
    @BindView(com.top.android.inji.R.id.text_and_broadcast)
    ViewGroup mTextAndContentLayout;
    @BindView(com.top.android.inji.R.id.broadcast)
    BroadcastLayout mBroadcastLayout;
    @BindView(com.top.android.inji.R.id.text)
    EditText mTextEdit;
    @BindView(com.top.android.inji.R.id.progress)
    ProgressBar mProgress;

    private MenuItem mRebroadcastMenuItem;

    private long mBroadcastId;
    private Broadcast mBroadcast;
    private CharSequence mText;

    private BroadcastResource mBroadcastResource;

    private boolean mRebroadcasted;

    public static RebroadcastBroadcastFragment newInstance(long broadcastId, Broadcast broadcast,
                                                           CharSequence text) {
        //noinspection deprecation
        RebroadcastBroadcastFragment fragment = new RebroadcastBroadcastFragment();
        Bundle arguments = FragmentUtils.ensureArguments(fragment);
        arguments.putLong(EXTRA_BROADCAST_ID, broadcastId);
        arguments.putParcelable(EXTRA_BROADCAST, broadcast);
        arguments.putCharSequence(EXTRA_TEXT, text);
        return fragment;
    }

    /**
     * @deprecated Use {@link #newInstance(long, Broadcast, CharSequence)} instead.
     */
    public RebroadcastBroadcastFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mBroadcast = arguments.getParcelable(EXTRA_BROADCAST);
        if (mBroadcast != null) {
            mBroadcastId = mBroadcast.id;
        } else {
            mBroadcastId = arguments.getLong(EXTRA_BROADCAST_ID);
        }
        mText = arguments.getCharSequence(EXTRA_TEXT);

        setHasOptionsMenu(true);

        EventBusUtils.register(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(com.top.android.inji.R.layout.broadcast_rebroadcast_broadcast_fragment, container,
                false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CustomTabsHelperFragment.attachTo(this);
        mBroadcastResource = BroadcastResource.attachTo(mBroadcastId, mBroadcast, this);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {
            mTextEdit.setText(mText);
        }
        //noinspection deprecation
        if (mBroadcastResource.has()) {
            //noinspection deprecation
            setBroadcast(mBroadcastResource.get());
        }
        updateRebroadcastStatus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBusUtils.unregister(this);

        mBroadcastResource.detach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(com.top.android.inji.R.menu.broadcast_rebroadcast_broadcast, menu);
        mRebroadcastMenuItem = menu.findItem(com.top.android.inji.R.id.action_rebroadcast);
        updateRebroadcastStatus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onFinish();
                return true;
            case com.top.android.inji.R.id.action_rebroadcast:
                onRebroadcast();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLoadBroadcastStarted(int requestCode) {
        updateRefreshing();
    }

    @Override
    public void onLoadBroadcastFinished(int requestCode) {
        updateRefreshing();
    }

    @Override
    public void onLoadBroadcastError(int requestCode, ApiError error) {
        LogUtils.e(error.toString());
        Activity activity = getActivity();
        ToastUtils.show(ApiError.getErrorString(error), activity);
    }

    @Override
    public void onBroadcastChanged(int requestCode, Broadcast newBroadcast) {
        setBroadcast(newBroadcast);
    }

    @Override
    public void onBroadcastRemoved(int requestCode) {}

    @Override
    public void onBroadcastWriteStarted(int requestCode) {}

    @Override
    public void onBroadcastWriteFinished(int requestCode) {}

    private void setBroadcast(Broadcast broadcast) {
        mBroadcastLayout.bindForRebroadcast(broadcast);
        updateRebroadcastStatus();
    }

    private void updateRefreshing() {
        //noinspection deprecation
        boolean hasBroadcast = mBroadcastResource.has();
        ViewUtils.fadeToVisibility(mProgress, !hasBroadcast);
        ViewUtils.fadeToVisibility(mTextAndContentLayout, hasBroadcast);
    }

    private void onRebroadcast() {
        String text = mTextEdit.getText().toString();
        rebroadcast(text);
    }

    private void rebroadcast(String text) {
        RebroadcastBroadcastManager.getInstance().write(mBroadcastResource.getEffectiveBroadcast(),
                text, getActivity());
        updateRebroadcastStatus();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastRebroadcasted(BroadcastRebroadcastedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        if (mBroadcastResource.isEffectiveBroadcastId(event.broadcastId)) {
            mRebroadcasted = true;
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastRebroadcastError(BroadcastRebroadcastErrorEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        if (mBroadcastResource.isEffectiveBroadcastId(event.broadcastId)) {
            updateRebroadcastStatus();
        }
    }

    private void updateRebroadcastStatus() {
        if (mRebroadcasted) {
            return;
        }
        RebroadcastBroadcastManager manager = RebroadcastBroadcastManager.getInstance();
        boolean hasBroadcast = mBroadcastResource.hasEffectiveBroadcast();
        boolean rebroadcasting = hasBroadcast && manager.isWriting(
                mBroadcastResource.getEffectiveBroadcastId());
        getActivity().setTitle(rebroadcasting ? com.top.android.inji.R.string.broadcast_rebroadcast_title_rebroadcasting
                : com.top.android.inji.R.string.broadcast_rebroadcast_title);
        boolean enabled = !rebroadcasting;
        mTextEdit.setEnabled(enabled);
        if (mRebroadcastMenuItem != null) {
            mRebroadcastMenuItem.setEnabled(enabled);
        }
        if (rebroadcasting) {
            mTextEdit.setText(manager.getText(mBroadcastResource.getEffectiveBroadcastId()));
        }
    }

    public void onFinish() {
        if (mTextEdit.getText().length() > 0) {
            ConfirmDiscardContentDialogFragment.show(this);
        } else {
            finish();
        }
    }

    @Override
    public void discardContent() {
        finish();
    }

    private void finish() {
        FragmentFinishable.finish(getActivity());
    }
}
