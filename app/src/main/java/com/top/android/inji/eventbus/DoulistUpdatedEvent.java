/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.eventbus;

import com.top.android.inji.network.api.info.frodo.Doulist;

import com.top.android.inji.network.api.info.frodo.Doulist;

public class DoulistUpdatedEvent extends Event {

    public Doulist doulist;

    public DoulistUpdatedEvent(Doulist doulist, Object source) {
        super(source);

        this.doulist = doulist;
    }
}
