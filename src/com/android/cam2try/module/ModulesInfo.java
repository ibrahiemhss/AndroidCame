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

package com.android.cam2try.module;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import com.android.cam2try.CaptureModule;
import com.android.cam2try.PhotoModule;
import com.android.cam2try.VideoModule;
import com.android.cam2try.app.AppController;
import com.android.cam2try.app.ModuleManager;
import com.android.cam2try.captureintent.CaptureIntentModule;
import com.android.cam2try.debug.Log;
import com.android.cam2try.one.OneCamera;
import com.android.cam2try.one.OneCameraException;
import com.android.cam2try.one.config.OneCameraFeatureConfig;
import com.android.cam2try.one.config.OneCameraFeatureConfig.HdrPlusSupportLevel;
import com.android.cam2try.settings.SettingsScopeNamespaces;
import com.android.cam2try.util.GcamHelper;
import com.android.cam2try.util.PhotoSphereHelper;
import com.android.cam2try.util.RefocusHelper;
import com.android.cam2try.R;

/**
 * A class holding the module information and registers them to
 * {@link com.android.cam2try.app.ModuleManager}.
 */
public class ModulesInfo
{
    private static final Log.Tag TAG = new Log.Tag("ModulesInfo");

    public static void setupModules(Context context, ModuleManager moduleManager, OneCameraFeatureConfig config)
    {
        Resources res = context.getResources();

        // 注册PhotoModule, 这个是Camera2的拍照模式
        int photoModuleId = context.getResources().getInteger(R.integer.camera_mode_photo);
        registerPhotoModule(moduleManager, photoModuleId, SettingsScopeNamespaces.PHOTO, config.isUsingCaptureModule());
        // 默认打开PhotoModule
        moduleManager.setDefaultModuleIndex(photoModuleId);

        // 注册录像模式
        registerVideoModule(moduleManager, res.getInteger(R.integer.camera_mode_video), SettingsScopeNamespaces.VIDEO);

        // 这是什么模式？
        if (PhotoSphereHelper.hasLightCycleCapture(context))
        {
            registerWideAngleModule(moduleManager, res.getInteger(R.integer.camera_mode_panorama),
                    SettingsScopeNamespaces.PANORAMA);
            registerPhotoSphereModule(moduleManager, res.getInteger(R.integer.camera_mode_photosphere),
                    SettingsScopeNamespaces.PANORAMA);
        }

        // 这又是什么模式？
        if (RefocusHelper.hasRefocusCapture(context))
        {
            registerRefocusModule(moduleManager, res.getInteger(R.integer.camera_mode_refocus),
                    SettingsScopeNamespaces.REFOCUS);
        }

        // 这又又是什么模式？
        if (GcamHelper.hasGcamAsSeparateModule(config))
        {
            registerGcamModule(moduleManager, res.getInteger(R.integer.camera_mode_gcam), SettingsScopeNamespaces
                    .PHOTO, config
                    .getHdrPlusSupportLevel(OneCamera.Facing.BACK));
        }

        // ....
        int imageCaptureIntentModuleId = res.getInteger(R.integer.camera_mode_capture_intent);
        registerCaptureIntentModule(moduleManager, imageCaptureIntentModuleId, SettingsScopeNamespaces.PHOTO, config
                .isUsingCaptureModule());
    }

    private static void registerPhotoModule(ModuleManager moduleManager, final int moduleId, final String namespace,
                                            final boolean enableCaptureModule)
    {
        moduleManager.registerModule(new ModuleManager.ModuleAgent()
        {

            @Override
            public int getModuleId()
            {
                return moduleId;
            }

            @Override
            public boolean requestAppForCamera()
            {
                // The PhotoModule requests the old app camere, while the new
                // capture module is using OneCamera. At some point we'll
                // refactor all modules to use OneCamera, then the new module
                // doesn't have to manage it itself.
                return !enableCaptureModule;
            }

            @Override
            public String getScopeNamespace()
            {
                return namespace;
            }

            @Override
            public ModuleController createModule(AppController app, Intent intent)
            {
                Log.v(TAG, "EnableCaptureModule = " + enableCaptureModule);
                return enableCaptureModule ? new CaptureModule(app) : new PhotoModule(app);
            }
        });
    }

