package com.example.voltesvsuperrobotstrike.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public final class Player {

    private static final float TARGET_WIDTH_FRACTION = 0.18f;
    private static final float MIN_WIDTH_DP = 64f;
    private static final float MAX_WIDTH_DP = 96f;
    private static final float SIDE_MARGIN_DP = 6f;
    private static final float BOTTOM_MARGIN_DP = 32f;

    private final Resources resources;
    private final int drawableResourceId;
    private final Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final float density;
    private final float sideMarginPixels;
    private final float bottomMarginPixels;

    private Bitmap bitmap;
    private int screenWidth;
    private int screenHeight;
    private int preparedWidth;
    private int preparedHeight;
    private float centerX;
    private float y;
    private float width;
    private float height;
    private volatile float targetCenterX;
    private volatile int bottomSystemInsetPixels;
    private boolean prepared;

    public Player(Resources resources, int drawableResourceId, float density) {
        this.resources = resources;
        this.drawableResourceId = drawableResourceId;
        this.density = density;
        sideMarginPixels = SIDE_MARGIN_DP * density;
        bottomMarginPixels = BOTTOM_MARGIN_DP * density;
    }

    public synchronized void prepare(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        if (prepared && preparedWidth == width && preparedHeight == height) {
            return;
        }

        boolean preserveHorizontalPosition = prepared && screenWidth > 0;
        float previousCenterRatio = preserveHorizontalPosition
                ? centerX / screenWidth
                : 0.5f;

        releaseBitmap();
        prepared = false;
        screenWidth = width;
        screenHeight = height;
        preparedWidth = width;
        preparedHeight = height;

        Bitmap sourceBitmap = BitmapFactory.decodeResource(resources, drawableResourceId);

        if (sourceBitmap == null || sourceBitmap.getWidth() <= 0 || sourceBitmap.getHeight() <= 0) {
            resetPreparationState();
            return;
        }

        int targetWidth = calculateTargetWidth(width);
        int targetHeight = Math.max(
                1,
                Math.round(sourceBitmap.getHeight() * (targetWidth / (float) sourceBitmap.getWidth()))
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

        bitmap = scaledBitmap;
        this.width = scaledBitmap.getWidth();
        this.height = scaledBitmap.getHeight();
        centerX = clampCenterX(previousCenterRatio * width);
        targetCenterX = centerX;
        y = calculateY();
        prepared = true;
    }

    public synchronized void update() {
        if (!prepared) {
            return;
        }

        float halfWidth = width / 2f;
        float minimumCenterX = halfWidth + sideMarginPixels;
        float maximumCenterX = screenWidth - halfWidth - sideMarginPixels;

        if (maximumCenterX < minimumCenterX) {
            centerX = screenWidth / 2f;
        } else {
            centerX = clamp(targetCenterX, minimumCenterX, maximumCenterX);
        }

        targetCenterX = centerX;
        y = calculateY();
    }

    public synchronized void draw(Canvas canvas) {
        if (!prepared || bitmap == null) {
            return;
        }

        canvas.drawBitmap(bitmap, centerX - width / 2f, y, bitmapPaint);
    }

    public void setTargetCenterX(float targetCenterX) {
        this.targetCenterX = targetCenterX;
    }

    public void setBottomSystemInsetPixels(int bottomSystemInsetPixels) {
        this.bottomSystemInsetPixels = Math.max(0, bottomSystemInsetPixels);
    }

    public synchronized float getCenterX() {
        return centerX;
    }

    public synchronized boolean isPrepared() {
        return prepared;
    }

    public int getDrawableResourceId() {
        return drawableResourceId;
    }

    public synchronized void release() {
        releaseBitmap();
        resetPreparationState();
    }

    private int calculateTargetWidth(int screenWidth) {
        int minimumWidth = Math.round(MIN_WIDTH_DP * density);
        int maximumWidth = Math.round(MAX_WIDTH_DP * density);
        int targetWidth = Math.round(screenWidth * TARGET_WIDTH_FRACTION);

        targetWidth = Math.max(minimumWidth, targetWidth);
        targetWidth = Math.min(maximumWidth, targetWidth);
        return Math.max(1, Math.min(screenWidth, targetWidth));
    }

    private float calculateY() {
        return Math.max(
                0f,
                screenHeight - height - bottomMarginPixels - bottomSystemInsetPixels
        );
    }

    private float clamp(float value, float minimum, float maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private float clampCenterX(float requestedCenterX) {
        float halfWidth = width / 2f;
        float minimumCenterX = halfWidth + sideMarginPixels;
        float maximumCenterX = screenWidth - halfWidth - sideMarginPixels;

        if (maximumCenterX < minimumCenterX) {
            return screenWidth / 2f;
        }

        return clamp(requestedCenterX, minimumCenterX, maximumCenterX);
    }

    private void releaseBitmap() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        bitmap = null;
    }

    private void resetPreparationState() {
        prepared = false;
        screenWidth = 0;
        screenHeight = 0;
        preparedWidth = 0;
        preparedHeight = 0;
        centerX = 0f;
        targetCenterX = 0f;
        y = 0f;
        width = 0f;
        height = 0f;
    }
}
