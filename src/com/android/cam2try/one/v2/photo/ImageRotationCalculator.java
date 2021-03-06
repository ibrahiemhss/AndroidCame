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

package com.android.cam2try.one.v2.photo;

import com.android.cam2try.app.OrientationManager;

import com.google.common.base.Supplier;

/**
 * Based on the current device orientation, calculates the JPEG rotation that
 * needs to be applied to render the resulting JPEG correctly.
 */
public interface ImageRotationCalculator
{

    /**
     * Calculates the correct JPEG orientation based on the given sampled device
     * orientation, and the sensor orientation.
     *
     * @return The JPEG rotation that needs to be applied to the final image.
     */
    public OrientationManager.DeviceOrientation toImageRotation();

    /**
     * Returns a supplier of the correct JPEG orientation based on the
     * sampled device orientation and the sensor orientation.
     *
     * @return Supplier of the JPEG rotation that needs to be applied to the
     * final image in degrees.
     */
    public Supplier<Integer> getSupplier();
}
