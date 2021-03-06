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

package com.android.cam2try.device;

/**
 * Provides a set of executable actions for a given cam2try device key.
 * <p>
 * In the case of Camera2 API, this is the example signature:
 *
 * <pre><code>
 * Provider implements CameraDeviceActionProvider<CameraDevice, String>
 * </code></pre>
 *
 * @param <TDevice> The type of cam2try device the actions produce.
 */
public interface CameraDeviceActionProvider<TDevice>
{

    /**
     * Return a new set of device and api specific actions for the given
     * types.
     */
    public SingleDeviceActions<TDevice> get(CameraDeviceKey key);
}
