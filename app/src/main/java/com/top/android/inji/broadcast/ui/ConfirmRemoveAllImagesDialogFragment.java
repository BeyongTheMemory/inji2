/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.broadcast.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import com.top.android.inji.R;

public class ConfirmRemoveAllImagesDialogFragment extends AppCompatDialogFragment {

    /**
     * @deprecated Use {@link #newInstance()} instead.
     */
    public ConfirmRemoveAllImagesDialogFragment() {}

    public static ConfirmRemoveAllImagesDialogFragment newInstance() {
        //noinspection deprecation
        return new ConfirmRemoveAllImagesDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity(), getTheme())
                .setMessage(com.top.android.inji.R.string.broadcast_send_remove_all_images_confirm)
                .setPositiveButton(com.top.android.inji.R.string.ok, (dialog, which) -> getListener().removeAllImages())
                .setNegativeButton(com.top.android.inji.R.string.cancel, null)
                .create();
    }

    private Listener getListener() {
        return (Listener) getParentFragment();
    }

    public static void show(Fragment fragment) {
        ConfirmRemoveAllImagesDialogFragment.newInstance()
                .show(fragment.getChildFragmentManager(), null);
    }

    public interface Listener {
        void removeAllImages();
    }
}