    private static void registerVideoModule(ModuleManager moduleManager, final int moduleId, final String namespace)
    {
        moduleManager.registerModule(new ModuleManager.ModuleAgent()
        {
            @Override
            public int getModuleId()
            {
                return moduleId;
            }

            @Override
            public boolean requestAppForCamera()
            {
                return true;
            }

            @Override
            public String getScopeNamespace()
            {
                return namespace;
            }

            @Override
            public ModuleController createModule(AppController app, Intent intent)
            {
                return new VideoModule(app);
            }
        });
    }

    private static void registerWideAngleModule(ModuleManager moduleManager, final int moduleId, final String namespace)
    {
        moduleManager.registerModule(new ModuleManager.ModuleAgent()
        {
            @Override
            public int getModuleId()
            {
                return moduleId;
            }

            @Override
            public boolean requestAppForCamera()
            {
                return true;
            }

            @Override
            public String getScopeNamespace()
            {
                return namespace;
            }

            @Override
            public ModuleController createModule(AppController app, Intent intent)
            {
                return PhotoSphereHelper.createWideAnglePanoramaModule(app);
            }
        });
    }

    private static void registerPhotoSphereModule(ModuleManager moduleManager, final int moduleId, final String
            namespace)
    {
        moduleManager.registerModule(new ModuleManager.ModuleAgent()
        {
            @Override
            public int getModuleId()
            {
                return moduleId;
            }

            @Override
            public boolean requestAppForCamera()
            {
                return true;
            }

            @Override
            public String getScopeNamespace()
            {
                return namespace;
            }

            @Override
            public ModuleController createModule(AppController app, Intent intent)
            {
                return PhotoSphereHelper.createPanoramaModule(app);
            }
        });
    }

    private static void registerRefocusModule(ModuleManager moduleManager, final int moduleId, final String namespace)
    {
        moduleManager.registerModule(new ModuleManager.ModuleAgent()
        {
            @Override
            public int getModuleId()
            {
                return moduleId;
            }

            @Override
            public boolean requestAppForCamera()
            {
                return true;
            }

            @Override
            public String getScopeNamespace()
            {
                return namespace;
            }

            @Override
            public ModuleController createModule(AppController app, Intent intent)
            {
                return RefocusHelper.createRefocusModule(app);
            }
        });
    }

    private static void registerGcamModule(ModuleManager moduleManager, final int moduleId, final String namespace,
                                           final HdrPlusSupportLevel hdrPlusSupportLevel)
    {
        moduleManager.registerModule(new ModuleManager.ModuleAgent()
        {
            @Override
            public int getModuleId()
            {
                return moduleId;
            }

            @Override
            public boolean requestAppForCamera()
            {
                return false;
            }

            @Override
            public String getScopeNamespace()
            {
                return namespace;
            }

            @Override
            public ModuleController createModule(AppController app, Intent intent)
            {
                return GcamHelper.createGcamModule(app, hdrPlusSupportLevel);
            }
        });
    }

    private static void registerCaptureIntentModule(ModuleManager moduleManager, final int moduleId, final String
            namespace, final boolean enableCaptureModule)
    {
        moduleManager.registerModule(new ModuleManager.ModuleAgent()
        {
            @Override
            public int getModuleId()
            {
                return moduleId;
            }

            @Override
            public boolean requestAppForCamera()
            {
                return !enableCaptureModule;
            }

            @Override
            public String getScopeNamespace()
            {
                return namespace;
            }

            @Override
            public ModuleController createModule(AppController app, Intent intent)
            {
                if (enableCaptureModule)
                {
                    try
                    {
                        return new CaptureIntentModule(app, intent, namespace);
                    } catch (OneCameraException ignored)
                    {
                    }
                }
                return new PhotoModule(app);
            }
        });
    }
}
