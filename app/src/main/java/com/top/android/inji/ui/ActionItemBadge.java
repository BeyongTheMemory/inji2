/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.top.android.inji.util.TooltipUtils;
import com.top.android.inji.util.ViewUtils;

import com.top.android.inji.R;
import com.top.android.inji.util.TooltipUtils;
import com.top.android.inji.util.ViewUtils;

public class ActionItemBadge {

    public static void setup(MenuItem menuItem, Drawable icon, int count, Activity activity) {

        View actionView = menuItem.getActionView();
        actionView.setOnClickListener(view -> activity.onMenuItemSelected(
                Window.FEATURE_OPTIONS_PANEL, menuItem));
        CharSequence title = menuItem.getTitle();
        if (!TextUtils.isEmpty(title)) {
            actionView.setContentDescription(title);
            TooltipUtils.setup(actionView);
        }

        ImageView iconImage = actionView.findViewById(com.top.android.inji.R.id.icon);
        iconImage.setImageDrawable(icon);

        TextView badgeText = actionView.findViewById(com.top.android.inji.R.id.badge);
        Context themedContext = badgeText.getContext();
        ViewCompat.setBackground(badgeText, new BadgeDrawable(themedContext));
        badgeText.setTextColor(ViewUtils.getColorFromAttrRes(com.top.android.inji.R.attr.colorPrimary, 0,
                themedContext));

        update(badgeText, count);
    }

    public static void setup(MenuItem menuItem, int iconResId, int count, Activity activity) {
        setup(menuItem, ContextCompat.getDrawable(activity, iconResId), count, activity);
    }

    private static void update(TextView badgeText, int count) {
        boolean hasBadge = count > 0;
        // Don't set the badge count to 0 if we are fading away.
        if (hasBadge) {
            badgeText.setText(String.valueOf(count));
        }
        // We are using android:animateLayoutChanges="true", so no need animating here.
        ViewUtils.setVisibleOrGone(badgeText, hasBadge);
    }

    public static void update(MenuItem menuItem, int count) {
        update(menuItem.getActionView().<TextView>findViewById(com.top.android.inji.R.id.badge), count);
    }

    private static class BadgeDrawable extends GradientDrawable {

        public BadgeDrawable(Context context) {
            setColor(ViewUtils.getColorFromAttrRes(com.top.android.inji.R.attr.colorControlNormal, 0, context));
        }

        @Override
        public void setBounds(int left, int top, int right, int bottom) {
            super.setBounds(left, top, right, bottom);

            setCornerRadius(Math.min(right - left, bottom - top));
        }
    }
}
