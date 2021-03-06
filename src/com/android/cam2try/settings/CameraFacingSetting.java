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

package com.android.cam2try.settings;

import com.android.cam2try.app.AppController;
import com.android.cam2try.one.OneCamera;
import com.android.cam2try.R;

import android.content.res.Resources;

/**
 * Handles the cam2try facing setting for a particular scope stored in
 * SharedPreferences keyed by Keys.KEY_CAMERA_ID.
 */
public class CameraFacingSetting
{
    private final SettingsManager mSettingsManager;

    private final String mSettingScope;

    private final String mCameraFacingSettingKey;

    private final int mCameraFacingBackValue;
    private final int mCameraFacingFrontValue;
    private final int mCameraFacingDefaultValue;

    public CameraFacingSetting(
            Resources resources,
            SettingsManager settingsManager,
            String moduleSettingScope,
            AppController appController)
    {
        mSettingsManager = settingsManager;

        mSettingScope = SettingsManager.getModuleSettingScope(moduleSettingScope);

        mCameraFacingSettingKey = Keys.KEY_CAMERA_ID;
        mCameraFacingDefaultValue =
                Integer.parseInt(resources.getString(R.string.pref_camera_id_default));

        if (appController.getCameraProvider().getCharacteristics(mCameraFacingDefaultValue).
                isFacingFront())
        {
            mCameraFacingFrontValue = 0;
            mCameraFacingBackValue = 1;
        } else
        {
            mCameraFacingBackValue = 0;
            mCameraFacingFrontValue = 1;
        }
    }

    @Override
    public String toString()
    {
        return isFacingBack() ? "Back Camera" : "Front Camera";
    }

    /**
     * Sets the default value for the cam2try facing setting.
     */
    public void setDefault()
    {
        mSettingsManager.setDefaults(
                Keys.KEY_CAMERA_ID,
                mCameraFacingDefaultValue,
                new int[]{mCameraFacingBackValue, mCameraFacingFrontValue});
    }

    /**
     * Whether the back cam2try should be opened.
     *
     * @return Whether the back cam2try should be opened.
     */
    public boolean isFacingBack()
    {
        return getCameraFacing() == OneCamera.Facing.BACK;
    }

    /**
     * Whether the front cam2try should be opened.
     *
     * @return Whether the front cam2try should be opened.
     */
    public boolean isFacingFront()
    {
        return getCameraFacing() == OneCamera.Facing.FRONT;
    }

    /**
     * Gets the current cam2try facing in the setting.
     *
     * @return The current cam2try facing in the setting.
     */
    public OneCamera.Facing getCameraFacing()
    {
        final int cameraId = mSettingsManager.getInteger(mSettingScope, mCameraFacingSettingKey);
        if (cameraId == mCameraFacingBackValue)
        {
            return OneCamera.Facing.BACK;
        } else if (cameraId == mCameraFacingFrontValue)
        {
            return OneCamera.Facing.FRONT;
        } else
        {
            return getDefaultCameraFacing();
        }
    }

    /**
     * Sets the cam2try facing setting.
     *
     * @param cameraFacing The new cam2try facing.
     */
    public void setCameraFacing(OneCamera.Facing cameraFacing)
    {
        final int cameraId = (cameraFacing == OneCamera.Facing.BACK) ?
                mCameraFacingBackValue : mCameraFacingFrontValue;
        mSettingsManager.set(mSettingScope, mCameraFacingSettingKey, cameraId);
    }

    /**
     * Changes the cam2try facing setting to the other side.
     *
     * @return The new cam2try facing.
     */
    public OneCamera.Facing switchCameraFacing()
    {
        final OneCamera.Facing originalFacing = getCameraFacing();
        final OneCamera.Facing newFacing = (originalFacing == OneCamera.Facing.BACK) ?
                OneCamera.Facing.FRONT : OneCamera.Facing.BACK;
        setCameraFacing(newFacing);
        return newFacing;
    }

    private OneCamera.Facing getDefaultCameraFacing()
    {
        if (mCameraFacingDefaultValue == mCameraFacingBackValue)
        {
            return OneCamera.Facing.BACK;
        } else
        {
            return OneCamera.Facing.FRONT;
        }
    }
}
