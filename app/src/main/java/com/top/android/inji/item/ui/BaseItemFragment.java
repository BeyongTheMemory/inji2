/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.item.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.top.android.inji.item.content.BaseItemFragmentResource;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.info.frodo.CollectableItem;
import com.top.android.inji.util.DrawableUtils;
import com.top.android.inji.util.FragmentUtils;
import com.top.android.inji.util.LogUtils;
import com.top.android.inji.util.RecyclerViewUtils;
import com.top.android.inji.util.ShareUtils;
import com.top.android.inji.util.StatusBarColorUtils;
import com.top.android.inji.util.ToastUtils;
import com.top.android.inji.util.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.top.android.inji.R;
import com.top.android.inji.item.content.BaseItemFragmentResource;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.info.frodo.CollectableItem;
import com.top.android.inji.ui.AppBarWrapperLayout;
import com.top.android.inji.ui.BarrierAdapter;
import com.top.android.inji.ui.ContentStateLayout;
import com.top.android.inji.ui.OnVerticalScrollWithPagingTouchSlopListener;
import com.top.android.inji.ui.RatioImageView;
import com.top.android.inji.ui.TransparentDoubleClickToolbar;
import com.top.android.inji.ui.WebViewActivity;
import com.top.android.inji.util.DrawableUtils;
import com.top.android.inji.util.FragmentUtils;
import com.top.android.inji.util.LogUtils;
import com.top.android.inji.util.RecyclerViewUtils;
import com.top.android.inji.util.ShareUtils;
import com.top.android.inji.util.StatusBarColorUtils;
import com.top.android.inji.util.ToastUtils;
import com.top.android.inji.util.ViewUtils;

