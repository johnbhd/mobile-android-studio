package com.example.voltesvsuperrobotstrike.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
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
    private static final float SKILL_COOLDOWN_SECONDS = 25f;
    private static final float SKILL_ACTIVATION_EFFECT_SECONDS = 0.20f;
    private static final float CREWZER_SKILL_DURATION_SECONDS = 6f;
    private static final float CREWZER_MOVEMENT_MULTIPLIER = 1.40f;
    private static final float CREWZER_FIRE_INTERVAL_MULTIPLIER = 0.85f;
    private static final float CREWZER_PROJECTILE_SPEED_MULTIPLIER = 1.15f;
    private static final float BOMBER_SKILL_DURATION_SECONDS = 4f;
    private static final float BOMBER_BOMB_INTERVAL_SECONDS = 1.30f;
    private static final float BOMBER_BOMB_SPEED_MULTIPLIER = 0.70f;
    private static final float BOMBER_BOMB_WIDTH_RATIO = 0.09f;
    private static final float BOMBER_BOMB_HEIGHT_RATIO = 0.09f;
    private static final float BOMBER_BOMB_DETONATION_THRESHOLD_RATIO = 0.25f;
    private static final float BOMBER_EXPLOSION_DURATION_SECONDS = 0.35f;
    private static final float BOMBER_EXPLOSION_RADIUS_RATIO = 0.20f;
    private static final float ENEMY_EXPLOSION_DURATION_SECONDS = 0.25f;
    private static final float ENEMY_EXPLOSION_RADIUS_RATIO = 0.10f;
    private static final float ENEMY_EXPLOSION_BITMAP_SCALE = 0.50f;
    private static final int BOMBER_BOMB_COUNT = 3;
    private static final float PANZER_SKILL_DURATION_SECONDS = 5f;
    private static final float PANZER_PROJECTILE_SCALE = 1.70f;
    private static final float PANZER_PROJECTILE_SPEED_MULTIPLIER = 0.88f;
    private static final float PANZER_FIRE_INTERVAL_MULTIPLIER = 1.20f;
    private static final float FRIGATE_SKILL_DURATION_SECONDS = 12f;
    private static final float FRIGATE_TWIN_SHOT_INTERVAL_SECONDS = 0.12f;
    private static final float LANDER_SKILL_DURATION_SECONDS = 15f;
    private static final float LANDER_SHIELD_RADIUS_MULTIPLIER = 0.72f;
    private static final float RAPID_FIRE_DURATION_SECONDS = 8f;
    private static final float RAPID_FIRE_INTERVAL_MULTIPLIER = 0.55f;
    private static final float TWIN_SHOT_DURATION_SECONDS = 8f;
    private static final float TWIN_SHOT_SPACING_RATIO = 0.18f;
    private static final float DOUBLE_SCORE_DURATION_SECONDS = 10f;
    private static final float POWER_UP_DROP_CHANCE = 0.10f;
    private static final int MAX_ACTIVE_POWER_UPS = 2;
    private static final int SHIELD_POWER_UP_WEIGHT = 25;
    private static final int RAPID_FIRE_POWER_UP_WEIGHT = 25;
    private static final int DOUBLE_SCORE_POWER_UP_WEIGHT = 20;
    private static final int EXTRA_LIFE_POWER_UP_WEIGHT = 15;
    private static final int TWIN_SHOT_POWER_UP_WEIGHT = 15;
    private static final float POWER_UP_SPEED_HEIGHT_RATIO = 0.15f;
    private static final float POWER_UP_WIDTH_RATIO = 0.09f;
    private static final float POWER_UP_EFFECT_WIDTH_RATIO = 0.14f;
    private static final float POWER_UP_EFFECT_FRAME_DURATION_SECONDS = 0.09f;
    private static final int POWER_UP_EFFECT_FRAME_COUNT = 4;
    private static final float GAME_OVER_HOLD_SECONDS = 1.0f;
    private static final float HEART_LIVE_SIZE_DP = 24f;
    private static final float HUD_MARGIN_DP = 10f;
    private static final float HUD_PANEL_PADDING_DP = 8f;
    private static final float HUD_ROW_SPACING_DP = 4f;
    private static final float HUD_HEART_SPACING_DP = 3f;
    private static final float HUD_HEIGHT_DP = 68f;
    private static final float PAUSE_BUTTON_SIZE_DP = 52f;
    private static final float HUD_PAUSE_GAP_DP = 6f;
    private static final float SKILL_EFFECT_RING_WIDTH_DP = 3f;
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
    private static final float BOTTOM_CENTER_ZONE_LEFT_RATIO = 0.35f;
    private static final float BOTTOM_CENTER_ZONE_RIGHT_RATIO = 0.65f;
    private static final float BOTTOM_CENTER_ZONE_TOP_RATIO = 0.75f;
    private static final float PLAYER_CAMPING_MOVEMENT_TOLERANCE_DP = 24f;
    private static final float CAMPING_DURATION_EASY_SECONDS = 4f;
    private static final float CAMPING_DURATION_NORMAL_SECONDS = 3f;
    private static final float CAMPING_DURATION_HARD_SECONDS = 2.5f;
    private static final float ANTI_CAMPING_COOLDOWN_EASY_SECONDS = 6f;
    private static final float ANTI_CAMPING_COOLDOWN_NORMAL_SECONDS = 5f;
    private static final float ANTI_CAMPING_COOLDOWN_HARD_SECONDS = 4f;
    private static final float BOAZANIAN_ROTATION_SPEED_DEGREES_PER_SECOND = 150f;
    private static final float HEAVY_BOMBER_DECISION_DELAY_MIN_SECONDS = 1.5f;
    private static final float HEAVY_BOMBER_DECISION_DELAY_MAX_SECONDS = 2.8f;
    private static final float HEAVY_BOMBER_MANEUVER_DURATION_MIN_SECONDS = 0.8f;
    private static final float HEAVY_BOMBER_MANEUVER_DURATION_MAX_SECONDS = 1.5f;
    private static final float HEAVY_BOMBER_RETREAT_DISTANCE_RATIO = 0.11f;
    private static final float HEAVY_BOMBER_RETREAT_SPEED_RATIO = 0.13f;
    private static final float HEAVY_BOMBER_STRAFE_SPEED_RATIO = 0.14f;
    private static final int SPAWN_LANE_COUNT = 5;
    private static final int[] SPAWN_LANE_WEIGHTS = {15, 20, 30, 20, 15};
    private static final float[] SPAWN_LANE_CENTER_FRACTIONS = {
            0.10f, 0.30f, 0.50f, 0.70f, 0.90f
    };
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
    private static final int TARGET_FRAME_RATE = 60;
    private static final long NANOS_PER_SECOND = 1_000_000_000L;
    private static final long TARGET_FRAME_DURATION_NANOS =
            NANOS_PER_SECOND / TARGET_FRAME_RATE;
    private static final long THREAD_JOIN_TIMEOUT_MILLIS = 500L;

    private final SurfaceHolder surfaceHolder;
    private final Object gameThreadLock = new Object();
    private final Paint infoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint hudPanelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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
    private final Paint skillProjectilePaint = new Paint(
            Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG
    );
    private final Paint skillEffectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint hudBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint heartPaint = new Paint(
            Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG
    );
    private final Paint scorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint difficultyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint livesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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
    private final ArrayList<BombardmentBomb> bombardmentBombs = new ArrayList<>();
    private final ArrayList<SkillExplosion> skillExplosions = new ArrayList<>();
    private final Random enemyRandom = new Random();
    private volatile Player player;
    private final String scoreLabel;
    private final String livesLabel;

    private volatile boolean running;
    private volatile boolean surfaceReady;
    private volatile boolean activityResumed;
    private volatile boolean menuPaused;
    private volatile boolean resetFrameTimeBaselineRequested;
    private volatile boolean skillActive;
    private volatile boolean skillActivationRequested;
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
    private Bitmap twinShotPowerUpBitmap;
    private Bitmap powerUpCollectEffectBitmap;
    private Bitmap heartLiveBitmap;
    private Bitmap crewzerAuraBitmap;
    private Bitmap bombardmentBombBitmap;
    private Bitmap bombardmentExplosionBitmap;
    private Bitmap panzerPowerShotBitmap;
    private Bitmap frigateBarrageShotBitmap;
    private Bitmap landerEnergyShieldBitmap;
    private Bitmap shieldEffectBitmap;
    private int preparedPowerUpWidth;
    private int preparedPowerUpHeight;
    private int preparedHeartLiveWidth;
    private int preparedHeartLiveHeight;
    private int preparedSkillEffectWidth;
    private int preparedSkillEffectHeight;
    private float enemySpawnCooldownSeconds = ENEMY_FIRST_SPAWN_DELAY_SECONDS;
    private float gameplayTimeSeconds;
    private boolean alternatingSpawnFromLeft;
    private int lastSpawnLane = -1;
    private int pressureSpawnLane = -1;
    private float bottomCenterCampTimerSeconds;
    private float antiCampingCooldownSeconds;
    private float campReferenceCenterX;
    private float campReferenceCenterY;
    private boolean campReferenceInitialized;

    private String selectedMachineId = "volt_crewzer";
    private String selectedDifficultyId = "normal";
    private String skillLabel;
    private int score;
    private String scoreLine;
    private int playerLives = INITIAL_PLAYER_LIVES;
    private boolean playerInvulnerable;
    private float playerInvulnerabilityTimerSeconds;
    private boolean shieldActive;
    private float rapidFireTimerSeconds;
    private float twinShotTimerSeconds;
    private float doubleScoreTimerSeconds;
    private float skillRemainingSeconds;
    private float skillCooldownRemainingSeconds;
    private float skillActivationEffectRemainingSeconds;
    private float skillActivationCenterX;
    private float skillActivationCenterY;
    private float bomberBombScheduleSeconds;
    private int bomberBombsSpawned;
    private String livesLine;
    private String powerUpStatusLine;
    private int lastRapidFireStatusSeconds = -1;
    private int lastTwinShotStatusSeconds = -1;
    private int lastDoubleScoreStatusSeconds = -1;
    private boolean lastShieldStatus;
    private String difficultyLine;
    private volatile boolean gameOver;
    private boolean gameOverCallbackSent;
    private float gameOverTimerSeconds;
    private int finalScore;
    private volatile GameOverListener gameOverListener;
    private volatile SkillStateListener skillStateListener;
    private SkillState lastNotifiedSkillState;
    private int lastNotifiedSkillSeconds = -1;
    private String lastNotifiedSkillLabel;

    public enum SkillState {
        READY,
        ACTIVE,
        COOLDOWN
    }

    public interface SkillStateListener {

        void onSkillStateChanged(
                String skillLabel,
                SkillState skillState,
                int remainingSeconds
        );
    }

    public interface GameOverListener {

        void onGameOver(
                int finalScore,
                String selectedMachineId,
                String selectedDifficultyId
        );
    }

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
        scoreLabel = getResources().getString(R.string.game_hud_score_label);
        livesLabel = getResources().getString(R.string.game_hud_lives_label);
        backgroundColor = ContextCompat.getColor(context, R.color.game_background);
        scrollingBackground = new ScrollingBackground(context);
        player = new Player(getResources(), R.drawable.volt_crewzer, density);

        initializePaints();
        updateScoreLine();
        updateLivesLine();
        updatePowerUpStatusLine();
        updateDifficultyLine();
        setFocusable(true);
        setClickable(true);
        initializeSystemBarInsets();
    }

    public void setGameOverListener(GameOverListener listener) {
        gameOverListener = listener;
    }

    public void setSkillStateListener(SkillStateListener listener) {
        skillStateListener = listener;
        lastNotifiedSkillState = null;
        lastNotifiedSkillSeconds = -1;
        lastNotifiedSkillLabel = null;
        notifySkillStateChangedIfNeeded();
    }

    public void requestSkillActivation() {
        if (menuPaused || gameOver || !activityResumed || !surfaceReady) {
            return;
        }

        skillActivationRequested = true;
    }

    public void setMenuPaused(boolean paused) {
        if (paused && gameOver) {
            return;
        }

        menuPaused = paused;
        if (paused) {
            skillActivationRequested = false;
        }
        resetTouchState();
        resetFrameTimeBaselineRequested = true;
        lastNotifiedSkillState = null;
        notifySkillStateChangedIfNeeded();
    }

    public boolean isMenuPaused() {
        return menuPaused;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isSkillInputReady() {
        return activityResumed && surfaceReady && !menuPaused && !gameOver;
    }

    private void initializePaints() {
        int mutedTextColor = ContextCompat.getColor(
                getContext(),
                R.color.game_hud_muted
        );
        int hudPanelColor = ContextCompat.getColor(
                getContext(),
                R.color.game_hud_panel
        );

        infoPaint.setColor(mutedTextColor);
        infoPaint.setTextSize(12f * getResources().getDisplayMetrics().scaledDensity);
        infoPaint.setTextAlign(Paint.Align.LEFT);
        infoPaint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));

        hudPanelPaint.setColor(hudPanelColor);

        hudBorderPaint.setColor(ContextCompat.getColor(
                getContext(),
                R.color.game_hud_border
        ));
        hudBorderPaint.setStyle(Paint.Style.STROKE);
        hudBorderPaint.setStrokeWidth(Math.max(1f, density));

        skillEffectPaint.setStrokeWidth(
                Math.max(1f, SKILL_EFFECT_RING_WIDTH_DP * density)
        );

        scorePaint.setColor(ContextCompat.getColor(
                getContext(),
                R.color.game_hud_value
        ));
        scorePaint.setTextSize(20f * getResources().getDisplayMetrics().scaledDensity);
        scorePaint.setTextAlign(Paint.Align.LEFT);
        scorePaint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));

        difficultyPaint.setTextSize(16f * getResources().getDisplayMetrics().scaledDensity);
        difficultyPaint.setTextAlign(Paint.Align.CENTER);
        difficultyPaint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));

        livesPaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
        livesPaint.setTextSize(12f * getResources().getDisplayMetrics().scaledDensity);
        livesPaint.setTextAlign(Paint.Align.LEFT);
        livesPaint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
    }

    public void configureGame(String selectedMachine, String selectedDifficulty) {
        selectedMachineId = isSupportedMachineId(selectedMachine)
                ? selectedMachine
                : "volt_crewzer";
        selectedDifficultyId = isSupportedDifficultyId(selectedDifficulty)
                ? selectedDifficulty
                : "normal";
        skillLabel = resolveSkillLabel(selectedMachineId);

        playerBullets.clear();
        enemyBullets.clear();
        powerUps.clear();
        powerUpCollectEffects.clear();
        bombardmentBombs.clear();
        skillExplosions.clear();
        skillActivationRequested = false;
        fireCooldownSeconds = AUTO_FIRE_INTERVAL_SECONDS;
        releaseProjectileBitmap();
        enemies.clear();
        enemySpawnCooldownSeconds = ENEMY_FIRST_SPAWN_DELAY_SECONDS;
        gameplayTimeSeconds = 0f;
        alternatingSpawnFromLeft = false;
        resetAntiCampingState();
        releaseEnemyBitmaps();
        releaseEnemyProjectileBitmaps();
        score = 0;
        updateScoreLine();
        playerLives = INITIAL_PLAYER_LIVES;
        playerInvulnerable = false;
        playerInvulnerabilityTimerSeconds = 0f;
        shieldActive = false;
        rapidFireTimerSeconds = 0f;
        twinShotTimerSeconds = 0f;
        doubleScoreTimerSeconds = 0f;
        skillActive = false;
        skillRemainingSeconds = 0f;
        skillCooldownRemainingSeconds = 0f;
        skillActivationEffectRemainingSeconds = 0f;
        skillActivationCenterX = 0f;
        skillActivationCenterY = 0f;
        bomberBombScheduleSeconds = 0f;
        bomberBombsSpawned = 0;
        gameOver = false;
        gameOverCallbackSent = false;
        gameOverTimerSeconds = 0f;
        finalScore = 0;
        menuPaused = false;
        resetFrameTimeBaselineRequested = true;
        updateLivesLine();
        updatePowerUpStatusLine();
        notifySkillStateChangedIfNeeded();

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
        prepareHeartLiveBitmapIfReady();
        prepareSkillEffectBitmapsIfReady();

        updateDifficultyLine();
    }

    public void resumeGame() {
        activityResumed = true;
        lastNotifiedSkillState = null;
        startGameThreadIfReady();
        notifySkillStateChangedIfNeeded();
    }

    public void pauseGame() {
        activityResumed = false;
        lastNotifiedSkillState = null;
        stopGameThread();
        notifySkillStateChangedIfNeeded();
    }

    public void releaseGame() {
        pauseGame();
        menuPaused = false;
        resetTouchState();
        scrollingBackground.release();
        playerBullets.clear();
        enemyBullets.clear();
        powerUps.clear();
        powerUpCollectEffects.clear();
        bombardmentBombs.clear();
        skillExplosions.clear();
        releaseProjectileBitmap();
        enemies.clear();
        releaseEnemyBitmaps();
        releaseEnemyProjectileBitmaps();
        releasePowerUpBitmaps();
        releaseHeartLiveBitmap();
        releaseSkillEffectBitmaps();

        Player currentPlayer = player;
        if (currentPlayer != null) {
            currentPlayer.release();
        }

        gameOverListener = null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceReady = true;
        applySurfaceFrameRateHint();
        updateSurfaceDimensions(getWidth(), getHeight());
        startGameThreadIfReady();
        lastNotifiedSkillState = null;
        notifySkillStateChangedIfNeeded();
    }

    @Override
    public void surfaceChanged(
            SurfaceHolder holder,
            int format,
            int width,
            int height
    ) {
        applySurfaceFrameRateHint();
        updateSurfaceDimensions(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceReady = false;
        stopGameThread();
    }

    private void applySurfaceFrameRateHint() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return;
        }

        Surface surface = surfaceHolder.getSurface();
        if (surface == null || !surface.isValid()) {
            return;
        }

        try {
            surface.setFrameRate(
                    TARGET_FRAME_RATE,
                    Surface.FRAME_RATE_COMPATIBILITY_FIXED_SOURCE
            );
        } catch (IllegalArgumentException | IllegalStateException ignored) {
            // The hint is optional; keep the existing Canvas pacing if it cannot apply.
        }
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
                bombardmentBombs.clear();
                skillExplosions.clear();
                fireCooldownSeconds = AUTO_FIRE_INTERVAL_SECONDS;
                enemies.clear();
                enemySpawnCooldownSeconds = ENEMY_FIRST_SPAWN_DELAY_SECONDS;
                gameplayTimeSeconds = 0f;
                alternatingSpawnFromLeft = false;
                resetAntiCampingState();
                releaseEnemyBitmaps();
                releaseEnemyProjectileBitmaps();
                releasePowerUpBitmaps();
                releaseHeartLiveBitmap();
                releaseSkillEffectBitmaps();
            }

            prepareProjectileBitmapIfReady();
            prepareEnemyBitmapsIfReady();
            prepareEnemyProjectileBitmapsIfReady();
            preparePowerUpBitmapsIfReady();
            prepareHeartLiveBitmapIfReady();
            prepareSkillEffectBitmapsIfReady();
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
        long previousFrameTimeNanos = System.nanoTime();
        long nextFrameDeadlineNanos = previousFrameTimeNanos
                + TARGET_FRAME_DURATION_NANOS;

        try {
            while (running) {
                long frameStartTime = System.nanoTime();
                float deltaSeconds;
                if (resetFrameTimeBaselineRequested) {
                    resetFrameTimeBaselineRequested = false;
                    previousFrameTimeNanos = frameStartTime;
                    nextFrameDeadlineNanos = frameStartTime
                            + TARGET_FRAME_DURATION_NANOS;
                    deltaSeconds = 0f;
                } else {
                    deltaSeconds = (frameStartTime - previousFrameTimeNanos)
                            / (float) NANOS_PER_SECOND;
                    previousFrameTimeNanos = frameStartTime;
                }

                if (deltaSeconds < 0f) {
                    deltaSeconds = 0f;
                } else if (deltaSeconds > MAX_DELTA_SECONDS) {
                    deltaSeconds = MAX_DELTA_SECONDS;
                }

                update(deltaSeconds);
                render();

                long afterFrameTimeNanos = System.nanoTime();
                long remainingNanos = nextFrameDeadlineNanos
                        - afterFrameTimeNanos;

                if (remainingNanos > 0L) {
                    if (!sleepForNanos(remainingNanos)) {
                        break;
                    }

                    long wakeTimeNanos = System.nanoTime();
                    long advancedDeadlineNanos = nextFrameDeadlineNanos
                            + TARGET_FRAME_DURATION_NANOS;
                    if (wakeTimeNanos >= advancedDeadlineNanos) {
                        nextFrameDeadlineNanos = wakeTimeNanos
                                + TARGET_FRAME_DURATION_NANOS;
                    } else {
                        nextFrameDeadlineNanos = advancedDeadlineNanos;
                    }
                } else {
                    nextFrameDeadlineNanos = afterFrameTimeNanos
                            + TARGET_FRAME_DURATION_NANOS;
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
        if (gameOver) {
            scrollingBackground.update(deltaSeconds);
            updateGameOver(deltaSeconds);
            return;
        }

        if (menuPaused) {
            return;
        }

        processSkillActivationRequest();
        updateMachineSkill(deltaSeconds);
        updatePlayerInvulnerability(deltaSeconds);
        updatePowerUpTimers(deltaSeconds);
        scrollingBackground.update(deltaSeconds);

        gameplayTimeSeconds += deltaSeconds;
        updateEnemySpawning(deltaSeconds);
        updateEnemies(deltaSeconds);

        Player currentPlayer = player;
        if (currentPlayer != null) {
            currentPlayer.update();
            updateAntiCampingState(deltaSeconds);
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

    private void updateGameOver(float deltaSeconds) {
        gameOverTimerSeconds = Math.max(0f, gameOverTimerSeconds - deltaSeconds);
        if (gameOverTimerSeconds > 0f || gameOverCallbackSent) {
            return;
        }

        gameOverCallbackSent = true;
        GameOverListener listener = gameOverListener;
        if (listener != null) {
            listener.onGameOver(
                    finalScore,
                    selectedMachineId,
                    selectedDifficultyId
            );
        }
    }

    private void render() {
        if (!surfaceReady || !surfaceHolder.getSurface().isValid()) {
            return;
        }

        Canvas canvas = null;
        boolean canvasLocked = false;

        try {
            canvas = surfaceHolder.lockHardwareCanvas();

            if (canvas == null) {
                return;
            }

            canvasLocked = true;
            canvas.drawColor(backgroundColor);

            scrollingBackground.draw(canvas);

            drawPowerUps(canvas);
            drawEnemies(canvas);
            drawPlayerBullets(canvas);
            drawBombardmentBombs(canvas);
            drawEnemyBullets(canvas);
            drawSkillExplosions(canvas);
            drawPowerUpCollectEffects(canvas);

            drawCrewzerOverdrive(canvas);

            Player currentPlayer = player;
            if (currentPlayer != null && shouldDrawPlayer()) {
                currentPlayer.draw(canvas);
            }

            drawLanderEnergyShield(canvas);
            drawUniversalShield(canvas);
            drawSkillActivationEffect(canvas);

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

    private void drawBombardmentBombs(Canvas canvas) {
        for (BombardmentBomb bomb : bombardmentBombs) {
            bomb.draw(canvas, skillProjectilePaint);
        }
    }

    private void drawEnemyBullets(Canvas canvas) {
        for (EnemyBullet enemyBullet : enemyBullets) {
            enemyBullet.draw(canvas, enemyProjectilePaint);
        }
    }

    private void drawSkillExplosions(Canvas canvas) {
        for (SkillExplosion explosion : skillExplosions) {
            explosion.draw(canvas, skillEffectPaint);
        }
    }

    private void drawCrewzerOverdrive(Canvas canvas) {
        if (!isCrewzerSkillActive()) {
            return;
        }

        Player currentPlayer = player;
        if (currentPlayer == null || !currentPlayer.isPrepared()) {
            return;
        }

        int previousColor = skillEffectPaint.getColor();
        int previousAlpha = skillEffectPaint.getAlpha();
        Paint.Style previousStyle = skillEffectPaint.getStyle();

        if (crewzerAuraBitmap != null && !crewzerAuraBitmap.isRecycled()) {
            skillEffectPaint.setAlpha(190);
            canvas.drawBitmap(
                    crewzerAuraBitmap,
                    currentPlayer.getCenterX() - crewzerAuraBitmap.getWidth() / 2f,
                    currentPlayer.getCenterY() - crewzerAuraBitmap.getHeight() / 2f,
                    skillEffectPaint
            );
            skillEffectPaint.setColor(previousColor);
            skillEffectPaint.setAlpha(previousAlpha);
            skillEffectPaint.setStyle(previousStyle);
            return;
        }

        float radius = Math.max(
                currentPlayer.getWidth(),
                currentPlayer.getHeight()
        ) * 0.72f;
        skillEffectPaint.setColor(0xFF5CD6FF);
        skillEffectPaint.setAlpha(145);
        skillEffectPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(
                currentPlayer.getCenterX(),
                currentPlayer.getCenterY(),
                radius,
                skillEffectPaint
        );
        skillEffectPaint.setAlpha(75);
        canvas.drawCircle(
                currentPlayer.getCenterX(),
                currentPlayer.getCenterY(),
                radius * 1.14f,
                skillEffectPaint
        );

        skillEffectPaint.setColor(previousColor);
        skillEffectPaint.setAlpha(previousAlpha);
        skillEffectPaint.setStyle(previousStyle);
    }

    private void drawLanderEnergyShield(Canvas canvas) {
        if (!isLanderSkillActive()) {
            return;
        }

        Player currentPlayer = player;
        if (currentPlayer == null || !currentPlayer.isPrepared()) {
            return;
        }

        int previousColor = skillEffectPaint.getColor();
        int previousAlpha = skillEffectPaint.getAlpha();
        Paint.Style previousStyle = skillEffectPaint.getStyle();

        if (landerEnergyShieldBitmap != null
                && !landerEnergyShieldBitmap.isRecycled()) {
            skillEffectPaint.setAlpha(200);
            canvas.drawBitmap(
                    landerEnergyShieldBitmap,
                    currentPlayer.getCenterX() - landerEnergyShieldBitmap.getWidth() / 2f,
                    currentPlayer.getCenterY() - landerEnergyShieldBitmap.getHeight() / 2f,
                    skillEffectPaint
            );
            skillEffectPaint.setColor(previousColor);
            skillEffectPaint.setAlpha(previousAlpha);
            skillEffectPaint.setStyle(previousStyle);
            return;
        }

        float radius = Math.max(
                currentPlayer.getWidth(),
                currentPlayer.getHeight()
        ) * LANDER_SHIELD_RADIUS_MULTIPLIER;
        skillEffectPaint.setColor(0xFF65D38A);
        skillEffectPaint.setAlpha(38);
        skillEffectPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(
                currentPlayer.getCenterX(),
                currentPlayer.getCenterY(),
                radius,
                skillEffectPaint
        );
        skillEffectPaint.setAlpha(190);
        skillEffectPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(
                currentPlayer.getCenterX(),
                currentPlayer.getCenterY(),
                radius,
                skillEffectPaint
        );

        skillEffectPaint.setColor(previousColor);
        skillEffectPaint.setAlpha(previousAlpha);
        skillEffectPaint.setStyle(previousStyle);
    }

    private void drawUniversalShield(Canvas canvas) {
        if (!shieldActive) {
            return;
        }

        Player currentPlayer = player;
        if (currentPlayer == null || !currentPlayer.isPrepared()) {
            return;
        }

        int previousColor = skillEffectPaint.getColor();
        int previousAlpha = skillEffectPaint.getAlpha();
        Paint.Style previousStyle = skillEffectPaint.getStyle();

        if (shieldEffectBitmap != null && !shieldEffectBitmap.isRecycled()) {
            skillEffectPaint.setAlpha(175);
            canvas.drawBitmap(
                    shieldEffectBitmap,
                    currentPlayer.getCenterX() - shieldEffectBitmap.getWidth() / 2f,
                    currentPlayer.getCenterY() - shieldEffectBitmap.getHeight() / 2f,
                    skillEffectPaint
            );
        } else {
            float radius = Math.max(
                    currentPlayer.getWidth(),
                    currentPlayer.getHeight()
            ) * LANDER_SHIELD_RADIUS_MULTIPLIER;
            skillEffectPaint.setColor(0xFF65D38A);
            skillEffectPaint.setAlpha(145);
            skillEffectPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(
                    currentPlayer.getCenterX(),
                    currentPlayer.getCenterY(),
                    radius,
                    skillEffectPaint
            );
        }

        skillEffectPaint.setColor(previousColor);
        skillEffectPaint.setAlpha(previousAlpha);
        skillEffectPaint.setStyle(previousStyle);
    }

    private void drawSkillActivationEffect(Canvas canvas) {
        if (skillActivationEffectRemainingSeconds <= 0f) {
            return;
        }

        float progress = 1f - skillActivationEffectRemainingSeconds
                / SKILL_ACTIVATION_EFFECT_SECONDS;
        float radius = Math.max(18f * density, screenWidth * 0.08f) * (0.55f + progress);
        int alpha = Math.round(255f * (1f - progress));
        int previousColor = skillEffectPaint.getColor();
        int previousAlpha = skillEffectPaint.getAlpha();
        Paint.Style previousStyle = skillEffectPaint.getStyle();

        skillEffectPaint.setColor(0xFFFFD447);
        skillEffectPaint.setAlpha(alpha);
        skillEffectPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(
                skillActivationCenterX,
                skillActivationCenterY,
                radius,
                skillEffectPaint
        );
        canvas.drawLine(
                skillActivationCenterX - radius * 1.35f,
                skillActivationCenterY,
                skillActivationCenterX + radius * 1.35f,
                skillActivationCenterY,
                skillEffectPaint
        );
        canvas.drawLine(
                skillActivationCenterX,
                skillActivationCenterY - radius * 1.35f,
                skillActivationCenterX,
                skillActivationCenterY + radius * 1.35f,
                skillEffectPaint
        );

        skillEffectPaint.setColor(previousColor);
        skillEffectPaint.setAlpha(previousAlpha);
        skillEffectPaint.setStyle(previousStyle);
    }

    private void processSkillActivationRequest() {
        if (!skillActivationRequested) {
            return;
        }

        skillActivationRequested = false;
        if (gameOver
                || menuPaused
                || skillActive
                || skillCooldownRemainingSeconds > 0f) {
            return;
        }

        startSelectedMachineSkill();
    }

    private void startSelectedMachineSkill() {
        float durationSeconds = getSkillDurationSeconds();
        if (durationSeconds <= 0f) {
            return;
        }

        Player currentPlayer = player;
        if (currentPlayer == null || !currentPlayer.isPrepared()) {
            return;
        }

        skillActive = true;
        skillRemainingSeconds = durationSeconds;
        skillActivationEffectRemainingSeconds = SKILL_ACTIVATION_EFFECT_SECONDS;
        skillActivationCenterX = currentPlayer.getCenterX();
        skillActivationCenterY = currentPlayer.getCenterY();
        fireCooldownSeconds = 0f;

        if (isBomberSkillActive()) {
            bomberBombsSpawned = 0;
            bomberBombScheduleSeconds = 0f;
        }

        notifySkillStateChangedIfNeeded();
    }

    private void updateMachineSkill(float deltaSeconds) {
        if (skillActivationEffectRemainingSeconds > 0f) {
            skillActivationEffectRemainingSeconds = Math.max(
                    0f,
                    skillActivationEffectRemainingSeconds - deltaSeconds
            );
        }

        if (skillActive) {
            skillRemainingSeconds = Math.max(
                    0f,
                    skillRemainingSeconds - deltaSeconds
            );

            if (isBomberSkillActive()) {
                updateBombardmentSchedule(deltaSeconds);
            }

            if (skillRemainingSeconds <= 0f) {
                finishMachineSkill();
            }
        } else if (skillCooldownRemainingSeconds > 0f) {
            skillCooldownRemainingSeconds = Math.max(
                    0f,
                    skillCooldownRemainingSeconds - deltaSeconds
            );
        }

        updateBombardmentBombs(deltaSeconds);
        updateSkillExplosions(deltaSeconds);
        notifySkillStateChangedIfNeeded();
    }

    private void finishMachineSkill() {
        skillActive = false;
        skillRemainingSeconds = 0f;
        skillCooldownRemainingSeconds = SKILL_COOLDOWN_SECONDS;
        fireCooldownSeconds = 0f;
        notifySkillStateChangedIfNeeded();
    }

    private void updateBombardmentSchedule(float deltaSeconds) {
        if (bomberBombsSpawned >= BOMBER_BOMB_COUNT) {
            return;
        }

        bomberBombScheduleSeconds -= deltaSeconds;
        if (bomberBombScheduleSeconds > 0f) {
            return;
        }

        spawnBombardmentBomb();
        bomberBombsSpawned++;
        bomberBombScheduleSeconds = BOMBER_BOMB_INTERVAL_SECONDS;
    }

    private void spawnBombardmentBomb() {
        Player currentPlayer = player;
        if (currentPlayer == null || !currentPlayer.isPrepared()) {
            return;
        }

        float width = Math.max(1f, screenWidth * BOMBER_BOMB_WIDTH_RATIO);
        float height = Math.max(1f, screenHeight * BOMBER_BOMB_HEIGHT_RATIO);
        float top = currentPlayer.getCenterY() - currentPlayer.getHeight() / 2f - height;
        bombardmentBombs.add(new BombardmentBomb(
                bombardmentBombBitmap,
                currentPlayer.getCenterX(),
                top,
                width,
                height,
                playerBulletSpeedPixelsPerSecond * BOMBER_BOMB_SPEED_MULTIPLIER
        ));
    }

    private void updateBombardmentBombs(float deltaSeconds) {
        float detonationThreshold = screenHeight * BOMBER_BOMB_DETONATION_THRESHOLD_RATIO;
        for (int index = bombardmentBombs.size() - 1; index >= 0; index--) {
            BombardmentBomb bomb = bombardmentBombs.get(index);
            bomb.update(deltaSeconds);

            int enemyIndex = findBombCollision(bomb);
            if (enemyIndex >= 0 || bomb.isPastDetonationThreshold(detonationThreshold)) {
                detonateBomb(index, bomb);
            }
        }
    }

    private int findBombCollision(BombardmentBomb bomb) {
        for (int enemyIndex = enemies.size() - 1; enemyIndex >= 0; enemyIndex--) {
            Enemy enemy = enemies.get(enemyIndex);
            if (rectanglesOverlap(
                    bomb.getX(),
                    bomb.getY(),
                    bomb.getX() + bomb.getWidth(),
                    bomb.getY() + bomb.getHeight(),
                    enemy.getX(),
                    enemy.getY(),
                    enemy.getX() + enemy.getWidth(),
                    enemy.getY() + enemy.getHeight()
            )) {
                return enemyIndex;
            }
        }
        return -1;
    }

    private void detonateBomb(int bombIndex, BombardmentBomb bomb) {
        bombardmentBombs.remove(bombIndex);
        float radius = screenWidth * BOMBER_EXPLOSION_RADIUS_RATIO;
        skillExplosions.add(new SkillExplosion(
                bombardmentExplosionBitmap,
                bomb.getCenterX(),
                bomb.getCenterY(),
                radius,
                BOMBER_EXPLOSION_DURATION_SECONDS
        ));

        resolveBombExplosion(bomb.getCenterX(), bomb.getCenterY(), radius);
    }

    private void resolveBombExplosion(float centerX, float centerY, float radius) {
        float radiusSquared = radius * radius;
        for (int enemyIndex = enemies.size() - 1; enemyIndex >= 0; enemyIndex--) {
            Enemy enemy = enemies.get(enemyIndex);
            float dx = enemy.getCenterX() - centerX;
            float dy = enemy.getY() + enemy.getHeight() / 2f - centerY;
            if (dx * dx + dy * dy > radiusSquared) {
                continue;
            }

            enemies.remove(enemyIndex);
            awardEnemyDestruction(enemy);
        }
    }

    private void updateSkillExplosions(float deltaSeconds) {
        for (int index = skillExplosions.size() - 1; index >= 0; index--) {
            SkillExplosion explosion = skillExplosions.get(index);
            explosion.update(deltaSeconds);
            if (explosion.isFinished()) {
                skillExplosions.remove(index);
            }
        }
    }

    private SkillState getSkillState() {
        if (skillActive) {
            return SkillState.ACTIVE;
        }
        if (skillCooldownRemainingSeconds > 0f) {
            return SkillState.COOLDOWN;
        }
        return SkillState.READY;
    }

    private int getSkillRemainingSeconds() {
        SkillState state = getSkillState();
        if (state == SkillState.ACTIVE) {
            return (int) Math.ceil(skillRemainingSeconds);
        }
        if (state == SkillState.COOLDOWN) {
            return (int) Math.ceil(skillCooldownRemainingSeconds);
        }
        return 0;
    }

    private void notifySkillStateChangedIfNeeded() {
        SkillStateListener listener = skillStateListener;
        if (listener == null) {
            return;
        }

        SkillState state = getSkillState();
        int remainingSeconds = getSkillRemainingSeconds();
        String skillLabel = getSkillLabel();
        if (state == lastNotifiedSkillState
                && remainingSeconds == lastNotifiedSkillSeconds
                && skillLabel.equals(lastNotifiedSkillLabel)) {
            return;
        }

        lastNotifiedSkillState = state;
        lastNotifiedSkillSeconds = remainingSeconds;
        lastNotifiedSkillLabel = skillLabel;
        listener.onSkillStateChanged(skillLabel, state, remainingSeconds);
    }

    private String getSkillLabel() {
        if (skillLabel == null) {
            skillLabel = resolveSkillLabel(selectedMachineId);
        }
        return skillLabel;
    }

    private String resolveSkillLabel(String machineId) {
        switch (machineId) {
            case "volt_bomber":
                return getResources().getString(R.string.game_skill_bomber);
            case "volt_panzer":
                return getResources().getString(R.string.game_skill_panzer);
            case "volt_frigate":
                return getResources().getString(R.string.game_skill_frigate);
            case "volt_lander":
                return getResources().getString(R.string.game_skill_lander);
            case "volt_crewzer":
            default:
                return getResources().getString(R.string.game_skill_crewzer);
        }
    }

    private float getSkillDurationSeconds() {
        switch (selectedMachineId) {
            case "volt_bomber":
                return BOMBER_SKILL_DURATION_SECONDS;
            case "volt_panzer":
                return PANZER_SKILL_DURATION_SECONDS;
            case "volt_frigate":
                return FRIGATE_SKILL_DURATION_SECONDS;
            case "volt_lander":
                return LANDER_SKILL_DURATION_SECONDS;
            case "volt_crewzer":
            default:
                return CREWZER_SKILL_DURATION_SECONDS;
        }
    }

    private boolean isCrewzerSkillActive() {
        return skillActive && "volt_crewzer".equals(selectedMachineId);
    }

    private boolean isBomberSkillActive() {
        return skillActive && "volt_bomber".equals(selectedMachineId);
    }

    private boolean isPanzerSkillActive() {
        return skillActive && "volt_panzer".equals(selectedMachineId);
    }

    private boolean isFrigateSkillActive() {
        return skillActive && "volt_frigate".equals(selectedMachineId);
    }

    private boolean isLanderSkillActive() {
        return skillActive && "volt_lander".equals(selectedMachineId);
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
            boolean wasHeavyBomberManeuvering = enemy.isHeavyBomberManeuvering();
            if (enemy.getType() == ENEMY_HEAVY_BOMBER) {
                enemy.updateHeavyBomberDecisionTimer(deltaSeconds);
            }
            enemy.update(deltaSeconds);
            enemy.updateFireCooldown(deltaSeconds);

            if (enemy.getType() == ENEMY_HEAVY_BOMBER
                    && !wasHeavyBomberManeuvering
                    && enemy.isHeavyBomberDecisionReady()
                    && enemy.getY() >= screenHeight * 0.25f
                    && enemy.getY() <= screenHeight * 0.55f) {
                enemy.resetHeavyBomberDecisionTimer(
                        getHeavyBomberDecisionDelaySeconds()
                );
                if (enemyRandom.nextFloat() < getHeavyBomberManeuverChance()) {
                    enemy.beginHeavyBomberManeuver(
                            chooseHeavyBomberStrafeDirection(enemy),
                            getHeavyBomberManeuverDurationSeconds(),
                            screenHeight * HEAVY_BOMBER_RETREAT_DISTANCE_RATIO,
                            screenHeight * HEAVY_BOMBER_RETREAT_SPEED_RATIO,
                            screenWidth * HEAVY_BOMBER_STRAFE_SPEED_RATIO
                    );
                }
            }
        }
    }

    private void resetAntiCampingState() {
        lastSpawnLane = -1;
        pressureSpawnLane = -1;
        bottomCenterCampTimerSeconds = 0f;
        antiCampingCooldownSeconds = 0f;
        campReferenceCenterX = 0f;
        campReferenceCenterY = 0f;
        campReferenceInitialized = false;
    }

    private void updateAntiCampingState(float deltaSeconds) {
        if (antiCampingCooldownSeconds > 0f) {
            antiCampingCooldownSeconds = Math.max(
                    0f,
                    antiCampingCooldownSeconds - deltaSeconds
            );
        }

        if (!isPlayerCampingBottomCenter()) {
            bottomCenterCampTimerSeconds = 0f;
            campReferenceInitialized = false;
            return;
        }

        Player currentPlayer = player;
        float currentCenterX = currentPlayer.getCenterX();
        float currentCenterY = currentPlayer.getCenterY();
        if (!campReferenceInitialized) {
            campReferenceCenterX = currentCenterX;
            campReferenceCenterY = currentCenterY;
            campReferenceInitialized = true;
            return;
        }

        float movementTolerancePixels = PLAYER_CAMPING_MOVEMENT_TOLERANCE_DP * density;
        if (Math.abs(currentCenterX - campReferenceCenterX) > movementTolerancePixels
                || Math.abs(currentCenterY - campReferenceCenterY) > movementTolerancePixels) {
            bottomCenterCampTimerSeconds = 0f;
            campReferenceCenterX = currentCenterX;
            campReferenceCenterY = currentCenterY;
            return;
        }

        bottomCenterCampTimerSeconds += deltaSeconds;
        if (bottomCenterCampTimerSeconds < getCampingDurationSeconds()
                || antiCampingCooldownSeconds > 0f) {
            return;
        }

        pressureSpawnLane = choosePressureSpawnLane();
        bottomCenterCampTimerSeconds = 0f;
        antiCampingCooldownSeconds = getAntiCampingCooldownSeconds();
    }

    private boolean isPlayerCampingBottomCenter() {
        Player currentPlayer = player;
        if (currentPlayer == null || !currentPlayer.isPrepared()) {
            return false;
        }

        float centerX = currentPlayer.getCenterX();
        float centerY = currentPlayer.getCenterY();
        return centerX >= screenWidth * BOTTOM_CENTER_ZONE_LEFT_RATIO
                && centerX <= screenWidth * BOTTOM_CENTER_ZONE_RIGHT_RATIO
                && centerY >= screenHeight * BOTTOM_CENTER_ZONE_TOP_RATIO;
    }

    private float getCampingDurationSeconds() {
        if ("easy".equals(selectedDifficultyId)) {
            return CAMPING_DURATION_EASY_SECONDS;
        }
        if ("hard".equals(selectedDifficultyId)) {
            return CAMPING_DURATION_HARD_SECONDS;
        }
        return CAMPING_DURATION_NORMAL_SECONDS;
    }

    private float getAntiCampingCooldownSeconds() {
        if ("easy".equals(selectedDifficultyId)) {
            return ANTI_CAMPING_COOLDOWN_EASY_SECONDS;
        }
        if ("hard".equals(selectedDifficultyId)) {
            return ANTI_CAMPING_COOLDOWN_HARD_SECONDS;
        }
        return ANTI_CAMPING_COOLDOWN_NORMAL_SECONDS;
    }

    private int choosePressureSpawnLane() {
        Player currentPlayer = player;
        if (currentPlayer == null || screenWidth <= 0) {
            return SPAWN_LANE_COUNT / 2;
        }

        int playerLane = getLaneForFraction(currentPlayer.getCenterX() / screenWidth);
        if (enemyRandom.nextBoolean()) {
            if (playerLane == 0) {
                return 1;
            }
            if (playerLane == SPAWN_LANE_COUNT - 1) {
                return SPAWN_LANE_COUNT - 2;
            }
            return enemyRandom.nextBoolean() ? playerLane - 1 : playerLane + 1;
        }
        return playerLane;
    }

    private int getLaneForFraction(float fraction) {
        int lane = Math.round(fraction * (SPAWN_LANE_COUNT - 1));
        return Math.max(0, Math.min(SPAWN_LANE_COUNT - 1, lane));
    }

    private float getHeavyBomberDecisionDelaySeconds() {
        return HEAVY_BOMBER_DECISION_DELAY_MIN_SECONDS
                + enemyRandom.nextFloat()
                * (HEAVY_BOMBER_DECISION_DELAY_MAX_SECONDS
                - HEAVY_BOMBER_DECISION_DELAY_MIN_SECONDS);
    }

    private float getHeavyBomberManeuverDurationSeconds() {
        return HEAVY_BOMBER_MANEUVER_DURATION_MIN_SECONDS
                + enemyRandom.nextFloat()
                * (HEAVY_BOMBER_MANEUVER_DURATION_MAX_SECONDS
                - HEAVY_BOMBER_MANEUVER_DURATION_MIN_SECONDS);
    }

    private float getHeavyBomberManeuverChance() {
        if ("easy".equals(selectedDifficultyId)) {
            return 0.20f;
        }
        if ("hard".equals(selectedDifficultyId)) {
            return 0.30f;
        }
        return 0.25f;
    }

    private int chooseHeavyBomberStrafeDirection(Enemy enemy) {
        float centerX = enemy.getCenterX();
        if (centerX < screenWidth * 0.30f) {
            return 1;
        }
        if (centerX > screenWidth * 0.70f) {
            return -1;
        }
        return enemyRandom.nextBoolean() ? -1 : 1;
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
                : preferredX * screenWidth - bitmap.getWidth() / 2f;
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
        if (enemyType == ENEMY_BOAZANIAN) {
            enemy.setRotationSpeedDegreesPerSecond(
                    BOAZANIAN_ROTATION_SPEED_DEGREES_PER_SECOND
            );
        } else if (enemyType == ENEMY_HEAVY_BOMBER) {
            enemy.configureHeavyBomber(getHeavyBomberDecisionDelaySeconds());
        }
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
            int lane = chooseSpawnLane();
            return SPAWN_LANE_CENTER_FRACTIONS[lane];
        }

        if (pressureSpawnLane >= 0 && index == 0) {
            int lane = pressureSpawnLane;
            pressureSpawnLane = -1;
            lastSpawnLane = lane;
            return SPAWN_LANE_CENTER_FRACTIONS[lane];
        }

        if (spawnPattern == SPAWN_ALTERNATING) {
            boolean leftFirst = alternatingSpawnFromLeft;
            if (index == 0) {
                alternatingSpawnFromLeft = !alternatingSpawnFromLeft;
            }
            return (index == 0) == leftFirst ? 0.10f : 0.68f;
        }

        if (count == 2 && spawnPattern != SPAWN_ROW) {
            return index == 0 ? 0.16f : 0.68f;
        }

        float fraction = (index + 1f) / (count + 1f);
        return fraction;
    }

    private int chooseSpawnLane() {
        if (pressureSpawnLane >= 0) {
            int lane = pressureSpawnLane;
            pressureSpawnLane = -1;
            lastSpawnLane = lane;
            return lane;
        }

        int lane = lastSpawnLane;
        for (int attempt = 0; attempt < 3 && lane == lastSpawnLane; attempt++) {
            int roll = enemyRandom.nextInt(100);
            int cumulativeWeight = 0;
            for (int index = 0; index < SPAWN_LANE_COUNT; index++) {
                cumulativeWeight += SPAWN_LANE_WEIGHTS[index];
                if (roll < cumulativeWeight) {
                    lane = index;
                    break;
                }
            }
        }

        if (lane < 0 || lane == lastSpawnLane) {
            lane = (lastSpawnLane + 1) % SPAWN_LANE_COUNT;
        }
        lastSpawnLane = lane;
        return lane;
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
                int shotCount = getFireSequenceShotCount(enemy.getType());
                if (enemy.getType() == ENEMY_BOAZANIAN) {
                    shotCount = Math.min(
                            shotCount,
                            maximumEnemyBullets - enemyBullets.size()
                    );
                }
                enemy.beginFireSequence(shotCount);
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
        if (enemyType == ENEMY_BOAZANIAN) {
            int roll = enemyRandom.nextInt(100);
            if ("easy".equals(selectedDifficultyId)) {
                return roll < 80 ? 2 : 3;
            }
            if ("hard".equals(selectedDifficultyId)) {
                return roll < 25 ? 2 : (roll < 65 ? 3 : 4);
            }
            return roll < 40 ? 2 : (roll < 80 ? 3 : 4);
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
        float velocityX = 0f;
        float velocityY = speed;
        if (enemyType == ENEMY_BOAZANIAN) {
            double angleRadians = Math.toRadians(
                    getBoazanianShotAngle(
                            enemy.getFireSequenceShotCount(),
                            enemy.getFireSequenceShotIndex()
                    )
            );
            velocityX = (float) Math.cos(angleRadians) * speed;
            velocityY = (float) Math.sin(angleRadians) * speed;
        }

        enemyBullets.add(new EnemyBullet(
                projectileBitmap,
                enemy.getCenterX(),
                spawnY,
                width,
                height,
                velocityX,
                velocityY,
                getEnemyBulletVisualType(enemyType)
        ));
    }

    private float getBoazanianShotAngle(int shotCount, int shotIndex) {
        if (shotCount <= 2) {
            return shotIndex == 0 ? 70f : 110f;
        }
        if (shotCount == 3) {
            switch (shotIndex) {
                case 0:
                    return 65f;
                case 1:
                    return 90f;
                default:
                    return 115f;
            }
        }
        switch (shotIndex) {
            case 0:
                return 55f;
            case 1:
                return 75f;
            case 2:
                return 105f;
            default:
                return 125f;
        }
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
        twinShotPowerUpBitmap = loadScaledPowerUpBitmap(R.drawable.powerup_twin_shot);

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
                && extraLifePowerUpBitmap != null
                && twinShotPowerUpBitmap != null;
    }

    private void releasePowerUpBitmaps() {
        releaseBitmap(shieldPowerUpBitmap);
        releaseBitmap(rapidFirePowerUpBitmap);
        releaseBitmap(doubleScorePowerUpBitmap);
        releaseBitmap(extraLifePowerUpBitmap);
        releaseBitmap(twinShotPowerUpBitmap);
        releaseBitmap(powerUpCollectEffectBitmap);
        shieldPowerUpBitmap = null;
        rapidFirePowerUpBitmap = null;
        doubleScorePowerUpBitmap = null;
        extraLifePowerUpBitmap = null;
        twinShotPowerUpBitmap = null;
        powerUpCollectEffectBitmap = null;
        preparedPowerUpWidth = 0;
        preparedPowerUpHeight = 0;
    }

    private void prepareSkillEffectBitmapsIfReady() {
        if (screenWidth <= 0 || screenHeight <= 0) {
            return;
        }

        if (preparedSkillEffectWidth == screenWidth
                && preparedSkillEffectHeight == screenHeight
                && areSkillEffectBitmapsReady()) {
            return;
        }

        releaseSkillEffectBitmaps();
        crewzerAuraBitmap = loadScaledSkillEffectBitmap(
                R.drawable.skill_crewzer_aura,
                0.30f
        );
        bombardmentBombBitmap = loadScaledSkillEffectBitmap(
                R.drawable.skill_bombardment_bomb,
                BOMBER_BOMB_WIDTH_RATIO
        );
        bombardmentExplosionBitmap = loadScaledSkillEffectBitmap(
                R.drawable.effect_bomb_explosion,
                BOMBER_EXPLOSION_RADIUS_RATIO * 2f
        );
        panzerPowerShotBitmap = loadScaledSkillEffectBitmap(
                R.drawable.skill_panzer_power_shot_effect,
                0.12f
        );
        frigateBarrageShotBitmap = loadScaledSkillEffectBitmap(
                R.drawable.skill_frigate_barrage_shot,
                0.06f
        );
        landerEnergyShieldBitmap = loadScaledSkillEffectBitmap(
                R.drawable.skill_lander_energy_shield_effect,
                0.30f
        );
        shieldEffectBitmap = loadScaledSkillEffectBitmap(
                R.drawable.shield_effect,
                0.30f
        );
        preparedSkillEffectWidth = screenWidth;
        preparedSkillEffectHeight = screenHeight;
    }

    private Bitmap loadScaledSkillEffectBitmap(
            int resourceId,
            float widthRatio
    ) {
        Bitmap sourceBitmap = BitmapFactory.decodeResource(
                getResources(),
                resourceId
        );
        if (sourceBitmap == null
                || sourceBitmap.getWidth() <= 0
                || sourceBitmap.getHeight() <= 0) {
            return null;
        }

        Bitmap croppedBitmap = cropToOpaqueBounds(sourceBitmap);
        if (croppedBitmap != sourceBitmap) {
            sourceBitmap.recycle();
        }

        int targetWidth = Math.max(
                1,
                Math.round(screenWidth * widthRatio)
        );
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

    private boolean areSkillEffectBitmapsReady() {
        return crewzerAuraBitmap != null
                && bombardmentBombBitmap != null
                && bombardmentExplosionBitmap != null
                && panzerPowerShotBitmap != null
                && frigateBarrageShotBitmap != null
                && landerEnergyShieldBitmap != null
                && shieldEffectBitmap != null;
    }

    private void releaseSkillEffectBitmaps() {
        releaseBitmap(crewzerAuraBitmap);
        releaseBitmap(bombardmentBombBitmap);
        releaseBitmap(bombardmentExplosionBitmap);
        releaseBitmap(panzerPowerShotBitmap);
        releaseBitmap(frigateBarrageShotBitmap);
        releaseBitmap(landerEnergyShieldBitmap);
        releaseBitmap(shieldEffectBitmap);
        crewzerAuraBitmap = null;
        bombardmentBombBitmap = null;
        bombardmentExplosionBitmap = null;
        panzerPowerShotBitmap = null;
        frigateBarrageShotBitmap = null;
        landerEnergyShieldBitmap = null;
        shieldEffectBitmap = null;
        preparedSkillEffectWidth = 0;
        preparedSkillEffectHeight = 0;
    }

    private void prepareHeartLiveBitmapIfReady() {
        if (screenWidth <= 0 || screenHeight <= 0) {
            return;
        }

        if (heartLiveBitmap != null
                && preparedHeartLiveWidth == screenWidth
                && preparedHeartLiveHeight == screenHeight) {
            return;
        }

        releaseHeartLiveBitmap();

        Bitmap sourceBitmap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.heart_live
        );
        if (sourceBitmap == null
                || sourceBitmap.getWidth() <= 0
                || sourceBitmap.getHeight() <= 0) {
            preparedHeartLiveWidth = screenWidth;
            preparedHeartLiveHeight = screenHeight;
            return;
        }

        Bitmap croppedBitmap = cropToOpaqueBounds(sourceBitmap);
        if (croppedBitmap != sourceBitmap) {
            sourceBitmap.recycle();
        }

        int targetSize = getHeartTargetSizePixels();
        heartLiveBitmap = Bitmap.createScaledBitmap(
                croppedBitmap,
                targetSize,
                targetSize,
                true
        );
        if (heartLiveBitmap != croppedBitmap) {
            croppedBitmap.recycle();
        }

        preparedHeartLiveWidth = screenWidth;
        preparedHeartLiveHeight = screenHeight;
    }

    private int getHeartTargetSizePixels() {
        float screenWidthDp = screenWidth / density;
        float hudWidthDp = screenWidthDp
                - (HUD_MARGIN_DP * 2f)
                - PAUSE_BUTTON_SIZE_DP
                - HUD_PAUSE_GAP_DP;
        float livesSectionWidthDp = Math.max(0f, hudWidthDp * 0.50f);
        float availableWidthForHeartsDp = livesSectionWidthDp
                - HUD_PANEL_PADDING_DP * 2f
                - HUD_HEART_SPACING_DP * (MAX_PLAYER_LIVES - 1);
        float heartSizeDp = availableWidthForHeartsDp / MAX_PLAYER_LIVES;
        heartSizeDp = Math.max(14f, Math.min(HEART_LIVE_SIZE_DP, heartSizeDp));
        return Math.max(1, Math.round(heartSizeDp * density));
    }

    private void releaseHeartLiveBitmap() {
        releaseBitmap(heartLiveBitmap);
        heartLiveBitmap = null;
        preparedHeartLiveWidth = 0;
        preparedHeartLiveHeight = 0;
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

                boolean bulletConsumed = bullet.consumeHit();
                if (bulletConsumed) {
                    playerBullets.remove(bulletIndex);
                }
                enemies.remove(enemyIndex);
                awardEnemyDestruction(enemy);
                break;
            }
        }
    }

    private void awardEnemyDestruction(Enemy enemy) {
        spawnEnemyExplosion(enemy);
        int enemyScore = getScoreForEnemy(enemy);
        score += doubleScoreTimerSeconds > 0f
                ? enemyScore * 2
                : enemyScore;
        updateScoreLine();
        spawnPowerUp(
                enemy.getCenterX(),
                enemy.getY() + enemy.getHeight() / 2f
        );
    }

    private void spawnEnemyExplosion(Enemy enemy) {
        float radius = screenWidth * ENEMY_EXPLOSION_RADIUS_RATIO;
        skillExplosions.add(new SkillExplosion(
                bombardmentExplosionBitmap,
                enemy.getCenterX(),
                enemy.getY() + enemy.getHeight() / 2f,
                radius,
                ENEMY_EXPLOSION_DURATION_SECONDS,
                ENEMY_EXPLOSION_BITMAP_SCALE
        ));
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
        if (roll < SHIELD_POWER_UP_WEIGHT) {
            return PowerUpType.SHIELD;
        }
        if (roll < SHIELD_POWER_UP_WEIGHT + RAPID_FIRE_POWER_UP_WEIGHT) {
            return PowerUpType.RAPID_FIRE;
        }
        if (roll < SHIELD_POWER_UP_WEIGHT
                + RAPID_FIRE_POWER_UP_WEIGHT
                + DOUBLE_SCORE_POWER_UP_WEIGHT) {
            return PowerUpType.DOUBLE_SCORE;
        }
        if (roll < SHIELD_POWER_UP_WEIGHT
                + RAPID_FIRE_POWER_UP_WEIGHT
                + DOUBLE_SCORE_POWER_UP_WEIGHT
                + EXTRA_LIFE_POWER_UP_WEIGHT) {
            return PowerUpType.EXTRA_LIFE;
        }
        return PowerUpType.TWIN_SHOT;
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
            case TWIN_SHOT:
                return twinShotPowerUpBitmap;
            default:
                return null;
        }
    }

    private void updatePowerUpTimers(float deltaSeconds) {
        if (rapidFireTimerSeconds > 0f) {
            rapidFireTimerSeconds = Math.max(0f, rapidFireTimerSeconds - deltaSeconds);
        }
        if (twinShotTimerSeconds > 0f) {
            twinShotTimerSeconds = Math.max(0f, twinShotTimerSeconds - deltaSeconds);
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
            case TWIN_SHOT:
                twinShotTimerSeconds = TWIN_SHOT_DURATION_SECONDS;
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
        if (currentPlayer == null || !currentPlayer.isPrepared()) {
            return;
        }

        for (int enemyIndex = enemies.size() - 1; enemyIndex >= 0; enemyIndex--) {
            Enemy enemy = enemies.get(enemyIndex);
            if (!intersectsPlayerAndEnemy(currentPlayer, enemy)) {
                continue;
            }

            if (isLanderSkillActive()) {
                enemies.remove(enemyIndex);
                awardEnemyDestruction(enemy);
            } else if (isCrewzerSkillActive()) {
                // Crewzer immunity absorbs the contact without consuming Shield.
            } else if (canPlayerTakeDamage(currentPlayer)) {
                enemies.remove(enemyIndex);
                damagePlayer();
            }
            break;
        }
    }

    private boolean isMachineSkillProtectingPlayer() {
        return isCrewzerSkillActive() || isLanderSkillActive();
    }

    private boolean canPlayerTakeDamage(Player currentPlayer) {
        return currentPlayer != null
                && currentPlayer.isPrepared()
                && playerLives > 0
                && !playerInvulnerable;
    }

    private void damagePlayer() {
        if (gameOver
                || playerLives <= 0
                || playerInvulnerable
                || isMachineSkillProtectingPlayer()) {
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

        if (playerLives == 0) {
            gameOver = true;
            gameOverTimerSeconds = GAME_OVER_HOLD_SECONDS;
            finalScore = score;
            playerInvulnerable = false;
            playerInvulnerabilityTimerSeconds = 0f;
            skillActive = false;
            skillRemainingSeconds = 0f;
            skillActivationRequested = false;
            skillActivationEffectRemainingSeconds = 0f;
            bombardmentBombs.clear();
            skillExplosions.clear();
            playerBullets.clear();
            enemyBullets.clear();
            powerUps.clear();
            powerUpCollectEffects.clear();
            notifySkillStateChangedIfNeeded();
        }
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

        return isLanderSkillActive()
                ? intersectsPlayerShieldBounds(
                        currentPlayer,
                        enemyLeft,
                        enemyTop,
                        enemyRight,
                        enemyBottom
                )
                : intersectsPlayerBounds(
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

        return isLanderSkillActive()
                ? intersectsPlayerShieldBounds(
                        currentPlayer,
                        bulletLeft,
                        bulletTop,
                        bulletRight,
                        bulletBottom
                )
                : intersectsPlayerBounds(
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

    private boolean intersectsPlayerShieldBounds(
            Player currentPlayer,
            float targetLeft,
            float targetTop,
            float targetRight,
            float targetBottom
    ) {
        float radius = Math.max(
                currentPlayer.getWidth(),
                currentPlayer.getHeight()
        ) * LANDER_SHIELD_RADIUS_MULTIPLIER;
        float centerX = currentPlayer.getCenterX();
        float centerY = currentPlayer.getCenterY();
        return rectanglesOverlap(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius,
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

        if (isBomberSkillActive()) {
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
        if (isFrigateSkillActive()) {
            return FRIGATE_TWIN_SHOT_INTERVAL_SECONDS;
        }

        float intervalSeconds = AUTO_FIRE_INTERVAL_SECONDS;
        if (isPanzerSkillActive()) {
            intervalSeconds *= PANZER_FIRE_INTERVAL_MULTIPLIER;
        }
        if (isCrewzerSkillActive()) {
            intervalSeconds *= CREWZER_FIRE_INTERVAL_MULTIPLIER;
        }
        if (rapidFireTimerSeconds > 0f) {
            intervalSeconds *= RAPID_FIRE_INTERVAL_MULTIPLIER;
        }

        return Math.max(0.23f, intervalSeconds);
    }

    private void firePlayerProjectile() {
        Player currentPlayer = player;
        if (currentPlayer == null || projectileBitmap == null) {
            return;
        }

        float playerCenterX = currentPlayer.getCenterX();
        float playerTop = currentPlayer.getCenterY() - currentPlayer.getHeight() / 2f;
        if (isPanzerSkillActive()) {
            spawnPanzerPowerShot(playerCenterX, playerTop);
            return;
        }

        boolean twinShotActive = isFrigateSkillActive()
                || isCrewzerSkillActive()
                || twinShotTimerSeconds > 0f;
        Bitmap shotBitmap = isFrigateSkillActive()
                ? frigateBarrageShotBitmap
                : projectileBitmap;
        float shotWidth = shotBitmap != null
                ? shotBitmap.getWidth()
                : projectileBitmap.getWidth();
        float shotHeight = shotBitmap != null
                ? shotBitmap.getHeight()
                : projectileBitmap.getHeight();
        float projectileSpeed = getCurrentPlayerProjectileSpeed();
        if (twinShotActive) {
            float shotSpacing = currentPlayer.getWidth() * TWIN_SHOT_SPACING_RATIO;
            spawnBullet(
                    playerCenterX - shotSpacing,
                    playerTop,
                    shotBitmap,
                    shotWidth,
                    shotHeight,
                    projectileSpeed,
                    1
            );
            spawnBullet(
                    playerCenterX + shotSpacing,
                    playerTop,
                    shotBitmap,
                    shotWidth,
                    shotHeight,
                    projectileSpeed,
                    1
            );
            return;
        }

        spawnBullet(
                playerCenterX,
                playerTop,
                shotBitmap,
                projectileBitmap.getWidth(),
                projectileBitmap.getHeight(),
                projectileSpeed,
                1
        );
    }

    private float getCurrentPlayerProjectileSpeed() {
        float speed = playerBulletSpeedPixelsPerSecond;
        if (isCrewzerSkillActive()) {
            speed *= CREWZER_PROJECTILE_SPEED_MULTIPLIER;
        }
        return speed;
    }

    private void spawnPanzerPowerShot(float centerX, float playerTop) {
        Bitmap shotBitmap = panzerPowerShotBitmap;
        float width = shotBitmap != null
                ? shotBitmap.getWidth()
                : projectileBitmap.getWidth() * PANZER_PROJECTILE_SCALE;
        float height = shotBitmap != null
                ? shotBitmap.getHeight()
                : projectileBitmap.getHeight() * PANZER_PROJECTILE_SCALE;
        spawnBullet(
                centerX,
                playerTop,
                shotBitmap,
                width,
                height,
                playerBulletSpeedPixelsPerSecond * PANZER_PROJECTILE_SPEED_MULTIPLIER,
                2
        );
    }

    private void spawnBullet(
            float centerX,
            float playerTop,
            Bitmap bitmap,
            float width,
            float height,
            float speedPixelsPerSecond,
            int remainingHits
    ) {
        float bulletX = centerX - width / 2f;
        float bulletY = playerTop - height + bulletPlayerOverlapPixels;

        playerBullets.add(new Bullet(
                bitmap,
                bulletX,
                bulletY,
                width,
                height,
                speedPixelsPerSecond,
                remainingHits
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
            if (!isMachineSkillProtectingPlayer()
                    && canPlayerTakeDamage(currentPlayer)) {
                damagePlayer();
            }
        }
    }

    private void removeOffScreenEnemyBullets() {
        for (int index = enemyBullets.size() - 1; index >= 0; index--) {
            if (enemyBullets.get(index).isOffScreen(screenWidth, screenHeight)) {
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
        scoreLine = String.format(Locale.US, "%06d", score);
    }

    private void updateLivesLine() {
        livesLine = String.format(Locale.US, "%s %d", livesLabel, playerLives);
    }

    private void updatePowerUpStatusLine() {
        int rapidFireSeconds = rapidFireTimerSeconds > 0f
                ? (int) Math.ceil(rapidFireTimerSeconds)
                : 0;
        int twinShotSeconds = twinShotTimerSeconds > 0f
                ? (int) Math.ceil(twinShotTimerSeconds)
                : 0;
        int doubleScoreSeconds = doubleScoreTimerSeconds > 0f
                ? (int) Math.ceil(doubleScoreTimerSeconds)
                : 0;
        if (rapidFireSeconds == lastRapidFireStatusSeconds
                && twinShotSeconds == lastTwinShotStatusSeconds
                && doubleScoreSeconds == lastDoubleScoreStatusSeconds
                && shieldActive == lastShieldStatus) {
            return;
        }

        lastRapidFireStatusSeconds = rapidFireSeconds;
        lastTwinShotStatusSeconds = twinShotSeconds;
        lastDoubleScoreStatusSeconds = doubleScoreSeconds;
        lastShieldStatus = shieldActive;

        StringBuilder status = new StringBuilder();
        if (shieldActive) {
            status.append("SHIELD");
        }
        if (rapidFireSeconds > 0) {
            appendPowerUpStatus(status, "RAPID ", rapidFireSeconds);
        }
        if (twinShotSeconds > 0) {
            appendPowerUpStatus(status, "TWIN ", twinShotSeconds);
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
        if (canvas.getWidth() <= 0 || canvas.getHeight() <= 0) {
            return;
        }

        float margin = HUD_MARGIN_DP * density;
        float panelPadding = HUD_PANEL_PADDING_DP * density;
        float rowSpacing = HUD_ROW_SPACING_DP * density;
        float panelTop = topSystemInsetPixels + margin;

        float pauseReservedWidth = (PAUSE_BUTTON_SIZE_DP + HUD_PAUSE_GAP_DP) * density;
        float hudLeft = margin;
        float hudRight = canvas.getWidth() - margin - pauseReservedWidth;
        if (hudRight <= hudLeft) {
            return;
        }

        float hudWidth = hudRight - hudLeft;
        float scoreWidth = Math.max(76f * density, hudWidth * 0.31f);
        float difficultyWidth = Math.max(48f * density, hudWidth * 0.19f);
        float livesWidth = hudWidth - scoreWidth - difficultyWidth;
        if (livesWidth < 0f) {
            livesWidth = 0f;
            difficultyWidth = Math.max(0f, hudWidth - scoreWidth);
        }

        float hudHeight = HUD_HEIGHT_DP * density;
        drawHudPanel(canvas, hudLeft, panelTop, hudWidth, hudHeight);

        float contentTop = panelTop + panelPadding;
        float labelBaseline = contentTop + infoPaint.getTextSize();
        canvas.drawText(scoreLabel, hudLeft + panelPadding, labelBaseline, infoPaint);

        float scoreBaseline = labelBaseline
                + rowSpacing
                + scorePaint.getTextSize();
        canvas.drawText(
                scoreLine,
                hudLeft + panelPadding,
                scoreBaseline,
                scorePaint
        );

        float difficultyCenterX = hudLeft + scoreWidth + difficultyWidth / 2f;
        float difficultyCenterY = panelTop + hudHeight / 2f;
        float difficultyBaseline = difficultyCenterY
                - (difficultyPaint.ascent() + difficultyPaint.descent()) / 2f;
        canvas.drawText(
                difficultyLine,
                difficultyCenterX,
                difficultyBaseline,
                difficultyPaint
        );

        float livesLeft = hudLeft + scoreWidth + difficultyWidth;
        canvas.drawText(livesLine, livesLeft + panelPadding, labelBaseline, livesPaint);

        int activeLives = Math.max(0, Math.min(MAX_PLAYER_LIVES, playerLives));
        if (heartLiveBitmap != null && activeLives > 0 && livesWidth > 0f) {
            float heartSpacing = getHeartSpacing(livesWidth, activeLives);
            float heartRowWidth = activeLives * heartLiveBitmap.getWidth()
                    + Math.max(0, activeLives - 1) * heartSpacing;
            float heartLeft = livesLeft + (livesWidth - heartRowWidth) / 2f;
            float heartTop = contentTop + livesPaint.getTextSize() + rowSpacing;
            for (int index = 0; index < activeLives; index++) {
                canvas.drawBitmap(
                        heartLiveBitmap,
                        heartLeft + index * (heartLiveBitmap.getWidth() + heartSpacing),
                        heartTop,
                        heartPaint
                );
            }
        }

        if (powerUpStatusLine != null && !powerUpStatusLine.isEmpty()) {
            canvas.drawText(
                    powerUpStatusLine,
                    hudLeft + panelPadding,
                    panelTop + hudHeight - panelPadding,
                    infoPaint
            );
        }
    }

    private void drawHudPanel(
            Canvas canvas,
            float left,
            float top,
            float width,
            float height
    ) {
        canvas.drawRect(
                left,
                top,
                left + width,
                top + height,
                hudPanelPaint
        );
        canvas.drawRect(
                left,
                top,
                left + width,
                top + height,
                hudBorderPaint
        );
    }

    private float getHeartSpacing(float livesWidth, int activeLives) {
        if (activeLives <= 1 || heartLiveBitmap == null) {
            return 0f;
        }

        float availableSpacing = (
                livesWidth
                        - HUD_PANEL_PADDING_DP * density * 2f
                        - activeLives * heartLiveBitmap.getWidth()
        ) / (activeLives - 1);
        return Math.max(
                density,
                Math.min(HUD_HEART_SPACING_DP * density, availableSpacing)
        );
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
        if (gameOver || menuPaused || !running || !surfaceReady) {
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

                float movementMultiplier = isCrewzerSkillActive()
                        ? CREWZER_MOVEMENT_MULTIPLIER
                        : 1f;
                float dragDeltaX = (event.getX(pointerIndex) - dragStartTouchX)
                        * movementMultiplier;
                float dragDeltaY = (event.getY(pointerIndex) - dragStartTouchY)
                        * movementMultiplier;
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

    private boolean sleepForNanos(long sleepNanos) {
        if (sleepNanos <= 0L) {
            return true;
        }

        long sleepMillis = sleepNanos / 1_000_000L;
        int remainingNanos = (int) (sleepNanos % 1_000_000L);

        try {
            Thread.sleep(sleepMillis, remainingNanos);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;
        }

        return true;
    }

    private void updateDifficultyLine() {
        difficultyLine = getDifficultyDisplayName(selectedDifficultyId);
        difficultyPaint.setColor(getDifficultyColor(selectedDifficultyId));
    }

    private int getDifficultyColor(String difficultyId) {
        switch (difficultyId) {
            case "easy":
                return ContextCompat.getColor(getContext(), R.color.difficulty_easy);
            case "hard":
                return ContextCompat.getColor(getContext(), R.color.difficulty_hard);
            case "normal":
            default:
                return ContextCompat.getColor(getContext(), R.color.difficulty_normal);
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
