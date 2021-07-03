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

package com.android.cam2try.captureintent;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.android.cam2try.CameraActivity;
import com.android.cam2try.CameraModule;
import com.android.cam2try.app.AppController;
import com.android.cam2try.app.CameraAppUI;
import com.android.cam2try.async.MainThread;
import com.android.cam2try.async.RefCountBase;
import com.android.cam2try.burst.BurstFacadeFactory;
import com.android.cam2try.captureintent.event.EventClickOnCameraKey;
import com.android.cam2try.captureintent.event.EventOnSurfaceTextureAvailable;
import com.android.cam2try.captureintent.event.EventOnSurfaceTextureDestroyed;
import com.android.cam2try.captureintent.event.EventOnSurfaceTextureUpdated;
import com.android.cam2try.captureintent.event.EventOnTextureViewLayoutChanged;
import com.android.cam2try.captureintent.event.EventPause;
import com.android.cam2try.captureintent.event.EventResume;
import com.android.cam2try.captureintent.event.EventTapOnCancelShutterButton;
import com.android.cam2try.captureintent.event.EventTapOnPreview;
import com.android.cam2try.captureintent.event.EventTapOnShutterButton;
import com.android.cam2try.captureintent.event.EventZoomRatioChanged;
import com.android.cam2try.captureintent.resource.ResourceConstructed;
import com.android.cam2try.captureintent.resource.ResourceConstructedImpl;
import com.android.cam2try.captureintent.state.StateBackground;
import com.android.cam2try.captureintent.stateful.State;
import com.android.cam2try.captureintent.stateful.StateMachine;
import com.android.cam2try.captureintent.stateful.StateMachineImpl;
import com.android.cam2try.debug.Log;
import com.android.cam2try.hardware.HardwareSpec;
import com.android.cam2try.one.OneCameraException;
import com.android.cam2try.one.OneCameraModule;
import com.android.cam2try.settings.SettingsManager;
import com.android.cam2try.ui.PreviewStatusListener;
import com.android.cam2try.ui.TouchCoordinate;
import com.android.cam2try.util.Size;
import com.android.cam2try.R;
import com.android.ex.camera2.portability.CameraAgent;

/**
 * The cam2try module that handles image capture intent.
 */
public class CaptureIntentModule extends CameraModule
{
    private static final Log.Tag TAG = new Log.Tag("CapIntModule");

    /**
     * The module UI.
     */
    private final CaptureIntentModuleUI mModuleUI;

    /**
     * The available resources after construction.
     */
    private final RefCountBase<ResourceConstructed> mResourceConstructed;

    /**
     * The module state machine.
     */
    private final StateMachine mStateMachine;

    private TouchCoordinate mTouchPointInsideShutterButton;

    public CaptureIntentModule(AppController appController, Intent intent,
                               String settingScopeNamespace) throws OneCameraException
    {
        super(appController);
        mModuleUI = new CaptureIntentModuleUI(
                appController.getCameraAppUI(),
                appController.getModuleLayoutRoot(),
                mUIListener);
        mStateMachine = new StateMachineImpl();
        mResourceConstructed = ResourceConstructedImpl.create(
                intent,
                mModuleUI,
                settingScopeNamespace,
                MainThread.create(),
                appController.getAndroidContext(),
                appController.getCameraOpener(),
                OneCameraModule.provideOneCameraManager(),
                appController.getLocationManager(),
                appController.getOrientationManager(),
                appController.getSettingsManager(),
                new BurstFacadeFactory.BurstFacadeStub(),
                appController,
                appController.getFatalErrorHandler());
        final State initialState = StateBackground.create(mStateMachine, mResourceConstructed);
        // Set the initial state.
        mStateMachine.setInitialState(initialState);
    }

    @Override
    public void onCameraAvailable(CameraAgent.CameraProxy cameraProxy)
    {
        // Do nothing for capture intent.
    }

    @Override
    public void onShutterButtonFocus(boolean pressed)
    {
        // Do nothing for capture intent.
    }

    @Override
    public void onShutterCoordinate(TouchCoordinate touchCoordinate)
    {
        mTouchPointInsideShutterButton = touchCoordinate;
    }

    @Override
    public void onShutterButtonClick()
    {
        mStateMachine.processEvent(new EventTapOnShutterButton(mTouchPointInsideShutterButton));
    }

    @Override
    public void onShutterButtonLongPressed()
    {
        // Do nothing for capture intent.
    }

