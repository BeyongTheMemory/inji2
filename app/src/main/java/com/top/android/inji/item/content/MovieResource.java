/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.item.content;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.top.android.inji.network.api.info.frodo.CollectableItem;
import com.top.android.inji.network.api.info.frodo.Movie;
import com.top.android.inji.network.api.info.frodo.SimpleMovie;
import com.top.android.inji.util.FragmentUtils;

import com.top.android.inji.network.api.info.frodo.CollectableItem;
import com.top.android.inji.network.api.info.frodo.Movie;
import com.top.android.inji.network.api.info.frodo.SimpleMovie;
import com.top.android.inji.util.FragmentUtils;

public class MovieResource extends BaseItemResource<SimpleMovie, Movie> {

    private static final String FRAGMENT_TAG_DEFAULT = MovieResource.class.getName();

    private static MovieResource newInstance(long movieId, SimpleMovie simpleMovie, Movie movie) {
        //noinspection deprecation
        MovieResource instance = new MovieResource();
        instance.setArguments(movieId, simpleMovie, movie);
        return instance;
    }

    public static MovieResource attachTo(long movieId, SimpleMovie simpleMovie, Movie movie,
                                         Fragment fragment, String tag,
                                         int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        MovieResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(movieId, simpleMovie, movie);
            instance.targetAt(fragment, requestCode);
            FragmentUtils.add(instance, activity, tag);
        }
        return instance;
    }

    public static MovieResource attachTo(long movieId, SimpleMovie simpleMovie, Movie movie,
                                         Fragment fragment) {
        return attachTo(movieId, simpleMovie, movie, fragment, FRAGMENT_TAG_DEFAULT,
                REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public MovieResource() {}

    @Override
    protected CollectableItem.Type getDefaultItemType() {
        return CollectableItem.Type.MOVIE;
    }
}
