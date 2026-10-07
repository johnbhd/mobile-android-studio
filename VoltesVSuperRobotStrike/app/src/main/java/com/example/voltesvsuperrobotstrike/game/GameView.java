package com.example.voltesvsuperrobotstrike.game;

import android.content.Context;
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

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final float MAX_DELTA_SECONDS = 0.1f;
    private static final long TARGET_FRAME_DURATION_NANOS = 1_000_000_000L / 60L;
    private static final long THREAD_JOIN_TIMEOUT_MILLIS = 500L;

    private final SurfaceHolder surfaceHolder;
    private final Object gameThreadLock = new Object();
    private final Paint titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint infoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint diagnosticPanelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float density;
    private final int backgroundColor;
    private final ScrollingBackground scrollingBackground;
    private volatile Player player;

    private volatile boolean running;
    private volatile boolean surfaceReady;
    private volatile boolean activityResumed;
    private volatile int screenWidth;
    private volatile int screenHeight;
    private volatile int bottomSystemInsetPixels;

    private Thread gameThread;
    private int activePointerId = MotionEvent.INVALID_POINTER_ID;
    private float dragStartTouchX;
    private float dragStartPlayerCenterX;

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
        scrollingBackground = new ScrollingBackground(context);
        player = new Player(getResources(), R.drawable.volt_crewzer, density);

        initializePaints();
        foundationTitle = getResources().getString(R.string.game_foundation_title);
        runningLine = getResources().getString(R.string.game_foundation_running);
        updateDiagnosticLines();
        setFocusable(true);
        setClickable(true);
        initializeSystemBarInsets();
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
        int diagnosticPanelColor = ContextCompat.getColor(
                getContext(),
                R.color.game_debug_panel
        );

        titlePaint.setColor(debugTextColor);
        titlePaint.setTextSize(20f * density);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));

        infoPaint.setColor(mutedTextColor);
        infoPaint.setTextSize(14f * density);
        infoPaint.setTextAlign(Paint.Align.CENTER);
        infoPaint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));

        diagnosticPanelPaint.setColor(diagnosticPanelColor);
    }

    public void configureGame(String selectedMachine, String selectedDifficulty) {
        selectedMachineId = isSupportedMachineId(selectedMachine)
                ? selectedMachine
                : "volt_crewzer";
        selectedDifficultyId = selectedDifficulty == null ? "normal" : selectedDifficulty;

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
        screenWidth = width;
        screenHeight = height;

        if (width > 0 && height > 0) {
            scrollingBackground.prepare(width, height);

            Player currentPlayer = player;
            if (currentPlayer != null) {
                currentPlayer.setBottomSystemInsetPixels(bottomSystemInsetPixels);
                currentPlayer.prepare(width, height);
            }
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

            Player currentPlayer = player;
            if (currentPlayer != null) {
                currentPlayer.draw(canvas);
            }

            int width = screenWidth > 0 ? screenWidth : canvas.getWidth();
            int height = screenHeight > 0 ? screenHeight : canvas.getHeight();
            drawDiagnostics(canvas, width, height);
        } finally {
            if (canvasLocked) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawDiagnostics(Canvas canvas, int width, int height) {
        float centerX = width / 2f;
        float firstLineY = Math.max(48f * density, height * 0.3f);
        float lineSpacing = 28f * density;
        float panelLeft = 16f * density;
        float panelTop = firstLineY - 30f * density;
        float panelRight = width - panelLeft;
        float panelBottom = firstLineY + lineSpacing * 5f + 10f * density;

        canvas.drawRect(
                panelLeft,
                panelTop,
                panelRight,
                panelBottom,
                diagnosticPanelPaint
        );
        canvas.drawText(foundationTitle, centerX, firstLineY, titlePaint);
        canvas.drawText(machineLine, centerX, firstLineY + lineSpacing * 2f, infoPaint);
        canvas.drawText(difficultyLine, centerX, firstLineY + lineSpacing * 3f, infoPaint);
        canvas.drawText(runningLine, centerX, firstLineY + lineSpacing * 5f, infoPaint);
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
                dragStartPlayerCenterX = currentPlayer.getCenterX();
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
                currentPlayer.setTargetCenterX(dragStartPlayerCenterX + dragDeltaX);
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
        dragStartPlayerCenterX = 0f;
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
