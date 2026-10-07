package com.example.voltesvsuperrobotstrike.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public final class Enemy {

    public static final int MOVEMENT_STRAIGHT = 0;
    public static final int MOVEMENT_DRIFT = 1;
    public static final int MOVEMENT_ZIGZAG = 2;
    public static final int MOVEMENT_SWAY = 3;
    public static final int MOVEMENT_PAUSE_DROP = 4;

    private static final float PAUSE_DROP_CYCLE_SECONDS = 2.8f;
    private static final float PAUSE_DROP_SECONDS = 0.45f;
    private static final float PAUSE_DROP_SPEED_MULTIPLIER = 0.25f;

    private final Bitmap bitmap;
    private final int type;
    private final float width;
    private final float height;
    private final float verticalSpeedPixelsPerSecond;
    private float horizontalSpeedPixelsPerSecond;
    private final float screenWidth;
    private final int movementPattern;
    private final float movementAmplitudePixels;
    private final float movementFrequencyRadiansPerSecond;
    private final float movementPhaseRadians;
    private final float movementAnchorX;
    private float movementTimeSeconds;
    private float fireCooldownSeconds;
    private int pendingFireShots;

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
        this(
                bitmap,
                type,
                x,
                y,
                verticalSpeedPixelsPerSecond,
                horizontalSpeedPixelsPerSecond,
                screenWidth,
                horizontalSpeedPixelsPerSecond == 0f
                        ? MOVEMENT_STRAIGHT
                        : MOVEMENT_DRIFT,
                0f,
                0f,
                0f
        );
    }

    public Enemy(
            Bitmap bitmap,
            int type,
            float x,
            float y,
            float verticalSpeedPixelsPerSecond,
            float horizontalSpeedPixelsPerSecond,
            float screenWidth,
            int movementPattern,
            float movementAmplitudePixels,
            float movementFrequencyRadiansPerSecond,
            float movementPhaseRadians
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
        this.movementPattern = movementPattern;
        this.movementAmplitudePixels = movementAmplitudePixels;
        this.movementFrequencyRadiansPerSecond = movementFrequencyRadiansPerSecond;
        this.movementPhaseRadians = movementPhaseRadians;
        movementAnchorX = x;
    }

    public void update(float deltaSeconds) {
        float verticalSpeedMultiplier = 1f;
        if (movementPattern == MOVEMENT_PAUSE_DROP) {
            float cyclePosition = movementTimeSeconds % PAUSE_DROP_CYCLE_SECONDS;
            if (cyclePosition < PAUSE_DROP_SECONDS) {
                verticalSpeedMultiplier = PAUSE_DROP_SPEED_MULTIPLIER;
            }
        }

        movementTimeSeconds += deltaSeconds;
        y += verticalSpeedPixelsPerSecond * verticalSpeedMultiplier * deltaSeconds;

        if (movementPattern == MOVEMENT_ZIGZAG
                || movementPattern == MOVEMENT_SWAY) {
            x = movementAnchorX + (float) Math.sin(
                    movementTimeSeconds * movementFrequencyRadiansPerSecond
                            + movementPhaseRadians
            ) * movementAmplitudePixels;
        } else {
            x += horizontalSpeedPixelsPerSecond * deltaSeconds;
        }

        clampHorizontalPosition();
    }

    private void clampHorizontalPosition() {
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

    public void beginFireSequence(int shotCount) {
        pendingFireShots = Math.max(1, shotCount);
    }

    public boolean hasPendingFireShots() {
        return pendingFireShots > 0;
    }

    public void consumeFireShot() {
        if (pendingFireShots > 0) {
            pendingFireShots--;
        }
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

    public int getMovementPattern() {
        return movementPattern;
    }
}
