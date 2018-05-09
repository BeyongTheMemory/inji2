/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.eventbus;

import com.top.android.inji.network.api.info.frodo.Notification;

import com.top.android.inji.network.api.info.frodo.Notification;

public class NotificationUpdatedEvent extends Event {

    public Notification notification;

    public NotificationUpdatedEvent(Notification notification, Object source) {
        super(source);

        this.notification = notification;
    }
}
