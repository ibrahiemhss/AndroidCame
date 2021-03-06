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

package com.android.cam2try.one.v2.imagesaver;

import com.android.cam2try.one.v2.camera2proxy.ImageProxy;
import com.android.cam2try.one.v2.camera2proxy.TotalCaptureResultProxy;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
interface SingleImageSaver
{
    /**
     * Implementations should save and close the image and thumbnail (if
     * present). Note that the metadata future may be cancelled or result in an
     * exception if the cam2try system is being closed or the hardware reports an
     * error.
     */
    public void saveAndCloseImage(ImageProxy fullSize, Optional<ImageProxy> thumbnail,
                                  ListenableFuture<TotalCaptureResultProxy> metadata);
}
