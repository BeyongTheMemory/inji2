/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.item.ui;

import android.content.Context;
import android.content.Intent;

import com.top.android.inji.network.api.info.frodo.CollectableItem;
import com.top.android.inji.network.api.info.frodo.Movie;
import com.top.android.inji.network.api.info.frodo.SimpleMovie;

import com.top.android.inji.BuildConfig;
import com.top.android.inji.network.api.info.frodo.CollectableItem;
import com.top.android.inji.network.api.info.frodo.Movie;
import com.top.android.inji.network.api.info.frodo.SimpleMovie;

public class ItemActivities {

    private ItemActivities() {}

    public static Intent makeIntent(CollectableItem item, Context context) {
        if (item instanceof Movie) {
            return MovieActivity.makeIntent((Movie) item, context);
        } else if (item instanceof SimpleMovie) {
            return MovieActivity.makeIntent((SimpleMovie) item, context);
        } else {
            switch (item.getType()) {
                case MOVIE:
                case TV:
                    return MovieActivity.makeIntent(item.id, context);
                default:
                    return null;
            }
        }
    }
}
