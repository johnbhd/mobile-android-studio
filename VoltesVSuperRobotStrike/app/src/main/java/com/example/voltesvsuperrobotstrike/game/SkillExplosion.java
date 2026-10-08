package com.example.voltesvsuperrobotstrike.game;

import android.graphics.Canvas;
import android.graphics.Paint;

/** Short-lived Canvas fallback for the Bomber skill explosion. */
public final class SkillExplosion {

    private final float centerX;
    private final float centerY;
    private final float radius;
    private final float durationSeconds;
    private float elapsedSeconds;

    public SkillExplosion(
            float centerX,
            float centerY,
            float radius,
            float durationSeconds
    ) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = Math.max(1f, radius);
        this.durationSeconds = Math.max(0.01f, durationSeconds);
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
