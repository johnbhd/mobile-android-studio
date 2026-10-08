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
    private static final float MIN_RETREAT_Y = -0.35f;

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
    private int fireSequenceShotCount;
    private int fireSequenceShotIndex;
    private float rotationDegrees;
    private float rotationSpeedDegreesPerSecond;
    private boolean heavyBomberMovementEnabled;
    private boolean heavyBomberManeuvering;
    private float heavyBomberDecisionTimerSeconds;
    private float heavyBomberStateTimerSeconds;
    private float heavyBomberRetreatRemainingPixels;
    private float heavyBomberRetreatSpeedPixelsPerSecond;
    private float heavyBomberStrafeSpeedPixelsPerSecond;
    private int heavyBomberStrafeDirection;

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
        if (heavyBomberManeuvering) {
            updateHeavyBomberManeuver(deltaSeconds);
        } else {
            updateStandardMovement(deltaSeconds);
        }

        if (rotationSpeedDegreesPerSecond != 0f) {
            rotationDegrees = (rotationDegrees
                    + rotationSpeedDegreesPerSecond * deltaSeconds) % 360f;
            if (rotationDegrees < 0f) {
                rotationDegrees += 360f;
            }
        }
    }

    private void updateStandardMovement(float deltaSeconds) {
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

    private void updateHeavyBomberManeuver(float deltaSeconds) {
        movementTimeSeconds += deltaSeconds;
        y = Math.max(
                height * MIN_RETREAT_Y,
                y - heavyBomberRetreatSpeedPixelsPerSecond * deltaSeconds
        );
        x += heavyBomberStrafeDirection
                * heavyBomberStrafeSpeedPixelsPerSecond
                * deltaSeconds;
        heavyBomberStateTimerSeconds -= deltaSeconds;
        heavyBomberRetreatRemainingPixels -=
                heavyBomberRetreatSpeedPixelsPerSecond * deltaSeconds;

        if (heavyBomberStateTimerSeconds <= 0f
                || heavyBomberRetreatRemainingPixels <= 0f) {
            heavyBomberManeuvering = false;
            heavyBomberStateTimerSeconds = 0f;
            heavyBomberRetreatRemainingPixels = 0f;
        }

        clampHorizontalPosition();
    }

    private void clampHorizontalPosition() {
        float maximumX = Math.max(0f, screenWidth - width);
        if (x < 0f) {
            x = 0f;
            if (heavyBomberManeuvering) {
                heavyBomberStrafeDirection = 1;
            } else {
                horizontalSpeedPixelsPerSecond = Math.abs(horizontalSpeedPixelsPerSecond);
            }
        } else if (x > maximumX) {
            x = maximumX;
            if (heavyBomberManeuvering) {
                heavyBomberStrafeDirection = -1;
            } else {
                horizontalSpeedPixelsPerSecond = -Math.abs(horizontalSpeedPixelsPerSecond);
            }
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
        fireSequenceShotCount = Math.max(1, shotCount);
        fireSequenceShotIndex = 0;
        pendingFireShots = fireSequenceShotCount;
    }

    public boolean hasPendingFireShots() {
        return pendingFireShots > 0;
    }

    public void consumeFireShot() {
        if (pendingFireShots > 0) {
            pendingFireShots--;
            fireSequenceShotIndex++;
        }
    }

    public int getFireSequenceShotCount() {
        return fireSequenceShotCount;
    }

    public int getFireSequenceShotIndex() {
        return fireSequenceShotIndex;
    }

    public void setRotationSpeedDegreesPerSecond(float rotationSpeedDegreesPerSecond) {
        this.rotationSpeedDegreesPerSecond = rotationSpeedDegreesPerSecond;
    }

    public void configureHeavyBomber(float initialDecisionDelaySeconds) {
        heavyBomberMovementEnabled = true;
        heavyBomberManeuvering = false;
        heavyBomberDecisionTimerSeconds = Math.max(0f, initialDecisionDelaySeconds);
        heavyBomberStateTimerSeconds = 0f;
        heavyBomberRetreatRemainingPixels = 0f;
        heavyBomberStrafeDirection = 0;
    }

    public void updateHeavyBomberDecisionTimer(float deltaSeconds) {
        if (heavyBomberMovementEnabled && !heavyBomberManeuvering) {
            heavyBomberDecisionTimerSeconds = Math.max(
                    0f,
                    heavyBomberDecisionTimerSeconds - deltaSeconds
            );
        }
    }

    public boolean isHeavyBomberDecisionReady() {
        return heavyBomberMovementEnabled
                && !heavyBomberManeuvering
                && heavyBomberDecisionTimerSeconds <= 0f;
    }

    public boolean isHeavyBomberManeuvering() {
        return heavyBomberManeuvering;
    }

    public void resetHeavyBomberDecisionTimer(float delaySeconds) {
        heavyBomberDecisionTimerSeconds = Math.max(0f, delaySeconds);
    }

    public void beginHeavyBomberManeuver(
            int strafeDirection,
            float stateDurationSeconds,
            float retreatDistancePixels,
            float retreatSpeedPixelsPerSecond,
            float strafeSpeedPixelsPerSecond
    ) {
        if (!heavyBomberMovementEnabled) {
            return;
        }

        heavyBomberManeuvering = true;
        heavyBomberStateTimerSeconds = Math.max(0f, stateDurationSeconds);
        heavyBomberRetreatRemainingPixels = Math.max(0f, retreatDistancePixels);
        heavyBomberRetreatSpeedPixelsPerSecond = Math.max(
                0f,
                retreatSpeedPixelsPerSecond
        );
        heavyBomberStrafeSpeedPixelsPerSecond = Math.max(
                0f,
                strafeSpeedPixelsPerSecond
        );
        heavyBomberStrafeDirection = strafeDirection < 0 ? -1 : 1;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (rotationSpeedDegreesPerSecond == 0f) {
            canvas.drawBitmap(bitmap, x, y, paint);
            return;
        }

        int saveCount = canvas.save();
        canvas.rotate(rotationDegrees, getCenterX(), y + height / 2f);
        canvas.drawBitmap(bitmap, x, y, paint);
        canvas.restoreToCount(saveCount);
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
