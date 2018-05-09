/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.broadcast.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.util.FragmentUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.top.android.inji.R;
import com.top.android.inji.eventbus.BroadcastUpdatedEvent;
import com.top.android.inji.eventbus.EventBusUtils;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.util.FragmentUtils;

public class ConfirmUnrebroadcastBroadcastDialogFragment extends AppCompatDialogFragment {

    private static final String KEY_PREFIX =
            ConfirmUnrebroadcastBroadcastDialogFragment.class.getName() + '.';

    private static final String EXTRA_BROADCAST = KEY_PREFIX + "broadcast";

    private Broadcast mBroadcast;

    /**
     * @deprecated Use {@link #newInstance(Broadcast)} instead.
     */
    public ConfirmUnrebroadcastBroadcastDialogFragment() {}

    public static ConfirmUnrebroadcastBroadcastDialogFragment newInstance(Broadcast broadcast) {
        //noinspection deprecation
        ConfirmUnrebroadcastBroadcastDialogFragment fragment =
                new ConfirmUnrebroadcastBroadcastDialogFragment();
        FragmentUtils.ensureArguments(fragment)
                .putParcelable(EXTRA_BROADCAST, broadcast);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBroadcast = getArguments().getParcelable(EXTRA_BROADCAST);

        EventBusUtils.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBusUtils.unregister(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity(), getTheme())
                .setMessage(com.top.android.inji.R.string.broadcast_unrebroadcast_confirm)
                .setPositiveButton(com.top.android.inji.R.string.ok, (dialog, which) ->
                        getListener().unrebroadcastBroadcast(mBroadcast))
                .setNegativeButton(com.top.android.inji.R.string.cancel, null)
                .create();
    }

    private Listener getListener() {
        return (Listener) getParentFragment();
    }

    public static void show(Broadcast broadcast, Fragment fragment) {
        ConfirmUnrebroadcastBroadcastDialogFragment.newInstance(broadcast)
                .show(fragment.getChildFragmentManager(), null);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastUpdated(BroadcastUpdatedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        Broadcast updatedBroadcast = event.update(mBroadcast, this);
        if (updatedBroadcast != null) {
            mBroadcast = updatedBroadcast;
        }
    }

    public interface Listener {
        void unrebroadcastBroadcast(Broadcast broadcast);
    }
}
