/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.eventbus;

import android.accounts.Account;

import com.top.android.inji.network.api.info.frodo.Notification;

import java.util.List;

import com.top.android.inji.network.api.info.frodo.Notification;

public class NotificationListUpdatedEvent extends Event {

    public Account account;
    public List<Notification> notificationList;

    public NotificationListUpdatedEvent(Account account, List<Notification> notificationList,
                                        Object source) {
        super(source);

        this.account = account;
        this.notificationList = notificationList;
    }
}
