package com.example.voltesvsuperrobotstrike.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public final class Bullet {

    private final Bitmap bitmap;
    private final float speedPixelsPerSecond;
    private final float width;
    private final float height;

    private float x;
    private float y;

    public Bullet(
            Bitmap bitmap,
            float x,
            float y,
            float speedPixelsPerSecond
    ) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.speedPixelsPerSecond = speedPixelsPerSecond;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
    }

    public void update(float deltaSeconds) {
        y -= speedPixelsPerSecond * deltaSeconds;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(bitmap, x, y, paint);
    }

    public boolean isOffScreen() {
        return y + height < 0f;
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
}
