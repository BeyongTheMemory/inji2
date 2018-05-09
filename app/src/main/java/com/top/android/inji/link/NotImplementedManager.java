/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.link;

import android.content.Context;

import com.top.android.inji.util.ToastUtils;

import com.top.android.inji.BuildConfig;
import com.top.android.inji.R;
import com.top.android.inji.broadcast.ui.SendBroadcastActivity;
import com.top.android.inji.settings.info.Settings;
import com.top.android.inji.util.ToastUtils;

public class NotImplementedManager {

    private NotImplementedManager() {}

    public static void editProfile(Context context) {
        UrlHandler.open("https://www.douban.com/accounts/", context);
    }

    public static void openDoumail(Context context) {
        UrlHandler.open("https://www.douban.com/doumail/", context);
    }

    public static void sendDoumail(Long userIdOrUid, Context context) {
        UrlHandler.open("https://www.douban.com/doumail/write?to=" + userIdOrUid, context);
    }

    public static void openSearch(Context context) {
        if (Settings.PROGRESSIVE_THIRD_PARTY_APP.getValue()
                && FrodoBridge.search(null, null, null, context)) {
            return;
        }
        UrlHandler.open("https://www.douban.com/search", context);
    }

    public static void showNotYetImplementedToast(Context context) {
        ToastUtils.show(R.string.not_yet_implemented, context);
    }

    public static void signUp(Context context) {
        UrlHandler.open("https://www.douban.com/accounts/register", context);
    }
}
