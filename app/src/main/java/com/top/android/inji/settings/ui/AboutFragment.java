/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.settings.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.top.android.inji.ui.KonamiCodeDetector;
import com.top.android.inji.util.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.top.android.inji.BuildConfig;
import com.top.android.inji.R;
import com.top.android.inji.profile.ui.ProfileActivity;
import com.top.android.inji.scalpel.ScalpelHelperFragment;
import com.top.android.inji.ui.KonamiCodeDetector;
import com.top.android.inji.util.AppUtils;

public class AboutFragment extends Fragment implements ConfirmEnableScalpelDialogFragment.Listener {

    @BindView(com.top.android.inji.R.id.container)
    LinearLayout mContainerLayout;
    @BindView(com.top.android.inji.R.id.toolbar)
    Toolbar mToolbar;
    @BindView(com.top.android.inji.R.id.version)
    TextView mVersionText;
    @BindView(com.top.android.inji.R.id.douban)
    Button mDoubanButton;

    public static AboutFragment newInstance() {
        //noinspection deprecation
        return new AboutFragment();
    }

    /**
     * @deprecated Use {@link #newInstance()} instead.
     */
    public AboutFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(com.top.android.inji.R.layout.about_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ScalpelHelperFragment.attachTo(this);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setTitle(null);

        // Seems that ScrollView intercepts touch event, so we have to set the onTouchListener on a
        // view inside it.
        mContainerLayout.setOnTouchListener(new KonamiCodeDetector(activity) {
            @Override
            public void onDetected() {
                onEnableScalpel();
            }
        });

        mVersionText.setText(getString(com.top.android.inji.R.string.about_version_format, BuildConfig.VERSION_NAME));
        mDoubanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Activity activity = getActivity();
//                activity.startActivity(ProfileActivity.makeIntent("douban-douya", activity));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AppUtils.navigateUp(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onEnableScalpel() {
        ConfirmEnableScalpelDialogFragment.show(this);
    }

    @Override
    public void enableScalpel() {
        ScalpelHelperFragment.setEnabled(true);
    }
}
