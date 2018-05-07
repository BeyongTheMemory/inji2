/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.followship.ui;

import android.content.Context;
import android.content.Intent;

public class FollowingListActivity extends FollowshipListActivity {

    public static Intent makeIntent(Long userId, Context context) {
        return new Intent(context, FollowingListActivity.class)
                .putExtra(EXTRA_user_id, userId);
    }

    @Override
    protected FollowshipListActivityFragment onCreateActivityFragment(String userIdOrUid) {
        return FollowingListActivityFragment.newInstance(userIdOrUid);
    }
}
