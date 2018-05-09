/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.content;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.top.android.inji.broadcast.content.DeleteBroadcastCommentManager;
import com.top.android.inji.broadcast.content.DeleteBroadcastManager;
import com.top.android.inji.broadcast.content.LikeBroadcastManager;
import com.top.android.inji.broadcast.content.RebroadcastBroadcastManager;
import com.top.android.inji.broadcast.content.SendBroadcastCommentManager;
import com.top.android.inji.broadcast.content.SendBroadcastManager;
import com.top.android.inji.followship.content.FollowUserManager;
import com.top.android.inji.item.content.CollectItemManager;
import com.top.android.inji.item.content.UncollectItemManager;

public class ResourceWriterService extends Service {

    private List<ResourceWriterManager> mWriterManagers = new ArrayList<>();

    public static Intent makeIntent(Context context) {
        return new Intent(context, ResourceWriterService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        addWriterManager(FollowUserManager.getInstance());
        addWriterManager(SendBroadcastManager.getInstance());
        addWriterManager(LikeBroadcastManager.getInstance());
        addWriterManager(RebroadcastBroadcastManager.getInstance());
        addWriterManager(DeleteBroadcastManager.getInstance());
        addWriterManager(DeleteBroadcastCommentManager.getInstance());
        addWriterManager(SendBroadcastCommentManager.getInstance());
        addWriterManager(CollectItemManager.getInstance());
        addWriterManager(UncollectItemManager.getInstance());
    }

    private void addWriterManager(ResourceWriterManager writerManager) {
        mWriterManagers.add(writerManager);
        writerManager.onBind(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        removeWriterManagers();
    }

    private void removeWriterManagers() {
        Iterator<ResourceWriterManager> iterator = mWriterManagers.iterator();
        while (iterator.hasNext()) {
            ResourceWriterManager writerManager = iterator.next();
            writerManager.onUnbind();
            iterator.remove();
        }
    }
}
