/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.item.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.top.android.inji.gallery.ui.GalleryActivity;
import com.top.android.inji.item.content.BaseItemFragmentResource;
import com.top.android.inji.item.content.ConfirmUncollectItemDialogFragment;
import com.top.android.inji.item.content.MovieFragmentResource;
import com.top.android.inji.item.content.UncollectItemManager;
import com.top.android.inji.link.UriHandler;
import com.top.android.inji.network.api.info.frodo.CollectableItem;
import com.top.android.inji.network.api.info.frodo.Doulist;
import com.top.android.inji.network.api.info.frodo.ItemAwardItem;
import com.top.android.inji.network.api.info.frodo.Movie;
import com.top.android.inji.network.api.info.frodo.Photo;
import com.top.android.inji.network.api.info.frodo.Rating;
import com.top.android.inji.network.api.info.frodo.SimpleCelebrity;
import com.top.android.inji.network.api.info.frodo.SimpleItemCollection;
import com.top.android.inji.network.api.info.frodo.SimpleItemForumTopic;
import com.top.android.inji.network.api.info.frodo.SimpleMovie;
import com.top.android.inji.network.api.info.frodo.SimpleReview;
import com.top.android.inji.util.DoubanUtils;
import com.top.android.inji.util.ImageUtils;

import java.util.List;

import com.top.android.inji.gallery.ui.GalleryActivity;
import com.top.android.inji.item.content.BaseItemFragmentResource;
import com.top.android.inji.item.content.ConfirmUncollectItemDialogFragment;
import com.top.android.inji.item.content.MovieFragmentResource;
import com.top.android.inji.item.content.UncollectItemManager;
import com.top.android.inji.link.UriHandler;
import com.top.android.inji.network.api.info.frodo.CollectableItem;
import com.top.android.inji.network.api.info.frodo.Doulist;
import com.top.android.inji.network.api.info.frodo.ItemAwardItem;
import com.top.android.inji.network.api.info.frodo.Movie;
import com.top.android.inji.network.api.info.frodo.Photo;
import com.top.android.inji.network.api.info.frodo.Rating;
import com.top.android.inji.network.api.info.frodo.SimpleCelebrity;
import com.top.android.inji.network.api.info.frodo.SimpleItemCollection;
import com.top.android.inji.network.api.info.frodo.SimpleItemForumTopic;
import com.top.android.inji.network.api.info.frodo.SimpleMovie;
import com.top.android.inji.network.api.info.frodo.SimpleReview;
import com.top.android.inji.ui.BarrierAdapter;
import com.top.android.inji.util.DoubanUtils;
import com.top.android.inji.util.ImageUtils;

public class MovieFragment extends BaseItemFragment<SimpleMovie, Movie>
        implements MovieFragmentResource.Listener, MovieDataAdapter.Listener,
        ConfirmUncollectItemDialogFragment.Listener {

    private MovieAdapter mAdapter;

    private boolean mBackdropBound;
    private boolean mExcludeFirstPhoto;

    public static MovieFragment newInstance(long movieId, SimpleMovie simpleMovie, Movie movie) {
        //noinspection deprecation
        MovieFragment fragment = new MovieFragment();
        fragment.setArguments(movieId, simpleMovie, movie);
        return fragment;
    }

    /**
     * @deprecated Use {@link #newInstance(long, SimpleMovie, Movie)} instead.
     */
    public MovieFragment() {}

    @Override
    protected BaseItemFragmentResource<SimpleMovie, Movie> onAttachResource(long itemId,
                                                                            SimpleMovie simpleItem,
                                                                            Movie item) {
        return MovieFragmentResource.attachTo(itemId, simpleItem, item, this);
    }

    @Override
    protected BarrierAdapter onCreateAdapter() {
        mAdapter = new MovieAdapter(this);
        return mAdapter;
    }

    @Override
    public void onChanged(int requestCode, Movie newMovie, Rating newRating,
                          List<Photo> newPhotoList, List<SimpleCelebrity> newCelebrityList,
                          List<ItemAwardItem> newAwardList,
                          List<SimpleItemCollection> newItemCollectionList,
                          List<SimpleReview> newReviewList,
                          List<SimpleItemForumTopic> newForumTopicList,
                          List<CollectableItem> newRecommendationList,
                          List<Doulist> newRelatedDoulistList) {
        update(newMovie, newRating, newPhotoList, newCelebrityList, newAwardList,
                newItemCollectionList, newReviewList, newForumTopicList, newRecommendationList,
                newRelatedDoulistList);
    }

    private void update(Movie movie, Rating rating, List<Photo> photoList,
                        List<SimpleCelebrity> celebrityList, List<ItemAwardItem> awardList,
                        List<SimpleItemCollection> itemCollectionList,
                        List<SimpleReview> reviewList, List<SimpleItemForumTopic> forumTopicList,
                        List<CollectableItem> recommendationList,
                        List<Doulist> relatedDoulistList) {

        if (movie != null) {
            super.updateWithSimpleItem(movie);
        }

        if (movie == null || photoList == null) {
            return;
        }

        if (!mBackdropBound) {
            boolean hasTrailer = movie.trailer != null;
            mExcludeFirstPhoto = false;
            String backdropUrl = null;
            if (hasTrailer) {
                backdropUrl = movie.trailer.coverUrl;
                mBackdropLayout.setOnClickListener(view -> {
                    // TODO
                    UriHandler.open(movie.trailer.videoUrl, view.getContext());
                });
            } else if (!photoList.isEmpty()) {
                backdropUrl = photoList.get(0).getLargeUrl();
                mExcludeFirstPhoto = true;
                mBackdropLayout.setOnClickListener(view -> {
                    // TODO
                    Context context = view.getContext();
                    context.startActivity(GalleryActivity.makeImageListIntent(photoList, 0, context));
                });
            } else if (movie.poster != null) {
                backdropUrl = movie.poster.getLargeUrl();
                mBackdropLayout.setOnClickListener(view -> {
                    // TODO
                    Context context = view.getContext();
                    context.startActivity(GalleryActivity.makeIntent(movie.poster, context));
                });
            } else if (movie.cover != null) {
                backdropUrl = movie.cover.getLargeUrl();
                mBackdropLayout.setOnClickListener(view -> {
                    // TODO
                    Context context = view.getContext();
                    context.startActivity(GalleryActivity.makeIntent(movie.cover, context));
                });
            }
            if (backdropUrl != null) {
                ImageUtils.loadItemBackdropAndFadeIn(mBackdropImage, backdropUrl,
                        hasTrailer ? mBackdropPlayImage : null);
            }
            mBackdropBound = true;
        }

        mAdapter.setData(new MovieDataAdapter.Data(movie, rating, photoList, mExcludeFirstPhoto,
                celebrityList, awardList, itemCollectionList, reviewList, forumTopicList,
                recommendationList, relatedDoulistList));
        if (mAdapter.getItemCount() > 0) {
            mContentStateLayout.setLoaded(true);
        }
    }

    @Override
    public void onItemCollectionChanged(int requestCode) {
        mAdapter.notifyItemCollectionChanged();
    }

    @Override
    public void onUncollectItem(Movie movie) {
        ConfirmUncollectItemDialogFragment.show(this);
    }

    @Override
    public void uncollect() {
        if (!mResource.hasItem()) {
            return;
        }
        Movie movie = mResource.getItem();
        UncollectItemManager.getInstance().write(movie.getType(), movie.id, getActivity());
    }

    @Override
    protected String makeItemUrl(long itemId) {
        return DoubanUtils.makeMovieUrl(itemId);
    }
}
