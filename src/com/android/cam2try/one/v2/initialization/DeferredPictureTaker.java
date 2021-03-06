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

package com.android.cam2try.one.v2.initialization;

import com.android.cam2try.one.OneCamera;
import com.android.cam2try.one.v2.photo.PictureTaker;
import com.android.cam2try.session.CaptureSession;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * A {@link PictureTaker} on which {@link #takePicture} may be called even if
 * the underlying cam2try is not yet ready.
 */
class DeferredPictureTaker implements PictureTaker
{
    private final Future<PictureTaker> mPictureTakerFuture;

    public DeferredPictureTaker(Future<PictureTaker> pictureTakerFuture)
    {
        mPictureTakerFuture = pictureTakerFuture;
    }

    @Override
    public void takePicture(OneCamera.PhotoCaptureParameters params, CaptureSession session)
    {
        if (mPictureTakerFuture.isDone())
        {
            try
            {
                PictureTaker taker = mPictureTakerFuture.get();
                taker.takePicture(params, session);
            } catch (InterruptedException | ExecutionException | CancellationException e)
            {
                return;
            }
        }
    }
}
