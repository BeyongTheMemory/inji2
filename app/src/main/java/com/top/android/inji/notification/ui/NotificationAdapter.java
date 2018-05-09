/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.notification.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.top.android.inji.link.UriHandler;
import com.top.android.inji.network.api.info.frodo.Notification;
import com.top.android.inji.ui.SimpleAdapter;
import com.top.android.inji.ui.TimeTextView;
import com.top.android.inji.util.RecyclerViewUtils;
import com.top.android.inji.util.ViewUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.top.android.inji.R;
import com.top.android.inji.link.UriHandler;
import com.top.android.inji.network.api.info.frodo.Notification;
import com.top.android.inji.ui.SimpleAdapter;
import com.top.android.inji.ui.TimeTextView;
import com.top.android.inji.util.RecyclerViewUtils;
import com.top.android.inji.util.ViewUtils;

public class NotificationAdapter extends SimpleAdapter<Notification,
        NotificationAdapter.ViewHolder> {

    private final ColorStateList mTextColorPrimary;
    private final ColorStateList mTextColorSecondary;

    private Listener mListener;

    public NotificationAdapter(List<Notification> notificationList, Context context) {
        super(notificationList);

        mTextColorPrimary = ViewUtils.getColorStateListFromAttrRes(android.R.attr.textColorPrimary,
                context);
        mTextColorSecondary = ViewUtils.getColorStateListFromAttrRes(
                android.R.attr.textColorSecondary, context);

        setHasStableIds(true);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ViewUtils.inflate(com.top.android.inji.R.layout.notification_item, parent));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Context context = RecyclerViewUtils.getContext(holder);
        final Notification notification = getItem(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onMarkNotificationAsRead(notification);
                }
                UriHandler.open(notification.targetUri, context);
            }
        });
        holder.textText.setText(notification.text);
        holder.textText.setTextColor(notification.read ? mTextColorSecondary : mTextColorPrimary);
        holder.timeText.setDoubanTime(notification.time);
    }

    public interface Listener {
        void onMarkNotificationAsRead(Notification notification);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(com.top.android.inji.R.id.text)
        public TextView textText;
        @BindView(com.top.android.inji.R.id.time)
        public TimeTextView timeText;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
