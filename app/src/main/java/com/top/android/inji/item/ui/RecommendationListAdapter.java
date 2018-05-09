/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.item.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.top.android.inji.link.UriHandler;
import com.top.android.inji.network.api.info.frodo.CollectableItem;
import com.top.android.inji.util.ImageUtils;
import com.top.android.inji.util.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.top.android.inji.R;
import com.top.android.inji.link.UriHandler;
import com.top.android.inji.network.api.info.frodo.CollectableItem;
import com.top.android.inji.ui.RatioImageView;
import com.top.android.inji.ui.SimpleAdapter;
import com.top.android.inji.util.ImageUtils;
import com.top.android.inji.util.ViewUtils;

public class RecommendationListAdapter
        extends SimpleAdapter<CollectableItem, RecommendationListAdapter.ViewHolder> {

    public RecommendationListAdapter() {
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(ViewUtils.inflate(R.layout.item_recommendation_item,
                parent));
        holder.coverImage.setRatio(27, 40);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CollectableItem item = getItem(position);
        ImageUtils.loadImage(holder.coverImage, item.cover);
        holder.titleText.setText(item.title);
        boolean hasRating = item.rating.hasRating();
        if (hasRating) {
            holder.ratingText.setText(item.rating.getRatingString(holder.ratingText.getContext()));
        } else {
            holder.ratingText.setText(item.getRatingUnavailableReason(
                    holder.ratingText.getContext()));
        }
        ViewUtils.setVisibleOrGone(holder.ratingStarText, hasRating);
        holder.itemView.setOnClickListener(view -> {
            // TODO
            UriHandler.open(item.url, view.getContext());
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cover)
        public RatioImageView coverImage;
        @BindView(R.id.title)
        public TextView titleText;
        @BindView(R.id.rating)
        public TextView ratingText;
        @BindView(R.id.rating_star)
        public TextView ratingStarText;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
