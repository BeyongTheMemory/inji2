/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.item.ui;

import android.support.v7.widget.RecyclerView;

import com.top.android.inji.ui.BarrierAdapter;

public class MovieAdapter extends BarrierAdapter {

    private MovieDataAdapter mDataAdapter;

    public MovieAdapter(MovieDataAdapter.Listener listener) {
        super(new MovieDataAdapter(listener));

        RecyclerView.Adapter<?>[] adapters = getAdapters();
        mDataAdapter = (MovieDataAdapter) adapters[0];
    }

    public void setData(MovieDataAdapter.Data data) {
        mDataAdapter.setData(data);
    }

    public void notifyItemCollectionChanged() {
        mDataAdapter.notifyItemCollectionChanged();
    }
}
