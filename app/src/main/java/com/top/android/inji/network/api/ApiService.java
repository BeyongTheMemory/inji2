/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.network.api;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.top.android.inji.network.api.info.apiv2.User;
import com.top.android.inji.network.api.info.dto.UserDTO;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.network.api.info.frodo.BroadcastLikerList;
import com.top.android.inji.network.api.info.frodo.BroadcastList;
import com.top.android.inji.network.api.info.frodo.CelebrityList;
import com.top.android.inji.network.api.info.frodo.CollectableItem;
import com.top.android.inji.network.api.info.frodo.Comment;
import com.top.android.inji.network.api.info.frodo.CommentList;
import com.top.android.inji.network.api.info.frodo.CompleteCollectableItem;
import com.top.android.inji.network.api.info.frodo.DiaryList;
import com.top.android.inji.network.api.info.frodo.DoulistList;
import com.top.android.inji.network.api.info.frodo.DoumailThread;
import com.top.android.inji.network.api.info.frodo.DoumailThreadList;
import com.top.android.inji.network.api.info.frodo.ItemAwardList;
import com.top.android.inji.network.api.info.frodo.ItemCollection;
import com.top.android.inji.network.api.info.frodo.ItemCollectionList;
import com.top.android.inji.network.api.info.frodo.ItemCollectionState;
import com.top.android.inji.network.api.info.frodo.ItemForumTopicList;
import com.top.android.inji.network.api.info.frodo.NotificationCount;
import com.top.android.inji.network.api.info.frodo.NotificationList;
import com.top.android.inji.network.api.info.frodo.PhotoList;
import com.top.android.inji.network.api.info.frodo.Rating;
import com.top.android.inji.network.api.info.frodo.ReviewList;
import com.top.android.inji.network.api.info.frodo.TimelineList;
import com.top.android.inji.network.api.info.frodo.UploadedImage;
import com.top.android.inji.network.api.info.frodo.UserItemList;
import com.top.android.inji.network.api.info.frodo.UserList;
import com.top.android.inji.network.api.info.request.LoginRequest;
import com.top.android.inji.network.api.info.response.AuthenticationResponse;
import com.top.android.inji.util.EncryptUtil;
import com.top.android.inji.util.IpUtil;
import com.top.android.inji.util.UrlUtil;

import java.io.IOException;
import java.util.List;

