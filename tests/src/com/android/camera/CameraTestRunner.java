/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.android.cam2try;

import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;

import com.android.cam2try.functional.CameraTest;
import com.android.cam2try.functional.ImageCaptureIntentTest;
import com.android.cam2try.functional.VideoCaptureIntentTest;
import com.android.cam2try.unittest.CameraUnitTest;

import junit.framework.TestSuite;

public class CameraTestRunner extends InstrumentationTestRunner
{

    @Override
    public TestSuite getAllTests()
    {
        TestSuite suite = new InstrumentationTestSuite(this);
        suite.addTestSuite(CameraTest.class);
        suite.addTestSuite(ImageCaptureIntentTest.class);
        suite.addTestSuite(VideoCaptureIntentTest.class);
        suite.addTestSuite(CameraUnitTest.class);
        return suite;
    }

    @Override
    public ClassLoader getLoader()
    {
        return CameraTestRunner.class.getClassLoader();
    }
}
