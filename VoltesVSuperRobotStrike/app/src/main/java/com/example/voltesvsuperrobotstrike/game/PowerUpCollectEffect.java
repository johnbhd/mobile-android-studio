package com.example.voltesvsuperrobotstrike.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public final class PowerUpCollectEffect {

    private final Bitmap spriteSheet;
    private final int frameCount;
    private final int frameWidth;
    private final int frameHeight;
    private final float centerX;
    private final float centerY;
    private final float width;
    private final float height;
    private final float frameDurationSeconds;
    private final float totalDurationSeconds;
    private final Rect sourceRect = new Rect();
    private final RectF destinationRect = new RectF();
    private float elapsedSeconds;

    public PowerUpCollectEffect(
            Bitmap spriteSheet,
            int frameCount,
            float centerX,
            float centerY,
            float width,
            float height,
            float frameDurationSeconds
    ) {
        this.spriteSheet = spriteSheet;
        this.frameCount = Math.max(1, frameCount);
        frameWidth = Math.max(1, spriteSheet.getWidth() / this.frameCount);
        frameHeight = spriteSheet.getHeight();
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width;
        this.height = height;
        this.frameDurationSeconds = Math.max(0.001f, frameDurationSeconds);
        totalDurationSeconds = this.frameDurationSeconds * this.frameCount;
    }

    public void update(float deltaSeconds) {
        elapsedSeconds = Math.min(
                totalDurationSeconds,
                elapsedSeconds + Math.max(0f, deltaSeconds)
        );
    }

    public void draw(Canvas canvas, Paint paint) {
        int frameIndex = Math.min(
                frameCount - 1,
                (int) (elapsedSeconds / frameDurationSeconds)
        );
        sourceRect.set(
                frameIndex * frameWidth,
                0,
                Math.min(spriteSheet.getWidth(), (frameIndex + 1) * frameWidth),
                frameHeight
        );
        destinationRect.set(
                centerX - width / 2f,
                centerY - height / 2f,
                centerX + width / 2f,
                centerY + height / 2f
        );
        canvas.drawBitmap(spriteSheet, sourceRect, destinationRect, paint);
    }

    public boolean isFinished() {
        return elapsedSeconds >= totalDurationSeconds;
    }
}