import com.top.android.inji.BuildConfig;
import com.top.android.inji.account.info.AccountContract;
import com.top.android.inji.network.GsonResponseBodyConverterFactory;
import com.top.android.inji.network.api.info.request.LoginRequest;
import com.top.android.inji.network.api.info.response.AuthenticationResponse;
import com.top.android.inji.network.api.info.apiv2.User;
import com.top.android.inji.network.api.info.dto.UserDTO;
import com.top.android.inji.network.api.info.frodo.Broadcast;
import com.top.android.inji.network.api.info.frodo.BroadcastLikerList;
import com.top.android.inji.network.api.info.frodo.BroadcastList;
import com.top.android.inji.network.api.info.frodo.CelebrityList;
import com.top.android.inji.network.api.info.frodo.CollectableItem;
import com.top.android.inji.network.api.info.frodo.Comment;
import com.top.android.inji.network.api.info.frodo.CommentList;
import com.top.android.inji.network.api.info.frodo.CompleteCollectableItem;
import com.top.android.inji.network.api.info.frodo.DiaryList;
import com.top.android.inji.network.api.info.frodo.DoulistList;
import com.top.android.inji.network.api.info.frodo.DoumailThread;
import com.top.android.inji.network.api.info.frodo.DoumailThreadList;
import com.top.android.inji.network.api.info.frodo.ItemAwardList;
import com.top.android.inji.network.api.info.frodo.ItemCollection;
import com.top.android.inji.network.api.info.frodo.ItemCollectionList;
import com.top.android.inji.network.api.info.frodo.ItemCollectionState;
import com.top.android.inji.network.api.info.frodo.ItemForumTopicList;
import com.top.android.inji.network.api.info.frodo.NotificationCount;
import com.top.android.inji.network.api.info.frodo.NotificationList;
import com.top.android.inji.network.api.info.frodo.PhotoList;
import com.top.android.inji.network.api.info.frodo.Rating;
import com.top.android.inji.network.api.info.frodo.ReviewList;
import com.top.android.inji.network.api.info.frodo.TimelineList;
import com.top.android.inji.network.api.info.frodo.UploadedImage;
import com.top.android.inji.network.api.info.frodo.UserItemList;
import com.top.android.inji.network.api.info.frodo.UserList;
import com.top.android.inji.util.CollectionUtils;
import com.top.android.inji.util.EncryptUtil;
import com.top.android.inji.util.IpUtil;
import com.top.android.inji.util.StringCompat;
import com.top.android.inji.util.StringUtils;
import com.top.android.inji.util.UriUtils;
import com.top.android.inji.util.UrlUtil;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public class ApiService {

    private static final ApiService sInstance = new ApiService();


    private AuthenticationService mAuthenticationService;
    private OkHttpClient mLifeStreamHttpClient;
    private LifeStreamService mLifeStreamService;
    private OkHttpClient mFrodoHttpClient;
    private FrodoService mFrodoService;

    public static ApiService getInstance() {
        return sInstance;
    }

    private ApiService() {
        mAuthenticationService = createAuthenticationService();
        mLifeStreamHttpClient = createApiHttpClient(AccountContract.AUTH_TOKEN_TYPE_API_V2,
                new LifeStreamInterceptor());
        mLifeStreamService = createApiService(ApiContract.Request.ApiV2.API_HOST,
                mLifeStreamHttpClient, LifeStreamService.class);
        mFrodoHttpClient = createApiHttpClient(AccountContract.AUTH_TOKEN_TYPE_FRODO,
                new FrodoInterceptor(), new FrodoSignatureInterceptor());
        mFrodoService = createApiService(ApiContract.Request.Frodo.API_HOST, mFrodoHttpClient,
                FrodoService.class);
    }

    private static AuthenticationService createAuthenticationService() {
        return new Retrofit.Builder()
                .addCallAdapterFactory(ApiCallAdapter.Factory.create())
                .addConverterFactory(GsonConverterFactory.create())
                // Make Retrofit happy.
                .baseUrl(UrlUtil.BASE_URL)
                .client(new OkHttpClient.Builder()
                        .addNetworkInterceptor(new StethoInterceptor())
                        .addInterceptor(chain -> {
                            Request request = chain.request();
                            Request.Builder requestBuilder = request.newBuilder();
                            request = requestBuilder
                                    .addHeader("Content-Type", "application/json;charset=UTF-8")
                                    .build();
                            return chain.proceed(request);
                        })
                        .build())
                .build()
                .create(AuthenticationService.class);
    }

    private static OkHttpClient createApiHttpClient(String authTokenType,
                                                    Interceptor... interceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                // AuthenticationInterceptor may retry the request, so it must be an application
                // interceptor.
                .addInterceptor(new ApiAuthenticationInterceptor(authTokenType));
        for (Interceptor interceptor : interceptors) {
            builder.addNetworkInterceptor(interceptor);
        }
        return builder
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
    }

    private static <T> T createApiService(String baseUrl, OkHttpClient client,
                                         Class<T> clientClass) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(ApiCallAdapter.Factory.create())
                .addConverterFactory(GsonResponseBodyConverterFactory.create())
                .baseUrl(baseUrl)
                .client(client)
                .build()
                .create(clientClass);
    }

    public ApiRequest<AuthenticationResponse> authenticate(Context context, String username,
                                                           String password) {
        //todo:获取clientID
        return mAuthenticationService.authenticate(new LoginRequest(username, EncryptUtil.MD5(password), IpUtil.getIp(context),"", BuildConfig.VERSION_NAME,1));
    }



    public ApiRequest<NotificationCount> getNotificationCount() {
        return mFrodoService.getNotificationCount();
    }

    public ApiRequest<NotificationList> getNotificationList(Integer start, Integer count) {
        return mFrodoService.getNotificationList(start, count);
    }

    public ApiRequest<UserDTO> getUser(String userIdOrUid) {
        if (TextUtils.isEmpty(userIdOrUid)) {
            userIdOrUid = "~me";
        }
        return mLifeStreamService.getUser(userIdOrUid);
    }

    public ApiRequest<User> follow(String userIdOrUid, boolean follow) {
        if (follow) {
            return mLifeStreamService.follow(userIdOrUid);
        } else {
            return mLifeStreamService.unfollow(userIdOrUid);
        }
    }

    public ApiRequest<UserList> getFollowingList(String userIdOrUid, Integer start, Integer count,
                                                 boolean followersFirst) {
        return mFrodoService.getFollowingList(userIdOrUid, start, count, followersFirst ? "true"
                : null);
    }

    public ApiRequest<UserList> getFollowingList(String userIdOrUid, Integer start, Integer count) {
        return getFollowingList(userIdOrUid, start, count, false);
    }

    public ApiRequest<UserList> getFollowerList(String userIdOrUid, Integer start, Integer count) {
        return mFrodoService.getFollowerList(userIdOrUid, start, count);
    }


    public ApiRequest<TimelineList> getTimelineList(String userIdOrUid, String topic, Long untilId,
                                                    Integer count) {
        return getTimelineList(userIdOrUid, topic, untilId, count, null, false);
    }

    public ApiRequest<TimelineList> getTimelineList(String userIdOrUid, String topic, Long untilId,
                                                    Integer count, Long lastVisitedId,
                                                    boolean guestOnly) {
        String url;
        if (TextUtils.isEmpty(userIdOrUid) && TextUtils.isEmpty(topic)) {
            url = "status/home_timeline";
        } else if (TextUtils.isEmpty(topic)) {
            url = StringUtils.formatUs("status/user_timeline/%s", userIdOrUid);
        } else {
            url = "status/topic/timeline";
        }
        return mFrodoService.getTimelineList(url, untilId, count, lastVisitedId, topic, guestOnly ?
                1 : null);
    }

    public ApiRequest<UploadedImage> uploadBroadcastImage(Uri uri, Context context) {
        String fileName = UriUtils.getDisplayName(uri, context);
        RequestBody body = new ImageTypeUriRequestBody(uri, context);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", fileName, body);
        return mFrodoService.uploadBroadcastImage(part);
    }

    public ApiRequest<Broadcast> sendBroadcast(String text, List<String> imageUrls,
                                               String linkTitle, String linkUrl) {
        boolean isImagesEmpty = CollectionUtils.isEmpty(imageUrls);
        if (isImagesEmpty && !TextUtils.isEmpty(linkUrl)) {
            return new ConvertBroadcastApiRequest(mFrodoService.sendBroadcastWithLifeStream(text,
                    null, linkTitle, linkUrl));
        }
        String imageUrlsString = !isImagesEmpty ? StringCompat.join(",", imageUrls) : null;
        return mFrodoService.sendBroadcast(text, imageUrlsString, linkTitle, linkUrl);
    }

    public ApiRequest<Broadcast> getBroadcast(long broadcastId) {
        return mFrodoService.getBroadcast(broadcastId);
    }

    public ApiRequest<CommentList> getBroadcastCommentList(long broadcastId, Integer start,
                                                           Integer count) {
        return mFrodoService.getBroadcastCommentList(broadcastId, start, count);
    }

    public ApiRequest<Broadcast> likeBroadcast(long broadcastId, boolean like) {
        if (like) {
            return mFrodoService.likeBroadcast(broadcastId);
        } else {
            return mFrodoService.unlikeBroadcast(broadcastId);
        }
    }

    public ApiRequest<Broadcast> rebroadcastBroadcast(long broadcastId, String text) {
        return mFrodoService.rebroadcastBroadcast(broadcastId, text);
    }

    public ApiRequest<BroadcastLikerList> getBroadcastLikerList(long broadcastId, Integer start,
                                                                Integer count) {
        return mFrodoService.getBroadcastLikerList(broadcastId, start, count);
    }

    public ApiRequest<BroadcastList> getBroadcastRebroadcastedBroadcastList(long broadcastId,
                                                                            Integer start,
                                                                            Integer count) {
        return mFrodoService.getBroadcastRebroadcastedBroadcastList(broadcastId, start, count);
    }

    public ApiRequest<Void> deleteBroadcastComment(long broadcastId, long commentId) {
        return mFrodoService.deleteBroadcastComment(broadcastId, commentId);
    }

    public ApiRequest<Comment> sendBroadcastComment(long broadcastId, String comment) {
        return mFrodoService.sendBroadcastComment(broadcastId, comment);
    }

    public ApiRequest<Void> deleteBroadcast(long broadcastId) {
        return mFrodoService.deleteBroadcast(broadcastId);
    }

    public ApiRequest<DiaryList> getDiaryList(String userIdOrUid, Integer start, Integer count) {
        // TODO: UserIdOrUidFrodoRequest
        return mFrodoService.getDiaryList(userIdOrUid, start, count);
    }

    public ApiRequest<UserItemList> getUserItemList(String userIdOrUid) {
        // TODO: UserIdOrUidFrodoRequest
        return mFrodoService.getUserItemList(userIdOrUid);
    }

    public ApiRequest<ReviewList> getUserReviewList(String userIdOrUid, Integer start,
                                                    Integer count) {
        // TODO: UserIdOrUidFrodoRequest
        return mFrodoService.getUserReviewList(userIdOrUid, start, count);
    }

    public <ItemType> ApiRequest<ItemType> getItem(CollectableItem.Type itemType, long itemId) {
        //noinspection unchecked
        return (ApiRequest<ItemType>) mFrodoService.getItem(itemType.getApiString(), itemId);
    }

    public ApiRequest<ItemCollection> collectItem(CollectableItem.Type itemType, long itemId,
                                                  ItemCollectionState state, int rating,
                                                  List<String> tags, String comment,
                                                  List<Long> gamePlatformIds,
                                                  boolean shareToBroadcast, boolean shareToWeibo,
                                                  boolean shareToWeChatMoments) {
        return mFrodoService.collectItem(itemType.getApiString(), itemId, state.getApiString(),
                rating, StringCompat.join(",", tags), comment, gamePlatformIds,
                shareToBroadcast ? 1 : null, shareToWeibo ? 1 : null,
                shareToWeChatMoments ? 1 : null);
    }

    public ApiRequest<ItemCollection> uncollectItem(CollectableItem.Type itemType, long itemId) {
        return mFrodoService.uncollectItem(itemType.getApiString(), itemId);
    }

    public ApiRequest<Rating> getItemRating(CollectableItem.Type itemType, long itemId) {
        return mFrodoService.getItemRating(itemType.getApiString(), itemId);
    }

    public ApiRequest<PhotoList> getItemPhotoList(CollectableItem.Type itemType, long itemId,
                                                  Integer start, Integer count) {
        return mFrodoService.getItemPhotoList(itemType.getApiString(), itemId, start, count);
    }

    public ApiRequest<CelebrityList> getItemCelebrityList(CollectableItem.Type itemType,
                                                          long itemId) {
        return mFrodoService.getItemCelebrityList(itemType.getApiString(), itemId);
    }

    public ApiRequest<ItemAwardList> getItemAwardList(CollectableItem.Type itemType, long itemId,
                                                      Integer start, Integer count) {
        return mFrodoService.getItemAwardList(itemType.getApiString(), itemId, start, count);
    }

    public ApiRequest<ItemCollectionList> getItemCollectionList(CollectableItem.Type itemType,
                                                                long itemId,
                                                                boolean followingsFirst,
                                                                Integer start, Integer count) {
        return mFrodoService.getItemCollectionList(itemType.getApiString(), itemId,
                followingsFirst ? 1 : null, start, count);
    }

    public ApiRequest<ReviewList> getItemReviewList(CollectableItem.Type itemType, long itemId,
                                                    Integer start, Integer count) {
        return mFrodoService.getItemReviewList(itemType.getApiString(), itemId, start, count);
    }

    public ApiRequest<ItemForumTopicList> getItemForumTopicList(CollectableItem.Type itemType,
                                                                long itemId, Integer episode,
                                                                Integer start, Integer count) {
        return mFrodoService.getItemForumTopicList(itemType.getApiString(), itemId, episode, start,
                count);
    }

    public ApiRequest<List<CollectableItem>> getItemRecommendationList(
            CollectableItem.Type itemType, long itemId, Integer count) {
        return mFrodoService.getItemRecommendationList(itemType.getApiString(), itemId, count);
    }

    public ApiRequest<DoulistList> getItemRelatedDoulistList(CollectableItem.Type itemType,
                                                             long itemId, Integer start,
                                                             Integer count) {
        return mFrodoService.getItemRelatedDoulistList(itemType.getApiString(), itemId, start,
                count);
    }

    public ApiRequest<DoumailThreadList> getDoumailThreadList(Integer start, Integer count) {
        return mFrodoService.getDoumailThreadList(start, count);
    }

    public ApiRequest<DoumailThread> getDoumailThread(long userId) {
        return mFrodoService.getDoumailThread(userId);
    }

    public void cancelApiRequests() {
        mLifeStreamHttpClient.dispatcher().cancelAll();
        mFrodoHttpClient.dispatcher().cancelAll();
    }

    public interface AuthenticationService {

        @POST(ApiContract.Request.Authentication.URL)
        ApiRequest<AuthenticationResponse> authenticate(@Body LoginRequest loginRequest);

    }

    public interface LifeStreamService {

        @GET("user/getUserByAccount")
        ApiRequest<UserDTO> getUser(@Path("account") String account);

        @POST("lifestream/user/{userIdOrUid}/follow")
        ApiRequest<User> follow(@Path("userIdOrUid") String userIdOrUid);

        @DELETE("lifestream/user/{userIdOrUid}/follow")
        ApiRequest<User> unfollow(@Path("userIdOrUid") String userIdOrUid);

    }

    public interface FrodoService {

        @GET("notification_chart")
        ApiRequest<NotificationCount> getNotificationCount();

        @GET("mine/notifications")
        ApiRequest<NotificationList> getNotificationList(@Query("start") Integer start,
                                                         @Query("count") Integer count);

        @GET("user/{userIdOrUid}/following")
        ApiRequest<UserList> getFollowingList(@Path("userIdOrUid") String userIdOrUid,
                                              @Query("start") Integer start,
                                              @Query("count") Integer count,
                                              @Query("contact_prior") String followersFirst);

        @GET("user/{userIdOrUid}/followers")
        ApiRequest<UserList> getFollowerList(@Path("userIdOrUid") String userIdOrUid,
                                             @Query("start") Integer start,
                                             @Query("count") Integer count);

        @GET
        ApiRequest<TimelineList> getTimelineList(@Url String url,
                                                 @Query("max_id") Long untilId,
                                                 @Query("count") Integer count,
                                                 @Query("last_visit_id") Long lastVisitedId,
                                                 @Query("name") String topic,
                                                 @Query("guest_only") Integer guestOnly);

        @POST("status/upload")
        @Multipart
        ApiRequest<UploadedImage> uploadBroadcastImage(@Part MultipartBody.Part part);

        @POST("status/create_status")
        @FormUrlEncoded
        ApiRequest<Broadcast> sendBroadcast(@Field("text") String text,
                                            @Field("image_urls") String imageUrls,
                                            @Field("rec_title") String linkTitle,
                                            @Field("rec_url") String linkUrl);

        @POST("https://api.douban.com/v2/lifestream/statuses")
        @FormUrlEncoded
        ApiRequest<com.top.android.inji.network.api.info.apiv2.Broadcast>
        sendBroadcastWithLifeStream(@Field("text") String text,
                                    @Field("image_urls") String imageUrls,
                                    @Field("rec_title") String linkTitle,
                                    @Field("rec_url") String linkUrl);

        @GET("status/{broadcastId}")
        ApiRequest<Broadcast> getBroadcast(@Path("broadcastId") long broadcastId);

        @GET("status/{broadcastId}/likers")
        ApiRequest<BroadcastLikerList> getBroadcastLikerList(@Path("broadcastId") long broadcastId,
                                                             @Query("start") Integer start,
                                                             @Query("count") Integer count);

        @GET("status/{broadcastId}/resharers_statuses")
        ApiRequest<BroadcastList> getBroadcastRebroadcastedBroadcastList(
                @Path("broadcastId") long broadcastId, @Query("start") Integer start,
                @Query("count") Integer count);

        @POST("status/{broadcastId}/like")
        ApiRequest<Broadcast> likeBroadcast(@Path("broadcastId") long broadcastId);

        @POST("status/{broadcastId}/unlike")
        ApiRequest<Broadcast> unlikeBroadcast(@Path("broadcastId") long broadcastId);

        @POST("status/{broadcastId}/reshare")
        @FormUrlEncoded
        ApiRequest<Broadcast> rebroadcastBroadcast(@Path("broadcastId") long broadcastId,
                                                   @Field("text") String text);

        @POST("status/{broadcastId}/report")
        @FormUrlEncoded
        ApiRequest<Void> reportBroadcast(@Path("broadcastId") long broadcastId,
                                         @Field("reason") int reason);

        @POST("status/{broadcastId}/delete")
        ApiRequest<Void> deleteBroadcast(@Path("broadcastId") long broadcastId);

        @GET("status/{broadcastId}/comments")
        ApiRequest<CommentList> getBroadcastCommentList(@Path("broadcastId") long broadcastId,
                                                        @Query("start") Integer start,
                                                        @Query("count") Integer count);

        @POST("status/{broadcastId}/create_comment")
        @FormUrlEncoded
        ApiRequest<Comment> sendBroadcastComment(@Path("broadcastId") long broadcastId,
                                                 @Field("text") String text);


        @POST("status/{broadcastId}/delete_comment")
        @FormUrlEncoded
        ApiRequest<Void> deleteBroadcastComment(@Path("broadcastId") long broadcastId,
                                                @Field("comment_id") long commentId);

        @GET("user/{userIdOrUid}/notes")
        ApiRequest<DiaryList> getDiaryList(@Path("userIdOrUid") String userIdOrUid,
                                           @Query("start") Integer start,
                                           @Query("count") Integer count);

        @GET("user/{userIdOrUid}/itemlist")
        ApiRequest<UserItemList> getUserItemList(@Path("userIdOrUid") String userIdOrUid);

        @GET("user/{userIdOrUid}/reviews")
        ApiRequest<ReviewList> getUserReviewList(@Path("userIdOrUid") String userIdOrUid,
                                                 @Query("start") Integer start,
                                                 @Query("count") Integer count);

        @GET("{itemType}/{itemId}")
        ApiRequest<CompleteCollectableItem> getItem(@Path("itemType") String itemType,
                                                    @Path("itemId") long itemId);

        @POST("{itemType}/{itemId}/{state}")
        @FormUrlEncoded
        ApiRequest<ItemCollection> collectItem(@Path("itemType") String itemType,
                                               @Path("itemId") long itemId,
                                               @Path("state") String state,
                                               @Field("rating") int rating,
                                               @Field("tags") String tags,
                                               @Field("comment") String comment,
                                               @Field("platform") List<Long> gamePlatformIds,
                                               @Field("sync_douban") Integer shareToBroadcast,
                                               @Field("sync_weibo") Integer shareToWeibo,
                                               @Field("sync_wechat_timeline") Integer shareToWeChatMoments);

        @POST("{itemType}/{itemId}/unmark")
        ApiRequest<ItemCollection> uncollectItem(@Path("itemType") String itemType,
                                                 @Path("itemId") long itemId);

        @GET("{itemType}/{itemId}/rating")
        ApiRequest<Rating> getItemRating(@Path("itemType") String itemType,
                                         @Path("itemId") long itemId);

        @GET("{itemType}/{itemId}/photos")
        ApiRequest<PhotoList> getItemPhotoList(@Path("itemType") String itemType,
                                               @Path("itemId") long itemId,
                                               @Query("start") Integer start,
                                               @Query("count") Integer count);

        @GET("{itemType}/{itemId}/celebrities")
        ApiRequest<CelebrityList> getItemCelebrityList(@Path("itemType") String itemType,
                                                       @Path("itemId") long itemId);

        @GET("{itemType}/{itemId}/awards")
        ApiRequest<ItemAwardList> getItemAwardList(@Path("itemType") String itemType,
                                                   @Path("itemId") long itemId,
                                                   @Query("start") Integer start,
                                                   @Query("count") Integer count);

        @GET("{itemType}/{itemId}/interests")
        ApiRequest<ItemCollectionList> getItemCollectionList(
                @Path("itemType") String itemType, @Path("itemId") long itemId,
                @Query("following") Integer followingsFirst, @Query("start") Integer start,
                @Query("count") Integer count);

        @GET("{itemType}/{itemId}/forum_topics")
        ApiRequest<ItemForumTopicList> getItemForumTopicList(@Path("itemType") String itemType,
                                                             @Path("itemId") long itemId,
                                                             @Query("episode") Integer episode,
                                                             @Query("start") Integer start,
                                                             @Query("count") Integer count);

        @GET("{itemType}/{itemId}/reviews")
        ApiRequest<ReviewList> getItemReviewList(@Path("itemType") String itemType,
                                                 @Path("itemId") long itemId,
                                                 @Query("start") Integer start,
                                                 @Query("count") Integer count);

        @GET("{itemType}/{itemId}/recommendations")
        ApiRequest<List<CollectableItem>> getItemRecommendationList(
                @Path("itemType") String itemType, @Path("itemId") long itemId,
                @Query("count") Integer count);

        @GET("{itemType}/{itemId}/related_doulists")
        ApiRequest<DoulistList> getItemRelatedDoulistList(@Path("itemType") String itemType,
                                                          @Path("itemId") long itemId,
                                                          @Query("start") Integer start,
                                                          @Query("count") Integer count);

        @GET("chat_list")
        ApiRequest<DoumailThreadList> getDoumailThreadList(@Query("start") Integer start,
                                                           @Query("count") Integer count);

        @GET("user/{userId}/chat")
        ApiRequest<DoumailThread> getDoumailThread(@Path("userId") long userId);
    }

    private static class ConvertBroadcastApiRequest extends ConvertApiRequest<
            com.top.android.inji.network.api.info.apiv2.Broadcast, Broadcast> {

        public ConvertBroadcastApiRequest(
                ApiRequest<com.top.android.inji.network.api.info.apiv2.Broadcast> request) {
            super(request);
        }

        @Override
        protected Broadcast transform(
                com.top.android.inji.network.api.info.apiv2.Broadcast responseBody) {
            return responseBody.toFrodo();
        }
    }
}
