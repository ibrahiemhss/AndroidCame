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

import com.android.cam2try.util.PhotoSphereHelper;

/**
 * This class breaks out the off-thread panorama support.
 */
public class PanoramaMetadataLoader
{
    /**
     * Extracts panorama metadata from the item with the given URI and fills the
     * {@code metadata}.
     */
    public static boolean loadPanoramaMetadata(final Context context, Uri contentUri,
                                               Metadata metadata)
    {
        PhotoSphereHelper.PanoramaMetadata panoramaMetadata =
                PhotoSphereHelper.getPanoramaMetadata(context, contentUri);
        // Note: The use of '==' here is in purpose as this is a singleton that
        // is returned if this is not a panorama, so pointer comparison works.
        if (panoramaMetadata == null || panoramaMetadata == PhotoSphereHelper.NOT_PANORAMA)
        {
            return false;
        }

        metadata.setPanorama(true);
        metadata.setPanorama360(panoramaMetadata.mIsPanorama360);
        metadata.setUsePanoramaViewer(panoramaMetadata.mUsePanoramaViewer);

        return true;
    }
}
