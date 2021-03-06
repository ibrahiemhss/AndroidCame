/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.cam2try.data;

import android.content.Context;
import android.net.Uri;

import com.android.cam2try.util.RefocusHelper;

/**
 * Loads RGBZ data.
 */
public class RgbzMetadataLoader
{

    /**
     * Checks whether this file is an RGBZ file and fill in the metadata.
     *
     * @param context The app context.
     */
    public static boolean loadRgbzMetadata(
            final Context context, Uri contentUri, Metadata metadata)
    {
        if (RefocusHelper.isRGBZ(context, contentUri))
        {
            metadata.setHasRgbzData(true);
            return true;
        }
        return false;
    }
}
