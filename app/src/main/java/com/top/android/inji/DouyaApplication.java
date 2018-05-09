/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji;

import android.app.Application;
import android.os.Build;
import android.webkit.WebView;

import com.bumptech.glide.request.target.ViewTarget;
import com.facebook.stetho.Stetho;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.top.android.inji.fabric.FabricUtils;
import com.top.android.inji.util.NightModeHelper;

import com.top.android.inji.fabric.FabricUtils;
import com.top.android.inji.util.NightModeHelper;

public class DouyaApplication extends Application {

    private static DouyaApplication sInstance;

    public DouyaApplication() {
        sInstance = this;
    }

    public static DouyaApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        NightModeHelper.setup(this);

        AndroidThreeTen.init(this);
        FabricUtils.init(this);
        ViewTarget.setTagId(R.id.glide_view_target_tag);
        Stetho.initializeWithDefaults(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (BuildConfig.DEBUG) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
    }
}
