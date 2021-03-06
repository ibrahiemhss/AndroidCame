/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.android.cam2try.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.android.cam2try.CameraModule;
import com.android.cam2try.app.AppController;

public class PhotoSphereHelper
{
    public static class PanoramaMetadata
    {
        // Whether a panorama viewer should be used
        public final boolean mUsePanoramaViewer;
        // Whether a panorama is 360 degrees
        public final boolean mIsPanorama360;

        public PanoramaMetadata(boolean usePanoramaViewer, boolean isPanorama360)
        {
            mUsePanoramaViewer = usePanoramaViewer;
            mIsPanorama360 = isPanorama360;
        }
    }

    public static class PanoramaViewHelper
    {

        public PanoramaViewHelper(Activity activity)
        {
            /* Do nothing */
        }

        public void onStart()
        {
            /* Do nothing */
        }

        public void onCreate()
        {
            /* Do nothing */
        }

        public void onResume()
        {
            /* Do nothing */
        }

        public void onPause()
        {
            /* Do nothing */
        }

        public void onStop()
        {
            /* Do nothing */
        }

        /**
         * @return The {@link android.content.Intent} to invoke the external
         * PhotoSphere viewer.
         */
        public Intent showPanorama(Activity activity, Uri uri)
        {
            /* Do nothing */
            return null;
        }

        public void showRgbz(Uri uri)
        {
            /* Do nothing */
        }
    }

    public static final PanoramaMetadata NOT_PANORAMA = new PanoramaMetadata(false, false);

    public static boolean hasLightCycleCapture(Context context)
    {
        return false;
    }

    public static PanoramaMetadata getPanoramaMetadata(Context context, Uri uri)
    {
        return NOT_PANORAMA;
    }

    public static CameraModule createPanoramaModule(AppController app)
    {
        return null;
    }

    public static CameraModule createWideAnglePanoramaModule(AppController app)
    {
        return null;
    }

    /**
     * Get the file path from a Media storage URI.
     */
    public static String getPathFromURI(ContentResolver contentResolver, Uri contentUri)
    {
        return null;
    }

    /**
     * Get the modified time from a Media storage URI.
     */
    public static long getModifiedTimeFromURI(ContentResolver contentResolver, Uri contentUri)
    {
        return 0;
    }

    /**
     * Get the resource id of the panorama horizontal icon.
     */
    public static int getPanoramaHorizontalDrawableId()
    {
        return 0;
    }

    /**
     * Get the resource id of the panorama vertical icon.
     */
    public static int getPanoramaVerticalDrawableId()
    {
        return 0;
    }

    /**
     * Get the resource id of the panorama orientation option icon array.
     */
    public static int getPanoramaOrientationOptionArrayId()
    {
        return 0;
    }

    /**
     * Get the resource id of the panorama orientation descriptions array.
     */
    public static int getPanoramaOrientationDescriptions()
    {
        return 0;
    }

    /**
     * Get the resource id of the panorama orientation indicator array.
     */
    public static int getPanoramaOrientationIndicatorArrayId()
    {
        return 0;
    }
}
