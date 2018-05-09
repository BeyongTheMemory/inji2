/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.calendar.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.top.android.inji.calendar.info.CalendarDay;
import com.top.android.inji.glide.AppWidgetTarget;
import com.top.android.inji.glide.GlideApp;
import com.top.android.inji.link.UriHandlerActivity;

import com.top.android.inji.R;
import com.top.android.inji.calendar.info.CalendarDay;
import com.top.android.inji.glide.AppWidgetTarget;
import com.top.android.inji.glide.GlideApp;
import com.top.android.inji.link.UriHandlerActivity;

public class CalendarAppWidgetProvider extends AppWidgetProvider {

    private static final AppWidgetTarget sPosterTarget = new AppWidgetTarget(com.top.android.inji.R.id.poster);

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        CalendarDay calendarDay = CalendarDay.SAMPLE;

        RemoteViews views = new RemoteViews(context.getPackageName(), com.top.android.inji.R.layout.calendar_appwidget);
        views.setTextViewText(com.top.android.inji.R.id.date, calendarDay.getDateText(context));
        views.setTextViewText(com.top.android.inji.R.id.day_of_week, calendarDay.getDayOfWeekText(context));
        views.setTextViewText(com.top.android.inji.R.id.chinese_calendar_date, calendarDay.getChineseCalendarDateText());
        views.setTextViewText(com.top.android.inji.R.id.day_of_month, calendarDay.getDayOfMonthText(context));
        views.setTextColor(com.top.android.inji.R.id.day_of_month, calendarDay.getDayOfMonthColor(context));
        views.setTextViewText(com.top.android.inji.R.id.comment, calendarDay.comment);
        PendingIntent moviePendingIntent = PendingIntent.getActivity(context,
                calendarDay.url.hashCode(), UriHandlerActivity.makeIntent(calendarDay.url, context),
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(com.top.android.inji.R.id.movie, moviePendingIntent);
        views.setTextViewText(com.top.android.inji.R.id.title, calendarDay.getTitleText(context));
        views.setProgressBar(com.top.android.inji.R.id.rating, calendarDay.getRatingProgressBarMax(),
                calendarDay.getProgressRatingBarProgress(), false);
        views.setTextViewText(com.top.android.inji.R.id.rating_text, calendarDay.getRatingText(context));
        views.setTextViewText(com.top.android.inji.R.id.event, calendarDay.getEventText(context));
        GlideApp.with(context)
                .asBitmap()
                .load(calendarDay.poster)
                .into(sPosterTarget.prepare(views, context, appWidgetIds));

        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }
}
