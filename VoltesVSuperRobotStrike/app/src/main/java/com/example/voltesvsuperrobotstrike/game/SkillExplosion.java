package com.example.voltesvsuperrobotstrike.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/** Short-lived bitmap or Canvas explosion effect. */
public final class SkillExplosion {

    private final Bitmap bitmap;
    private final float centerX;
    private final float centerY;
    private final float radius;
    private final float durationSeconds;
    private final float bitmapScale;
    private final RectF bitmapDestinationRect = new RectF();
    private float elapsedSeconds;

    public SkillExplosion(
            Bitmap bitmap,
            float centerX,
            float centerY,
            float radius,
            float durationSeconds
    ) {
        this(
                bitmap,
                centerX,
                centerY,
                radius,
                durationSeconds,
                1f
        );
    }

    public SkillExplosion(
            Bitmap bitmap,
            float centerX,
            float centerY,
            float radius,
            float durationSeconds,
            float bitmapScale
    ) {
        this.bitmap = bitmap;
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = Math.max(1f, radius);
        this.durationSeconds = Math.max(0.01f, durationSeconds);
        this.bitmapScale = Math.max(0.01f, bitmapScale);

        if (bitmap != null) {
            float bitmapWidth = bitmap.getWidth() * this.bitmapScale;
            float bitmapHeight = bitmap.getHeight() * this.bitmapScale;
            bitmapDestinationRect.set(
                    centerX - bitmapWidth / 2f,
                    centerY - bitmapHeight / 2f,
                    centerX + bitmapWidth / 2f,
                    centerY + bitmapHeight / 2f
            );
        }
    }

    public void update(float deltaSeconds) {
        elapsedSeconds = Math.min(
                durationSeconds,
                elapsedSeconds + Math.max(0f, deltaSeconds)
        );
    }

    public void draw(Canvas canvas, Paint paint) {
        float progress = Math.min(1f, elapsedSeconds / durationSeconds);
        float currentRadius = radius * (0.45f + progress * 0.55f);
        int alpha = Math.round(255f * (1f - progress));

        int previousColor = paint.getColor();
        int previousAlpha = paint.getAlpha();
        Paint.Style previousStyle = paint.getStyle();
        float previousStrokeWidth = paint.getStrokeWidth();

        if (bitmap != null && !bitmap.isRecycled()) {
            paint.setAlpha(alpha);
            canvas.drawBitmap(
                    bitmap,
                    null,
                    bitmapDestinationRect,
                    paint
            );
            paint.setColor(previousColor);
            paint.setAlpha(previousAlpha);
            paint.setStyle(previousStyle);
            paint.setStrokeWidth(previousStrokeWidth);
            return;
        }

        paint.setColor(0xFFFFD447);
        paint.setAlpha(alpha);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerX, centerY, currentRadius * 0.62f, paint);
        paint.setColor(0xFFFF7A30);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Math.max(2f, radius * 0.08f));
        canvas.drawCircle(centerX, centerY, currentRadius, paint);

        paint.setColor(previousColor);
        paint.setAlpha(previousAlpha);
        paint.setStyle(previousStyle);
        paint.setStrokeWidth(previousStrokeWidth);
    }

    public boolean isFinished() {
        return elapsedSeconds >= durationSeconds;
    }
}
