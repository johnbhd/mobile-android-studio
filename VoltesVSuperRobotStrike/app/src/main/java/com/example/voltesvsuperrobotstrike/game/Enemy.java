package com.example.voltesvsuperrobotstrike.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public final class Enemy {

    private final Bitmap bitmap;
    private final int type;
    private final float width;
    private final float height;
    private final float verticalSpeedPixelsPerSecond;
    private float horizontalSpeedPixelsPerSecond;
    private final float screenWidth;
    private float fireCooldownSeconds;

    private float x;
    private float y;

    public Enemy(
            Bitmap bitmap,
            int type,
            float x,
            float y,
            float verticalSpeedPixelsPerSecond,
            float horizontalSpeedPixelsPerSecond,
            float screenWidth
    ) {
        this.bitmap = bitmap;
        this.type = type;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        this.x = x;
        this.y = y;
        this.verticalSpeedPixelsPerSecond = verticalSpeedPixelsPerSecond;
        this.horizontalSpeedPixelsPerSecond = horizontalSpeedPixelsPerSecond;
        this.screenWidth = screenWidth;
    }

    public void update(float deltaSeconds) {
        x += horizontalSpeedPixelsPerSecond * deltaSeconds;
        y += verticalSpeedPixelsPerSecond * deltaSeconds;

        float maximumX = Math.max(0f, screenWidth - width);
        if (x < 0f) {
            x = 0f;
            horizontalSpeedPixelsPerSecond = Math.abs(horizontalSpeedPixelsPerSecond);
        } else if (x > maximumX) {
            x = maximumX;
            horizontalSpeedPixelsPerSecond = -Math.abs(horizontalSpeedPixelsPerSecond);
        }
    }

    public void updateFireCooldown(float deltaSeconds) {
        fireCooldownSeconds -= deltaSeconds;
    }

    public boolean isReadyToFire() {
        return fireCooldownSeconds <= 0f;
    }

    public void resetFireCooldown(float cooldownSeconds) {
        fireCooldownSeconds = Math.max(0f, cooldownSeconds);
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(bitmap, x, y, paint);
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

    public float getCenterX() {
        return x + width / 2f;
    }

    public float getBottom() {
        return y + height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getType() {
        return type;
    }
}
