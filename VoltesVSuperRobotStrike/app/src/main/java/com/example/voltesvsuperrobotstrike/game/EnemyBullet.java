package com.example.voltesvsuperrobotstrike.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public final class EnemyBullet {

    public static final int VISUAL_SCOUT = 0;
    public static final int VISUAL_HORNET = 1;
    public static final int VISUAL_HEAVY = 2;
    public static final int VISUAL_CRAB = 3;
    public static final int VISUAL_ELITE = 4;
    public static final int VISUAL_SCOUT_DRONE_2 = 5;
    public static final int VISUAL_BOAZANIAN = 6;

    private static final int SCOUT_COLOR = 0xFFFF6878;
    private static final int HORNET_COLOR = 0xFFFFB347;
    private static final int HEAVY_COLOR = 0xFFFFD447;
    private static final int CRAB_COLOR = 0xFFB58CFF;
    private static final int ELITE_COLOR = 0xFFFF8AD8;
    private static final int SCOUT_DRONE_2_COLOR = 0xFF72E6FF;
    private static final int BOAZANIAN_COLOR = 0xFFB9FF70;

    private final Bitmap bitmap;
    private final float width;
    private final float height;
    private final float verticalSpeedPixelsPerSecond;
    private final int visualType;

    private float x;
    private float y;

    public EnemyBullet(
            Bitmap bitmap,
            float centerX,
            float y,
            float width,
            float height,
            float verticalSpeedPixelsPerSecond,
            int visualType
    ) {
        this.bitmap = bitmap;
        this.width = width;
        this.height = height;
        this.verticalSpeedPixelsPerSecond = verticalSpeedPixelsPerSecond;
        this.visualType = visualType;
        x = centerX - width / 2f;
        this.y = y;
    }

    public void update(float deltaSeconds) {
        y += verticalSpeedPixelsPerSecond * deltaSeconds;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (bitmap != null && !bitmap.isRecycled()) {
            canvas.drawBitmap(bitmap, x, y, paint);
            return;
        }

        paint.setColor(getColor());

        float centerX = x + width / 2f;
        float centerY = y + height / 2f;
        if (visualType == VISUAL_HEAVY
                || visualType == VISUAL_CRAB
                || visualType == VISUAL_BOAZANIAN) {
            float radius = Math.min(width, height) / 2f;
            canvas.drawCircle(centerX, centerY, radius, paint);
            return;
        }

        float cornerRadius = Math.min(width, height) / 2f;
        canvas.drawRoundRect(
                x,
                y,
                x + width,
                y + height,
                cornerRadius,
                cornerRadius,
                paint
        );
    }

    public boolean isOffScreen(float screenHeight) {
        return y >= screenHeight;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    private int getColor() {
        switch (visualType) {
            case VISUAL_HORNET:
                return HORNET_COLOR;
            case VISUAL_HEAVY:
                return HEAVY_COLOR;
            case VISUAL_CRAB:
                return CRAB_COLOR;
            case VISUAL_ELITE:
                return ELITE_COLOR;
            case VISUAL_SCOUT_DRONE_2:
                return SCOUT_DRONE_2_COLOR;
            case VISUAL_BOAZANIAN:
                return BOAZANIAN_COLOR;
            case VISUAL_SCOUT:
            default:
                return SCOUT_COLOR;
        }
    }
}
