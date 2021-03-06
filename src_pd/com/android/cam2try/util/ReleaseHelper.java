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

package com.android.cam2try.util;

import android.app.Activity;

import com.android.cam2try.data.FilmstripItem;
import com.android.cam2try.settings.SettingsManager;

public class ReleaseHelper
{
    public static void showReleaseInfoDialogOnStart(Activity activity,
                                                    SettingsManager settingsManager)
    {
        // Do nothing.
    }

    public static void showReleaseInfoDialog(Activity activity, Callback<Void> callback)
    {
        callback.onCallback(null);
    }

    public static boolean shouldShowReleaseInfoDialogOnShare(FilmstripItem data)
    {
        return false;
    }

    public static boolean shouldLogVerbose()
    {
        return false;
    }
}
