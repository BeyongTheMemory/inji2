/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.broadcast.content;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ObjectsCompat;
import android.text.TextUtils;

import com.top.android.inji.app.Notifications;
import com.top.android.inji.content.ResourceWriter;
import com.top.android.inji.content.ResourceWriterManager;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.network.api.info.frodo.UploadedImage;
import com.top.android.inji.util.CollectionUtils;
import com.top.android.inji.util.LogUtils;
import com.top.android.inji.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import com.top.android.inji.R;
import com.top.android.inji.app.Notifications;
import com.top.android.inji.broadcast.ui.SendBroadcastActivity;
import com.top.android.inji.broadcast.ui.SendBroadcastFragment;
import com.top.android.inji.content.ResourceWriter;
import com.top.android.inji.content.ResourceWriterManager;
import com.top.android.inji.eventbus.BroadcastSendErrorEvent;
import com.top.android.inji.eventbus.BroadcastSentEvent;
import com.top.android.inji.eventbus.BroadcastWriteStartedEvent;
import com.top.android.inji.eventbus.EventBusUtils;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.ApiRequest;
import com.top.android.inji.network.api.ApiService;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.network.api.info.frodo.UploadedImage;
import com.top.android.inji.util.CollectionUtils;
import com.top.android.inji.util.LogUtils;
import com.top.android.inji.util.ToastUtils;

class SendBroadcastWriter extends ResourceWriter<SendBroadcastWriter> {

    private static int sNextId = 1;

    private long mId;

    private String mText;
    private List<Uri> mImageUris;
    private String mLinkTitle;
    private String mLinkUrl;

    private boolean mHasImages;
    private List<String> mUploadedImageUrls;

    private ApiRequest<?> mRequest;

    SendBroadcastWriter(String text, List<Uri> imageUris, String linkTitle, String linkUrl,
                        ResourceWriterManager<SendBroadcastWriter> manager) {
        super(manager);

        mId = sNextId;
        ++sNextId;

        mText = text;
        mImageUris = imageUris;
        mLinkTitle = linkTitle;
        mLinkUrl = linkUrl;

        mHasImages = !CollectionUtils.isEmpty(mImageUris);
        if (mHasImages) {
            mUploadedImageUrls = new ArrayList<>();
        }
    }

    public long getId() {
        return mId;
    }

    public String getText() {
        return mText;
    }

    public List<Uri> getImageUris() {
        return mImageUris;
    }

    public String getLinkTitle() {
        return mLinkTitle;
    }

    public String getLinkUrl() {
        return mLinkUrl;
    }

    @Override
    public void onStart() {
        if (mHasImages) {
            sendWithImages();
        } else {
            sendSimple();
        }
        EventBusUtils.postAsync(new BroadcastWriteStartedEvent(mId, this));
    }

    @Override
    public void onDestroy() {
        if (mRequest != null) {
            mRequest.cancel();
            mRequest = null;
        }
    }

    private void sendSimple() {
        ApiRequest<Broadcast> request = ApiService.getInstance().sendBroadcast(mText, null,
                mLinkTitle, mLinkUrl);
        request.enqueue(new ApiRequest.Callback<Broadcast>() {
            @Override
            public void onResponse(Broadcast response) {
                onSuccessSimple(response);
            }
            @Override
            public void onErrorResponse(ApiError error) {
                onErrorSimple(error);
            }
        });
        mRequest = request;
    }

    private void sendWithImages() {
        ToastUtils.show(com.top.android.inji.R.string.broadcast_sending, getContext());
        continueSendingWithImages();
    }

    private void continueSendingWithImages() {
        if (mUploadedImageUrls.size() < mImageUris.size()) {
            Context context = getContext();
            String notificationText = context.getString(
                    com.top.android.inji.R.string.broadcast_sending_notification_text_uploading_images_format,
                    mUploadedImageUrls.size() + 1, mImageUris.size());
            startForeground(notificationText);
            ApiRequest<UploadedImage> request = ApiService.getInstance().uploadBroadcastImage(
                    mImageUris.get(mUploadedImageUrls.size()), context);
            request.enqueue(new ApiRequest.Callback<UploadedImage>() {
                @Override
                public void onResponse(UploadedImage response) {
                    onImageUploadSuccess(response);
                }
                @Override
                public void onErrorResponse(ApiError error) {
                    onErrorWithImages(error);
                }
            });
            mRequest = request;
        } else {
            startForeground(getContext().getString(
                    com.top.android.inji.R.string.broadcast_sending_notification_text_sending));
            ApiRequest<Broadcast> request = ApiService.getInstance().sendBroadcast(mText,
                    mUploadedImageUrls, null, null);
            request.enqueue(new ApiRequest.Callback<Broadcast>() {
                @Override
                public void onResponse(Broadcast response) {
                    onSuccessWithImages(response);
                }
                @Override
                public void onErrorResponse(ApiError error) {
                    onErrorWithImages(error);
                }
            });
            mRequest = request;
        }
    }

