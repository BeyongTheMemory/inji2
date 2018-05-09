/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.eventbus;

public class BroadcastDeletedEvent extends Event {

    public long broadcastId;

    public BroadcastDeletedEvent(long broadcastId, Object source) {
        super(source);

        this.broadcastId = broadcastId;
    }
}
