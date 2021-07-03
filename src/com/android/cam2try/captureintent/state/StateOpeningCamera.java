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

package com.android.cam2try.captureintent.state;

import android.view.View;

import com.android.cam2try.ButtonManager;
import com.android.cam2try.app.CameraAppUI;
import com.android.cam2try.async.RefCountBase;
import com.android.cam2try.captureintent.event.EventOnOpenCameraFailed;
import com.android.cam2try.captureintent.event.EventOnOpenCameraSucceeded;
import com.android.cam2try.captureintent.event.EventPause;
import com.android.cam2try.captureintent.event.EventTapOnCancelIntentButton;
import com.android.cam2try.captureintent.event.EventTapOnConfirmPhotoButton;
import com.android.cam2try.captureintent.event.EventTapOnRetakePhotoButton;
import com.android.cam2try.captureintent.event.EventTapOnSwitchCameraButton;
import com.android.cam2try.captureintent.resource.ResourceConstructed;
import com.android.cam2try.captureintent.resource.ResourceSurfaceTexture;
import com.android.cam2try.captureintent.stateful.EventHandler;
import com.android.cam2try.captureintent.stateful.State;
import com.android.cam2try.captureintent.stateful.StateImpl;
import com.android.cam2try.debug.Log;
import com.android.cam2try.device.CameraId;
import com.android.cam2try.hardware.HardwareSpec;
import com.android.cam2try.one.OneCamera;
import com.android.cam2try.one.OneCameraAccessException;
import com.android.cam2try.one.OneCameraCaptureSetting;
import com.android.cam2try.one.OneCameraCharacteristics;
import com.android.cam2try.one.v2.photo.ImageRotationCalculator;
import com.android.cam2try.one.v2.photo.ImageRotationCalculatorImpl;
import com.android.cam2try.settings.Keys;
import com.android.cam2try.settings.SettingsManager;
import com.android.cam2try.util.Size;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;

import javax.annotation.Nonnull;

/**
 * Represents a state that the module is waiting for a cam2try to be opened.
 */
public final class StateOpeningCamera extends StateImpl
{
    private static final Log.Tag TAG = new Log.Tag("StateOpeningCamera");

    private final RefCountBase<ResourceConstructed> mResourceConstructed;
    private final RefCountBase<ResourceSurfaceTexture> mResourceSurfaceTexture;
    private final OneCamera.Facing mCameraFacing;
    private final CameraId mCameraId;
    private final OneCameraCharacteristics mCameraCharacteristics;
    private final String mCameraSettingsScope;

    /**
     * The desired picture size.
     */
    private Size mPictureSize;

    /**
     * Whether is paused in the middle of opening cam2try.
     */
    private boolean mIsPaused;

    private OneCameraCaptureSetting mOneCameraCaptureSetting;

    private OneCamera.OpenCallback mCameraOpenCallback = new OneCamera.OpenCallback()
    {
        @Override
        public void onFailure()
        {
            getStateMachine().processEvent(new EventOnOpenCameraFailed());
        }

        @Override
        public void onCameraClosed()
        {
            // Not used anymore.
        }

        @Override
        public void onCameraOpened(@Nonnull final OneCamera camera)
        {
            getStateMachine().processEvent(new EventOnOpenCameraSucceeded(camera));
        }
    };

    public static StateOpeningCamera from(
            State previousState,
            RefCountBase<ResourceConstructed> resourceConstructed,
            RefCountBase<ResourceSurfaceTexture> resourceSurfaceTexture,
            OneCamera.Facing cameraFacing,
            CameraId cameraId,
            OneCameraCharacteristics cameraCharacteristics)
    {
        return new StateOpeningCamera(previousState, resourceConstructed,
                resourceSurfaceTexture, cameraFacing, cameraId, cameraCharacteristics);
    }

