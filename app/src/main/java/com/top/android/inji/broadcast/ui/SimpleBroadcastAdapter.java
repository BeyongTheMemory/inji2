/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.broadcast.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.ui.SimpleAdapter;
import com.top.android.inji.ui.TimeTextView;
import com.top.android.inji.util.ImageUtils;
import com.top.android.inji.util.RecyclerViewUtils;
import com.top.android.inji.util.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.top.android.inji.R;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.profile.ui.ProfileActivity;
import com.top.android.inji.ui.SimpleAdapter;
import com.top.android.inji.ui.TimeTextView;
import com.top.android.inji.util.ImageUtils;
import com.top.android.inji.util.RecyclerViewUtils;
import com.top.android.inji.util.ViewUtils;

public class SimpleBroadcastAdapter
        extends SimpleAdapter<Broadcast, SimpleBroadcastAdapter.ViewHolder> {

    public SimpleBroadcastAdapter() {
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        //noinspection deprecation
        return getItem(position).id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(ViewUtils.inflate(com.top.android.inji.R.layout.simple_broadcast_item,
                parent));
        ViewUtils.setTextViewLinkClickable(holder.textText);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Context context = RecyclerViewUtils.getContext(holder);
        Broadcast broadcast = getItem(position);
        holder.itemView.setOnClickListener(view -> {
            // rebroadcastedBroadcast can always be empty.
            //if (TextUtils.isEmpty(broadcast.isSimpleRebroadcast()) {
            if (TextUtils.isEmpty(broadcast.text)) {
                return;
            }
            // TODO: Can we pass the broadcast? But rebroadcastedBroadcast and parentBroadcast will
            // be missing.
            context.startActivity(BroadcastActivity.makeIntent(broadcast.id, context));
        });
        ImageUtils.loadAvatar(holder.avatarImage, broadcast.author.avatar);
        holder.avatarImage.setOnClickListener(view -> context.startActivity(
                ProfileActivity.makeIntent(broadcast.author, context)));
        holder.nameText.setText(broadcast.author.name);
        holder.timeText.setDoubanTime(broadcast.createdAt);
        holder.textText.setText(broadcast.getRebroadcastText(context));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(com.top.android.inji.R.id.avatar)
        public ImageView avatarImage;
        @BindView(com.top.android.inji.R.id.name)
        public TextView nameText;
        @BindView(com.top.android.inji.R.id.time)
        public TimeTextView timeText;
        @BindView(com.top.android.inji.R.id.text)
        public TextView textText;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
