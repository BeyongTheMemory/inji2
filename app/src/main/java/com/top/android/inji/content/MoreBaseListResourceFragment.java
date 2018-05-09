/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.content;

import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.info.frodo.BaseList;

import java.util.List;

import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.info.frodo.BaseList;

public abstract class MoreBaseListResourceFragment<ResponseType extends BaseList, ResourceType>
        extends MoreRawListResourceFragment<ResponseType, ResourceType> {

    private int mTotalSize = -1;

    public int getTotalSize() {
        return mTotalSize;
    }

    @Override
    protected final void onLoadFinished(boolean more, int count, boolean successful,
                                        ResponseType response, ApiError error) {
        if (successful) {
            mTotalSize = response.total;
        }
        onLoadFinished(more, count, successful, successful ? response.getList() : null, error);
    }

    protected abstract void onLoadFinished(boolean more, int count, boolean successful,
                                           List<ResourceType> response, ApiError error);
}
