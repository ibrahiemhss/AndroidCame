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
 * Listener for device closing lifecycle events.
 */
public interface SingleDeviceCloseListener
{
    /**
     * Occurs when the device is closed.
     */
    public void onDeviceClosed();

    /**
     * Occurs when there is an exception closing the device.
     */
    public void onDeviceClosingException(Throwable exception);
}
