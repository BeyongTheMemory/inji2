/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.top.android.inji.app;

import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.top.android.inji.util.FileTypeUtils;

import java.io.FileInputStream;
import java.io.InputStream;

import com.top.android.inji.util.FileTypeUtils;

public class ImageTypeFileProvider extends FileProvider {

    @Override
    public String getType(@NonNull Uri uri) {

        String type = super.getType(uri);
        if (!TextUtils.equals(type, "application/octet-stream")) {
            return type;
        }

        try (ParcelFileDescriptor parcelFileDescriptor = openFile(uri, "r")) {
            if (parcelFileDescriptor == null) {
                return type;
            }
            try (InputStream inputStream = new FileInputStream(
                    parcelFileDescriptor.getFileDescriptor())) {
                type = FileTypeUtils.getImageMimeType(inputStream, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return type;
    }
}
