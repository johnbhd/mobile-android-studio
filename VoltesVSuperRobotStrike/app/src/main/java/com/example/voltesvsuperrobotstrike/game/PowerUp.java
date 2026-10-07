package com.example.voltesvsuperrobotstrike.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public final class PowerUp {

    private final PowerUpType type;
    private final Bitmap bitmap;
    private final float width;
    private final float height;
    private final float verticalSpeedPixelsPerSecond;
    private float x;
    private float y;

    public PowerUp(
            PowerUpType type,
            Bitmap bitmap,
            float x,
            float y,
            float verticalSpeedPixelsPerSecond
    ) {
        this.type = type;
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        this.verticalSpeedPixelsPerSecond = verticalSpeedPixelsPerSecond;
    }

    public void update(float deltaSeconds) {
        y += verticalSpeedPixelsPerSecond * deltaSeconds;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(bitmap, x, y, paint);
    }

    public boolean isOffScreen(float screenHeight) {
        return y >= screenHeight;
    }

    public PowerUpType getType() {
        return type;
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

    public float getCenterX() {
        return x + width / 2f;
    }

    public float getCenterY() {
        return y + height / 2f;
    }
}
