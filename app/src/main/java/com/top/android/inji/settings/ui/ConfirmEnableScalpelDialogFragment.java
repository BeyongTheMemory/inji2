/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.settings.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import com.top.android.inji.R;

public class ConfirmEnableScalpelDialogFragment extends AppCompatDialogFragment {

    /**
     * @deprecated Use {@link #newInstance()} instead.
     */
    public ConfirmEnableScalpelDialogFragment() {}

    public static ConfirmEnableScalpelDialogFragment newInstance() {
        //noinspection deprecation
        return new ConfirmEnableScalpelDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity(), getTheme())
                .setTitle(com.top.android.inji.R.string.about_confirm_enable_scalpel_title)
                .setMessage(com.top.android.inji.R.string.about_confirm_enable_scalpel_message)
                .setPositiveButton(com.top.android.inji.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getListener().enableScalpel();
                    }
                })
                .setNegativeButton(com.top.android.inji.R.string.cancel, null)
                .create();
    }

    private Listener getListener() {
        return (Listener) getParentFragment();
    }

    public static void show(Fragment fragment) {
        ConfirmEnableScalpelDialogFragment.newInstance()
                .show(fragment.getChildFragmentManager(), null);
    }

    public interface Listener {
        void enableScalpel();
    }
}