    @Override
    public void init(
            final CameraActivity activity, boolean isSecureCamera, boolean isCaptureIntent)
    {
        mResourceConstructed.get().getAppController()
                .setPreviewStatusListener(mPreviewStatusListener);

        // Issue cancel countdown event when the button is pressed.
        // TODO: Make this part of the official API the way shutter button events are.
        mResourceConstructed.get().getAppController().getCameraAppUI()
                .setCancelShutterButtonListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mStateMachine.processEvent(new EventTapOnCancelShutterButton());
                    }
                });

    }

    @Override
    public void resume()
    {
        mModuleUI.onModuleResumed();
        mStateMachine.processEvent(new EventResume());
    }

    @Override
    public void pause()
    {
        mModuleUI.setCountdownFinishedListener(null);
        mModuleUI.onModulePaused();
        mStateMachine.processEvent(new EventPause());
    }

    @Override
    public void destroy()
    {
        // Never called. Do nothing here.
    }

    @Override
    public void onPreviewVisibilityChanged(int visibility)
    {
        // Do nothing.
    }

    @Override
    public void onLayoutOrientationChanged(boolean isLandscape)
    {
        // Do nothing.
    }

    @Override
    public void hardResetSettings(SettingsManager settingsManager)
    {
        // Do nothing.
    }

    @Override
    public HardwareSpec getHardwareSpec()
    {
        /**
         * Instead of passively providing CameraAppUI the hardware spec here,
         * {@link com.android.cam2try.captureintent.state.StateOpeningCamera}
         * will actively specify hardware spec.
         */
        return null;
    }

    @Override
    public CameraAppUI.BottomBarUISpec getBottomBarSpec()
    {
        /**
         * Instead of passively providing CameraAppUI the bottom bar spec here,
         * {@link com.android.cam2try.captureintent.state.StateOpeningCamera}
         * will actively specify bottom bar spec.
         */
        return null;
    }

    @Override
    public boolean isUsingBottomBar()
    {
        return true;
    }

    @Override
    public String getPeekAccessibilityString()
    {
        return mResourceConstructed.get().getContext().getResources()
                .getString(R.string.photo_accessibility_peek);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_CAMERA:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                mStateMachine.processEvent(new EventClickOnCameraKey());
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                // Prevent default.
                return true;
        }
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                mStateMachine.processEvent(new EventClickOnCameraKey());
                return true;
        }
        return false;
    }

    /**
     * The listener to listen events from the UI.
     */
    private final CaptureIntentModuleUI.Listener mUIListener =
            new CaptureIntentModuleUI.Listener()
            {
                @Override
                public void onZoomRatioChanged(final float zoomRatio)
                {
                    mStateMachine.processEvent(new EventZoomRatioChanged(zoomRatio));
                }
            };

    /**
     * The listener to listen events from the preview.
     */
    private final PreviewStatusListener mPreviewStatusListener = new PreviewStatusListener()
    {
        @Override
        public void onPreviewLayoutChanged(View v, int left, int top, int right,
                                           int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom)
        {
            final Size previewLayoutSize = new Size(right - left, bottom - top);
            mStateMachine.processEvent(new EventOnTextureViewLayoutChanged(previewLayoutSize));
        }

        @Override
        public boolean shouldAutoAdjustTransformMatrixOnLayout()
        {
            return CaptureIntentConfig.WORKAROUND_PREVIEW_STRETCH_BUG_NEXUS4;
        }

        @Override
        public void onPreviewFlipped()
        {
            // Do nothing because when preview is flipped, TextureView will lay
            // itself out again, which will then trigger a transform matrix
            // update.
        }

        @Override
        public GestureDetector.OnGestureListener getGestureListener()
        {
            return new GestureDetector.SimpleOnGestureListener()
            {
                @Override
                public boolean onSingleTapUp(MotionEvent ev)
                {
                    final Point tapPoint = new Point((int) ev.getX(), (int) ev.getY());
                    mStateMachine.processEvent(new EventTapOnPreview(tapPoint));
                    return true;
                }
            };
        }

        @Override
        public View.OnTouchListener getTouchListener()
        {
            return null;
        }

        @Override
        public void onSurfaceTextureAvailable(
                final SurfaceTexture surfaceTexture, int width, int height)
        {
            mStateMachine.processEvent(new EventOnSurfaceTextureAvailable(surfaceTexture));
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
        {
            mStateMachine.processEvent(new EventOnSurfaceTextureDestroyed());
            return true;
        }

        @Override
        public void onSurfaceTextureSizeChanged(
                SurfaceTexture surfaceTexture, int width, int height)
        {
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface)
        {
            mStateMachine.processEvent(new EventOnSurfaceTextureUpdated());
        }
    };
}
