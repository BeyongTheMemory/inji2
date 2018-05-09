/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.eventbus;

import com.top.android.inji.network.api.info.dto.UserDTO;

import com.top.android.inji.network.api.info.dto.UserDTO;

public class UserUpdatedEvent extends Event {

    public UserDTO mUser;

    public UserUpdatedEvent(UserDTO user, Object source) {
        super(source);

        this.mUser = user;
    }
}
