/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.ui;

import android.app.Activity;

public interface FragmentFinishable {

    void finishFromFragment();

    void finishAfterTransitionFromFragment();

    static void finish(Activity activity) {
        ((FragmentFinishable) activity).finishFromFragment();
    }

    static void finishAfterTransition(Activity activity) {
        ((FragmentFinishable) activity).finishAfterTransitionFromFragment();
    }
}