public abstract class BaseItemFragment<SimpleItemType extends CollectableItem,
        ItemType extends SimpleItemType> extends Fragment
        implements BaseItemFragmentResource.Listener<ItemType> {

    private static final String KEY_PREFIX = BaseItemFragment.class.getName() + '.';

    private static final String EXTRA_ITEM_ID = KEY_PREFIX + "item_id";
    private static final String EXTRA_SIMPLE_ITEM = KEY_PREFIX + "simple_item";
    private static final String EXTRA_ITEM = KEY_PREFIX + "item";

    @BindView(R.id.appBarWrapper)
    AppBarWrapperLayout mAppBarWrapperLayout;
    @BindView(R.id.toolbar)
    TransparentDoubleClickToolbar mToolbar;
    @BindView(R.id.backdrop_wrapper)
    ViewGroup mBackdropWrapperLayout;
    @BindView(R.id.backdrop_layout)
    ViewGroup mBackdropLayout;
    @BindView(R.id.backdrop)
    RatioImageView mBackdropImage;
    @BindView(R.id.backdrop_scrim)
    View mBackdropScrim;
    @BindView(R.id.backdrop_play)
    ImageView mBackdropPlayImage;
    @BindView(R.id.contentState)
    ContentStateLayout mContentStateLayout;
    @BindView(R.id.content)
    ItemContentRecyclerView mContentList;
    @BindView(R.id.content_state_views)
    ItemContentStateViewsLayout mContentStateViewsLayout;

    private long mItemId;
    private SimpleItemType mSimpleItem;
    private ItemType mItem;

    protected BaseItemFragmentResource<SimpleItemType, ItemType> mResource;

    private BarrierAdapter mAdapter;

    public BaseItemFragment<SimpleItemType, ItemType> setArguments(long itemId,
                                                                   SimpleItemType simpleItem,
                                                                   ItemType item) {
        Bundle arguments = FragmentUtils.ensureArguments(this);
        arguments.putLong(EXTRA_ITEM_ID, itemId);
        arguments.putParcelable(EXTRA_SIMPLE_ITEM, simpleItem);
        arguments.putParcelable(EXTRA_ITEM, item);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mItemId = arguments.getLong(EXTRA_ITEM_ID);
        mSimpleItem = arguments.getParcelable(EXTRA_SIMPLE_ITEM);
        mItem = arguments.getParcelable(EXTRA_ITEM);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mResource = onAttachResource(mItemId, mSimpleItem, mItem);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        StatusBarColorUtils.set(Color.TRANSPARENT, activity);
        ViewUtils.setLayoutFullscreen(activity);

        mBackdropImage.setRatio(16, 9);
        ViewCompat.setBackground(mBackdropScrim, DrawableUtils.makeScrimDrawable(Gravity.TOP));

        mContentList.setLayoutManager(new LinearLayoutManager(activity));
        mAdapter = onCreateAdapter();
        mContentList.setAdapter(mAdapter);
        mContentList.setBackdropRatio(mBackdropImage.getRatio());
        mContentList.setBackdropWrapper(mBackdropWrapperLayout);
        mContentList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int mScrollY;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (recyclerView.getChildCount() == 0) {
                    return;
                }
                View firstChild = recyclerView.getChildAt(0);
                int firstPosition = recyclerView.getChildAdapterPosition(firstChild);
                boolean firstItemInLayout = firstPosition == 0;
                if (mScrollY == 0) {
                    if (!firstItemInLayout) {
                        // We are restored from previous scroll position and we don't have a
                        // scrollY.
                        ViewUtils.setVisibleOrInvisible(mBackdropLayout, false);
                        return;
                    } else {
                        // We scrolled towards top so the first item became visible now.
                        // Won't do anything if it is not hidden.
                        ViewUtils.fadeIn(mBackdropLayout);
                        mScrollY = recyclerView.getPaddingTop() - firstChild.getTop();
                    }
                } else {
                    mScrollY += dy;
                }
                // FIXME: Animate out backdrop layout later.
                mBackdropLayout.setTranslationY((float) -mScrollY / 2);
            }
        });
        int colorPrimaryDark = ViewUtils.getColorFromAttrRes(R.attr.colorPrimaryDark, 0, activity);
        mContentList.addOnScrollListener(new OnVerticalScrollWithPagingTouchSlopListener(activity) {
            private int mStatusBarColor = Color.TRANSPARENT;
            @Override
            public void onScrolledUp() {
                if (mAppBarWrapperLayout.isHidden()) {
                    mToolbar.setTransparent(!hasFirstChildReachedTop());
                }
                mAppBarWrapperLayout.show();
            }
            @Override
            public void onScrolledDown() {
                if (hasFirstChildReachedTop()) {
                    mAppBarWrapperLayout.hide();
                }
            }
            @Override
            public void onScrolled(int dy) {
                boolean initialize = dy == 0;
                boolean hasFirstChildReachedTop = hasFirstChildReachedTop();
                int statusBarColor = hasFirstChildReachedTop ? colorPrimaryDark : Color.TRANSPARENT;
                if (mStatusBarColor != statusBarColor) {
                    mStatusBarColor = statusBarColor;
                    if (initialize) {
                        StatusBarColorUtils.set(mStatusBarColor, activity);
                    } else {
                        StatusBarColorUtils.animateTo(mStatusBarColor, activity);
                    }
                }
                if (mAppBarWrapperLayout.isShowing()) {
                    if (initialize) {
                        mToolbar.setTransparent(!hasFirstChildReachedTop);
                    } else {
                        mToolbar.animateToTransparent(!hasFirstChildReachedTop);
                    }
                }
            }
            private boolean hasFirstChildReachedTop() {
                return RecyclerViewUtils.hasFirstChildReachedTop(mContentList,
                        mToolbar.getBottom());
            }
        });
        mToolbar.setOnDoubleClickListener(view -> {
            mContentList.smoothScrollToPosition(0);
            return true;
        });

        mContentStateViewsLayout.setBackdropRatio(mBackdropImage.getRatio());

        if (mResource.hasSimpleItem()) {
            updateWithSimpleItem(mResource.getSimpleItem());
        }
        mContentStateLayout.setLoading();
        if (mResource.isAnyLoaded()) {
            mResource.notifyChanged();
        }

        if (mAdapter.getItemCount() == 0) {
            mToolbar.getBackground().setAlpha(0);
        }
    }

    protected abstract BaseItemFragmentResource<SimpleItemType, ItemType> onAttachResource(
            long itemId, SimpleItemType simpleItem, ItemType item);

    protected abstract BarrierAdapter onCreateAdapter();

    @Override
    public void onDestroy() {
        super.onDestroy();

        mResource.detach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.action_share:
                share();
                return true;
            case R.id.action_view_on_web:
                viewOnWeb();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLoadError(int requestCode, ApiError error) {
        LogUtils.e(error.toString());
        if (mAdapter.getItemCount() > 0) {
            mAdapter.setError();
        } else {
            mContentStateLayout.setError();
        }
        Activity activity = getActivity();
        ToastUtils.show(ApiError.getErrorString(error), activity);
    }

    @Override
    public void onItemChanged(int requestCode, ItemType newItem) {}

    protected void updateWithSimpleItem(SimpleItemType simpleItem) {
        getActivity().setTitle(simpleItem.title);
    }

    private void share() {
        ShareUtils.shareText(makeUrl(), getActivity());
    }

    private void viewOnWeb() {
        startActivity(WebViewActivity.makeIntent(makeUrl(), true, getActivity()));
    }

    private String makeUrl() {
        if (mResource.hasSimpleItem()) {
            return mResource.getSimpleItem().getUrl();
        } else {
            return makeItemUrl(mResource.getItemId());
        }
    }

    protected abstract String makeItemUrl(long itemId);
}
