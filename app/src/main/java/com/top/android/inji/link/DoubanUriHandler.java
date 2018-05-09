/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.link;

import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;
import android.text.TextUtils;

import com.top.android.inji.util.UriUtils;

import java.util.List;
import java.util.regex.Pattern;

import com.top.android.inji.BuildConfig;
import com.top.android.inji.broadcast.ui.BroadcastActivity;
import com.top.android.inji.broadcast.ui.BroadcastListActivity;
import com.top.android.inji.followship.ui.FollowerListActivity;
import com.top.android.inji.followship.ui.FollowingListActivity;
import com.top.android.inji.item.ui.MovieActivity;
import com.top.android.inji.profile.ui.ProfileActivity;
import com.top.android.inji.util.UriUtils;

public class DoubanUriHandler {

    private static final Pattern DISPATCH_URI_PATTERN = Pattern.compile(
            "https://www.douban.com/doubanapp/dispatch\\?uri=.*");
    private static final String DISPATCH_URI_QUERY_PARAMETER_URI = "uri";
    private static final String DISPATCH_URI_QUERY_PARAMETER_URI_PREFIX = "douban://douban.com";

    private static final String AUTHORITY = "www.douban.com";
    private static final String AUTHORITY_MOVIE = "movie.douban.com";
    private static final String AUTHORITY_FRODO = "douban.com";

    private enum UriType {

        // NOTE: * must come before # if they are at the same position, or UriMatcher tree won't
        // backtrack.

        TOPIC_BROADCAST_LIST("update/topic/*"),
        // Handled below.
        //TOPIC_BROADCAST_LIST_FRODO(AUTHORITY_FRODO, "status/topic?name=*"),
        BROADCAST("people/*/status/#"),
        BROADCAST_FRODO(AUTHORITY_FRODO, "status/#"),
        BROADCAST_CHILD_FRODO(AUTHORITY_FRODO,"status/#/*"),
        // Reordered for correct behavior
        TOPIC_BROADCAST_LIST_FRODO(AUTHORITY_FRODO, "status/*"),
        USER("people/*"),
        USER_FRODO(AUTHORITY_FRODO, "user/*"),
        USER_FOLLOWER_LIST("people/*/rev_contacts"),
        USER_FOLLOWER_LIST_FRODO(AUTHORITY_FRODO, "user/*/follower"),
        USER_FOLLOWING_LIST("people/*/contacts"),
        USER_FOLLOWING_LIST_FRODO(AUTHORITY_FRODO, "user/*/following"),
        // Reordered for correct behavior
        // Not handling "people/*/statuses" because Frodo API cannot handle it.
        USER_BROADCAST_LIST("people/#/statuses"),
        MOVIE(AUTHORITY_MOVIE, "subject/#"),
        MOVIE_FRODO(AUTHORITY_FRODO, "movie/#");

        String mAuthority;
        String mPath;

        UriType(String authority, String path) {
            mAuthority = authority;
            mPath = path;
        }

        UriType(String path) {
            this(AUTHORITY, path);
        }

        public String getAuthority() {
            return mAuthority;
        }

        public String getPath() {
            return mPath;
        }
    }

    private static final UriMatcher MATCHER;
    static {
        MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        for (UriType uriType : UriType.values()) {
            MATCHER.addURI(uriType.getAuthority(), uriType.getPath(), uriType.ordinal());
        }
    }

    private DoubanUriHandler() {}

    public static boolean open(Uri uri, Context context) {

        if (DISPATCH_URI_PATTERN.matcher(uri.toString()).matches()) {
            uri = Uri.parse(DISPATCH_URI_QUERY_PARAMETER_URI_PREFIX + uri.getQueryParameter(
                    DISPATCH_URI_QUERY_PARAMETER_URI));
        }

        int code = MATCHER.match(uri);
        if (code == UriMatcher.NO_MATCH) {
            return false;
        }
        UriType uriType = UriType.values()[code];

        Intent intent;
        switch (uriType) {
            case USER_BROADCAST_LIST:
                intent = BroadcastListActivity.makeIntent(Long.parseLong(uri.getPathSegments().get(1)), context);
                break;
            case TOPIC_BROADCAST_LIST:
                intent = BroadcastListActivity.makeTopicIntent(uri.getLastPathSegment(), context);
                break;
            case TOPIC_BROADCAST_LIST_FRODO: {
                if (!TextUtils.equals(uri.getLastPathSegment(), "topic")) {
                    return false;
                }
                intent = BroadcastListActivity.makeTopicIntent(uri.getQueryParameter("name"),
                        context);
                break;
            }
            case BROADCAST:
            case BROADCAST_FRODO:
                intent = BroadcastActivity.makeIntent(UriUtils.parseId(uri), context);
                break;
            case BROADCAST_CHILD_FRODO: {
                long broadcastId = Long.parseLong(uri.getPathSegments().get(1));
                intent = BroadcastActivity.makeIntent(broadcastId, context);
                break;
            }
            case USER:
            case USER_FRODO:
                intent = ProfileActivity.makeIntent(Long.parseLong(uri.getLastPathSegment()), context);
                break;
            case USER_FOLLOWER_LIST:
            case USER_FOLLOWER_LIST_FRODO:
                intent = FollowerListActivity.makeIntent(uri.getPathSegments().get(1), context);
                break;
            case USER_FOLLOWING_LIST:
            case USER_FOLLOWING_LIST_FRODO:
                //todo:关注用户
                return false;
              //  intent = FollowingListActivity.makeIntent(uri.getPathSegments().get(1), context);
              //  break;
            case MOVIE:
            case MOVIE_FRODO:
                intent = MovieActivity.makeIntent(UriUtils.parseId(uri), context);
                break;
            default:
                return false;
        }

        context.startActivity(intent);
        return true;
    }

    public static boolean open(String uri, Context context) {
        return open(Uri.parse(uri), context);
    }
}
