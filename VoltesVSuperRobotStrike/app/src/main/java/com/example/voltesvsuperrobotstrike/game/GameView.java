package com.example.voltesvsuperrobotstrike.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.content.ContextCompat;

import com.example.voltesvsuperrobotstrike.R;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final float MAX_DELTA_SECONDS = 0.1f;
    private static final long TARGET_FRAME_DURATION_NANOS = 1_000_000_000L / 60L;
    private static final long THREAD_JOIN_TIMEOUT_MILLIS = 500L;

    private final SurfaceHolder surfaceHolder;
    private final Object gameThreadLock = new Object();
    private final Paint titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint infoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float density;
    private final int backgroundColor;

    private volatile boolean running;
    private volatile boolean surfaceReady;
    private volatile boolean activityResumed;
    private volatile int screenWidth;
    private volatile int screenHeight;

    private Thread gameThread;

    private String selectedMachineId = "volt_crewzer";
    private String selectedDifficultyId = "normal";
    private String foundationTitle;
    private String machineLine;
    private String difficultyLine;
    private String runningLine;

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
        backgroundColor = ContextCompat.getColor(context, R.color.game_background);

        initializePaints();
        foundationTitle = getResources().getString(R.string.game_foundation_title);
        runningLine = getResources().getString(R.string.game_foundation_running);
        updateDiagnosticLines();
        setFocusable(true);
    }

    private void initializePaints() {
        int debugTextColor = ContextCompat.getColor(
                getContext(),
                R.color.game_debug_text
        );
        int mutedTextColor = ContextCompat.getColor(
                getContext(),
                R.color.game_debug_muted
        );

        titlePaint.setColor(debugTextColor);
        titlePaint.setTextSize(20f * density);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));

        infoPaint.setColor(mutedTextColor);
        infoPaint.setTextSize(14f * density);
        infoPaint.setTextAlign(Paint.Align.CENTER);
        infoPaint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
    }

    public void configureGame(String selectedMachine, String selectedDifficulty) {
        selectedMachineId = selectedMachine == null ? "volt_crewzer" : selectedMachine;
        selectedDifficultyId = selectedDifficulty == null ? "normal" : selectedDifficulty;
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
        screenWidth = width;
        screenHeight = height;
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
        // Future gameplay state updates will run here using deltaSeconds.
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

            int width = screenWidth > 0 ? screenWidth : canvas.getWidth();
            int height = screenHeight > 0 ? screenHeight : canvas.getHeight();
            float centerX = width / 2f;
            float firstLineY = Math.max(48f * density, height * 0.3f);
            float lineSpacing = 28f * density;

            canvas.drawText(
                    foundationTitle,
                    centerX,
                    firstLineY,
                    titlePaint
            );
            canvas.drawText(machineLine, centerX, firstLineY + lineSpacing * 2f, infoPaint);
            canvas.drawText(
                    difficultyLine,
                    centerX,
                    firstLineY + lineSpacing * 3f,
                    infoPaint
            );
            canvas.drawText(
                    runningLine,
                    centerX,
                    firstLineY + lineSpacing * 5f,
                    infoPaint
            );
        } finally {
            if (canvasLocked) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
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

        machineLine = getResources().getString(
                R.string.game_foundation_machine,
                machineDisplayName
        );
        difficultyLine = getResources().getString(
                R.string.game_foundation_difficulty,
                difficultyDisplayName
        );
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
