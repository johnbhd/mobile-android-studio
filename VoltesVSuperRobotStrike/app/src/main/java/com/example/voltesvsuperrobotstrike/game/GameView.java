package com.example.voltesvsuperrobotstrike.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.voltesvsuperrobotstrike.R;

import java.util.ArrayList;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final float MAX_DELTA_SECONDS = 0.1f;
    private static final float AUTO_FIRE_INTERVAL_SECONDS = 0.35f;
    private static final float PLAYER_BULLET_SPEED_DP_PER_SECOND = 700f;
    private static final float BULLET_PLAYER_OVERLAP_DP = 2f;
    private static final long TARGET_FRAME_DURATION_NANOS = 1_000_000_000L / 60L;
    private static final long THREAD_JOIN_TIMEOUT_MILLIS = 500L;

    private final SurfaceHolder surfaceHolder;
    private final Object gameThreadLock = new Object();
    private final Paint infoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint diagnosticPanelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint projectilePaint = new Paint(
            Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG
    );
    private final float density;
    private final float playerBulletSpeedPixelsPerSecond;
    private final float bulletPlayerOverlapPixels;
    private final int backgroundColor;
    private final ScrollingBackground scrollingBackground;
    private final ArrayList<Bullet> playerBullets = new ArrayList<>();
    private volatile Player player;

    private volatile boolean running;
    private volatile boolean surfaceReady;
    private volatile boolean activityResumed;
    private volatile int screenWidth;
    private volatile int screenHeight;
    private volatile int topSystemInsetPixels;
    private volatile int bottomSystemInsetPixels;

    private Thread gameThread;
    private int activePointerId = MotionEvent.INVALID_POINTER_ID;
    private float dragStartTouchX;
    private float dragStartTouchY;
    private float dragStartPlayerCenterX;
    private float dragStartPlayerCenterY;

    private Bitmap projectileBitmap;
    private int preparedProjectileResourceId;
    private float fireCooldownSeconds = AUTO_FIRE_INTERVAL_SECONDS;

    private String selectedMachineId = "volt_crewzer";
    private String selectedDifficultyId = "normal";
    private String diagnosticLine;

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        density = getResources().getDisplayMetrics().density;
        playerBulletSpeedPixelsPerSecond = PLAYER_BULLET_SPEED_DP_PER_SECOND * density;
        bulletPlayerOverlapPixels = BULLET_PLAYER_OVERLAP_DP * density;
        backgroundColor = ContextCompat.getColor(context, R.color.game_background);
        scrollingBackground = new ScrollingBackground(context);
        player = new Player(getResources(), R.drawable.volt_crewzer, density);

        initializePaints();
        updateDiagnosticLines();
        setFocusable(true);
        setClickable(true);
        initializeSystemBarInsets();
    }

    private void initializePaints() {
        int mutedTextColor = ContextCompat.getColor(
                getContext(),
                R.color.game_debug_muted
        );
        int diagnosticPanelColor = ContextCompat.getColor(
                getContext(),
                R.color.game_debug_panel
        );

        infoPaint.setColor(mutedTextColor);
        infoPaint.setTextSize(13f * getResources().getDisplayMetrics().scaledDensity);
        infoPaint.setTextAlign(Paint.Align.LEFT);
        infoPaint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));

        diagnosticPanelPaint.setColor(diagnosticPanelColor);
    }

    public void configureGame(String selectedMachine, String selectedDifficulty) {
        selectedMachineId = isSupportedMachineId(selectedMachine)
                ? selectedMachine
                : "volt_crewzer";
        selectedDifficultyId = selectedDifficulty == null ? "normal" : selectedDifficulty;

        playerBullets.clear();
        fireCooldownSeconds = AUTO_FIRE_INTERVAL_SECONDS;
        releaseProjectileBitmap();

        int drawableResourceId = getMachineDrawableResource(selectedMachineId);
        Player currentPlayer = player;

        if (currentPlayer == null
                || currentPlayer.getDrawableResourceId() != drawableResourceId) {
            Player replacementPlayer = new Player(
                    getResources(),
                    drawableResourceId,
                    density
            );
            replacementPlayer.setBottomSystemInsetPixels(bottomSystemInsetPixels);

            if (screenWidth > 0 && screenHeight > 0) {
                replacementPlayer.prepare(screenWidth, screenHeight);
            }

            player = replacementPlayer;

            if (currentPlayer != null) {
                currentPlayer.release();
            }
        }

        prepareProjectileBitmapIfReady();

        updateDiagnosticLines();
    }

    public void resumeGame() {
        activityResumed = true;
        startGameThreadIfReady();
    }

    public void pauseGame() {
        activityResumed = false;
        stopGameThread();
    }

    public void releaseGame() {
        pauseGame();
        scrollingBackground.release();
        playerBullets.clear();
        releaseProjectileBitmap();

        Player currentPlayer = player;
        if (currentPlayer != null) {
            currentPlayer.release();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceReady = true;
        updateSurfaceDimensions(getWidth(), getHeight());
        startGameThreadIfReady();
    }

    @Override
    public void surfaceChanged(
            SurfaceHolder holder,
            int format,
            int width,
            int height
    ) {
        updateSurfaceDimensions(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceReady = false;
        stopGameThread();
    }

    private void updateSurfaceDimensions(int width, int height) {
        boolean dimensionsChanged = screenWidth != width || screenHeight != height;
        boolean restartGameThread = running;

        if (restartGameThread) {
            stopGameThread();
        }

        screenWidth = width;
        screenHeight = height;

        if (width > 0 && height > 0) {
            scrollingBackground.prepare(width, height);

            Player currentPlayer = player;
            if (currentPlayer != null) {
                currentPlayer.setBottomSystemInsetPixels(bottomSystemInsetPixels);
                currentPlayer.prepare(width, height);
            }

            if (dimensionsChanged) {
                playerBullets.clear();
                fireCooldownSeconds = AUTO_FIRE_INTERVAL_SECONDS;
            }

            prepareProjectileBitmapIfReady();
        }

        if (restartGameThread) {
            startGameThreadIfReady();
        }
    }

    private void startGameThreadIfReady() {
        synchronized (gameThreadLock) {
            if (!activityResumed || !surfaceReady) {
                return;
            }

            if (gameThread != null && gameThread.isAlive()) {
                return;
            }

            running = true;
            gameThread = new Thread(this, "VoltesVGameThread");
            gameThread.start();
        }
    }

    private void stopGameThread() {
        Thread threadToStop;

        synchronized (gameThreadLock) {
            running = false;
            threadToStop = gameThread;
        }

        if (threadToStop == null || threadToStop == Thread.currentThread()) {
            return;
        }

        threadToStop.interrupt();

        try {
            threadToStop.join(THREAD_JOIN_TIMEOUT_MILLIS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }

        synchronized (gameThreadLock) {
            if (gameThread == threadToStop && !threadToStop.isAlive()) {
                gameThread = null;
            }
        }
    }

    @Override
    public void run() {
        long previousFrameTime = System.nanoTime();

        try {
            while (running) {
                long frameStartTime = System.nanoTime();
                float deltaSeconds = (frameStartTime - previousFrameTime) / 1_000_000_000f;
                previousFrameTime = frameStartTime;

                if (deltaSeconds < 0f) {
                    deltaSeconds = 0f;
                } else if (deltaSeconds > MAX_DELTA_SECONDS) {
                    deltaSeconds = MAX_DELTA_SECONDS;
                }

                update(deltaSeconds);
                render();

                if (!paceFrame(frameStartTime)) {
                    break;
                }
            }
        } finally {
            synchronized (gameThreadLock) {
                running = false;

                if (gameThread == Thread.currentThread()) {
                    gameThread = null;
                }
            }
        }
    }

    private void update(float deltaSeconds) {
        scrollingBackground.update(deltaSeconds);

        Player currentPlayer = player;
        if (currentPlayer != null) {
            currentPlayer.update();
        }

        updateAutomaticFire(deltaSeconds);
        updatePlayerBullets(deltaSeconds);
    }

    private void render() {
        if (!surfaceReady || !surfaceHolder.getSurface().isValid()) {
            return;
        }

        Canvas canvas = null;
        boolean canvasLocked = false;

        try {
            canvas = surfaceHolder.lockCanvas();

            if (canvas == null) {
                return;
            }

            canvasLocked = true;
            canvas.drawColor(backgroundColor);

            scrollingBackground.draw(canvas);

            drawPlayerBullets(canvas);

            Player currentPlayer = player;
            if (currentPlayer != null) {
                currentPlayer.draw(canvas);
            }

            drawDiagnostics(canvas);
        } finally {
            if (canvasLocked) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawDiagnostics(Canvas canvas) {
        if (diagnosticLine == null || diagnosticLine.isEmpty()) {
            return;
        }

        float panelLeft = 12f * density;
        float panelTop = topSystemInsetPixels + 8f * density;
        float panelPadding = 8f * density;
        float textLeft = panelLeft + 8f * density;
        float textBaseline = panelTop + infoPaint.getTextSize() + 6f * density;
        float panelWidth = infoPaint.measureText(diagnosticLine) + panelPadding * 2f;
        float panelRight = panelLeft + panelWidth;
        float panelBottom = textBaseline + 6f * density;

        Player currentPlayer = player;
        if (currentPlayer != null && currentPlayer.isPrepared()) {
            float playerLeft = currentPlayer.getCenterX() - currentPlayer.getWidth() / 2f;
            float playerTop = currentPlayer.getCenterY() - currentPlayer.getHeight() / 2f;
            float playerRight = currentPlayer.getCenterX() + currentPlayer.getWidth() / 2f;
            float playerBottom = currentPlayer.getCenterY() + currentPlayer.getHeight() / 2f;

            if (rectanglesOverlap(
                    panelLeft,
                    panelTop,
                    panelRight,
                    panelBottom,
                    playerLeft,
                    playerTop,
                    playerRight,
                    playerBottom
            )) {
                panelLeft = canvas.getWidth() - panelWidth - 12f * density;
                panelRight = panelLeft + panelWidth;

                if (rectanglesOverlap(
                        panelLeft,
                        panelTop,
                        panelRight,
                        panelBottom,
                        playerLeft,
                        playerTop,
                        playerRight,
                        playerBottom
                )) {
                    panelTop = Math.min(
                            playerBottom + 8f * density,
                            canvas.getHeight() - (panelBottom - (topSystemInsetPixels + 8f * density))
                    );
                    panelBottom = panelTop + (textBaseline - (topSystemInsetPixels + 8f * density));
                }
            }
        }

        textLeft = panelLeft + panelPadding;
        textBaseline = panelTop + infoPaint.getTextSize() + 6f * density;
        panelBottom = textBaseline + 6f * density;

        canvas.drawRect(
                panelLeft,
                panelTop,
                panelRight,
                panelBottom,
                diagnosticPanelPaint
        );
        canvas.drawText(diagnosticLine, textLeft, textBaseline, infoPaint);
    }

    private void drawPlayerBullets(Canvas canvas) {
        for (Bullet bullet : playerBullets) {
            bullet.draw(canvas, projectilePaint);
        }
    }

    private void updateAutomaticFire(float deltaSeconds) {
        Player currentPlayer = player;
        if (currentPlayer == null
                || !currentPlayer.isPrepared()
                || projectileBitmap == null) {
            return;
        }

        fireCooldownSeconds -= deltaSeconds;
        if (fireCooldownSeconds > 0f) {
            return;
        }

        firePlayerProjectile();
        fireCooldownSeconds = AUTO_FIRE_INTERVAL_SECONDS;
    }

    private void firePlayerProjectile() {
        Player currentPlayer = player;
        if (currentPlayer == null || projectileBitmap == null) {
            return;
        }

        float playerCenterX = currentPlayer.getCenterX();
        float playerTop = currentPlayer.getCenterY() - currentPlayer.getHeight() / 2f;
        spawnBullet(playerCenterX, playerTop);
    }

    private void spawnBullet(float centerX, float playerTop) {
        float bulletX = centerX - projectileBitmap.getWidth() / 2f;
        float bulletY = playerTop - projectileBitmap.getHeight() + bulletPlayerOverlapPixels;

        playerBullets.add(new Bullet(
                projectileBitmap,
                bulletX,
                bulletY,
                playerBulletSpeedPixelsPerSecond
        ));
    }

    private void updatePlayerBullets(float deltaSeconds) {
        for (int index = playerBullets.size() - 1; index >= 0; index--) {
            Bullet bullet = playerBullets.get(index);
            bullet.update(deltaSeconds);

            if (bullet.isOffScreen()) {
                playerBullets.remove(index);
            }
        }
    }

    private void prepareProjectileBitmapIfReady() {
        Player currentPlayer = player;
        if (currentPlayer == null
                || !currentPlayer.isPrepared()
                || screenWidth <= 0
                || screenHeight <= 0) {
            return;
        }

        int projectileResourceId = getProjectileDrawableResource(selectedMachineId);
        int targetWidth = Math.max(
                1,
                Math.round(currentPlayer.getWidth() * getProjectileWidthRatio(selectedMachineId))
        );

        if (projectileBitmap != null
                && preparedProjectileResourceId == projectileResourceId
                && projectileBitmap.getWidth() == targetWidth) {
            return;
        }

        releaseProjectileBitmap();

        Bitmap sourceBitmap = BitmapFactory.decodeResource(
                getResources(),
                projectileResourceId
        );
        if (sourceBitmap == null
                || sourceBitmap.getWidth() <= 0
                || sourceBitmap.getHeight() <= 0) {
            preparedProjectileResourceId = 0;
            return;
        }

        int targetHeight = Math.max(
                1,
                Math.round(sourceBitmap.getHeight()
                        * (targetWidth / (float) sourceBitmap.getWidth()))
        );
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                sourceBitmap,
                targetWidth,
                targetHeight,
                true
        );

        if (scaledBitmap != sourceBitmap) {
            sourceBitmap.recycle();
        }

        projectileBitmap = scaledBitmap;
        preparedProjectileResourceId = projectileResourceId;
    }

    private void releaseProjectileBitmap() {
        if (projectileBitmap != null && !projectileBitmap.isRecycled()) {
            projectileBitmap.recycle();
        }
        projectileBitmap = null;
        preparedProjectileResourceId = 0;
    }

    private boolean rectanglesOverlap(
            float firstLeft,
            float firstTop,
            float firstRight,
            float firstBottom,
            float secondLeft,
            float secondTop,
            float secondRight,
            float secondBottom
    ) {
        return firstLeft < secondRight
                && firstRight > secondLeft
                && firstTop < secondBottom
                && firstBottom > secondTop;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!running || !surfaceReady) {
            resetTouchState();
            return false;
        }

        Player currentPlayer = player;
        if (currentPlayer == null || !currentPlayer.isPrepared()) {
            resetTouchState();
            return false;
        }

        int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                activePointerId = event.getPointerId(0);
                dragStartTouchX = event.getX();
                dragStartTouchY = event.getY();
                dragStartPlayerCenterX = currentPlayer.getCenterX();
                dragStartPlayerCenterY = currentPlayer.getCenterY();
                return true;

            case MotionEvent.ACTION_MOVE:
                if (activePointerId == MotionEvent.INVALID_POINTER_ID) {
                    return false;
                }

                int pointerIndex = event.findPointerIndex(activePointerId);
                if (pointerIndex < 0) {
                    resetTouchState();
                    return false;
                }

                float dragDeltaX = event.getX(pointerIndex) - dragStartTouchX;
                float dragDeltaY = event.getY(pointerIndex) - dragStartTouchY;
                currentPlayer.setTargetCenter(
                        dragStartPlayerCenterX + dragDeltaX,
                        dragStartPlayerCenterY + dragDeltaY
                );
                return true;

            case MotionEvent.ACTION_POINTER_DOWN:
                return activePointerId != MotionEvent.INVALID_POINTER_ID;

            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerId(event.getActionIndex()) == activePointerId) {
                    resetTouchState();
                }
                return true;

            case MotionEvent.ACTION_UP:
                boolean handledUp = activePointerId != MotionEvent.INVALID_POINTER_ID;
                resetTouchState();
                if (handledUp) {
                    performClick();
                }
                return handledUp;

            case MotionEvent.ACTION_CANCEL:
                resetTouchState();
                return true;

            default:
                return activePointerId != MotionEvent.INVALID_POINTER_ID;
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void initializeSystemBarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(this, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            topSystemInsetPixels = systemBars.top;
            bottomSystemInsetPixels = systemBars.bottom;

            Player currentPlayer = player;
            if (currentPlayer != null) {
                currentPlayer.setBottomSystemInsetPixels(bottomSystemInsetPixels);
            }

            return insets;
        });
        ViewCompat.requestApplyInsets(this);
    }

    private void resetTouchState() {
        activePointerId = MotionEvent.INVALID_POINTER_ID;
        dragStartTouchX = 0f;
        dragStartTouchY = 0f;
        dragStartPlayerCenterX = 0f;
        dragStartPlayerCenterY = 0f;
    }

    private boolean paceFrame(long frameStartTime) {
        long elapsedNanos = System.nanoTime() - frameStartTime;
        long remainingNanos = TARGET_FRAME_DURATION_NANOS - elapsedNanos;

        if (remainingNanos <= 0L) {
            return true;
        }

        long sleepMillis = remainingNanos / 1_000_000L;
        int sleepNanos = (int) (remainingNanos % 1_000_000L);

        try {
            Thread.sleep(sleepMillis, sleepNanos);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;
        }

        return true;
    }

    private void updateDiagnosticLines() {
        String machineDisplayName = getMachineDisplayName(selectedMachineId);
        String difficultyDisplayName = getDifficultyDisplayName(selectedDifficultyId);

        diagnosticLine = machineDisplayName + " - " + difficultyDisplayName;
    }

    private String getMachineDisplayName(String machineId) {
        switch (machineId) {
            case "volt_bomber":
                return "VOLT BOMBER";
            case "volt_panzer":
                return "VOLT PANZER";
            case "volt_frigate":
                return "VOLT FRIGATE";
            case "volt_lander":
                return "VOLT LANDER";
            case "volt_crewzer":
            default:
                return "VOLT CREWZER";
        }
    }

    private int getMachineDrawableResource(String machineId) {
        switch (machineId) {
            case "volt_bomber":
                return R.drawable.volt_bomber;
            case "volt_panzer":
                return R.drawable.volt_panzer;
            case "volt_frigate":
                return R.drawable.volt_frigate;
            case "volt_lander":
                return R.drawable.volt_lander;
            case "volt_crewzer":
            default:
                return R.drawable.volt_crewzer;
        }
    }

    private int getProjectileDrawableResource(String machineId) {
        switch (machineId) {
            case "volt_bomber":
                return R.drawable.bullet_bomber;
            case "volt_panzer":
                return R.drawable.bullet_panzer;
            case "volt_frigate":
                return R.drawable.bullet_frigate;
            case "volt_lander":
                return R.drawable.bullet_lander;
            case "volt_crewzer":
            default:
                return R.drawable.bullet_crewzer;
        }
    }

    private float getProjectileWidthRatio(String machineId) {
        switch (machineId) {
            case "volt_bomber":
                return 0.82f;
            case "volt_panzer":
                return 0.78f;
            case "volt_frigate":
                return 0.90f;
            case "volt_lander":
                return 0.66f;
            case "volt_crewzer":
            default:
                return 0.95f;
        }
    }

    private boolean isSupportedMachineId(String machineId) {
        return "volt_crewzer".equals(machineId)
                || "volt_bomber".equals(machineId)
                || "volt_panzer".equals(machineId)
                || "volt_frigate".equals(machineId)
                || "volt_lander".equals(machineId);
    }

    private String getDifficultyDisplayName(String difficultyId) {
        switch (difficultyId) {
            case "easy":
                return "EASY";
            case "hard":
                return "HARD";
            case "normal":
            default:
                return "NORMAL";
        }
    }
}
