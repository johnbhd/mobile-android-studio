package com.example.voltesvsuperrobotstrike.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/** A temporary Bomber skill projectile owned and updated by GameView. */
public final class BombardmentBomb {

    private final Bitmap bitmap;
    private final float width;
    private final float height;
    private final float speedPixelsPerSecond;

    private float x;
    private float y;

    public BombardmentBomb(
            Bitmap bitmap,
            float centerX,
            float top,
            float width,
            float height,
            float speedPixelsPerSecond
    ) {
        this.bitmap = bitmap;
        this.width = Math.max(1f, width);
        this.height = Math.max(1f, height);
        this.speedPixelsPerSecond = Math.max(0f, speedPixelsPerSecond);
        x = centerX - this.width / 2f;
        y = top;
    }

    public void update(float deltaSeconds) {
        y -= speedPixelsPerSecond * deltaSeconds;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (bitmap != null && !bitmap.isRecycled()) {
            canvas.drawBitmap(bitmap, x, y, paint);
            return;
        }

        int previousColor = paint.getColor();
        Paint.Style previousStyle = paint.getStyle();
        paint.setColor(0xFFFF7A30);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getCenterX(), y + height * 0.55f, width * 0.38f, paint);
        paint.setColor(0xFFFFD447);
        canvas.drawRect(
                getCenterX() - width * 0.12f,
                y,
                getCenterX() + width * 0.12f,
                y + height * 0.40f,
                paint
        );
        paint.setColor(previousColor);
        paint.setStyle(previousStyle);
    }

    public boolean isPastDetonationThreshold(float thresholdY) {
        return y <= thresholdY;
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