    private StateOpeningCamera(State previousState,
                               RefCountBase<ResourceConstructed> resourceConstructed,
                               RefCountBase<ResourceSurfaceTexture> resourceSurfaceTexture,
                               OneCamera.Facing cameraFacing,
                               CameraId cameraId,
                               OneCameraCharacteristics cameraCharacteristics)
    {
        super(previousState);
        mResourceConstructed = resourceConstructed;
        mResourceConstructed.addRef();     // Will be balanced in onLeave().
        mResourceSurfaceTexture = resourceSurfaceTexture;
        mResourceSurfaceTexture.addRef();  // Will be balanced in onLeave().
        mCameraFacing = cameraFacing;
        mCameraId = cameraId;
        mCameraCharacteristics = cameraCharacteristics;
        mIsPaused = false;
        mCameraSettingsScope = SettingsManager.getCameraSettingScope(mCameraId.getValue());
        registerEventHandlers();
    }

    private void registerEventHandlers()
    {
        /** Handles EventPause. */
        EventHandler<EventPause> pauseHandler = new EventHandler<EventPause>()
        {
            @Override
            public Optional<State> processEvent(EventPause event)
            {
                mIsPaused = true;
                return NO_CHANGE;
            }
        };
        setEventHandler(EventPause.class, pauseHandler);

        /** Handles EventOnOpenCameraSucceeded. */
        EventHandler<EventOnOpenCameraSucceeded> onOpenCameraSucceededHandler =
                new EventHandler<EventOnOpenCameraSucceeded>()
                {
                    @Override
                    public Optional<State> processEvent(EventOnOpenCameraSucceeded event)
                    {
                        final OneCamera camera = event.getCamera();
                        if (mIsPaused)
                        {
                            // Just close the cam2try and finish.
                            camera.close();
                            return Optional.of((State) StateBackgroundWithSurfaceTexture.from(
                                    StateOpeningCamera.this,
                                    mResourceConstructed,
                                    mResourceSurfaceTexture));
                        }

                        mResourceConstructed.get().getMainThread().execute(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mResourceConstructed.get().getModuleUI().applyModuleSpecs(
                                        getHardwareSpec(), getBottomBarSpec());
                            }
                        });

                        return Optional.of((State) StateStartingPreview.from(
                                StateOpeningCamera.this,
                                mResourceConstructed,
                                mResourceSurfaceTexture,
                                camera,
                                mCameraId,
                                mCameraFacing,
                                mCameraCharacteristics,
                                mPictureSize,
                                mOneCameraCaptureSetting));
                    }
                };
        setEventHandler(EventOnOpenCameraSucceeded.class, onOpenCameraSucceededHandler);

        /** Handles EventOnOpenCameraFailed. */
        EventHandler<EventOnOpenCameraFailed> onOpenCameraFailedHandler =
                new EventHandler<EventOnOpenCameraFailed>()
                {
                    @Override
                    public Optional<State> processEvent(EventOnOpenCameraFailed event)
                    {
                        Log.e(TAG, "processOnCameraOpenFailure");
                        return Optional.of((State) StateFatal.from(
                                StateOpeningCamera.this, mResourceConstructed));
                    }
                };
        setEventHandler(EventOnOpenCameraFailed.class, onOpenCameraFailedHandler);
    }

    @Override
    public Optional<State> onEnter()
    {
        if (mCameraCharacteristics == null)
        {
            Log.e(TAG, "mCameraCharacteristics is null");
            return Optional.of((State) StateFatal.from(this, mResourceConstructed));
        }
        try
        {
            mPictureSize = mResourceConstructed.get().getResolutionSetting().getPictureSize(
                    mCameraId, mCameraFacing);
            mOneCameraCaptureSetting = OneCameraCaptureSetting.create(
                    mPictureSize,
                    mResourceConstructed.get().getAppController().getSettingsManager(),
                    getHardwareSpec(),
                    mCameraSettingsScope,
                    false);
        } catch (OneCameraAccessException ex)
        {
            Log.e(TAG, "Failed while open cam2try", ex);
            return Optional.of((State) StateFatal.from(this, mResourceConstructed));
        }

        final ImageRotationCalculator imageRotationCalculator = ImageRotationCalculatorImpl.from(
                mResourceConstructed.get().getOrientationManager(), mCameraCharacteristics);

        mResourceConstructed.get().getOneCameraOpener().open(
                mCameraId,
                mOneCameraCaptureSetting,
                mResourceConstructed.get().getCameraHandler(),
                mResourceConstructed.get().getMainThread(),
                imageRotationCalculator,
                mResourceConstructed.get().getBurstFacade(),
                mResourceConstructed.get().getSoundPlayer(),
                mCameraOpenCallback,
                mResourceConstructed.get().getFatalErrorHandler());
        return Optional.absent();
    }

    @Override
    public void onLeave()
    {
        mResourceConstructed.close();
        mResourceSurfaceTexture.close();
    }

    @VisibleForTesting
    public boolean isPaused()
    {
        return mIsPaused;
    }

    private HardwareSpec getHardwareSpec()
    {
        return new HardwareSpec()
        {
            @Override
            public boolean isFrontCameraSupported()
            {
                return mResourceConstructed.get()
                        .getOneCameraManager().hasCameraFacing(OneCamera.Facing.FRONT);
            }

            @Override
            public boolean isHdrSupported()
            {
                return false;
            }

            @Override
            public boolean isHdrPlusSupported()
            {
                return false;
            }

            @Override
            public boolean isFlashSupported()
            {
                return mCameraCharacteristics.isFlashSupported();
            }
        };
    }

    private CameraAppUI.BottomBarUISpec getBottomBarSpec()
    {
        CameraAppUI.BottomBarUISpec bottomBarSpec = new CameraAppUI.BottomBarUISpec();
        /** Camera switch button UI spec. */
        bottomBarSpec.enableCamera = true;
        bottomBarSpec.cameraCallback = new ButtonManager.ButtonCallback()
        {
            @Override
            public void onStateChanged(int cameraId)
            {
                getStateMachine().processEvent(new EventTapOnSwitchCameraButton());
            }
        };
        /** Grid lines button UI spec. */
        bottomBarSpec.enableGridLines = true;
        /** HDR button UI spec. */
        bottomBarSpec.enableHdr = false;
        bottomBarSpec.hideHdr = true;
        bottomBarSpec.hdrCallback = null;
        /** Timer button UI spec. */
        bottomBarSpec.enableSelfTimer = true;
        bottomBarSpec.showSelfTimer = true;
        /** Flash button UI spec. */
        bottomBarSpec.enableFlash = mCameraCharacteristics.isFlashSupported();

        /** Setup exposure compensation */
        bottomBarSpec.isExposureCompensationSupported = mCameraCharacteristics
                .isExposureCompensationSupported();
        bottomBarSpec.enableExposureCompensation = bottomBarSpec.isExposureCompensationSupported;
        bottomBarSpec.minExposureCompensation =
                mCameraCharacteristics.getMinExposureCompensation();
        bottomBarSpec.maxExposureCompensation =
                mCameraCharacteristics.getMaxExposureCompensation();
        bottomBarSpec.exposureCompensationStep =
                mCameraCharacteristics.getExposureCompensationStep();
        bottomBarSpec.exposureCompensationSetCallback =
                new CameraAppUI.BottomBarUISpec.ExposureCompensationSetCallback()
                {
                    @Override
                    public void setExposure(int value)
                    {
                        mResourceConstructed.get().getSettingsManager().set(
                                mCameraSettingsScope, Keys.KEY_EXPOSURE, value);
                    }
                };

        /** Intent image review UI spec. */
        bottomBarSpec.showCancel = true;
        bottomBarSpec.cancelCallback = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getStateMachine().processEvent(new EventTapOnCancelIntentButton());
            }
        };
        bottomBarSpec.showDone = true;
        bottomBarSpec.doneCallback = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getStateMachine().processEvent(new EventTapOnConfirmPhotoButton());
            }
        };
        bottomBarSpec.showRetake = true;
        bottomBarSpec.retakeCallback = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getStateMachine().processEvent(new EventTapOnRetakePhotoButton());
            }
        };
        return bottomBarSpec;
    }
}
