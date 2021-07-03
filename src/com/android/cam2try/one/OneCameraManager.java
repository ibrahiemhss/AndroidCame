/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.android.cam2try.one;

import com.android.cam2try.device.CameraId;
import com.android.cam2try.one.OneCamera.Facing;

/**
 * The cam2try manager is responsible for providing details about the
 * available cam2try hardware on the current device.
 */
public interface OneCameraManager
{

    /**
     * Returns true if this hardware platform currently has any cameras at
     * all.
     */
    public boolean hasCamera();

    /**
     * Returns whether the device has a cam2try facing the given direction.
     */
    public boolean hasCameraFacing(Facing facing);

    /**
     * Get a platform specific device key for the first cam2try in the list
     * of all cam2try devices.
     */
    public CameraId findFirstCamera();

    /**
     * Get a platform specific device key for a cam2try facing a particular
     * direction.
     */
    public CameraId findFirstCameraFacing(Facing facing);

    /**
     * Retrieve the characteristics for the cam2try facing at the given
     * direction. The first cam2try found in the given direction will be chosen.
     *
     * @return A #{link com.android.cam2try.one.OneCameraCharacteristics} object
     * to provide cam2try characteristics information. Returns null if
     * there is no cam2try facing the given direction.
     */
    public OneCameraCharacteristics getOneCameraCharacteristics(CameraId cameraId)
            throws OneCameraAccessException;

    public static class Factory
    {

    }
}