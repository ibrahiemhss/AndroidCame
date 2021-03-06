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

package com.android.cam2try.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.android.cam2try.R;

/**
 * Renders a circular progress bar on the screen.
 */
public class ProgressRenderer
{
    private final int mProgressRadius;
    private final Paint mProgressBasePaint;
    private final Paint mProgressPaint;

    private RectF mArcBounds = new RectF(0, 0, 1, 1);
    private int mProgressAngleDegrees = 270;
    private boolean mVisible = false;
    private final View mParentView;
    private final Runnable mInvalidateParentViewRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            mParentView.invalidate();
        }
    };

    /**
     * After we reach 100%, keep on painting the progress for another x milliseconds
     * before hiding it.
     */
    private static final int SHOW_PROGRESS_X_ADDITIONAL_MS = 100;

    public ProgressRenderer(Context context, View parent)
    {
        mParentView = parent;
        mProgressRadius = context.getResources().getDimensionPixelSize(R.dimen.pie_progress_radius);
        int pieProgressWidth = context.getResources().getDimensionPixelSize(
                R.dimen.pie_progress_width);
        mProgressBasePaint = createProgressPaint(pieProgressWidth, 0.2f);
        mProgressPaint = createProgressPaint(pieProgressWidth, 1.0f);
    }

    /**
     * Shows a progress indicator. If the progress is '100', the progress
     * indicator will be hidden.
     * <p>
     * Can be called from any thread.
     *
     * @param percent the progress in percent (0-100).
     */
    public void setProgress(int percent)
    {
        // Clamp the value.
        percent = Math.min(100, Math.max(percent, 0));
        mProgressAngleDegrees = (int) ((360f / 100) * percent);

        // We hide the progress once we drew the 100% state once.
        if (percent < 100)
        {
            mVisible = true;
        }
        mParentView.post(mInvalidateParentViewRunnable);
    }

    /**
     * Draw the current progress (if < 100%) centered at the given location.
     */
    public void onDraw(Canvas canvas, int centerX, int centerY)
    {
        if (!mVisible)
        {
            return;
        }
        mArcBounds = new RectF(centerX - mProgressRadius, centerY - mProgressRadius, centerX
                + mProgressRadius,
                centerY + mProgressRadius);

        canvas.drawCircle(centerX, centerY, mProgressRadius, mProgressBasePaint);
        canvas.drawArc(mArcBounds, -90, mProgressAngleDegrees, false, mProgressPaint);

        // After we reached 100%, we paint the progress renderer for another x
        // milliseconds until we hide it.
        if (mProgressAngleDegrees == 360)
        {
            mVisible = false;
            mParentView.postDelayed(
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mParentView.invalidate();
                        }
                    }, SHOW_PROGRESS_X_ADDITIONAL_MS);
        }
    }

    /**
     * @return Whether the progress renderer is visible.
     */
    public boolean isVisible()
    {
        return mVisible;
    }

    private static Paint createProgressPaint(int width, float alpha)
    {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        // 20% alpha.
        paint.setColor(Color.argb((int) (alpha * 255), 255, 255, 255));
        paint.setStrokeWidth(width);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }
}
