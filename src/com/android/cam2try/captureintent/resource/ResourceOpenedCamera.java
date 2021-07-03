/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.android.cam2try.captureintent.resource;

import android.graphics.PointF;
import android.view.Surface;

import com.android.cam2try.async.SafeCloseable;
import com.android.cam2try.device.CameraId;
import com.android.cam2try.one.OneCamera;
import com.android.cam2try.one.OneCameraCaptureSetting;
import com.android.cam2try.one.OneCameraCharacteristics;
import com.android.cam2try.util.Size;

/**
 * Defines an interface that any implementation of this is responsible for
 * retaining and releasing an opened {@link com.android.cam2try.one.OneCamera}.
 */
public interface ResourceOpenedCamera extends SafeCloseable
{
    /**
     * Obtains the opened cam2try.
     *
     * @return A {@link com.android.cam2try.one.OneCamera} object.
     */
    public OneCamera getCamera();

    /**
     * Obtains key for this one cam2try object
     *
     * @return A {@link com.android.cam2try.one.OneCamera} object.
     */
    public CameraId getCameraId();

    /**
     * Obtains the facing of the opened cam2try.
     *
     * @return A {@link com.android.cam2try.one.OneCamera.Facing}.
     */
    public OneCamera.Facing getCameraFacing();

    /**
     * Obtains the characteristics of the opened cam2try.
     *
     * @return A {@link com.android.cam2try.one.OneCameraCharacteristics}
     * object.
     */
    public OneCameraCharacteristics getCameraCharacteristics();

    /**
     * Obtains the chosen size for any picture taken by this cam2try.
     *
     * @return A {@link com.android.cam2try.util.Size} object.
     */
    public Size getPictureSize();

    /**
     * Obtains the capture setting of the opened cam2try.
     *
     * @return A {@link com.android.cam2try.one.OneCameraCaptureSetting} object.
     */
    public OneCameraCaptureSetting getCaptureSetting();

    /**
     * Obtains the current zoom ratio applied on this cam2try.
     *
     * @return The current zoom ratio.
     */
    public float getZoomRatio();

    /**
     * Changes the zoom ratio on this cam2try.
     *
     * @param zoomRatio The new zoom ratio to be applied.
     */
    public void setZoomRatio(float zoomRatio);

    /**
     * Starts preview video on a particular surface.
     *
     * @param previewSurface       A {@link android.view.Surface} that the preview
     *                             will be displayed on.
     * @param captureReadyCallback A {@link com.android.cam2try.one.OneCamera.CaptureReadyCallback}.
     */
    public void startPreview(
            Surface previewSurface, OneCamera.CaptureReadyCallback captureReadyCallback);

    /**
     * Trigger active focus at a specific point.
     *
     * @param point The focus point.
     */
    public void triggerFocusAndMeterAtPoint(PointF point);
}
