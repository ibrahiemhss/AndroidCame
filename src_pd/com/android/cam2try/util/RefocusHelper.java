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

package com.android.cam2try.util;

import android.content.Context;
import android.net.Uri;

import com.android.cam2try.CameraModule;
import com.android.cam2try.app.AppController;

public class RefocusHelper
{
    public static CameraModule createRefocusModule(AppController app)
    {
        return null;
    }

    public static boolean hasRefocusCapture(Context context)
    {
        return false;
    }

    public static boolean isRGBZ(Context context, Uri contentUri)
    {
        return false;
    }
}
