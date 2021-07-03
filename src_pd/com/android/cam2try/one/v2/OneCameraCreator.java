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

package com.android.cam2try.one.v2;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.util.DisplayMetrics;

import com.android.cam2try.FatalErrorHandler;
import com.android.cam2try.SoundPlayer;
import com.android.cam2try.async.MainThread;
import com.android.cam2try.burst.BurstFacade;
import com.android.cam2try.debug.Log;
import com.android.cam2try.one.OneCamera;
import com.android.cam2try.one.OneCameraAccessException;
import com.android.cam2try.one.OneCameraCaptureSetting;
import com.android.cam2try.one.OneCameraCharacteristics;
import com.android.cam2try.one.config.OneCameraFeatureConfig;
import com.android.cam2try.one.config.OneCameraFeatureConfig.CaptureSupportLevel;
import com.android.cam2try.one.v2.camera2proxy.AndroidCameraDeviceProxy;
import com.android.cam2try.one.v2.common.PictureSizeCalculator;
import com.android.cam2try.one.v2.imagesaver.ImageSaver;
import com.android.cam2try.one.v2.imagesaver.JpegImageBackendImageSaver;
import com.android.cam2try.one.v2.imagesaver.YuvImageBackendImageSaver;
import com.android.cam2try.one.v2.photo.ImageRotationCalculator;
import com.android.cam2try.processing.ProcessingServiceManager;
import com.android.cam2try.processing.imagebackend.ImageBackend;

public class OneCameraCreator
{
    private static Log.Tag TAG = new Log.Tag("OneCamCreator");

    public static OneCamera create(
            CameraDevice device,
            CameraCharacteristics characteristics,
            OneCameraFeatureConfig featureConfig,
            OneCameraCaptureSetting captureSetting,
            DisplayMetrics displayMetrics,
            Context context,
            MainThread mainThread,
            ImageRotationCalculator imageRotationCalculator,
            BurstFacade burstController,
            SoundPlayer soundPlayer,
            FatalErrorHandler fatalErrorHandler) throws OneCameraAccessException
    {
        // TODO: Might want to switch current cam2try to vendor HDR.

        CaptureSupportLevel captureSupportLevel = featureConfig
                .getCaptureSupportLevel(characteristics);
        Log.i(TAG, "Camera support level: " + captureSupportLevel.name());

        OneCameraCharacteristics oneCharacteristics =
                new OneCameraCharacteristicsImpl(characteristics);

        PictureSizeCalculator pictureSizeCalculator =
                new PictureSizeCalculator(oneCharacteristics);
        PictureSizeCalculator.Configuration configuration = null;

        OneCameraFactory cameraFactory = null;
        ImageSaver.Builder imageSaverBuilder = null;
        ImageBackend imageBackend = ProcessingServiceManager.instance().getImageBackend();

        // Depending on the support level of the cam2try, choose the right
        // configuration.
        switch (captureSupportLevel)
        {
            case LIMITED_JPEG:
            case LEGACY_JPEG:
                // LIMITED and LEGACY have different picture takers which will
                // be selected by the support level that is passes into
                // #createOneCamera below - otherwise they use the same OneCamera and image backend.
                cameraFactory = new SimpleOneCameraFactory(ImageFormat.JPEG,
                        featureConfig.getMaxAllowedImageReaderCount(),
                        imageRotationCalculator);
                configuration = pictureSizeCalculator.computeConfiguration(
                        captureSetting.getCaptureSize(),
                        ImageFormat.JPEG);
                imageSaverBuilder = new JpegImageBackendImageSaver(imageRotationCalculator,
                        imageBackend, configuration.getPostCaptureCrop());
                break;
            case LIMITED_YUV:
                // Same as above, but we're using YUV images.
                cameraFactory = new SimpleOneCameraFactory(ImageFormat.YUV_420_888,
                        featureConfig.getMaxAllowedImageReaderCount(),
                        imageRotationCalculator);
                configuration = pictureSizeCalculator.computeConfiguration(
                        captureSetting.getCaptureSize(),
                        ImageFormat.YUV_420_888);
                imageSaverBuilder = new YuvImageBackendImageSaver(imageRotationCalculator,
                        imageBackend,
                        configuration.getPostCaptureCrop());
                break;
            case ZSL:
                // ZSL has its own OneCamera and produces YUV images.
                cameraFactory = new ZslOneCameraFactory(ImageFormat.YUV_420_888,
                        featureConfig.getMaxAllowedImageReaderCount());
                configuration = pictureSizeCalculator.computeConfiguration(
                        captureSetting.getCaptureSize(),
                        ImageFormat.YUV_420_888);
                imageSaverBuilder = new YuvImageBackendImageSaver(imageRotationCalculator,
                        imageBackend, configuration.getPostCaptureCrop());
                break;
        }

        Log.i(TAG, "Picture Size Configuration: " + configuration);

        return cameraFactory.createOneCamera(new AndroidCameraDeviceProxy(device),
                new OneCameraCharacteristicsImpl(characteristics),
                captureSupportLevel,
                mainThread,
                configuration.getNativeOutputSize(),
                imageSaverBuilder,
                captureSetting.getFlashSetting(),
                captureSetting.getExposureSetting(),
                captureSetting.getHdrSceneSetting(),
                burstController,
                fatalErrorHandler);
    }
}
