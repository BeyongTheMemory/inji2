/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.calendar.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.top.android.inji.calendar.info.CalendarDay;
import com.top.android.inji.link.UriHandler;
import com.top.android.inji.util.ImageUtils;
import com.top.android.inji.util.TintHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.top.android.inji.R;
import com.top.android.inji.calendar.info.CalendarDay;
import com.top.android.inji.link.UriHandler;
import com.top.android.inji.util.ImageUtils;
import com.top.android.inji.util.TintHelper;

public class CalendarFragment extends Fragment {

    @BindView(com.top.android.inji.R.id.toolbar)
    Toolbar mToolbar;
    @BindView(com.top.android.inji.R.id.date)
    TextView mDateText;
    @BindView(com.top.android.inji.R.id.day_of_week)
    TextView mDayOfWeekText;
    @BindView(com.top.android.inji.R.id.chinese_calendar_date)
    TextView mChineseCalendarDateText;
    @BindView(com.top.android.inji.R.id.day_of_month)
    TextView mDayOfMonthText;
    @BindView(com.top.android.inji.R.id.comment)
    TextView mCommentText;
    @BindView(com.top.android.inji.R.id.movie)
    ViewGroup mMovieLayout;
    @BindView(com.top.android.inji.R.id.title)
    TextView mTitleText;
    @BindView(com.top.android.inji.R.id.rating)
    RatingBar mRatingBar;
    @BindView(com.top.android.inji.R.id.rating_text)
    TextView mRatingText;
    @BindView(com.top.android.inji.R.id.event)
    TextView mEventText;
    @BindView(com.top.android.inji.R.id.poster)
    ImageView mPosterImage;

    public static CalendarFragment newInstance() {
        //noinspection deprecation
        return new CalendarFragment();
    }

    /**
     * @deprecated Use {@link #newInstance()} instead.
     */
    public CalendarFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(com.top.android.inji.R.layout.calendar_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        TintHelper.onSetSupportActionBar(mToolbar);

        // TODO
        CalendarDay calendarDay = CalendarDay.SAMPLE;

        mDateText.setText(calendarDay.getDateText(activity));
        mDayOfWeekText.setText(calendarDay.getDayOfWeekText(activity));
        mChineseCalendarDateText.setText(calendarDay.getChineseCalendarDateText());
        mDayOfMonthText.setText(calendarDay.getDayOfMonthText(activity));
        mDayOfMonthText.setTextColor(calendarDay.getThemedDayOfMonthColor(
                mDayOfMonthText.getContext()));
        mCommentText.setText(calendarDay.comment);
        mMovieLayout.setOnClickListener(view -> UriHandler.open(calendarDay.url,
                view.getContext()));
        mTitleText.setText(calendarDay.getTitleText(activity));
        mRatingBar.setRating(calendarDay.getRatingBarRating());
        mRatingText.setText(calendarDay.getRatingText(activity));
        mEventText.setText(calendarDay.getEventText(activity));
        ImageUtils.loadImage(mPosterImage, calendarDay.poster);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
