/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.broadcast.ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.top.android.inji.util.ClipboardUtils;
import com.top.android.inji.util.FragmentUtils;
import com.top.android.inji.util.ObjectUtils;
import com.top.android.inji.util.UrlUtils;
import com.top.android.inji.util.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.top.android.inji.R;
import com.top.android.inji.util.ClipboardUtils;
import com.top.android.inji.util.FragmentUtils;
import com.top.android.inji.util.ObjectUtils;
import com.top.android.inji.util.UrlUtils;
import com.top.android.inji.util.ViewUtils;

public class EditLinkDialogFragment extends AppCompatDialogFragment {

    private static final String KEY_PREFIX = SendBroadcastFragment.class.getName() + '.';

    private static final String EXTRA_LINK_INFO = KEY_PREFIX + "link_info";

    @BindView(com.top.android.inji.R.id.url_layout)
    TextInputLayout mUrlLayout;
    @BindView(com.top.android.inji.R.id.url)
    EditText mUrlEdit;
    @BindView(com.top.android.inji.R.id.title)
    EditText mTitleEdit;

    private SendBroadcastFragment.LinkInfo mExtraLinkInfo;

    public static EditLinkDialogFragment newInstance(SendBroadcastFragment.LinkInfo linkInfo) {
        //noinspection deprecation
        EditLinkDialogFragment fragment = new EditLinkDialogFragment();
        Bundle arguments = FragmentUtils.ensureArguments(fragment);
        arguments.putParcelable(EXTRA_LINK_INFO, linkInfo);
        return fragment;
    }

    /**
     * @deprecated Use {@link #newInstance(SendBroadcastFragment.LinkInfo)} instead.
     */
    public EditLinkDialogFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mExtraLinkInfo = arguments.getParcelable(EXTRA_LINK_INFO);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        View contentView = LayoutInflater.from(getActivity()).inflate(
                com.top.android.inji.R.layout.broadcast_edit_link_dialog, null);
        ButterKnife.bind(this, contentView);
        if (savedInstanceState == null) {
            if (mExtraLinkInfo != null) {
                mUrlEdit.setText(mExtraLinkInfo.url);
                mTitleEdit.setText(mExtraLinkInfo.title);
            } else {
                String clipboardText = ObjectUtils.toString(ClipboardUtils.readText(activity));
                if (UrlUtils.isValidUrl(clipboardText)) {
                    mUrlEdit.setText(clipboardText);
                }
            }
        }
        ViewUtils.hideTextInputLayoutErrorOnTextChange(mUrlEdit, mUrlLayout);
        AlertDialog dialog = new AlertDialog.Builder(activity, getTheme())
                .setTitle(com.top.android.inji.R.string.broadcast_send_edit_link_title)
                .setView(contentView)
                .setPositiveButton(com.top.android.inji.R.string.ok, null)
                .setNegativeButton(com.top.android.inji.R.string.cancel, null)
                .create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // Don't let OK dismiss our dialog.
        dialog.setOnShowListener(dialog2 -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> onOk());
        });
        return dialog;
    }

    private void onOk() {
        String url = mUrlEdit.getText().toString();
        if (!UrlUtils.isValidUrl(url)) {
            mUrlLayout.setError(getContext().getString(
                    com.top.android.inji.R.string.broadcast_send_edit_link_url_error));
            return;
        }
        String title = mTitleEdit.getText().toString();
        SendBroadcastFragment.LinkInfo linkInfo = new SendBroadcastFragment.LinkInfo(url, title,
                null, null);
        getListener().setLink(linkInfo);
        dismiss();
    }

    private Listener getListener() {
        return (Listener) getParentFragment();
    }

    public static void show(SendBroadcastFragment.LinkInfo linkInfo, Fragment fragment) {
        EditLinkDialogFragment.newInstance(linkInfo)
                .show(fragment.getChildFragmentManager(), null);
    }

    public interface Listener {
        void setLink(SendBroadcastFragment.LinkInfo linkInfo);
    }
}
