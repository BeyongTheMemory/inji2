/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.item.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.top.android.inji.network.api.info.frodo.SimpleItemCollection;
import com.top.android.inji.util.ImageUtils;
import com.top.android.inji.util.TimeUtils;
import com.top.android.inji.util.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.top.android.inji.R;
import com.top.android.inji.network.api.info.frodo.SimpleItemCollection;
import com.top.android.inji.profile.ui.ProfileActivity;
import com.top.android.inji.ui.SimpleAdapter;
import com.top.android.inji.util.ImageUtils;
import com.top.android.inji.util.TimeUtils;
import com.top.android.inji.util.ViewUtils;

public class ItemCollectionListAdapter
        extends SimpleAdapter<SimpleItemCollection, ItemCollectionListAdapter.ViewHolder> {

    public ItemCollectionListAdapter() {
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ViewUtils.inflate(R.layout.item_collection_item, parent));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SimpleItemCollection itemCollection = getItem(position);
        ImageUtils.loadAvatar(holder.avatarImage, itemCollection.user.avatar);
        holder.avatarImage.setOnClickListener(view -> {
            Context context = view.getContext();
            context.startActivity(ProfileActivity.makeIntent(itemCollection.user, context));
        });
        holder.nameText.setText(itemCollection.user.name);
        boolean hasRating = itemCollection.rating != null;
        ViewUtils.setVisibleOrGone(holder.ratingBar, hasRating);
        if (hasRating) {
            holder.ratingBar.setRating(itemCollection.rating.getRatingBarRating());
        }
        holder.dateText.setText(TimeUtils.formatDate(TimeUtils.parseDoubanDateTime(
                itemCollection.createdAt), holder.dateText.getContext()));
        String voteCount = itemCollection.voteCount > 0 ?
                holder.voteCountText.getContext().getString(
                        R.string.item_collection_vote_count_format, itemCollection.voteCount)
                : null;
        holder.voteCountText.setText(voteCount);
        holder.voteLayout.setActivated(itemCollection.isVoted);
        holder.voteLayout.setOnClickListener(view -> {
            // TODO
        });
        holder.commentText.setText(itemCollection.comment);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar)
        public ImageView avatarImage;
        @BindView(R.id.name)
        public TextView nameText;
        @BindView(R.id.rating)
        public RatingBar ratingBar;
        @BindView(R.id.date)
        public TextView dateText;
        @BindView(R.id.vote_layout)
        public ViewGroup voteLayout;
        @BindView(R.id.vote_count)
        public TextView voteCountText;
        @BindView(R.id.menu)
        public ImageButton menuButton;
        @BindView(R.id.comment)
        public TextView commentText;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
