/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.broadcast.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.top.android.inji.gallery.ui.GalleryActivity;
import com.top.android.inji.link.UriHandler;
import com.top.android.inji.ui.DividerItemDecoration;
import com.top.android.inji.ui.HorizontalUploadImageAdapter;
import com.top.android.inji.ui.OnHorizontalScrollListener;
import com.top.android.inji.ui.UploadImageLayout;
import com.top.android.inji.util.CollectionUtils;
import com.top.android.inji.util.DrawableUtils;
import com.top.android.inji.util.ImageUtils;
import com.top.android.inji.util.ObjectUtils;
import com.top.android.inji.util.ViewUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.top.android.inji.R;
import com.top.android.inji.gallery.ui.GalleryActivity;
import com.top.android.inji.link.UriHandler;
import com.top.android.inji.ui.DividerItemDecoration;
import com.top.android.inji.ui.HorizontalUploadImageAdapter;
import com.top.android.inji.ui.OnHorizontalScrollListener;
import com.top.android.inji.ui.UploadImageLayout;
import com.top.android.inji.util.CollectionUtils;
import com.top.android.inji.util.DrawableUtils;
import com.top.android.inji.util.ImageUtils;
import com.top.android.inji.util.ObjectUtils;
import com.top.android.inji.util.ViewUtils;

/**
 * @see BroadcastLayout
 */
public class SendBroadcastAttachmentLayout extends FrameLayout {

    @BindView(com.top.android.inji.R.id.sendbroadcastattachmentlayout_link_layout)
    ViewGroup mLinkLayout;
    @BindView(com.top.android.inji.R.id.sendbroadcastattachmentlayout_link_title)
    TextView mLinkTitleText;
    @BindView(com.top.android.inji.R.id.sendbroadcastattachmentlayout_link_description)
    TextView mLinkDescriptionText;
    @BindView(com.top.android.inji.R.id.sendbroadcastattachmentlayout_link_image)
    ImageView mLinkImage;
    @BindView(com.top.android.inji.R.id.sendbroadcastattachmentlayout_single_image)
    UploadImageLayout mSingleImageLayout;
    @BindView(com.top.android.inji.R.id.sendbroadcastattachmentlayout_image_list_layout)
    ViewGroup mImageListLayout;
    @BindView(com.top.android.inji.R.id.sendbroadcastattachmentlayout_image_list_description_layout)
    FrameLayout mImageListDescriptionLayout;
    @BindView(com.top.android.inji.R.id.sendbroadcastattachmentlayout_image_list_description)
    TextView mImageListDescriptionText;
    @BindView(com.top.android.inji.R.id.sendbroadcastattachmentlayout_image_list)
    RecyclerView mImageList;

    private HorizontalUploadImageAdapter mImageListAdapter;

    public SendBroadcastAttachmentLayout(@NonNull Context context) {
        super(context);

        init();
    }

    public SendBroadcastAttachmentLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public SendBroadcastAttachmentLayout(@NonNull Context context, @Nullable AttributeSet attrs,
                                         int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public SendBroadcastAttachmentLayout(@NonNull Context context, @Nullable AttributeSet attrs,
                                         int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private void init() {

        ViewUtils.inflateInto(com.top.android.inji.R.layout.send_broadcast_attachment_layout, this);
        ButterKnife.bind(this);

        ViewCompat.setBackground(mImageListDescriptionLayout, DrawableUtils.makeScrimDrawable());
        mImageList.setHasFixedSize(true);
        mImageList.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        mImageList.addItemDecoration(new DividerItemDecoration(DividerItemDecoration.HORIZONTAL,
                com.top.android.inji.R.drawable.transparent_divider_vertical_2dp, getContext()));
        mImageListAdapter = new HorizontalUploadImageAdapter();
        mImageList.setAdapter(mImageListAdapter);
        mImageList.addOnScrollListener(new OnHorizontalScrollListener() {
            private boolean mShowingDescription = true;
            @Override
            public void onScrolledLeft() {
                if (!mShowingDescription) {
                    mShowingDescription = true;
                    ViewUtils.fadeIn(mImageListDescriptionLayout);
                }
            }
            @Override
            public void onScrolledRight() {
                if (mShowingDescription) {
                    mShowingDescription = false;
                    ViewUtils.fadeOut(mImageListDescriptionLayout);
                }
            }
        });
    }

    public void bind(SendBroadcastFragment.LinkInfo linkInfo, List<Uri> imageUris,
                     boolean scrollImageListToEnd) {
        boolean hasLink = linkInfo != null;
        ViewUtils.setVisibleOrGone(mLinkLayout, hasLink);
        if (hasLink) {
            String linkTitle = linkInfo.title;
            if (TextUtils.isEmpty(linkTitle)) {
                linkTitle = getContext().getString(com.top.android.inji.R.string.broadcast_send_link_default_title);
            }
            mLinkTitleText.setText(linkTitle);
            mLinkDescriptionText.setText(ObjectUtils.firstNonNull(linkInfo.description,
                    linkInfo.url));
            boolean hasLinkImage = linkInfo.imageUrl != null;
            ViewUtils.setVisibleOrGone(mLinkImage, hasLinkImage);
            if (hasLinkImage) {
                ImageUtils.loadImage(mLinkImage, linkInfo.imageUrl);
            }
            mLinkLayout.setOnClickListener(view -> UriHandler.open(linkInfo.url,
                    view.getContext()));
        }
        boolean hasImage = !CollectionUtils.isEmpty(imageUris);
        boolean hasSingleImage = hasImage && imageUris.size() == 1;
        ViewUtils.setVisibleOrGone(mSingleImageLayout, hasSingleImage);
        if (hasSingleImage) {
            Uri imageUri = imageUris.get(0);
            mSingleImageLayout.setVisibility(VISIBLE);
            mSingleImageLayout.loadImage(imageUri);
            mSingleImageLayout.setOnClickListener(view -> {
                Context context = view.getContext();
                context.startActivity(GalleryActivity.makeIntent(imageUri, context));
            });
        }
        boolean hasImageList = hasImage && imageUris.size() > 1;
        ViewUtils.setVisibleOrGone(mImageListLayout, hasImageList);
        if (hasImageList) {
            mImageListDescriptionText.setText(mImageListDescriptionText.getContext().getString(
                    com.top.android.inji.R.string.broadcast_image_list_count_format, imageUris.size()));
            mImageListAdapter.replace(imageUris);
            mImageListAdapter.setOnItemClickListener((parent, itemView, item, position) -> {
                Context context = itemView.getContext();
                context.startActivity(GalleryActivity.makeIntent(imageUris, position, context));
            });
            if (scrollImageListToEnd) {
                mImageList.scrollToPosition(imageUris.size() - 1);
            }
        }
    }

    public void bind(SendBroadcastFragment.LinkInfo linkInfo, List<Uri> imageUris) {
        bind(linkInfo, imageUris, false);
    }

    public void setOnRemoveImageListener(
            HorizontalUploadImageAdapter.OnRemoveImageListener listener) {
        mSingleImageLayout.setRemoveButtonOnClickListener(view -> listener.onRemoveImage(0));
        mImageListAdapter.setOnRemoveImageListener(listener);
    }
}
