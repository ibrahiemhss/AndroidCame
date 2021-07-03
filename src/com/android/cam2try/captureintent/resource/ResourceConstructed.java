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

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.android.cam2try.FatalErrorHandler;
import com.android.cam2try.SoundPlayer;
import com.android.cam2try.app.AppController;
import com.android.cam2try.app.LocationManager;
import com.android.cam2try.app.OrientationManager;
import com.android.cam2try.async.MainThread;
import com.android.cam2try.async.SafeCloseable;
import com.android.cam2try.burst.BurstFacade;
import com.android.cam2try.captureintent.CaptureIntentModuleUI;
import com.android.cam2try.one.OneCameraManager;
import com.android.cam2try.one.OneCameraOpener;
import com.android.cam2try.settings.CameraFacingSetting;
import com.android.cam2try.settings.ResolutionSetting;
import com.android.cam2try.settings.SettingsManager;

/**
 * Defines an interface that any implementation of this should retain basic
 * resources to construct a state machine of
 * {@link com.android.cam2try.captureintent.CaptureIntentModule}.
 */
public interface ResourceConstructed extends SafeCloseable
{
    /**
     * Obtains the intent that starts this activity.
     *
     * @return An {@link android.content.Intent} object.
     */
    public Intent getIntent();

    /**
     * Obtains the UI object associated with the intent module.
     *
     * @return A {@link com.android.cam2try.captureintent.CaptureIntentModuleUI}
     * object.
     */
    public CaptureIntentModuleUI getModuleUI();

    /**
     * Obtains the scope namespace used in
     * {@link com.android.cam2try.settings.SettingsManager}.
     *
     * @return The scope namespace of the intent module.
     */
    public String getSettingScopeNamespace();

    /**
     * Obtains the main thread.
     *
     * @return A {@link com.android.cam2try.async.MainThread} object.
     */
    public MainThread getMainThread();

    /**
     * Obtains the Android application context.
     *
     * @return A {@link android.content.Context} object.
     */
    public Context getContext();

    /**
     * Obtains the hardware manager that provides the ability to query for
     * hardware specific characteristics.
     *
     * @return An {@link com.android.cam2try.one.OneCameraManager} object.
     */
    public OneCameraManager getOneCameraManager();

    /**
     * Obtains the cam2try manager that controls cam2try devices.
     *
     * @return An {@link com.android.cam2try.one.OneCameraOpener} object.
     */
    public OneCameraOpener getOneCameraOpener();

    /**
     * Obtains the location manager that is able to report device current
     * location.
     *
     * @return A {@link com.android.cam2try.app.LocationManager} object.
     */
    public LocationManager getLocationManager();

    /**
     * Obtains the orientation manager that is able to report device current
     * orientation.
     *
     * @return An {@link com.android.cam2try.app.OrientationManager} object.
     */
    public OrientationManager getOrientationManager();

    /**
     * Obtains the settings manager of the activity.
     *
     * @return A {@link com.android.cam2try.settings.SettingsManager} object.
     */
    public SettingsManager getSettingsManager();

    /**
     * Obtains the burst facade.
     *
     * @return A {@link com.android.cam2try.burst.BurstFacade} object.
     */
    public BurstFacade getBurstFacade();

    /**
     * Obtains the current cam2try facing setting.
     *
     * @return A {@link com.android.cam2try.settings.CameraFacingSetting} object.
     */
    public CameraFacingSetting getCameraFacingSetting();

    /**
     * Obtains the current resolution setting.
     *
     * @return A {@link com.android.cam2try.settings.ResolutionSetting} object.
     */
    public ResolutionSetting getResolutionSetting();

    /**
     * Obtains a handler to perform cam2try related operations.
     *
     * @return A {@link android.os.Handler} object.
     */
    public Handler getCameraHandler();

    /**
     * Obtains a sound player that is able to play various capture sounds.
     *
     * @return A {@link com.android.cam2try.SoundPlayer} object.
     */
    public SoundPlayer getSoundPlayer();

    /**
     * Obtains the app controller
     *
     * @return An {@link com.android.cam2try.app.AppController} object.
     * @deprecated Please avoid to use app controller.
     */
    @Deprecated
    public AppController getAppController();

    /**
     * Obtains the fatal error handler.
     *
     * @return An {@link FatalErrorHandler} object.
     */
    public FatalErrorHandler getFatalErrorHandler();
}
