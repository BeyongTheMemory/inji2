/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.ui;

public interface AppBarHost {

    void showAppBar();

    void hideAppBar();

    void setToolBarOnDoubleClickListener(DoubleClickToolbar.OnDoubleClickListener listener);
}