    private void startForeground(CharSequence contentText) {
        if (mUploadedImageUrls.isEmpty()) {
            createNotificationChannel();
        }
        String contentTitle = getContext().getString(com.top.android.inji.R.string.broadcast_sending_notification_title);
        Notification notification = createNotificationBuilder(contentTitle, contentText)
                .setOngoing(true)
                .build();
        getService().startForeground(Notifications.Ids.SENDING_BROADCAST, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        Context context = getContext();
        String channelName = context.getString(
                Notifications.Channels.SEND_BROADCAST.NAME_RES);
        @SuppressLint("WrongConstant")
        NotificationChannel channel = new NotificationChannel(
                Notifications.Channels.SEND_BROADCAST.ID, channelName,
                Notifications.Channels.SEND_BROADCAST.IMPORTANCE);
        String channelDescription = context.getString(
                com.top.android.inji.R.string.notification_channel_send_broadcast_description);
        channel.setDescription(channelDescription);
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    private NotificationCompat.Builder createNotificationBuilder(CharSequence contentTitle,
                                                                 CharSequence contentText) {
        Context context = getContext();
        return new NotificationCompat.Builder(context,
                Notifications.Channels.SEND_BROADCAST.ID)
                .setColor(ContextCompat.getColor(context, com.top.android.inji.R.color.douya_primary))
                .setSmallIcon(com.top.android.inji.R.drawable.notification_icon)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setTicker(contentText);
    }

    @Override
    protected void stopSelf() {

        if (mHasImages) {
            getService().stopForeground(true);
        }

        super.stopSelf();
    }

    private void onSuccessSimple(Broadcast broadcast) {

        ToastUtils.show(com.top.android.inji.R.string.broadcast_send_successful, getContext());

        EventBusUtils.postAsync(new BroadcastSentEvent(mId, broadcast, this));

        stopSelf();
    }

    private void onErrorSimple(ApiError error) {

        LogUtils.e(error.toString());
        Context context = getContext();
        ToastUtils.show(context.getString(com.top.android.inji.R.string.broadcast_send_failed_format,
                ApiError.getErrorString(error)), context);

        EventBusUtils.postAsync(new BroadcastSendErrorEvent(mId, this));

        stopSelf();
    }

    private void onImageUploadSuccess(UploadedImage uploadedImage) {
        mUploadedImageUrls.add(uploadedImage.url);
        continueSendingWithImages();
    }

    private void onSuccessWithImages(Broadcast broadcast) {

        ToastUtils.showLong(com.top.android.inji.R.string.broadcast_send_successful, getContext());

        EventBusUtils.postAsync(new BroadcastSentEvent(mId, broadcast, this));

        stopSelf();
    }

    private void onErrorWithImages(ApiError error) {

        LogUtils.e(error.toString());
        Context context = getContext();
        ToastUtils.showLong(context.getString(com.top.android.inji.R.string.broadcast_send_failed_format,
                ApiError.getErrorString(error)), context);
        notifyError(error);

        EventBusUtils.postAsync(new BroadcastSendErrorEvent(mId, this));

        stopSelf();
    }

    private void notifyError(ApiError error) {
        Context context = getContext();
        String contentTitle = context.getString(
                com.top.android.inji.R.string.broadcast_send_failed_notification_title_format, ApiError.getErrorString(
                        error));
        String contentText = context.getString(com.top.android.inji.R.string.broadcast_send_failed_notification_text);
        SendBroadcastFragment.LinkInfo linkInfo;
        if (!TextUtils.isEmpty(mLinkUrl)) {
            linkInfo = new SendBroadcastFragment.LinkInfo(mLinkUrl, mLinkTitle, null, null);
        } else {
            linkInfo = null;
        }
        Intent intent = SendBroadcastActivity.makeIntent(mText, mImageUris, linkInfo, context);
        int requestCode = ObjectsCompat.hash(mText, mImageUris, mLinkTitle, mLinkUrl);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = createNotificationBuilder(contentTitle, contentText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        NotificationManagerCompat.from(context).notify(Notifications.Ids.SEND_BROADCAST_FAILED,
                notification);
    }
}
