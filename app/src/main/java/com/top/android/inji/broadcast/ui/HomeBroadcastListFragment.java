/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.broadcast.ui;

import com.top.android.inji.broadcast.content.HomeBroadcastListResource;
import com.top.android.inji.broadcast.content.TimelineBroadcastListResource;
import com.top.android.inji.main.ui.MainActivity;
import com.top.android.inji.network.api.info.frodo.Broadcast;

import butterknife.BindDimen;
import com.top.android.inji.R;
import com.top.android.inji.broadcast.content.HomeBroadcastListResource;
import com.top.android.inji.broadcast.content.TimelineBroadcastListResource;
import com.top.android.inji.main.ui.MainActivity;
import com.top.android.inji.network.api.info.frodo.Broadcast;

public class HomeBroadcastListFragment extends BaseTimelineBroadcastListFragment
        implements HomeBroadcastListResource.Listener {

    @BindDimen(com.top.android.inji.R.dimen.toolbar_and_tab_height)
    int mToolbarAndTabHeight;

    public static HomeBroadcastListFragment newInstance() {
        //noinspection deprecation
        return new HomeBroadcastListFragment();
    }

    /**
     * @deprecated Use {@link #newInstance()} instead.
     */
    public HomeBroadcastListFragment() {}

    @Override
    protected int getExtraPaddingTop() {
        return mToolbarAndTabHeight;
    }

    @Override
    protected TimelineBroadcastListResource onAttachResource() {
        return HomeBroadcastListResource.attachTo(this);
    }

    @Override
    public void onBroadcastInserted(int requestCode, int position, Broadcast insertedBroadcast) {
        boolean hasFirstItemView = mList.getLayoutManager().findViewByPosition(0) != null;
        onItemInserted(position, insertedBroadcast);
        if (position == 0 && hasFirstItemView) {
            mList.scrollToPosition(0);
        }
    }

    @Override
    protected void onSwipeRefresh() {
        super.onSwipeRefresh();

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.onRefresh();
    }
}
