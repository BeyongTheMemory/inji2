/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.eventbus;

import com.top.android.inji.network.api.info.frodo.Diary;

import com.top.android.inji.network.api.info.frodo.Diary;

public class DiaryUpdatedEvent extends Event {

    public Diary diary;

    public DiaryUpdatedEvent(Diary diary, Object source) {
        super(source);

        this.diary = diary;
    }
}
