/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.app;

import android.support.v4.app.NotificationManagerCompat;

import com.top.android.inji.R;

public interface Notifications {

    interface Channels {

        interface SEND_BROADCAST {
            String ID = "send_broadcast";
            int NAME_RES = R.string.notification_channel_send_broadcast_name;
            int IMPORTANCE = NotificationManagerCompat.IMPORTANCE_DEFAULT;
        }
    }

    interface Ids {
        int SENDING_BROADCAST = 1;
        int SEND_BROADCAST_FAILED = 2;
    }
}
