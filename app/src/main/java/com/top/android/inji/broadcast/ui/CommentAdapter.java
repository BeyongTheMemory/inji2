/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.broadcast.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.top.android.inji.network.api.info.frodo.Comment;
import com.top.android.inji.ui.ClickableSimpleAdapter;
import com.top.android.inji.ui.TimeTextView;
import com.top.android.inji.util.ImageUtils;
import com.top.android.inji.util.RecyclerViewUtils;
import com.top.android.inji.util.ViewUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.top.android.inji.R;
import com.top.android.inji.network.api.info.frodo.Comment;
import com.top.android.inji.profile.ui.ProfileActivity;
import com.top.android.inji.ui.ClickableSimpleAdapter;
import com.top.android.inji.ui.TimeTextView;
import com.top.android.inji.util.ImageUtils;
import com.top.android.inji.util.RecyclerViewUtils;
import com.top.android.inji.util.ViewUtils;

public class CommentAdapter extends ClickableSimpleAdapter<Comment, CommentAdapter.ViewHolder> {

    public CommentAdapter(List<Comment> commentList,
                          OnItemClickListener<Comment> onItemClickListener) {
        super(commentList, onItemClickListener);

        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(ViewUtils.inflate(com.top.android.inji.R.layout.broadcast_comment_item,
                parent));
        ViewUtils.setTextViewLinkClickable(holder.textText);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comment comment = getItem(position);
        ImageUtils.loadAvatar(holder.avatarImage, comment.author.avatar);
        Context context = RecyclerViewUtils.getContext(holder);
        holder.avatarImage.setOnClickListener(view -> context.startActivity(
                ProfileActivity.makeIntent(comment.author, context)));
        holder.nameText.setText(comment.author.name);
        holder.timeText.setDoubanTime(comment.createdAt);
        holder.textText.setText(comment.getTextWithEntities());
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.avatarImage.setImageDrawable(null);
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
