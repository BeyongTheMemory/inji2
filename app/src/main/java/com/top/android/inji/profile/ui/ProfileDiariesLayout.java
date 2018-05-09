/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.profile.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.top.android.inji.link.UriHandler;
import com.top.android.inji.network.api.info.apiv2.User;
import com.top.android.inji.network.api.info.frodo.Diary;
import com.top.android.inji.ui.FriendlyCardView;
import com.top.android.inji.util.ImageUtils;
import com.top.android.inji.util.StringUtils;
import com.top.android.inji.util.ViewUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.top.android.inji.R;
import com.top.android.inji.link.UriHandler;
import com.top.android.inji.network.api.info.apiv2.User;
import com.top.android.inji.network.api.info.frodo.Diary;
import com.top.android.inji.ui.FriendlyCardView;
import com.top.android.inji.util.ImageUtils;
import com.top.android.inji.util.StringUtils;
import com.top.android.inji.util.ViewUtils;

public class ProfileDiariesLayout extends FriendlyCardView {

    private static final int DIARY_COUNT_MAX = 3;

    @BindView(com.top.android.inji.R.id.title)
    TextView mTitleText;
    @BindView(com.top.android.inji.R.id.diary_list)
    LinearLayout mDiaryList;
    @BindView(com.top.android.inji.R.id.empty)
    View mEmptyView;
    @BindView(com.top.android.inji.R.id.view_more)
    TextView mViewMoreText;

    public ProfileDiariesLayout(Context context) {
        super(context);

        init();
    }

    public ProfileDiariesLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ProfileDiariesLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        ViewUtils.inflateInto(com.top.android.inji.R.layout.profile_diaries_layout, this);
        ButterKnife.bind(this);
    }

    public void bind(final User user, List<Diary> diaryList) {

        final Context context = getContext();
        OnClickListener viewMoreListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
                UriHandler.open(StringUtils.formatUs("https://www.douban.com/people/%s/notes",
                        user.getIdOrUid()), context);
                //context.startActivity(DiaryListActivity.makeIntent(mUser, context));
            }
        };
        mTitleText.setOnClickListener(viewMoreListener);
        mViewMoreText.setOnClickListener(viewMoreListener);

        int i = 0;
        for (final Diary diary : diaryList) {

            if (i >= DIARY_COUNT_MAX) {
                break;
            }

            if (i >= mDiaryList.getChildCount()) {
                LayoutInflater.from(context)
                        .inflate(com.top.android.inji.R.layout.profile_diary_item, mDiaryList);
            }
            View diaryLayout = mDiaryList.getChildAt(i);
            DiaryLayoutHolder holder = (DiaryLayoutHolder) diaryLayout.getTag();
            if (holder == null) {
                holder = new DiaryLayoutHolder(diaryLayout);
                diaryLayout.setTag(holder);
                ViewUtils.setTextViewLinkClickable(holder.titleText);
            }

            if (!TextUtils.isEmpty(diary.cover)) {
                holder.coverImage.setVisibility(VISIBLE);
                ImageUtils.loadImage(holder.coverImage, diary.cover);
            } else {
                holder.coverImage.setVisibility(GONE);
            }
            holder.titleText.setText(diary.title);
            holder.abstractText.setText(diary.abstract_);
            diaryLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO
                    UriHandler.open(StringUtils.formatUs("https://www.douban.com/note/%d",
                            diary.id), context);
                    //context.startActivity(DiaryActivity.makeIntent(diary, context));
                }
            });

            ++i;
        }

        ViewUtils.setVisibleOrGone(mDiaryList, i != 0);
        ViewUtils.setVisibleOrGone(mEmptyView, i == 0);

        if (user.diaryCount > i) {
            mViewMoreText.setText(context.getString(com.top.android.inji.R.string.view_more_with_count_format,
                    user.diaryCount));
        } else {
            mViewMoreText.setVisibility(GONE);
        }

        for (int count = mDiaryList.getChildCount(); i < count; ++i) {
            ViewUtils.setVisibleOrGone(mDiaryList.getChildAt(i), false);
        }
    }

    static class DiaryLayoutHolder {

        @BindView(com.top.android.inji.R.id.cover)
        public ImageView coverImage;
        @BindView(com.top.android.inji.R.id.title)
        public TextView titleText;
        @BindView(com.top.android.inji.R.id.abstract_)
        public TextView abstractText;

        public DiaryLayoutHolder(View diaryLayout) {
            ButterKnife.bind(this, diaryLayout);
        }
    }
}
