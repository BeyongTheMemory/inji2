/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.photo.content;

import com.top.android.inji.content.MoreBaseListResourceFragment;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.info.frodo.Photo;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import com.top.android.inji.content.MoreBaseListResourceFragment;
import com.top.android.inji.eventbus.PhotoDeletedEvent;
import com.top.android.inji.network.api.ApiError;
import com.top.android.inji.network.api.info.frodo.Photo;
import com.top.android.inji.network.api.info.frodo.PhotoList;

public abstract class BasePhotoListResource extends MoreBaseListResourceFragment<PhotoList, Photo> {

    @Override
    protected void onLoadStarted() {
        getListener().onLoadPhotoListStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean more, int count, boolean successful, List<Photo> response,
                                  ApiError error) {
        if (successful) {
            if (more) {
                append(response);
                getListener().onLoadPhotoListFinished(getRequestCode());
                getListener().onPhotoListAppended(getRequestCode(),
                        Collections.unmodifiableList(response));
            } else {
                set(response);
                getListener().onLoadPhotoListFinished(getRequestCode());
                getListener().onPhotoListChanged(getRequestCode(),
                        Collections.unmodifiableList(get()));
            }
        } else {
            getListener().onLoadPhotoListFinished(getRequestCode());
            getListener().onLoadPhotoListError(getRequestCode(), error);
        }
    }

    protected void appendAndNotifyListener(List<Photo> photoList) {
        append(photoList);
        getListener().onPhotoListAppended(getRequestCode(), photoList);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onPhotoDeleted(PhotoDeletedEvent event) {

        if (event.isFromMyself(this) || isEmpty()) {
            return;
        }

        List<Photo> photoList = get();
        for (int i = 0, size = photoList.size(); i < size; ) {
            Photo photo = photoList.get(i);
            if (photo.id == event.photoId) {
                photoList.remove(i);
                getListener().onPhotoRemoved(getRequestCode(), i);
                --size;
            } else {
                ++i;
            }
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadPhotoListStarted(int requestCode);
        void onLoadPhotoListFinished(int requestCode);
        void onLoadPhotoListError(int requestCode, ApiError error);
        /**
         * @param newPhotoList Unmodifiable.
         */
        void onPhotoListChanged(int requestCode, List<Photo> newPhotoList);
        /**
         * @param appendedPhotoList Unmodifiable.
         */
        void onPhotoListAppended(int requestCode, List<Photo> appendedPhotoList);
        void onPhotoRemoved(int requestCode, int position);
    }
}
