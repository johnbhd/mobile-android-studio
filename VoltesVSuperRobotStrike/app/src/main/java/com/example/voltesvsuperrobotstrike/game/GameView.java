package com.example.voltesvsuperrobotstrike.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
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
import java.util.Locale;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final float MAX_DELTA_SECONDS = 0.1f;
    private static final float AUTO_FIRE_INTERVAL_SECONDS = 0.35f;
    private static final float RAPID_FIRE_DURATION_SECONDS = 8f;
    private static final float RAPID_FIRE_INTERVAL_MULTIPLIER = 0.55f;
    private static final float DOUBLE_SCORE_DURATION_SECONDS = 10f;
    private static final float POWER_UP_DROP_CHANCE = 0.10f;
    private static final int MAX_ACTIVE_POWER_UPS = 2;
    private static final float POWER_UP_SPEED_HEIGHT_RATIO = 0.15f;
    private static final float POWER_UP_WIDTH_RATIO = 0.09f;
    private static final float POWER_UP_EFFECT_WIDTH_RATIO = 0.14f;
    private static final float POWER_UP_EFFECT_FRAME_DURATION_SECONDS = 0.09f;
    private static final int POWER_UP_EFFECT_FRAME_COUNT = 4;
    private static final int MAX_PLAYER_LIVES = 5;
    private static final float PLAYER_BULLET_SPEED_DP_PER_SECOND = 700f;
    private static final float BULLET_PLAYER_OVERLAP_DP = 2f;
    private static final float BULLET_HITBOX_INSET_RATIO = 0.05f;
    private static final float ENEMY_HITBOX_INSET_RATIO = 0.10f;
    private static final float PLAYER_HITBOX_INSET_RATIO = 0.12f;
    private static final int INITIAL_PLAYER_LIVES = 3;
    private static final float PLAYER_INVULNERABILITY_SECONDS = 2.0f;
    private static final float PLAYER_BLINK_INTERVAL_SECONDS = 0.125f;
    private static final float ENEMY_FIRE_INITIAL_DELAY_MIN_SECONDS = 0.8f;
    private static final float ENEMY_FIRE_INITIAL_DELAY_MAX_SECONDS = 1.8f;
    private static final float ENEMY_FIRE_CAP_RETRY_SECONDS = 0.3f;
    private static final float ENEMY_BULLET_HITBOX_INSET_RATIO = 0.05f;
    private static final float SCOUT_FIRE_INTERVAL_MIN_SECONDS = 2.0f;
    private static final float SCOUT_FIRE_INTERVAL_MAX_SECONDS = 2.6f;
    private static final float HORNET_FIRE_INTERVAL_MIN_SECONDS = 1.6f;
    private static final float HORNET_FIRE_INTERVAL_MAX_SECONDS = 2.2f;
    private static final float HEAVY_FIRE_INTERVAL_MIN_SECONDS = 2.5f;
    private static final float HEAVY_FIRE_INTERVAL_MAX_SECONDS = 3.2f;
    private static final float CRAB_FIRE_INTERVAL_MIN_SECONDS = 2.0f;
    private static final float CRAB_FIRE_INTERVAL_MAX_SECONDS = 2.7f;
    private static final float ELITE_FIRE_INTERVAL_MIN_SECONDS = 1.4f;
    private static final float ELITE_FIRE_INTERVAL_MAX_SECONDS = 1.9f;
    private static final float SCOUT_DRONE_2_FIRE_INTERVAL_MIN_SECONDS = 1.9f;
    private static final float SCOUT_DRONE_2_FIRE_INTERVAL_MAX_SECONDS = 2.5f;
    private static final float BOAZANIAN_FIRE_INTERVAL_MIN_SECONDS = 1.8f;
    private static final float BOAZANIAN_FIRE_INTERVAL_MAX_SECONDS = 2.4f;
    private static final float ENEMY_BURST_SHOT_INTERVAL_SECONDS = 0.20f;
    private static final float SCOUT_BULLET_SPEED_HEIGHT_RATIO = 0.42f;
    private static final float HORNET_BULLET_SPEED_HEIGHT_RATIO = 0.48f;
    private static final float HEAVY_BULLET_SPEED_HEIGHT_RATIO = 0.30f;
    private static final float CRAB_BULLET_SPEED_HEIGHT_RATIO = 0.35f;
    private static final float ELITE_BULLET_SPEED_HEIGHT_RATIO = 0.45f;
    private static final float SCOUT_DRONE_2_BULLET_SPEED_HEIGHT_RATIO = 0.40f;
    private static final float BOAZANIAN_BULLET_SPEED_HEIGHT_RATIO = 0.38f;
    private static final int SCORE_SCOUT = 100;
    private static final int SCORE_HORNET = 150;
    private static final int SCORE_HEAVY_BOMBER = 250;
    private static final int SCORE_CRAB = 300;
    private static final int SCORE_ELITE = 500;
    private static final int SCORE_SCOUT_DRONE_2 = 125;
    private static final int SCORE_BOAZANIAN = 400;
    private static final float ENEMY_FIRST_SPAWN_DELAY_SECONDS = 0.85f;
    private static final float ENEMY_OPENING_PHASE_SECONDS = 20f;
    private static final float ENEMY_MID_PHASE_SECONDS = 45f;
    private static final float ENEMY_SIDE_MARGIN_DP = 6f;
    private static final float SCOUT_SPEED_HEIGHT_RATIO = 0.24f;
    private static final float HORNET_SPEED_HEIGHT_RATIO = 0.27f;
    private static final float HEAVY_BOMBER_SPEED_HEIGHT_RATIO = 0.15f;
    private static final float CRAB_SPEED_HEIGHT_RATIO = 0.19f;
    private static final float ELITE_SPEED_HEIGHT_RATIO = 0.22f;
    private static final float HORNET_DRIFT_WIDTH_RATIO = 0.06f;
    private static final float CRAB_DRIFT_WIDTH_RATIO = 0.04f;
    private static final float ELITE_DRIFT_WIDTH_RATIO = 0.03f;
    private static final float SCOUT_DRONE_2_DRIFT_WIDTH_RATIO = 0.05f;
    private static final float BOAZANIAN_DRIFT_WIDTH_RATIO = 0.04f;
    private static final int ENEMY_SCOUT = 0;
    private static final int ENEMY_HORNET = 1;
    private static final int ENEMY_HEAVY_BOMBER = 2;
    private static final int ENEMY_CRAB = 3;
    private static final int ENEMY_ELITE = 4;
    private static final int ENEMY_SCOUT_DRONE_2 = 5;
    private static final int ENEMY_BOAZANIAN = 6;
    private static final int SPAWN_SINGLE = 0;
    private static final int SPAWN_PAIR = 1;
    private static final int SPAWN_ALTERNATING = 2;
    private static final int SPAWN_ROW = 3;
    private static final int SPAWN_STAGGERED = 4;
    private static final int SPAWN_ELITE_ESCORT = 5;
    private static final int[] EASY_OPENING_ENEMY_POOL = {
            ENEMY_SCOUT, ENEMY_SCOUT, ENEMY_SCOUT, ENEMY_SCOUT, ENEMY_SCOUT,
            ENEMY_SCOUT_DRONE_2, ENEMY_HORNET, ENEMY_HORNET
    };
    private static final int[] EASY_MID_ENEMY_POOL = {
            ENEMY_SCOUT, ENEMY_SCOUT, ENEMY_SCOUT, ENEMY_SCOUT,
            ENEMY_SCOUT_DRONE_2, ENEMY_SCOUT_DRONE_2,
            ENEMY_HORNET, ENEMY_HORNET, ENEMY_HEAVY_BOMBER, ENEMY_CRAB
    };
    private static final int[] EASY_FULL_ENEMY_POOL = {
            ENEMY_SCOUT, ENEMY_SCOUT, ENEMY_SCOUT, ENEMY_SCOUT, ENEMY_SCOUT, ENEMY_SCOUT,
            ENEMY_SCOUT_DRONE_2, ENEMY_SCOUT_DRONE_2,
            ENEMY_HORNET, ENEMY_HORNET, ENEMY_HEAVY_BOMBER, ENEMY_CRAB,
            ENEMY_BOAZANIAN, ENEMY_ELITE
    };
    private static final int[] NORMAL_OPENING_ENEMY_POOL = {
            ENEMY_SCOUT, ENEMY_SCOUT, ENEMY_SCOUT, ENEMY_SCOUT,
            ENEMY_SCOUT_DRONE_2, ENEMY_SCOUT_DRONE_2,
            ENEMY_HORNET, ENEMY_HORNET
    };
    private static final int[] NORMAL_MID_ENEMY_POOL = {
            ENEMY_SCOUT, ENEMY_SCOUT, ENEMY_SCOUT,
            ENEMY_SCOUT_DRONE_2, ENEMY_SCOUT_DRONE_2,
            ENEMY_HORNET, ENEMY_HORNET,
            ENEMY_HEAVY_BOMBER, ENEMY_HEAVY_BOMBER, ENEMY_CRAB
    };
    private static final int[] NORMAL_FULL_ENEMY_POOL = {
            ENEMY_SCOUT, ENEMY_SCOUT, ENEMY_SCOUT,
            ENEMY_SCOUT_DRONE_2, ENEMY_SCOUT_DRONE_2,
            ENEMY_HORNET, ENEMY_HORNET,
            ENEMY_HEAVY_BOMBER, ENEMY_HEAVY_BOMBER,
            ENEMY_CRAB, ENEMY_CRAB,
            ENEMY_BOAZANIAN, ENEMY_BOAZANIAN, ENEMY_ELITE
    };
    private static final int[] HARD_OPENING_ENEMY_POOL = {
            ENEMY_SCOUT, ENEMY_SCOUT, ENEMY_SCOUT,
            ENEMY_SCOUT_DRONE_2, ENEMY_SCOUT_DRONE_2,
            ENEMY_HORNET, ENEMY_HORNET
    };
    private static final int[] HARD_MID_ENEMY_POOL = {
            ENEMY_SCOUT, ENEMY_SCOUT, ENEMY_SCOUT_DRONE_2,
            ENEMY_HORNET, ENEMY_HORNET,
            ENEMY_HEAVY_BOMBER, ENEMY_HEAVY_BOMBER,
            ENEMY_CRAB, ENEMY_CRAB, ENEMY_BOAZANIAN, ENEMY_ELITE
    };
    private static final int[] HARD_FULL_ENEMY_POOL = {
            ENEMY_SCOUT, ENEMY_SCOUT,
            ENEMY_SCOUT_DRONE_2, ENEMY_SCOUT_DRONE_2,
            ENEMY_HORNET, ENEMY_HORNET,
            ENEMY_HEAVY_BOMBER, ENEMY_HEAVY_BOMBER,
            ENEMY_CRAB, ENEMY_CRAB,
            ENEMY_BOAZANIAN, ENEMY_BOAZANIAN,
            ENEMY_ELITE, ENEMY_ELITE, ENEMY_ELITE
    };
    private static final int[] BASIC_ESCORT_POOL = {
            ENEMY_SCOUT,
            ENEMY_SCOUT_DRONE_2,
            ENEMY_HORNET,
            ENEMY_HEAVY_BOMBER,
            ENEMY_CRAB
    };
    private static final long TARGET_FRAME_DURATION_NANOS = 1_000_000_000L / 60L;
    private static final long THREAD_JOIN_TIMEOUT_MILLIS = 500L;

    private final SurfaceHolder surfaceHolder;
    private final Object gameThreadLock = new Object();
    private final Paint infoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint diagnosticPanelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint projectilePaint = new Paint(
            Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG
    );
    private final Paint enemyProjectilePaint = new Paint(
            Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG
    );
    private final Paint enemyPaint = new Paint(
            Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG
    );
    private final Paint powerUpPaint = new Paint(
            Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG
    );
    private final Paint powerUpEffectPaint = new Paint(
            Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG
    );
    private final Paint scorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float density;
    private final float playerBulletSpeedPixelsPerSecond;
    private final float bulletPlayerOverlapPixels;
    private final float enemySideMarginPixels;
    private final int backgroundColor;
    private final ScrollingBackground scrollingBackground;
    private final ArrayList<Bullet> playerBullets = new ArrayList<>();
    private final ArrayList<EnemyBullet> enemyBullets = new ArrayList<>();
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<PowerUp> powerUps = new ArrayList<>();
    private final ArrayList<PowerUpCollectEffect> powerUpCollectEffects = new ArrayList<>();
    private final Random enemyRandom = new Random();
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
    private Bitmap scoutEnemyBitmap;
    private Bitmap hornetEnemyBitmap;
    private Bitmap heavyBomberEnemyBitmap;
    private Bitmap crabEnemyBitmap;
    private Bitmap eliteEnemyBitmap;
    private Bitmap scoutDrone2EnemyBitmap;
    private Bitmap boazanianEnemyBitmap;
    private int preparedEnemyWidth;
    private int preparedEnemyHeight;
    private Bitmap scoutEnemyProjectileBitmap;
    private Bitmap scoutDrone2EnemyProjectileBitmap;
    private Bitmap hornetEnemyProjectileBitmap;
    private Bitmap heavyBomberEnemyProjectileBitmap;
    private Bitmap crabEnemyProjectileBitmap;
    private Bitmap boazanianEnemyProjectileBitmap;
    private Bitmap eliteEnemyProjectileBitmap;
    private int preparedEnemyProjectileWidth;
    private int preparedEnemyProjectileHeight;
    private Bitmap shieldPowerUpBitmap;
    private Bitmap rapidFirePowerUpBitmap;
    private Bitmap doubleScorePowerUpBitmap;
    private Bitmap extraLifePowerUpBitmap;
    private Bitmap powerUpCollectEffectBitmap;
    private int preparedPowerUpWidth;
    private int preparedPowerUpHeight;
    private float enemySpawnCooldownSeconds = ENEMY_FIRST_SPAWN_DELAY_SECONDS;
    private float gameplayTimeSeconds;
    private boolean alternatingSpawnFromLeft;

    private String selectedMachineId = "volt_crewzer";
    private String selectedDifficultyId = "normal";
    private int score;
    private String scoreLine;
    private int playerLives = INITIAL_PLAYER_LIVES;
    private boolean playerInvulnerable;
    private float playerInvulnerabilityTimerSeconds;
    private boolean shieldActive;
    private float rapidFireTimerSeconds;
    private float doubleScoreTimerSeconds;
    private String livesLine;
    private String powerUpStatusLine;
    private int lastRapidFireStatusSeconds = -1;
    private int lastDoubleScoreStatusSeconds = -1;
    private boolean lastShieldStatus;
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
        enemySideMarginPixels = ENEMY_SIDE_MARGIN_DP * density;
        backgroundColor = ContextCompat.getColor(context, R.color.game_background);
        scrollingBackground = new ScrollingBackground(context);
        player = new Player(getResources(), R.drawable.volt_crewzer, density);

        initializePaints();
        updateScoreLine();
        updateLivesLine();
        updatePowerUpStatusLine();
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

        scorePaint.setColor(ContextCompat.getColor(
                getContext(),
                R.color.game_debug_text
        ));
        scorePaint.setTextSize(20f * getResources().getDisplayMetrics().scaledDensity);
        scorePaint.setTextAlign(Paint.Align.LEFT);
        scorePaint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
    }

    public void configureGame(String selectedMachine, String selectedDifficulty) {
        selectedMachineId = isSupportedMachineId(selectedMachine)
                ? selectedMachine
                : "volt_crewzer";
        selectedDifficultyId = isSupportedDifficultyId(selectedDifficulty)
                ? selectedDifficulty
                : "normal";

        playerBullets.clear();
        enemyBullets.clear();
        powerUps.clear();
        powerUpCollectEffects.clear();
        fireCooldownSeconds = AUTO_FIRE_INTERVAL_SECONDS;
        releaseProjectileBitmap();
        enemies.clear();
        enemySpawnCooldownSeconds = ENEMY_FIRST_SPAWN_DELAY_SECONDS;
        gameplayTimeSeconds = 0f;
        alternatingSpawnFromLeft = false;
        releaseEnemyBitmaps();
        releaseEnemyProjectileBitmaps();
        score = 0;
        updateScoreLine();
        playerLives = INITIAL_PLAYER_LIVES;
        playerInvulnerable = false;
        playerInvulnerabilityTimerSeconds = 0f;
        shieldActive = false;
        rapidFireTimerSeconds = 0f;
        doubleScoreTimerSeconds = 0f;
        updateLivesLine();
        updatePowerUpStatusLine();

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
        prepareEnemyBitmapsIfReady();
        prepareEnemyProjectileBitmapsIfReady();
        preparePowerUpBitmapsIfReady();

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
        enemyBullets.clear();
        powerUps.clear();
        powerUpCollectEffects.clear();
        releaseProjectileBitmap();
        enemies.clear();
        releaseEnemyBitmaps();
        releaseEnemyProjectileBitmaps();
        releasePowerUpBitmaps();

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
                enemyBullets.clear();
                powerUps.clear();
                powerUpCollectEffects.clear();
                fireCooldownSeconds = AUTO_FIRE_INTERVAL_SECONDS;
                enemies.clear();
                enemySpawnCooldownSeconds = ENEMY_FIRST_SPAWN_DELAY_SECONDS;
                gameplayTimeSeconds = 0f;
                alternatingSpawnFromLeft = false;
                releaseEnemyBitmaps();
                releaseEnemyProjectileBitmaps();
                releasePowerUpBitmaps();
            }

            prepareProjectileBitmapIfReady();
            prepareEnemyBitmapsIfReady();
            prepareEnemyProjectileBitmapsIfReady();
            preparePowerUpBitmapsIfReady();
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
        updatePlayerInvulnerability(deltaSeconds);
        updatePowerUpTimers(deltaSeconds);
        scrollingBackground.update(deltaSeconds);

        gameplayTimeSeconds += deltaSeconds;
        updateEnemySpawning(deltaSeconds);
        updateEnemies(deltaSeconds);

        Player currentPlayer = player;
        if (currentPlayer != null) {
            currentPlayer.update();
        }

        updateAutomaticFire(deltaSeconds);
        updatePlayerBullets(deltaSeconds);
        updateEnemyShooting();
        updateEnemyBullets(deltaSeconds);
        checkPlayerBulletEnemyCollisions();
        checkPlayerEnemyCollisions();
        resolveEnemyBulletPlayerCollisions();
        updatePowerUps(deltaSeconds);
        resolvePlayerPowerUpCollisions();
        updatePowerUpCollectEffects(deltaSeconds);
        removeOffScreenEnemies();
        removeOffScreenPlayerBullets();
        removeOffScreenEnemyBullets();
        removeOffScreenPowerUps();
        removeFinishedPowerUpCollectEffects();
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

            drawPowerUps(canvas);
            drawEnemies(canvas);
            drawPlayerBullets(canvas);
            drawEnemyBullets(canvas);
            drawPowerUpCollectEffects(canvas);

            Player currentPlayer = player;
            if (currentPlayer != null && shouldDrawPlayer()) {
                currentPlayer.draw(canvas);
            }

            drawHud(canvas);
        } finally {
            if (canvasLocked) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawPlayerBullets(Canvas canvas) {
        for (Bullet bullet : playerBullets) {
            bullet.draw(canvas, projectilePaint);
        }
    }

    private void drawEnemyBullets(Canvas canvas) {
        for (EnemyBullet enemyBullet : enemyBullets) {
            enemyBullet.draw(canvas, enemyProjectilePaint);
        }
    }

    private void drawEnemies(Canvas canvas) {
        for (Enemy enemy : enemies) {
            enemy.draw(canvas, enemyPaint);
        }
    }

    private void updateEnemySpawning(float deltaSeconds) {
        if (screenWidth <= 0
                || screenHeight <= 0
                || !areEnemyBitmapsReady()) {
            return;
        }

        enemySpawnCooldownSeconds -= deltaSeconds;
        if (enemySpawnCooldownSeconds > 0f) {
            return;
        }

        enemySpawnCooldownSeconds = getEnemySpawnIntervalSeconds();
        if (enemies.size() >= getMaximumActiveEnemies()) {
            return;
        }

        spawnEnemyWave();
    }

    private void updateEnemies(float deltaSeconds) {
        for (Enemy enemy : enemies) {
            enemy.update(deltaSeconds);
            enemy.updateFireCooldown(deltaSeconds);
        }
    }

    private void removeOffScreenEnemies() {
        for (int index = enemies.size() - 1; index >= 0; index--) {
            if (enemies.get(index).isOffScreen(screenHeight)) {
                enemies.remove(index);
            }
        }
    }

    private void spawnEnemyWave() {
        int availableSlots = getMaximumActiveEnemies() - enemies.size();
        if (availableSlots <= 0) {
            return;
        }

        int spawnPattern = selectSpawnPattern();
        if (spawnPattern == SPAWN_ELITE_ESCORT) {
            spawnEnemy(ENEMY_ELITE, getPatternSpawnX(SPAWN_ELITE_ESCORT, 0, 1), 0f);
            availableSlots--;
            if (availableSlots > 0) {
                spawnEnemy(
                        selectBasicEscortType(),
                        getPatternSpawnX(SPAWN_ELITE_ESCORT, 1, 2),
                        0f
                );
            }
            if (availableSlots > 1 && enemyRandom.nextBoolean()) {
                spawnEnemy(
                        selectBasicEscortType(),
                        getPatternSpawnX(SPAWN_ELITE_ESCORT, 0, 2),
                        -0.55f
                );
            }
            return;
        }

        int requestedCount = getSpawnPatternCount(spawnPattern);
        int count = Math.min(requestedCount, availableSlots);
        for (int index = 0; index < count; index++) {
            float spawnX = getPatternSpawnX(spawnPattern, index, count);
            float spawnYOffset = spawnPattern == SPAWN_STAGGERED
                    ? index * -0.55f
                    : 0f;
            spawnEnemy(selectEnemyType(), spawnX, spawnYOffset);
        }
    }

    private void spawnEnemy(
            int enemyType,
            float preferredX,
            float spawnYOffset
    ) {
        Bitmap bitmap = getEnemyBitmap(enemyType);
        if (bitmap == null) {
            return;
        }

        float minimumX = enemySideMarginPixels;
        float maximumX = screenWidth - bitmap.getWidth() - enemySideMarginPixels;
        float spawnX = Float.isNaN(preferredX)
                ? minimumX + enemyRandom.nextFloat() * Math.max(0f, maximumX - minimumX)
                : preferredX;
        if (maximumX <= minimumX) {
            spawnX = Math.max(0f, (screenWidth - bitmap.getWidth()) / 2f);
        } else {
            spawnX = Math.max(minimumX, Math.min(spawnX, maximumX));
        }

        float spawnY = -bitmap.getHeight() + spawnYOffset * bitmap.getHeight();
        float verticalSpeed = screenHeight
                * getEnemySpeedRatio(enemyType)
                * getEnemySpeedMultiplier();
        float horizontalSpeed = getEnemyHorizontalSpeed(enemyType);
        int movementPattern = selectMovementPattern(enemyType);
        float movementAmplitude = getMovementAmplitudePixels(enemyType, movementPattern);
        float movementFrequency = getMovementFrequency(enemyType, movementPattern);
        float movementPhase = enemyRandom.nextFloat() * (float) (Math.PI * 2.0);

        Enemy enemy = new Enemy(
                bitmap,
                enemyType,
                spawnX,
                spawnY,
                verticalSpeed,
                horizontalSpeed,
                screenWidth,
                movementPattern,
                movementAmplitude,
                movementFrequency,
                movementPhase
        );
        enemy.resetFireCooldown(getInitialEnemyFireDelaySeconds());
        enemies.add(enemy);
    }

    private int selectSpawnPattern() {
        int roll = enemyRandom.nextInt(100);
        if ("easy".equals(selectedDifficultyId)) {
            if (roll < 70) {
                return SPAWN_SINGLE;
            }
            if (roll < 85) {
                return SPAWN_ALTERNATING;
            }
            return SPAWN_PAIR;
        }
        if ("hard".equals(selectedDifficultyId)) {
            if (roll < 30) {
                return SPAWN_SINGLE;
            }
            if (roll < 50) {
                return SPAWN_PAIR;
            }
            if (roll < 65) {
                return SPAWN_ALTERNATING;
            }
            if (roll < 80) {
                return SPAWN_ROW;
            }
            if (roll < 92) {
                return SPAWN_STAGGERED;
            }
            return isFullRosterPhase() ? SPAWN_ELITE_ESCORT : SPAWN_STAGGERED;
        }
        if (roll < 50) {
            return SPAWN_SINGLE;
        }
        if (roll < 68) {
            return SPAWN_PAIR;
        }
        if (roll < 80) {
            return SPAWN_ALTERNATING;
        }
        if (roll < 90) {
            return SPAWN_ROW;
        }
        if (roll < 97) {
            return SPAWN_STAGGERED;
        }
        return isFullRosterPhase() ? SPAWN_ELITE_ESCORT : SPAWN_STAGGERED;
    }

    private int getSpawnPatternCount(int spawnPattern) {
        switch (spawnPattern) {
            case SPAWN_PAIR:
            case SPAWN_ALTERNATING:
            case SPAWN_STAGGERED:
                return 2;
            case SPAWN_ROW:
                return enemyRandom.nextBoolean() ? 2 : 3;
            case SPAWN_SINGLE:
            default:
                return 1;
        }
    }

    private float getPatternSpawnX(int spawnPattern, int index, int count) {
        if (count <= 1) {
            return Float.NaN;
        }

        if (spawnPattern == SPAWN_ALTERNATING) {
            boolean leftFirst = alternatingSpawnFromLeft;
            if (index == 0) {
                alternatingSpawnFromLeft = !alternatingSpawnFromLeft;
            }
            return screenWidth * ((index == 0) == leftFirst ? 0.10f : 0.68f);
        }

        if (count == 2 && spawnPattern != SPAWN_ROW) {
            return screenWidth * (index == 0 ? 0.16f : 0.68f);
        }

        float fraction = (index + 1f) / (count + 1f);
        return screenWidth * fraction;
    }

    private int selectBasicEscortType() {
        return BASIC_ESCORT_POOL[enemyRandom.nextInt(BASIC_ESCORT_POOL.length)];
    }

    private void updateEnemyShooting() {
        int maximumEnemyBullets = getMaximumActiveEnemyBullets();
        if (maximumEnemyBullets <= 0) {
            return;
        }

        for (Enemy enemy : enemies) {
            if (!enemy.isReadyToFire()) {
                continue;
            }

            if (enemyBullets.size() >= maximumEnemyBullets
                    || enemy.getY() < 0f
                    || enemy.getBottom() >= screenHeight) {
                enemy.resetFireCooldown(ENEMY_FIRE_CAP_RETRY_SECONDS);
                continue;
            }

            if (!enemy.hasPendingFireShots()) {
                enemy.beginFireSequence(getFireSequenceShotCount(enemy.getType()));
            }

            spawnEnemyBullet(enemy);
            enemy.consumeFireShot();
            enemy.resetFireCooldown(enemy.hasPendingFireShots()
                    ? ENEMY_BURST_SHOT_INTERVAL_SECONDS
                    : getNextEnemyFireIntervalSeconds(enemy.getType()));
        }
    }

    private int getFireSequenceShotCount(int enemyType) {
        if (enemyType == ENEMY_SCOUT_DRONE_2) {
            return 2;
        }
        if (enemyType == ENEMY_ELITE && enemyRandom.nextInt(4) == 0) {
            return 3;
        }
        return 1;
    }

    private void spawnEnemyBullet(Enemy enemy) {
        int enemyType = enemy.getType();
        Bitmap projectileBitmap = getEnemyProjectileBitmap(enemyType);
        float width = projectileBitmap == null
                ? Math.max(1f, screenWidth * getEnemyBulletWidthRatio(enemyType))
                : projectileBitmap.getWidth();
        float height = projectileBitmap == null
                ? width * getEnemyBulletHeightRatio(enemyType)
                : projectileBitmap.getHeight();
        float spawnY = enemy.getBottom() - height * 0.2f;
        float speed = screenHeight * getEnemyBulletSpeedHeightRatio(enemyType);

        enemyBullets.add(new EnemyBullet(
                projectileBitmap,
                enemy.getCenterX(),
                spawnY,
                width,
                height,
                speed,
                getEnemyBulletVisualType(enemyType)
        ));
    }

    private float getInitialEnemyFireDelaySeconds() {
        return ENEMY_FIRE_INITIAL_DELAY_MIN_SECONDS
                + enemyRandom.nextFloat()
                * (ENEMY_FIRE_INITIAL_DELAY_MAX_SECONDS - ENEMY_FIRE_INITIAL_DELAY_MIN_SECONDS);
    }

    private float getNextEnemyFireIntervalSeconds(int enemyType) {
        float minimumInterval = getEnemyFireIntervalMinimumSeconds(enemyType);
        float maximumInterval = getEnemyFireIntervalMaximumSeconds(enemyType);
        float baseInterval = minimumInterval
                + enemyRandom.nextFloat() * (maximumInterval - minimumInterval);
        return baseInterval * getEnemyFireIntervalMultiplier();
    }

    private float getEnemyFireIntervalMinimumSeconds(int enemyType) {
        switch (enemyType) {
            case ENEMY_HORNET:
                return HORNET_FIRE_INTERVAL_MIN_SECONDS;
            case ENEMY_HEAVY_BOMBER:
                return HEAVY_FIRE_INTERVAL_MIN_SECONDS;
            case ENEMY_CRAB:
                return CRAB_FIRE_INTERVAL_MIN_SECONDS;
            case ENEMY_ELITE:
                return ELITE_FIRE_INTERVAL_MIN_SECONDS;
            case ENEMY_SCOUT_DRONE_2:
                return SCOUT_DRONE_2_FIRE_INTERVAL_MIN_SECONDS;
            case ENEMY_BOAZANIAN:
                return BOAZANIAN_FIRE_INTERVAL_MIN_SECONDS;
            case ENEMY_SCOUT:
            default:
                return SCOUT_FIRE_INTERVAL_MIN_SECONDS;
        }
    }

    private float getEnemyFireIntervalMaximumSeconds(int enemyType) {
        switch (enemyType) {
            case ENEMY_HORNET:
                return HORNET_FIRE_INTERVAL_MAX_SECONDS;
            case ENEMY_HEAVY_BOMBER:
                return HEAVY_FIRE_INTERVAL_MAX_SECONDS;
            case ENEMY_CRAB:
                return CRAB_FIRE_INTERVAL_MAX_SECONDS;
            case ENEMY_ELITE:
                return ELITE_FIRE_INTERVAL_MAX_SECONDS;
            case ENEMY_SCOUT_DRONE_2:
                return SCOUT_DRONE_2_FIRE_INTERVAL_MAX_SECONDS;
            case ENEMY_BOAZANIAN:
                return BOAZANIAN_FIRE_INTERVAL_MAX_SECONDS;
            case ENEMY_SCOUT:
            default:
                return SCOUT_FIRE_INTERVAL_MAX_SECONDS;
        }
    }

    private float getEnemyFireIntervalMultiplier() {
        if ("easy".equals(selectedDifficultyId)) {
            return 1.25f;
        }
        if ("hard".equals(selectedDifficultyId)) {
            return 0.80f;
        }
        return 1.00f;
    }

    private int getMaximumActiveEnemyBullets() {
        if ("easy".equals(selectedDifficultyId)) {
            return 2;
        }
        if ("hard".equals(selectedDifficultyId)) {
            return 4;
        }
        return 3;
    }

    private float getEnemyBulletWidthRatio(int enemyType) {
        switch (enemyType) {
            case ENEMY_HEAVY_BOMBER:
            case ENEMY_ELITE:
                return 0.06f;
            case ENEMY_BOAZANIAN:
                return 0.055f;
            case ENEMY_CRAB:
                return 0.05f;
            case ENEMY_HORNET:
            case ENEMY_SCOUT:
            default:
                return 0.03f;
        }
    }

    private float getEnemyBulletHeightRatio(int enemyType) {
        switch (enemyType) {
            case ENEMY_HORNET:
            case ENEMY_SCOUT:
                return 2.2f;
            case ENEMY_ELITE:
                return 1.4f;
            case ENEMY_SCOUT_DRONE_2:
                return 1.8f;
            case ENEMY_BOAZANIAN:
                return 1.0f;
            case ENEMY_HEAVY_BOMBER:
            case ENEMY_CRAB:
            default:
                return 1.0f;
        }
    }

    private float getEnemyBulletSpeedHeightRatio(int enemyType) {
        switch (enemyType) {
            case ENEMY_HORNET:
                return HORNET_BULLET_SPEED_HEIGHT_RATIO;
            case ENEMY_HEAVY_BOMBER:
                return HEAVY_BULLET_SPEED_HEIGHT_RATIO;
            case ENEMY_CRAB:
                return CRAB_BULLET_SPEED_HEIGHT_RATIO;
            case ENEMY_ELITE:
                return ELITE_BULLET_SPEED_HEIGHT_RATIO;
            case ENEMY_SCOUT_DRONE_2:
                return SCOUT_DRONE_2_BULLET_SPEED_HEIGHT_RATIO;
            case ENEMY_BOAZANIAN:
                return BOAZANIAN_BULLET_SPEED_HEIGHT_RATIO;
            case ENEMY_SCOUT:
            default:
                return SCOUT_BULLET_SPEED_HEIGHT_RATIO;
        }
    }

    private int getEnemyBulletVisualType(int enemyType) {
        switch (enemyType) {
            case ENEMY_HORNET:
                return EnemyBullet.VISUAL_HORNET;
            case ENEMY_HEAVY_BOMBER:
                return EnemyBullet.VISUAL_HEAVY;
            case ENEMY_CRAB:
                return EnemyBullet.VISUAL_CRAB;
            case ENEMY_ELITE:
                return EnemyBullet.VISUAL_ELITE;
            case ENEMY_SCOUT_DRONE_2:
                return EnemyBullet.VISUAL_SCOUT_DRONE_2;
            case ENEMY_BOAZANIAN:
                return EnemyBullet.VISUAL_BOAZANIAN;
            case ENEMY_SCOUT:
            default:
                return EnemyBullet.VISUAL_SCOUT;
        }
    }

    private Bitmap getEnemyProjectileBitmap(int enemyType) {
        switch (enemyType) {
            case ENEMY_HORNET:
                return hornetEnemyProjectileBitmap;
            case ENEMY_HEAVY_BOMBER:
                return heavyBomberEnemyProjectileBitmap;
            case ENEMY_CRAB:
                return crabEnemyProjectileBitmap;
            case ENEMY_ELITE:
                return eliteEnemyProjectileBitmap;
            case ENEMY_SCOUT_DRONE_2:
                return scoutDrone2EnemyProjectileBitmap;
            case ENEMY_BOAZANIAN:
                return boazanianEnemyProjectileBitmap;
            case ENEMY_SCOUT:
            default:
                return scoutEnemyProjectileBitmap;
        }
    }

    private int selectEnemyType() {
        int[] enemyPool = getEnemyPool();
        return enemyPool[enemyRandom.nextInt(enemyPool.length)];
    }

    private int[] getEnemyPool() {
        boolean easy = "easy".equals(selectedDifficultyId);
        boolean hard = "hard".equals(selectedDifficultyId);
        float openingPhaseSeconds = getOpeningRosterPhaseSeconds();
        float midPhaseSeconds = getFullRosterStartSeconds();

        if (gameplayTimeSeconds < openingPhaseSeconds) {
            if (easy) {
                return EASY_OPENING_ENEMY_POOL;
            }
            if (hard) {
                return HARD_OPENING_ENEMY_POOL;
            }
            return NORMAL_OPENING_ENEMY_POOL;
        }

        if (gameplayTimeSeconds < midPhaseSeconds) {
            if (easy) {
                return EASY_MID_ENEMY_POOL;
            }
            if (hard) {
                return HARD_MID_ENEMY_POOL;
            }
            return NORMAL_MID_ENEMY_POOL;
        }

        if (easy) {
            return EASY_FULL_ENEMY_POOL;
        }
        if (hard) {
            return HARD_FULL_ENEMY_POOL;
        }
        return NORMAL_FULL_ENEMY_POOL;
    }

    private float getOpeningRosterPhaseSeconds() {
        if ("easy".equals(selectedDifficultyId)) {
            return ENEMY_OPENING_PHASE_SECONDS * 1.25f;
        }
        if ("hard".equals(selectedDifficultyId)) {
            return ENEMY_OPENING_PHASE_SECONDS * 0.75f;
        }
        return ENEMY_OPENING_PHASE_SECONDS;
    }

    private float getFullRosterStartSeconds() {
        if ("easy".equals(selectedDifficultyId)) {
            return ENEMY_MID_PHASE_SECONDS * 1.22f;
        }
        if ("hard".equals(selectedDifficultyId)) {
            return ENEMY_MID_PHASE_SECONDS * 0.78f;
        }
        return ENEMY_MID_PHASE_SECONDS;
    }

    private boolean isFullRosterPhase() {
        return gameplayTimeSeconds >= getFullRosterStartSeconds();
    }

    private Bitmap getEnemyBitmap(int enemyType) {
        switch (enemyType) {
            case ENEMY_HORNET:
                return hornetEnemyBitmap;
            case ENEMY_HEAVY_BOMBER:
                return heavyBomberEnemyBitmap;
            case ENEMY_CRAB:
                return crabEnemyBitmap;
            case ENEMY_ELITE:
                return eliteEnemyBitmap;
            case ENEMY_SCOUT_DRONE_2:
                return scoutDrone2EnemyBitmap;
            case ENEMY_BOAZANIAN:
                return boazanianEnemyBitmap;
            case ENEMY_SCOUT:
            default:
                return scoutEnemyBitmap;
        }
    }

    private float getEnemySpeedRatio(int enemyType) {
        switch (enemyType) {
            case ENEMY_HORNET:
                return HORNET_SPEED_HEIGHT_RATIO;
            case ENEMY_HEAVY_BOMBER:
                return HEAVY_BOMBER_SPEED_HEIGHT_RATIO;
            case ENEMY_CRAB:
                return CRAB_SPEED_HEIGHT_RATIO;
            case ENEMY_ELITE:
                return ELITE_SPEED_HEIGHT_RATIO;
            case ENEMY_SCOUT_DRONE_2:
                return 0.25f;
            case ENEMY_BOAZANIAN:
                return 0.21f;
            case ENEMY_SCOUT:
            default:
                return SCOUT_SPEED_HEIGHT_RATIO;
        }
    }

    private float getEnemyHorizontalSpeed(int enemyType) {
        float horizontalSpeedRatio;
        switch (enemyType) {
            case ENEMY_HORNET:
                horizontalSpeedRatio = HORNET_DRIFT_WIDTH_RATIO;
                break;
            case ENEMY_CRAB:
                horizontalSpeedRatio = CRAB_DRIFT_WIDTH_RATIO;
                break;
            case ENEMY_ELITE:
                horizontalSpeedRatio = ELITE_DRIFT_WIDTH_RATIO;
                break;
            case ENEMY_SCOUT_DRONE_2:
                horizontalSpeedRatio = SCOUT_DRONE_2_DRIFT_WIDTH_RATIO;
                break;
            case ENEMY_BOAZANIAN:
                horizontalSpeedRatio = BOAZANIAN_DRIFT_WIDTH_RATIO;
                break;
            case ENEMY_HEAVY_BOMBER:
            case ENEMY_SCOUT:
            default:
                return 0f;
        }

        float horizontalSpeed = screenWidth * horizontalSpeedRatio;
        return enemyRandom.nextBoolean() ? horizontalSpeed : -horizontalSpeed;
    }

    private int selectMovementPattern(int enemyType) {
        switch (enemyType) {
            case ENEMY_SCOUT_DRONE_2:
                return enemyRandom.nextBoolean()
                        ? Enemy.MOVEMENT_STRAIGHT
                        : Enemy.MOVEMENT_ZIGZAG;
            case ENEMY_HORNET:
                switch (enemyRandom.nextInt(3)) {
                    case 1:
                        return Enemy.MOVEMENT_ZIGZAG;
                    case 2:
                        return Enemy.MOVEMENT_SWAY;
                    case 0:
                    default:
                        return Enemy.MOVEMENT_DRIFT;
                }
            case ENEMY_CRAB:
                return enemyRandom.nextBoolean()
                        ? Enemy.MOVEMENT_DRIFT
                        : Enemy.MOVEMENT_SWAY;
            case ENEMY_BOAZANIAN:
                return enemyRandom.nextBoolean()
                        ? Enemy.MOVEMENT_SWAY
                        : Enemy.MOVEMENT_ZIGZAG;
            case ENEMY_ELITE:
                switch (enemyRandom.nextInt(3)) {
                    case 1:
                        return Enemy.MOVEMENT_DRIFT;
                    case 2:
                        return Enemy.MOVEMENT_SWAY;
                    case 0:
                    default:
                        return Enemy.MOVEMENT_STRAIGHT;
                }
            case ENEMY_HEAVY_BOMBER:
                if (enemyRandom.nextInt(4) == 0) {
                    return Enemy.MOVEMENT_PAUSE_DROP;
                }
                return enemyRandom.nextBoolean()
                        ? Enemy.MOVEMENT_STRAIGHT
                        : Enemy.MOVEMENT_DRIFT;
            case ENEMY_SCOUT:
            default:
                return enemyRandom.nextBoolean()
                        ? Enemy.MOVEMENT_STRAIGHT
                        : Enemy.MOVEMENT_DRIFT;
        }
    }

    private float getMovementAmplitudePixels(int enemyType, int movementPattern) {
        if (movementPattern == Enemy.MOVEMENT_ZIGZAG) {
            return screenWidth * (enemyType == ENEMY_SCOUT_DRONE_2 ? 0.08f : 0.07f);
        }
        if (movementPattern == Enemy.MOVEMENT_SWAY) {
            return screenWidth * (enemyType == ENEMY_BOAZANIAN ? 0.10f : 0.08f);
        }
        return 0f;
    }

    private float getMovementFrequency(int enemyType, int movementPattern) {
        if (movementPattern == Enemy.MOVEMENT_ZIGZAG) {
            return enemyType == ENEMY_BOAZANIAN ? 2.2f : 2.7f;
        }
        if (movementPattern == Enemy.MOVEMENT_SWAY) {
            return enemyType == ENEMY_BOAZANIAN ? 1.25f : 1.55f;
        }
        return 0f;
    }

    private float getEnemySpeedMultiplier() {
        if ("easy".equals(selectedDifficultyId)) {
            return 0.85f;
        }
        if ("hard".equals(selectedDifficultyId)) {
            return 1.20f;
        }
        return 1.00f;
    }

    private float getEnemySpawnIntervalSeconds() {
        if ("easy".equals(selectedDifficultyId)) {
            return 1.35f;
        }
        if ("hard".equals(selectedDifficultyId)) {
            return 0.65f;
        }
        return 0.95f;
    }

    private int getMaximumActiveEnemies() {
        if ("easy".equals(selectedDifficultyId)) {
            return 5;
        }
        if ("hard".equals(selectedDifficultyId)) {
            return 9;
        }
        return 7;
    }

    private void prepareEnemyBitmapsIfReady() {
        if (screenWidth <= 0 || screenHeight <= 0) {
            return;
        }

        if (preparedEnemyWidth == screenWidth
                && preparedEnemyHeight == screenHeight
                && areEnemyBitmapsReady()) {
            return;
        }

        releaseEnemyBitmaps();
        scoutEnemyBitmap = loadScaledEnemyBitmap(
                R.drawable.enemy_scout_drone,
                0.11f
        );
        hornetEnemyBitmap = loadScaledEnemyBitmap(
                R.drawable.enemy_hornet_fighter,
                0.12f
        );
        heavyBomberEnemyBitmap = loadScaledEnemyBitmap(
                R.drawable.enemy_heavy_bomber,
                0.17f
        );
        crabEnemyBitmap = loadScaledEnemyBitmap(
                R.drawable.enemy_crab_tank,
                0.16f
        );
        eliteEnemyBitmap = loadScaledEnemyBitmap(
                R.drawable.enemy_elite_commander,
                0.18f
        );
        scoutDrone2EnemyBitmap = loadScaledEnemyBitmap(
                R.drawable.enemy_scout_drone_2,
                0.115f
        );
        boazanianEnemyBitmap = loadScaledEnemyBitmap(
                R.drawable.enemy_boizanian_spike,
                0.15f
        );
        preparedEnemyWidth = screenWidth;
        preparedEnemyHeight = screenHeight;
    }

    private void prepareEnemyProjectileBitmapsIfReady() {
        if (screenWidth <= 0 || screenHeight <= 0) {
            return;
        }

        if (preparedEnemyProjectileWidth == screenWidth
                && preparedEnemyProjectileHeight == screenHeight
                && areEnemyProjectileBitmapsReady()) {
            return;
        }

        releaseEnemyProjectileBitmaps();
        scoutEnemyProjectileBitmap = loadScaledEnemyProjectileBitmap(
                R.drawable.enemy_projectile_scout_drone,
                0.030f,
                true
        );
        scoutDrone2EnemyProjectileBitmap = loadScaledEnemyProjectileBitmap(
                R.drawable.enemy_projectile_scout_drone_2,
                0.035f,
                true
        );
        hornetEnemyProjectileBitmap = loadScaledEnemyProjectileBitmap(
                R.drawable.enemy_projectile_hornet_fighter,
                0.030f,
                true
        );
        heavyBomberEnemyProjectileBitmap = loadScaledEnemyProjectileBitmap(
                R.drawable.enemy_projectile_heavy_bomber,
                0.060f,
                true
        );
        crabEnemyProjectileBitmap = loadScaledEnemyProjectileBitmap(
                R.drawable.enemy_projectile_crab_tank,
                0.050f,
                false
        );
        boazanianEnemyProjectileBitmap = loadScaledEnemyProjectileBitmap(
                R.drawable.enemy_projectile_boazanian_spike,
                0.050f,
                false
        );
        eliteEnemyProjectileBitmap = loadScaledEnemyProjectileBitmap(
                R.drawable.enemy_projectile_elite_commander,
                0.060f,
                true
        );
        preparedEnemyProjectileWidth = screenWidth;
        preparedEnemyProjectileHeight = screenHeight;
    }

    private Bitmap loadScaledEnemyProjectileBitmap(
            int resourceId,
            float visibleWidthRatio,
            boolean rotate180
    ) {
        Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        if (sourceBitmap == null
                || sourceBitmap.getWidth() <= 0
                || sourceBitmap.getHeight() <= 0) {
            return null;
        }

        Bitmap croppedBitmap = cropToOpaqueBounds(sourceBitmap);
        if (croppedBitmap != sourceBitmap) {
            sourceBitmap.recycle();
        }

        int targetWidth = Math.max(1, Math.round(screenWidth * visibleWidthRatio));
        int targetHeight = Math.max(
                1,
                Math.round(croppedBitmap.getHeight()
                        * (targetWidth / (float) croppedBitmap.getWidth()))
        );
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                croppedBitmap,
                targetWidth,
                targetHeight,
                true
        );

        if (scaledBitmap != croppedBitmap) {
            croppedBitmap.recycle();
        }

        if (!rotate180) {
            return scaledBitmap;
        }

        Matrix rotation = new Matrix();
        rotation.postRotate(180f);
        Bitmap rotatedBitmap = Bitmap.createBitmap(
                scaledBitmap,
                0,
                0,
                scaledBitmap.getWidth(),
                scaledBitmap.getHeight(),
                rotation,
                true
        );
        if (rotatedBitmap != scaledBitmap) {
            scaledBitmap.recycle();
        }
        return rotatedBitmap;
    }

    private Bitmap cropToOpaqueBounds(Bitmap sourceBitmap) {
        int sourceWidth = sourceBitmap.getWidth();
        int sourceHeight = sourceBitmap.getHeight();
        int minimumX = sourceWidth;
        int minimumY = sourceHeight;
        int maximumX = -1;
        int maximumY = -1;
        int[] rowPixels = new int[sourceWidth];

        for (int y = 0; y < sourceHeight; y++) {
            sourceBitmap.getPixels(rowPixels, 0, sourceWidth, 0, y, sourceWidth, 1);
            for (int x = 0; x < sourceWidth; x++) {
                if (((rowPixels[x] >>> 24) & 0xFF) <= 8) {
                    continue;
                }

                minimumX = Math.min(minimumX, x);
                minimumY = Math.min(minimumY, y);
                maximumX = Math.max(maximumX, x);
                maximumY = Math.max(maximumY, y);
            }
        }

        if (maximumX < minimumX || maximumY < minimumY) {
            return sourceBitmap;
        }
        if (minimumX == 0
                && minimumY == 0
                && maximumX == sourceWidth - 1
                && maximumY == sourceHeight - 1) {
            return sourceBitmap;
        }

        return Bitmap.createBitmap(
                sourceBitmap,
                minimumX,
                minimumY,
                maximumX - minimumX + 1,
                maximumY - minimumY + 1
        );
    }

    private Bitmap loadScaledEnemyBitmap(int resourceId, float widthRatio) {
        Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        if (sourceBitmap == null
                || sourceBitmap.getWidth() <= 0
                || sourceBitmap.getHeight() <= 0) {
            return null;
        }

        int targetWidth = Math.max(1, Math.round(screenWidth * widthRatio));
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

        return scaledBitmap;
    }

    private boolean areEnemyBitmapsReady() {
        return scoutEnemyBitmap != null
                && hornetEnemyBitmap != null
                && heavyBomberEnemyBitmap != null
                && crabEnemyBitmap != null
                && eliteEnemyBitmap != null
                && scoutDrone2EnemyBitmap != null
                && boazanianEnemyBitmap != null;
    }

    private boolean areEnemyProjectileBitmapsReady() {
        return scoutEnemyProjectileBitmap != null
                && scoutDrone2EnemyProjectileBitmap != null
                && hornetEnemyProjectileBitmap != null
                && heavyBomberEnemyProjectileBitmap != null
                && crabEnemyProjectileBitmap != null
                && boazanianEnemyProjectileBitmap != null
                && eliteEnemyProjectileBitmap != null;
    }

    private void releaseEnemyBitmaps() {
        releaseBitmap(scoutEnemyBitmap);
        releaseBitmap(hornetEnemyBitmap);
        releaseBitmap(heavyBomberEnemyBitmap);
        releaseBitmap(crabEnemyBitmap);
        releaseBitmap(eliteEnemyBitmap);
        releaseBitmap(scoutDrone2EnemyBitmap);
        releaseBitmap(boazanianEnemyBitmap);
        scoutEnemyBitmap = null;
        hornetEnemyBitmap = null;
        heavyBomberEnemyBitmap = null;
        crabEnemyBitmap = null;
        eliteEnemyBitmap = null;
        scoutDrone2EnemyBitmap = null;
        boazanianEnemyBitmap = null;
        preparedEnemyWidth = 0;
        preparedEnemyHeight = 0;
    }

    private void releaseEnemyProjectileBitmaps() {
        releaseBitmap(scoutEnemyProjectileBitmap);
        releaseBitmap(scoutDrone2EnemyProjectileBitmap);
        releaseBitmap(hornetEnemyProjectileBitmap);
        releaseBitmap(heavyBomberEnemyProjectileBitmap);
        releaseBitmap(crabEnemyProjectileBitmap);
        releaseBitmap(boazanianEnemyProjectileBitmap);
        releaseBitmap(eliteEnemyProjectileBitmap);
        scoutEnemyProjectileBitmap = null;
        scoutDrone2EnemyProjectileBitmap = null;
        hornetEnemyProjectileBitmap = null;
        heavyBomberEnemyProjectileBitmap = null;
        crabEnemyProjectileBitmap = null;
        boazanianEnemyProjectileBitmap = null;
        eliteEnemyProjectileBitmap = null;
        preparedEnemyProjectileWidth = 0;
        preparedEnemyProjectileHeight = 0;
    }

    private void preparePowerUpBitmapsIfReady() {
        if (screenWidth <= 0 || screenHeight <= 0) {
            return;
        }

        if (preparedPowerUpWidth == screenWidth
                && preparedPowerUpHeight == screenHeight
                && arePowerUpBitmapsReady()) {
            return;
        }

        releasePowerUpBitmaps();
        shieldPowerUpBitmap = loadScaledPowerUpBitmap(R.drawable.powerup_shield);
        rapidFirePowerUpBitmap = loadScaledPowerUpBitmap(R.drawable.powerup_rapid_fire);
        doubleScorePowerUpBitmap = loadScaledPowerUpBitmap(R.drawable.powerup_double_score);
        extraLifePowerUpBitmap = loadScaledPowerUpBitmap(R.drawable.powerup_extra_life);

        int effectResourceId = getResources().getIdentifier(
                "effect_powerup_collect",
                "drawable",
                getContext().getPackageName()
        );
        if (effectResourceId != 0) {
            powerUpCollectEffectBitmap = BitmapFactory.decodeResource(
                    getResources(),
                    effectResourceId
            );
        }

        preparedPowerUpWidth = screenWidth;
        preparedPowerUpHeight = screenHeight;
    }

    private Bitmap loadScaledPowerUpBitmap(int resourceId) {
        Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        if (sourceBitmap == null
                || sourceBitmap.getWidth() <= 0
                || sourceBitmap.getHeight() <= 0) {
            return null;
        }

        Bitmap croppedBitmap = cropToOpaqueBounds(sourceBitmap);
        if (croppedBitmap != sourceBitmap) {
            sourceBitmap.recycle();
        }

        int targetWidth = Math.max(1, Math.round(screenWidth * POWER_UP_WIDTH_RATIO));
        int targetHeight = Math.max(
                1,
                Math.round(croppedBitmap.getHeight()
                        * (targetWidth / (float) croppedBitmap.getWidth()))
        );
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                croppedBitmap,
                targetWidth,
                targetHeight,
                true
        );

        if (scaledBitmap != croppedBitmap) {
            croppedBitmap.recycle();
        }

        return scaledBitmap;
    }

    private boolean arePowerUpBitmapsReady() {
        return shieldPowerUpBitmap != null
                && rapidFirePowerUpBitmap != null
                && doubleScorePowerUpBitmap != null
                && extraLifePowerUpBitmap != null;
    }

    private void releasePowerUpBitmaps() {
        releaseBitmap(shieldPowerUpBitmap);
        releaseBitmap(rapidFirePowerUpBitmap);
        releaseBitmap(doubleScorePowerUpBitmap);
        releaseBitmap(extraLifePowerUpBitmap);
        releaseBitmap(powerUpCollectEffectBitmap);
        shieldPowerUpBitmap = null;
        rapidFirePowerUpBitmap = null;
        doubleScorePowerUpBitmap = null;
        extraLifePowerUpBitmap = null;
        powerUpCollectEffectBitmap = null;
        preparedPowerUpWidth = 0;
        preparedPowerUpHeight = 0;
    }

    private void releaseBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    private void checkPlayerBulletEnemyCollisions() {
        for (int bulletIndex = playerBullets.size() - 1; bulletIndex >= 0; bulletIndex--) {
            Bullet bullet = playerBullets.get(bulletIndex);

            for (int enemyIndex = enemies.size() - 1; enemyIndex >= 0; enemyIndex--) {
                Enemy enemy = enemies.get(enemyIndex);

                if (!intersects(bullet, enemy)) {
                    continue;
                }

                float enemyCenterX = enemy.getCenterX();
                float enemyCenterY = enemy.getY() + enemy.getHeight() / 2f;
                playerBullets.remove(bulletIndex);
                enemies.remove(enemyIndex);
                int enemyScore = getScoreForEnemy(enemy);
                score += doubleScoreTimerSeconds > 0f
                        ? enemyScore * 2
                        : enemyScore;
                updateScoreLine();
                spawnPowerUp(enemyCenterX, enemyCenterY);
                break;
            }
        }
    }

    private void spawnPowerUp(float centerX, float centerY) {
        if (powerUps.size() >= MAX_ACTIVE_POWER_UPS
                || !arePowerUpBitmapsReady()
                || enemyRandom.nextFloat() >= POWER_UP_DROP_CHANCE) {
            return;
        }

        PowerUpType type = selectPowerUpType();
        Bitmap bitmap = getPowerUpBitmap(type);
        if (bitmap == null) {
            return;
        }

        float x = Math.max(
                0f,
                Math.min(screenWidth - bitmap.getWidth(), centerX - bitmap.getWidth() / 2f)
        );
        float y = Math.max(0f, centerY - bitmap.getHeight() / 2f);
        powerUps.add(new PowerUp(
                type,
                bitmap,
                x,
                y,
                screenHeight * POWER_UP_SPEED_HEIGHT_RATIO
        ));
    }

    private PowerUpType selectPowerUpType() {
        int roll = enemyRandom.nextInt(100);
        if (roll < 30) {
            return PowerUpType.SHIELD;
        }
        if (roll < 60) {
            return PowerUpType.RAPID_FIRE;
        }
        if (roll < 85) {
            return PowerUpType.DOUBLE_SCORE;
        }
        return PowerUpType.EXTRA_LIFE;
    }

    private Bitmap getPowerUpBitmap(PowerUpType type) {
        switch (type) {
            case SHIELD:
                return shieldPowerUpBitmap;
            case RAPID_FIRE:
                return rapidFirePowerUpBitmap;
            case DOUBLE_SCORE:
                return doubleScorePowerUpBitmap;
            case EXTRA_LIFE:
                return extraLifePowerUpBitmap;
            default:
                return null;
        }
    }

    private void updatePowerUpTimers(float deltaSeconds) {
        if (rapidFireTimerSeconds > 0f) {
            rapidFireTimerSeconds = Math.max(0f, rapidFireTimerSeconds - deltaSeconds);
        }
        if (doubleScoreTimerSeconds > 0f) {
            doubleScoreTimerSeconds = Math.max(0f, doubleScoreTimerSeconds - deltaSeconds);
        }
        updatePowerUpStatusLine();
    }

    private void updatePowerUps(float deltaSeconds) {
        for (PowerUp powerUp : powerUps) {
            powerUp.update(deltaSeconds);
        }
    }

    private void resolvePlayerPowerUpCollisions() {
        Player currentPlayer = player;
        if (currentPlayer == null || !currentPlayer.isPrepared()) {
            return;
        }

        for (int index = powerUps.size() - 1; index >= 0; index--) {
            PowerUp powerUp = powerUps.get(index);
            if (!intersectsPlayerAndPowerUp(currentPlayer, powerUp)) {
                continue;
            }

            powerUps.remove(index);
            applyPowerUp(powerUp);
        }
    }

    private void applyPowerUp(PowerUp powerUp) {
        switch (powerUp.getType()) {
            case SHIELD:
                shieldActive = true;
                break;
            case RAPID_FIRE:
                rapidFireTimerSeconds = RAPID_FIRE_DURATION_SECONDS;
                break;
            case DOUBLE_SCORE:
                doubleScoreTimerSeconds = DOUBLE_SCORE_DURATION_SECONDS;
                break;
            case EXTRA_LIFE:
                playerLives = Math.min(MAX_PLAYER_LIVES, playerLives + 1);
                updateLivesLine();
                break;
            default:
                return;
        }

        updatePowerUpStatusLine();
        addPowerUpCollectEffect(powerUp.getCenterX(), powerUp.getCenterY());
    }

    private void addPowerUpCollectEffect(float centerX, float centerY) {
        if (powerUpCollectEffectBitmap == null) {
            return;
        }

        float effectSize = screenWidth * POWER_UP_EFFECT_WIDTH_RATIO;
        powerUpCollectEffects.add(new PowerUpCollectEffect(
                powerUpCollectEffectBitmap,
                POWER_UP_EFFECT_FRAME_COUNT,
                centerX,
                centerY,
                effectSize,
                effectSize,
                POWER_UP_EFFECT_FRAME_DURATION_SECONDS
        ));
    }

    private void updatePowerUpCollectEffects(float deltaSeconds) {
        for (PowerUpCollectEffect effect : powerUpCollectEffects) {
            effect.update(deltaSeconds);
        }
    }

    private void drawPowerUps(Canvas canvas) {
        for (PowerUp powerUp : powerUps) {
            powerUp.draw(canvas, powerUpPaint);
        }
    }

    private void drawPowerUpCollectEffects(Canvas canvas) {
        for (PowerUpCollectEffect effect : powerUpCollectEffects) {
            effect.draw(canvas, powerUpEffectPaint);
        }
    }

    private void removeOffScreenPowerUps() {
        for (int index = powerUps.size() - 1; index >= 0; index--) {
            if (powerUps.get(index).isOffScreen(screenHeight)) {
                powerUps.remove(index);
            }
        }
    }

    private void removeFinishedPowerUpCollectEffects() {
        for (int index = powerUpCollectEffects.size() - 1; index >= 0; index--) {
            if (powerUpCollectEffects.get(index).isFinished()) {
                powerUpCollectEffects.remove(index);
            }
        }
    }

    private void checkPlayerEnemyCollisions() {
        Player currentPlayer = player;
        if (!canPlayerTakeDamage(currentPlayer)) {
            return;
        }

        for (int enemyIndex = enemies.size() - 1; enemyIndex >= 0; enemyIndex--) {
            Enemy enemy = enemies.get(enemyIndex);
            if (!intersectsPlayerAndEnemy(currentPlayer, enemy)) {
                continue;
            }

            enemies.remove(enemyIndex);
            damagePlayer();
            break;
        }
    }

    private boolean canPlayerTakeDamage(Player currentPlayer) {
        return currentPlayer != null
                && currentPlayer.isPrepared()
                && playerLives > 0
                && !playerInvulnerable;
    }

    private void damagePlayer() {
        if (playerLives <= 0 || playerInvulnerable) {
            return;
        }

        if (shieldActive) {
            shieldActive = false;
            updatePowerUpStatusLine();
            return;
        }

        playerLives = Math.max(0, playerLives - 1);
        playerInvulnerable = true;
        playerInvulnerabilityTimerSeconds = PLAYER_INVULNERABILITY_SECONDS;
        updateLivesLine();
    }

    private void updatePlayerInvulnerability(float deltaSeconds) {
        if (!playerInvulnerable) {
            return;
        }

        playerInvulnerabilityTimerSeconds -= deltaSeconds;
        if (playerInvulnerabilityTimerSeconds <= 0f) {
            playerInvulnerabilityTimerSeconds = 0f;
            playerInvulnerable = false;
        }
    }

    private boolean shouldDrawPlayer() {
        if (!playerInvulnerable) {
            return true;
        }

        int blinkPhase = (int) (
                playerInvulnerabilityTimerSeconds / PLAYER_BLINK_INTERVAL_SECONDS
        );
        return blinkPhase % 2 == 0;
    }

    private boolean intersectsPlayerAndEnemy(Player currentPlayer, Enemy enemy) {
        float enemyHorizontalInset = enemy.getWidth() * ENEMY_HITBOX_INSET_RATIO;
        float enemyVerticalInset = enemy.getHeight() * ENEMY_HITBOX_INSET_RATIO;
        float enemyLeft = enemy.getX() + enemyHorizontalInset;
        float enemyTop = enemy.getY() + enemyVerticalInset;
        float enemyRight = enemy.getX() + enemy.getWidth() - enemyHorizontalInset;
        float enemyBottom = enemy.getY() + enemy.getHeight() - enemyVerticalInset;

        return intersectsPlayerBounds(
                currentPlayer,
                enemyLeft,
                enemyTop,
                enemyRight,
                enemyBottom
        );
    }

    private boolean intersectsPlayerAndPowerUp(
            Player currentPlayer,
            PowerUp powerUp
    ) {
        return intersectsPlayerBounds(
                currentPlayer,
                powerUp.getX(),
                powerUp.getY(),
                powerUp.getX() + powerUp.getWidth(),
                powerUp.getY() + powerUp.getHeight()
        );
    }

    private boolean intersectsPlayerAndEnemyBullet(
            Player currentPlayer,
            EnemyBullet enemyBullet
    ) {
        float bulletHorizontalInset = enemyBullet.getWidth()
                * ENEMY_BULLET_HITBOX_INSET_RATIO;
        float bulletVerticalInset = enemyBullet.getHeight()
                * ENEMY_BULLET_HITBOX_INSET_RATIO;
        float bulletLeft = enemyBullet.getX() + bulletHorizontalInset;
        float bulletTop = enemyBullet.getY() + bulletVerticalInset;
        float bulletRight = enemyBullet.getX()
                + enemyBullet.getWidth()
                - bulletHorizontalInset;
        float bulletBottom = enemyBullet.getY()
                + enemyBullet.getHeight()
                - bulletVerticalInset;

        return intersectsPlayerBounds(
                currentPlayer,
                bulletLeft,
                bulletTop,
                bulletRight,
                bulletBottom
        );
    }

    private boolean intersectsPlayerBounds(
            Player currentPlayer,
            float targetLeft,
            float targetTop,
            float targetRight,
            float targetBottom
    ) {
        float playerWidth = currentPlayer.getWidth();
        float playerHeight = currentPlayer.getHeight();
        float playerHorizontalInset = playerWidth * PLAYER_HITBOX_INSET_RATIO;
        float playerVerticalInset = playerHeight * PLAYER_HITBOX_INSET_RATIO;
        float playerCenterX = currentPlayer.getCenterX();
        float playerCenterY = currentPlayer.getCenterY();
        float playerLeft = playerCenterX - playerWidth / 2f + playerHorizontalInset;
        float playerTop = playerCenterY - playerHeight / 2f + playerVerticalInset;
        float playerRight = playerCenterX + playerWidth / 2f - playerHorizontalInset;
        float playerBottom = playerCenterY + playerHeight / 2f - playerVerticalInset;

        return rectanglesOverlap(
                playerLeft,
                playerTop,
                playerRight,
                playerBottom,
                targetLeft,
                targetTop,
                targetRight,
                targetBottom
        );
    }

    private boolean intersects(Bullet bullet, Enemy enemy) {
        float bulletHorizontalInset = bullet.getWidth() * BULLET_HITBOX_INSET_RATIO;
        float bulletVerticalInset = bullet.getHeight() * BULLET_HITBOX_INSET_RATIO;
        float bulletLeft = bullet.getX() + bulletHorizontalInset;
        float bulletTop = bullet.getY() + bulletVerticalInset;
        float bulletRight = bullet.getX() + bullet.getWidth() - bulletHorizontalInset;
        float bulletBottom = bullet.getY() + bullet.getHeight() - bulletVerticalInset;

        float enemyHorizontalInset = enemy.getWidth() * ENEMY_HITBOX_INSET_RATIO;
        float enemyVerticalInset = enemy.getHeight() * ENEMY_HITBOX_INSET_RATIO;
        float enemyLeft = enemy.getX() + enemyHorizontalInset;
        float enemyTop = enemy.getY() + enemyVerticalInset;
        float enemyRight = enemy.getX() + enemy.getWidth() - enemyHorizontalInset;
        float enemyBottom = enemy.getY() + enemy.getHeight() - enemyVerticalInset;

        return bulletLeft < enemyRight
                && bulletRight > enemyLeft
                && bulletTop < enemyBottom
                && bulletBottom > enemyTop;
    }

    private int getScoreForEnemy(Enemy enemy) {
        switch (enemy.getType()) {
            case ENEMY_HORNET:
                return SCORE_HORNET;
            case ENEMY_HEAVY_BOMBER:
                return SCORE_HEAVY_BOMBER;
            case ENEMY_CRAB:
                return SCORE_CRAB;
            case ENEMY_ELITE:
                return SCORE_ELITE;
            case ENEMY_SCOUT_DRONE_2:
                return SCORE_SCOUT_DRONE_2;
            case ENEMY_BOAZANIAN:
                return SCORE_BOAZANIAN;
            case ENEMY_SCOUT:
            default:
                return SCORE_SCOUT;
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
        fireCooldownSeconds = getCurrentPlayerFireIntervalSeconds();
    }

    private float getCurrentPlayerFireIntervalSeconds() {
        return rapidFireTimerSeconds > 0f
                ? AUTO_FIRE_INTERVAL_SECONDS * RAPID_FIRE_INTERVAL_MULTIPLIER
                : AUTO_FIRE_INTERVAL_SECONDS;
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
        for (Bullet bullet : playerBullets) {
            bullet.update(deltaSeconds);
        }
    }

    private void updateEnemyBullets(float deltaSeconds) {
        for (EnemyBullet enemyBullet : enemyBullets) {
            enemyBullet.update(deltaSeconds);
        }
    }

    private void resolveEnemyBulletPlayerCollisions() {
        Player currentPlayer = player;
        if (currentPlayer == null || !currentPlayer.isPrepared()) {
            return;
        }

        for (int index = enemyBullets.size() - 1; index >= 0; index--) {
            EnemyBullet enemyBullet = enemyBullets.get(index);
            if (!intersectsPlayerAndEnemyBullet(currentPlayer, enemyBullet)) {
                continue;
            }

            enemyBullets.remove(index);
            if (canPlayerTakeDamage(currentPlayer)) {
                damagePlayer();
            }
        }
    }

    private void removeOffScreenEnemyBullets() {
        for (int index = enemyBullets.size() - 1; index >= 0; index--) {
            if (enemyBullets.get(index).isOffScreen(screenHeight)) {
                enemyBullets.remove(index);
            }
        }
    }

    private void removeOffScreenPlayerBullets() {
        for (int index = playerBullets.size() - 1; index >= 0; index--) {
            if (playerBullets.get(index).isOffScreen()) {
                playerBullets.remove(index);
            }
        }
    }

    private void updateScoreLine() {
        scoreLine = String.format(Locale.US, "SCORE %06d", score);
    }

    private void updateLivesLine() {
        livesLine = String.format(Locale.US, "LIVES %d", playerLives);
    }

    private void updatePowerUpStatusLine() {
        int rapidFireSeconds = rapidFireTimerSeconds > 0f
                ? (int) Math.ceil(rapidFireTimerSeconds)
                : 0;
        int doubleScoreSeconds = doubleScoreTimerSeconds > 0f
                ? (int) Math.ceil(doubleScoreTimerSeconds)
                : 0;
        if (rapidFireSeconds == lastRapidFireStatusSeconds
                && doubleScoreSeconds == lastDoubleScoreStatusSeconds
                && shieldActive == lastShieldStatus) {
            return;
        }

        lastRapidFireStatusSeconds = rapidFireSeconds;
        lastDoubleScoreStatusSeconds = doubleScoreSeconds;
        lastShieldStatus = shieldActive;

        StringBuilder status = new StringBuilder();
        if (shieldActive) {
            status.append("SHIELD");
        }
        if (rapidFireSeconds > 0) {
            appendPowerUpStatus(status, "RAPID ", rapidFireSeconds);
        }
        if (doubleScoreSeconds > 0) {
            appendPowerUpStatus(status, "2X SCORE ", doubleScoreSeconds);
        }
        powerUpStatusLine = status.toString();
    }

    private void appendPowerUpStatus(
            StringBuilder status,
            String label,
            int seconds
    ) {
        if (status.length() > 0) {
            status.append(" | ");
        }
        status.append(label).append(seconds).append('s');
    }

    private void drawHud(Canvas canvas) {
        if ((scoreLine == null || scoreLine.isEmpty())
                && (livesLine == null || livesLine.isEmpty())
                && (powerUpStatusLine == null || powerUpStatusLine.isEmpty())
                && (diagnosticLine == null || diagnosticLine.isEmpty())) {
            return;
        }

        float panelLeft = 12f * density;
        float panelTop = topSystemInsetPixels + 8f * density;
        float panelPadding = 8f * density;
        float lineSpacing = 4f * density;
        boolean hasScore = scoreLine != null && !scoreLine.isEmpty();
        boolean hasLives = livesLine != null && !livesLine.isEmpty();
        boolean hasPowerUpStatus = powerUpStatusLine != null && !powerUpStatusLine.isEmpty();
        boolean hasDiagnostic = diagnosticLine != null && !diagnosticLine.isEmpty();
        float contentWidth = 0f;
        float contentHeight = 0f;

        if (hasScore) {
            contentWidth = Math.max(contentWidth, scorePaint.measureText(scoreLine));
            contentHeight += scorePaint.getTextSize();
        }
        if (hasLives) {
            contentWidth = Math.max(contentWidth, scorePaint.measureText(livesLine));
            if (contentHeight > 0f) {
                contentHeight += lineSpacing;
            }
            contentHeight += scorePaint.getTextSize();
        }
        if (hasDiagnostic) {
            contentWidth = Math.max(contentWidth, infoPaint.measureText(diagnosticLine));
            if (contentHeight > 0f) {
                contentHeight += lineSpacing;
            }
            contentHeight += infoPaint.getTextSize();
        }
        if (hasPowerUpStatus) {
            contentWidth = Math.max(contentWidth, infoPaint.measureText(powerUpStatusLine));
            if (contentHeight > 0f) {
                contentHeight += lineSpacing;
            }
            contentHeight += infoPaint.getTextSize();
        }

        float panelWidth = contentWidth + panelPadding * 2f;
        float panelHeight = contentHeight + panelPadding * 2f;
        float panelRight = panelLeft + panelWidth;
        float panelBottom = panelTop + panelHeight;

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
                    float minimumTop = topSystemInsetPixels + 8f * density;
                    float maximumTop = canvas.getHeight() - panelHeight - 8f * density;
                    panelTop = Math.max(minimumTop, Math.min(
                            playerBottom + 8f * density,
                            maximumTop
                    ));
                    panelBottom = panelTop + panelHeight;
                }
            }
        }

        canvas.drawRect(
                panelLeft,
                panelTop,
                panelRight,
                panelBottom,
                diagnosticPanelPaint
        );

        float textLeft = panelLeft + panelPadding;
        float textBaseline = panelTop + panelPadding;
        if (hasScore) {
            textBaseline += scorePaint.getTextSize();
            canvas.drawText(scoreLine, textLeft, textBaseline, scorePaint);
        }
        if (hasLives) {
            if (hasScore) {
                textBaseline += lineSpacing + scorePaint.getTextSize();
            } else {
                textBaseline += scorePaint.getTextSize();
            }
            canvas.drawText(livesLine, textLeft, textBaseline, scorePaint);
        }
        if (hasDiagnostic) {
            if (hasScore || hasLives) {
                textBaseline += lineSpacing + infoPaint.getTextSize();
            } else {
                textBaseline += infoPaint.getTextSize();
            }
            canvas.drawText(diagnosticLine, textLeft, textBaseline, infoPaint);
        }
        if (hasPowerUpStatus) {
            if (hasScore || hasLives || hasDiagnostic) {
                textBaseline += lineSpacing + infoPaint.getTextSize();
            } else {
                textBaseline += infoPaint.getTextSize();
            }
            canvas.drawText(powerUpStatusLine, textLeft, textBaseline, infoPaint);
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

    private boolean isSupportedDifficultyId(String difficultyId) {
        return "easy".equals(difficultyId)
                || "normal".equals(difficultyId)
                || "hard".equals(difficultyId);
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
