/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.util;

import android.content.Context;

import com.top.android.inji.network.api.info.UrlGettable;

import com.top.android.inji.network.api.info.UrlGettable;

public class ShareUtils {

    private ShareUtils() {}

    public static void shareText(String text, Context context) {
        AppUtils.startActivityWithChooser(IntentUtils.makeSendText(text), context);
    }

    public static void shareUrl(UrlGettable urlGettable, Context context) {
        shareText(urlGettable.getUrl(), context);
    }
}
