package com.example.voltesvsuperrobotstrike.game;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.example.voltesvsuperrobotstrike.R;

public class ScrollingBackground {

    private static final String TAG = "ScrollingBackground";
    private static final int SEGMENT_COUNT = 5;
    private static final float SCROLL_SPEED_DP_PER_SECOND = 120f;

    private static final int[] LOOP_RESOURCE_IDS = {
            R.drawable.map_falcon_a,
            R.drawable.map_falcon_b,
            R.drawable.map_falcon_c,
            R.drawable.map_falcon_d
    };

    private final Resources resources;
    private final Paint bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
    private final Segment[] segments = new Segment[SEGMENT_COUNT];
    private final Bitmap[] loopBitmaps = new Bitmap[LOOP_RESOURCE_IDS.length];
    private final Object backgroundLock = new Object();
    private final float scrollSpeedPixelsPerSecond;

    private Bitmap startBitmap;
    private int screenWidth;
    private int screenHeight;
    private int nextLoopIndex;
    private boolean prepared;

    public ScrollingBackground(Context context) {
        resources = context.getApplicationContext().getResources();
        scrollSpeedPixelsPerSecond = SCROLL_SPEED_DP_PER_SECOND
                * resources.getDisplayMetrics().density;

        for (int index = 0; index < segments.length; index++) {
            segments[index] = new Segment();
        }
    }

    public void prepare(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        synchronized (backgroundLock) {
            if (prepared && screenWidth == width && screenHeight == height) {
                return;
            }

            releaseBitmaps();
            screenWidth = width;
            screenHeight = height;

            startBitmap = loadScaledBitmap(R.drawable.map_falcon_start, width);

            for (int index = 0; index < LOOP_RESOURCE_IDS.length; index++) {
                loopBitmaps[index] = loadScaledBitmap(LOOP_RESOURCE_IDS[index], width);
            }

            if (!hasAllBitmaps()) {
                Log.e(TAG, "Falcon map preparation failed; using the fallback background.");
                releaseBitmaps();
                prepared = false;
                return;
            }

            resetSegmentPositions();
            prepared = true;
        }
    }

    public void update(float deltaSeconds) {
        if (deltaSeconds <= 0f) {
            return;
        }

        synchronized (backgroundLock) {
            if (!prepared) {
                return;
            }

            float distance = scrollSpeedPixelsPerSecond * deltaSeconds;

            for (Segment segment : segments) {
                segment.y += distance;
            }

            recycleSegmentsBelowScreen();
        }
    }

    public void draw(Canvas canvas) {
        synchronized (backgroundLock) {
            if (!prepared) {
                return;
            }

            for (Segment segment : segments) {
                float segmentTop = segment.y;
                float segmentBottom = segmentTop + segment.bitmap.getHeight();
                if (segmentBottom <= 0f || segmentTop >= screenHeight) {
                    continue;
                }

                canvas.drawBitmap(segment.bitmap, 0f, segment.y, bitmapPaint);
            }
        }
    }

    public void release() {
        synchronized (backgroundLock) {
            releaseBitmaps();
            prepared = false;
            screenWidth = 0;
            screenHeight = 0;
            nextLoopIndex = 0;
        }
    }

    private Bitmap loadScaledBitmap(int resourceId, int targetWidth) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap sourceBitmap = BitmapFactory.decodeResource(resources, resourceId, options);

        if (sourceBitmap == null || sourceBitmap.getWidth() <= 0) {
            return null;
        }

        int targetHeight = Math.round(
                sourceBitmap.getHeight() * (targetWidth / (float) sourceBitmap.getWidth())
        );
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                sourceBitmap,
                targetWidth,
                targetHeight,
                true
        );

        if (scaledBitmap != sourceBitmap) {
            sourceBitmap.recycle();
        }

        return scaledBitmap;
    }

    private boolean hasAllBitmaps() {
        if (startBitmap == null) {
            return false;
        }

        for (Bitmap bitmap : loopBitmaps) {
            if (bitmap == null) {
                return false;
            }
        }

        return true;
    }

    private void resetSegmentPositions() {
        nextLoopIndex = 0;

        segments[0].bitmap = startBitmap;
        segments[0].y = screenHeight - startBitmap.getHeight();

        for (int index = 1; index < segments.length; index++) {
            segments[index].bitmap = loopBitmaps[index - 1];
            segments[index].y = segments[index - 1].y - segments[index].bitmap.getHeight();
        }
    }

    private void recycleSegmentsBelowScreen() {
        for (Segment segment : segments) {
            if (segment.y < screenHeight) {
                continue;
            }

            Segment topmostSegment = findTopmostSegment(segment);
            segment.bitmap = loopBitmaps[nextLoopIndex];
            segment.y = topmostSegment.y - segment.bitmap.getHeight();
            nextLoopIndex = (nextLoopIndex + 1) % loopBitmaps.length;
        }
    }

    private Segment findTopmostSegment(Segment excludedSegment) {
        Segment topmostSegment = null;

        for (Segment segment : segments) {
            if (segment == excludedSegment) {
                continue;
            }

            if (topmostSegment == null || segment.y < topmostSegment.y) {
                topmostSegment = segment;
            }
        }

        return topmostSegment;
    }

    private void releaseBitmaps() {
        if (startBitmap != null && !startBitmap.isRecycled()) {
            startBitmap.recycle();
        }
        startBitmap = null;

        for (int index = 0; index < loopBitmaps.length; index++) {
            Bitmap bitmap = loopBitmaps[index];

            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }

            loopBitmaps[index] = null;
        }

        for (Segment segment : segments) {
            segment.bitmap = null;
            segment.y = 0f;
        }
    }

    private static class Segment {

        private Bitmap bitmap;
        private float y;
    }
}
