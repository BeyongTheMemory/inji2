/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.eventbus;

public class DiaryDeletedEvent extends Event {

    public long diaryId;

    public DiaryDeletedEvent(long diaryId, Object source) {
        super(source);

        this.diaryId = diaryId;
    }
}