/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package android.support.v4.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;

import com.top.android.inji.R;
import com.top.android.inji.util.ViewUtils;

/**
 * Offers <code>app:progressOffset</code>, <code>app:progressDistanceOffset</code>, and defaults
 * progress color to <code>?colorPrimary</code>.
 */
public class FriendlySwipeRefreshLayout extends SwipeRefreshLayout {

    private static final int CIRCLE_DIAMETER_DP = CIRCLE_DIAMETER;
    private static final int CIRCLE_DIAMETER_LARGE_DP = CIRCLE_DIAMETER_LARGE;
    private static final int CIRCLE_SHADOW_DP = 7;
    private static final int DEFAULT_CIRCLE_DISTANCE_DP = 64; // DEFAULT_CIRCLE_TARGET

    private int mSize = DEFAULT;
    private int mCircleDiameter;
    private int mDefaultCircleDistance;

    private CanChildScrollUpCallback mCanChildScrollUpCallback;

    public FriendlySwipeRefreshLayout(Context context) {
        super(context);

        init(null);
    }

    public FriendlySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    @SuppressWarnings("RestrictedApi")
    private void init(AttributeSet attrs) {

        updateCircleDiameter();
        Context context = getContext();
        mDefaultCircleDistance = ViewUtils.dpToPxOffset(DEFAULT_CIRCLE_DISTANCE_DP, context);

        TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs,
                R.styleable.FriendlySwipeRefreshLayout, 0, 0);
        int progressOffset = a.getDimensionPixelOffset(
                R.styleable.FriendlySwipeRefreshLayout_progressOffset, 0);
        int progressDistanceOffset = a.getDimensionPixelOffset(
                R.styleable.FriendlySwipeRefreshLayout_progressDistanceOffset, 0);
        a.recycle();

        if (progressOffset != 0 || progressDistanceOffset != 0) {
            setProgressViewOffset(progressOffset, progressDistanceOffset);
        }

        int backgroundColor = ViewUtils.getColorFromAttrRes(R.attr.colorBackgroundFloating,
                Color.WHITE, context);
        ((ShapeDrawable) mCircleView.getBackground()).getPaint().setColor(backgroundColor);
        mProgress.setBackgroundColor(backgroundColor);
        setColorSchemeColors(ViewUtils.getColorFromAttrRes(R.attr.colorControlActivated,
                Color.BLACK, context));
    }

    @Override
    public void setSize(int size) {
        super.setSize(size);

        if (size == LARGE || size == DEFAULT) {
            mSize = size;
            updateCircleDiameter();
        }
    }

    private void updateCircleDiameter() {
        int circleDiameterDp = mSize == DEFAULT ? CIRCLE_DIAMETER_DP : CIRCLE_DIAMETER_LARGE_DP;
        circleDiameterDp += CIRCLE_SHADOW_DP;
        mCircleDiameter = ViewUtils.dpToPxSize(circleDiameterDp, getContext());
    }

    public void setProgressViewOffset(int offset, int distanceOffset) {
        int progressStart = offset - mCircleDiameter;
        int progressEnd = progressStart + mDefaultCircleDistance + distanceOffset;
        setProgressViewOffset(false, progressStart, progressEnd);
    }

    public void setProgressViewOffset(int offset) {
        setProgressViewOffset(offset, 0);
    }

    @Override
    public int getProgressCircleDiameter() {
        return mCircleDiameter;
    }

    public CanChildScrollUpCallback getCanChildScrollUpCallback() {
        return mCanChildScrollUpCallback;
    }

    public void setCanChildScrollUpCallback(CanChildScrollUpCallback canChildScrollUpCallback) {
        mCanChildScrollUpCallback = canChildScrollUpCallback;
    }

    @Override
    public boolean canChildScrollUp() {
        if (mCanChildScrollUpCallback != null) {
            return mCanChildScrollUpCallback.canChildScrollUp();
        }
        return super.canChildScrollUp();
    }

    public interface CanChildScrollUpCallback {
        boolean canChildScrollUp();
    }
}
